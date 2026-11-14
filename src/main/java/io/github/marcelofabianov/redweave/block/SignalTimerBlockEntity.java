package io.github.marcelofabianov.redweave.block;

import com.mojang.logging.LogUtils;
import io.github.marcelofabianov.redweave.config.NeoForgeTimerLimits;
import io.github.marcelofabianov.redweave.domain.module.Connection;
import io.github.marcelofabianov.redweave.domain.module.SignalModule;
import io.github.marcelofabianov.redweave.domain.module.SignalTimer;
import io.github.marcelofabianov.redweave.domain.module.TimerCycle;
import io.github.marcelofabianov.redweave.domain.module.TimerLimits;
import io.github.marcelofabianov.redweave.domain.module.TimerState;
import io.github.marcelofabianov.redweave.menu.TimerMenu;
import io.github.marcelofabianov.redweave.menu.TimerScreenTexts;
import io.github.marcelofabianov.redweave.registry.RedweaveBlockEntities;
import io.github.marcelofabianov.redweave.registry.RedweaveBlocks;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

/**
 * What a Signal Timer block keeps besides its module: its timer and its two connections, all in its
 * NBT and none in the save of the network (answers 387 and 411). The rules stay in the domain.
 */
public final class SignalTimerBlockEntity extends ModuleBlockEntity {
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String FITTED = "Fitted the Signal Timer at {} to the config: {}";
  private static final String RESTARTED = "A Signal Timer at {} had no valid cycle and starts anew";

  private Optional<SignalTimer> timer = Optional.empty();
  private Connection output = Connection.EDGE;
  private Connection pause = Connection.EDGE;

  public SignalTimerBlockEntity(BlockPos pos, BlockState state) {
    super(RedweaveBlockEntities.SIGNAL_TIMER.get(), pos, state);
  }

  /** A timer just placed: stopped, on the edges, one second with a half-second pulse (390). */
  @Override
  void place(SignalModule placed) {
    TimerLimits limits = new NeoForgeTimerLimits();
    timer = Optional.of(SignalTimer.stopped(TimerCycle.initial(limits)));
    output = Connection.EDGE;
    pause = Connection.EDGE;
    setModule(placed);
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
    setChanged();
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
    viewer.openMenu(
        new SimpleMenuProvider(
            (id, inventory, opener) -> new TimerMenu(id, viewer, worldPosition),
            RedweaveBlocks.SIGNAL_TIMER.get().getName()),
        extra -> TimerMenu.writeOpening(extra, viewer, worldPosition));
  }

  /** Keeps the timer after a tick or a change, and marks the block to save only when it changed. */
  public void setTimer(SignalTimer next) {
    if (!timer.equals(Optional.of(next))) {
      timer = Optional.of(next);
      setChanged();
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
  public void onLoad() {
    super.onLoad();
    if (level == null || level.isClientSide || module().isEmpty()) {
      return;
    }
    TimerLimits limits = new NeoForgeTimerLimits();
    if (timer.isEmpty()) {
      LOGGER.warn(RESTARTED, worldPosition);
      timer = Optional.of(SignalTimer.stopped(TimerCycle.initial(limits)));
      setChanged();
    } else if (!timer.get().cycle().fits(limits)) {
      LOGGER.warn(FITTED, worldPosition, timer.get().cycle().intervalTicks());
      timer = timer.map(found -> found.withCycle(found.cycle().fittedTo(limits)));
      setChanged();
    }
  }

  @Override
  protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.saveAdditional(tag, registries);
    timer.ifPresent(found -> TimerSave.writeTimer(tag, found));
    ModuleSave.writeConnection(tag, TimerSave.OUTPUT, output);
    ModuleSave.writeConnection(tag, TimerSave.PAUSE, pause);
  }

  @Override
  protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.loadAdditional(tag, registries);
    timer = TimerSave.readTimer(tag);
    output = ModuleSave.readConnection(tag, TimerSave.OUTPUT);
    pause = ModuleSave.readConnection(tag, TimerSave.PAUSE);
  }
}
