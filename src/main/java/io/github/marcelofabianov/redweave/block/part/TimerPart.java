package io.github.marcelofabianov.redweave.block.part;

import com.mojang.logging.LogUtils;
import io.github.marcelofabianov.redweave.block.ModuleMessages;
import io.github.marcelofabianov.redweave.block.PartSpaceBlockEntity;
import io.github.marcelofabianov.redweave.config.NeoForgeTimerLimits;
import io.github.marcelofabianov.redweave.domain.module.Connection;
import io.github.marcelofabianov.redweave.domain.module.SignalTimer;
import io.github.marcelofabianov.redweave.domain.module.TimerCycle;
import io.github.marcelofabianov.redweave.domain.module.TimerLimits;
import io.github.marcelofabianov.redweave.domain.module.TimerState;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import io.github.marcelofabianov.redweave.menu.TimerMenu;
import io.github.marcelofabianov.redweave.menu.TimerScreenTexts;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import io.github.marcelofabianov.redweave.server.ModuleServices;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.Item;
import org.slf4j.Logger;

/**
 * The Signal Timer as a part: _docs/funcionalidades/signal-timer.md. A module plate with its output
 * on the front edge and its `Pause` on the back one (answers 383, 416 and 422), and its timer and
 * connections in its NBT, none in the save of the network (answers 387 and 411). Its running LED is
 * the `running` of its timer (answer 401).
 */
public final class TimerPart extends ModulePart {
  public static final String ID = "signal_timer";

  /** The looks of the top: the running LED and the lit arrow (answers 401 and 418). */
  public static final List<String> LOOKS = ModuleLooks.leds();

  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String FITTED = "Fitted the Signal Timer at {} to the config: {}";
  private static final String RESTARTED = "A Signal Timer at {} had no valid cycle and starts anew";

  private Optional<SignalTimer> timer = Optional.empty();
  private Connection output = Connection.EDGE;
  private Connection pause = Connection.EDGE;

  private TimerPart(Direction face) {
    super(face);
  }

  /** A timer on the side of its space, from its NBT, or a new one from an empty tag. */
  public static TimerPart load(Direction face, CompoundTag tag) {
    TimerPart part = new TimerPart(face);
    part.loadPlate(tag);
    if (!tag.isEmpty()) {
      part.readRule(tag);
    }
    return part;
  }

  /** What the Configurator copies: its settings, never what it is doing now (answer 725). */
  @Override
  protected List<String> copiedKeys() {
    return List.of(TimerSave.INTERVAL, TimerSave.PULSE, TimerSave.OUTPUT, TimerSave.PAUSE);
  }

  @Override
  protected void dropChannels(ChannelDrop drop) {
    output = drop.of(output);
    pause = drop.of(pause);
  }

  /** Reads its rule from its save, as it loads and as the Configurator pastes (answer 725). */
  @Override
  protected void readRule(CompoundTag tag) {
    timer = TimerSave.readTimer(tag);
    output = ModuleSave.readConnection(tag, TimerSave.OUTPUT);
    pause = ModuleSave.readConnection(tag, TimerSave.PAUSE);
  }

  @Override
  protected String id() {
    return ID;
  }

  @Override
  public Item item() {
    return RedweaveItems.SIGNAL_TIMER.get();
  }

  @Override
  protected String lookSuffix() {
    return ModuleLooks.leds(timer.map(SignalTimer::isRunning).orElse(false), powered());
  }

  /** A timer just placed: stopped, on the edges, one second with a half-second pulse (390). */
  @Override
  protected void placeRule() {
    timer = Optional.of(SignalTimer.stopped(TimerCycle.initial(new NeoForgeTimerLimits())));
    output = Connection.EDGE;
    pause = Connection.EDGE;
  }

  /** It reads its `Pause`, ticks its timer in the domain and writes its output (401). */
  @Override
  protected void tickRule(ModuleServices modules, PartSpaceBlockEntity space) {
    if (timer.isEmpty()) {
      return;
    }
    Signal paused = ModuleTicks.read(modules, space, this, pause);
    SignalTimer next = timer.get().tick(paused);
    setTimer(next);
    ModuleTicks.write(modules, space, this, next.output());
  }

  @Override
  protected void saveRule(CompoundTag tag) {
    timer.ifPresent(found -> TimerSave.writeTimer(tag, found));
    ModuleSave.writeConnection(tag, TimerSave.OUTPUT, output);
    ModuleSave.writeConnection(tag, TimerSave.PAUSE, pause);
  }

  public Optional<SignalTimer> timer() {
    return timer;
  }

  @Override
  public Connection output() {
    return output;
  }

  public Connection pause() {
    return pause;
  }

  /** Starts or stops the timer, and says so with its name (answers 395, 421 and 427). */
  @Override
  public Optional<Component> toggle() {
    timer = timer.map(SignalTimer::toggle);
    markDirty();
    if (timer.isEmpty() || module().isEmpty()) {
      return Optional.empty();
    }
    String key =
        timer.get().isRunning() ? ModuleMessages.TIMER_RUNNING : ModuleMessages.TIMER_STOPPED;
    return Optional.of(Component.translatable(key, module().get().displayName()));
  }

  /** `Running`, `Stopped` or `Paused` (answers 417 and 433). */
  @Override
  public Optional<String> stateKey() {
    return timer.map(found -> stateKeyOf(found.state()));
  }

  private static String stateKeyOf(TimerState state) {
    return switch (state) {
      case RUNNING -> TimerScreenTexts.RUNNING;
      case PAUSED -> TimerScreenTexts.PAUSED;
      case STOPPED -> TimerScreenTexts.STOPPED;
    };
  }

  @Override
  public void openScreen(ServerPlayer viewer) {
    PartSpot spot = spot();
    viewer.openMenu(
        new SimpleMenuProvider(
            (id, inventory, opener) -> new TimerMenu(id, viewer, spot), item().getDescription()),
        extra -> TimerMenu.writeOpening(extra, viewer, spot));
  }

  /** Keeps the timer after a tick or a change, and marks the space to save only when it changed. */
  public void setTimer(SignalTimer next) {
    if (!timer.equals(Optional.of(next))) {
      timer = Optional.of(next);
      markDirty();
    }
  }

  /** The owner moved the output to its edge or to a channel (answers 376 and 404). */
  public void connectOutput(Connection connection) {
    output = connection;
    changed();
  }

  /** The owner moved the `Pause` to its edge or to a channel (answers 376 and 404). */
  public void connectPause(Connection connection) {
    pause = connection;
    changed();
  }

  /**
   * On the server, a loaded timer fits the limits of the config of now: an interval outside them
   * goes to the nearest limit and the cycle restarts, with a warning (answer 425 and rule 14).
   */
  @Override
  public void loaded(PartSpaceBlockEntity space) {
    if (module().isEmpty()) {
      return;
    }
    TimerLimits limits = new NeoForgeTimerLimits();
    if (timer.isEmpty()) {
      LOGGER.warn(RESTARTED, space.getBlockPos());
      timer = Optional.of(SignalTimer.stopped(TimerCycle.initial(limits)));
      markDirty();
    } else if (!timer.get().cycle().fits(limits)) {
      LOGGER.warn(FITTED, space.getBlockPos(), timer.get().cycle().intervalTicks());
      timer = timer.map(found -> found.withCycle(found.cycle().fittedTo(limits)));
      markDirty();
    }
  }
}
