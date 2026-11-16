package io.github.marcelofabianov.redweave.block.part;

import com.mojang.logging.LogUtils;
import io.github.marcelofabianov.redweave.block.ModuleMessages;
import io.github.marcelofabianov.redweave.block.PartSpaceBlockEntity;
import io.github.marcelofabianov.redweave.config.NeoForgeRandomizerLimits;
import io.github.marcelofabianov.redweave.domain.module.Connection;
import io.github.marcelofabianov.redweave.domain.module.Draws;
import io.github.marcelofabianov.redweave.domain.module.RandomizerLimits;
import io.github.marcelofabianov.redweave.domain.module.SignalRandomizer;
import io.github.marcelofabianov.redweave.menu.RandomizerMenu;
import io.github.marcelofabianov.redweave.menu.RandomizerScreenTexts;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import io.github.marcelofabianov.redweave.server.ModuleServices;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

/**
 * The Signal Randomizer as a part: _docs/funcionalidades/signal-randomizer.md. Its randomizer and
 * its output are all in its NBT (answer 481), and its running LED is the `running` of its
 * randomizer (answer 488). The rules stay in the domain; the draws come from the world (483).
 */
public final class RandomizerPart extends ModulePart {
  public static final String ID = "signal_randomizer";

  /** The looks of the top: the running LED and the lit arrow, as the timer (answer 488). */
  public static final List<String> LOOKS = ModuleLooks.leds();

  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String FITTED = "Fitted the Signal Randomizer at {} to the config: {}";
  private static final String RESTARTED = "A Signal Randomizer at {} had no valid interval";

  private Optional<SignalRandomizer> randomizer = Optional.empty();
  private Connection output = Connection.EDGE;

  private RandomizerPart(Direction face) {
    super(face);
  }

  /** A randomizer on the side of its space, from its NBT, or a new one from an empty tag. */
  public static RandomizerPart load(Direction face, CompoundTag tag) {
    RandomizerPart part = new RandomizerPart(face);
    part.loadPlate(tag);
    if (!tag.isEmpty()) {
      part.readRule(tag);
    }
    return part;
  }

  /** What the Configurator copies: its settings, never what it is doing now (answer 725). */
  @Override
  protected List<String> copiedKeys() {
    return List.of(RandomizerSave.MODE, RandomizerSave.INTERVAL, RandomizerSave.OUTPUT);
  }

  @Override
  protected void dropChannels(ChannelDrop drop) {
    output = drop.of(output);
  }

  /** Reads its rule from its save, as it loads and as the Configurator pastes (answer 725). */
  @Override
  protected void readRule(CompoundTag tag) {
    randomizer = RandomizerSave.readRandomizer(tag);
    output = ModuleSave.readConnection(tag, RandomizerSave.OUTPUT);
  }

  @Override
  protected String id() {
    return ID;
  }

  @Override
  public Item item() {
    return RedweaveItems.SIGNAL_RANDOMIZER.get();
  }

  @Override
  protected String lookSuffix() {
    return ModuleLooks.leds(randomizer.map(SignalRandomizer::running).orElse(false), powered());
  }

  /** A randomizer just placed: stopped, binary, every second, on its edge (answer 482). */
  @Override
  protected void placeRule() {
    randomizer = Optional.of(SignalRandomizer.placed(new NeoForgeRandomizerLimits()));
    output = Connection.EDGE;
  }

  /** It draws at each interval and holds the value until the next draw (answers 478 and 480). */
  @Override
  protected void tickRule(ModuleServices modules, PartSpaceBlockEntity space) {
    if (randomizer.isEmpty()) {
      return;
    }
    SignalRandomizer next = randomizer.get().tick(draws());
    setRandomizer(next);
    ModuleTicks.write(modules, space, this, next.output());
  }

  @Override
  protected void saveRule(CompoundTag tag) {
    randomizer.ifPresent(found -> RandomizerSave.writeRandomizer(tag, found));
    ModuleSave.writeConnection(tag, RandomizerSave.OUTPUT, output);
  }

  public Optional<SignalRandomizer> randomizer() {
    return randomizer;
  }

  @Override
  public Connection output() {
    return output;
  }

  /** Starts or stops it, and says so with its name, as the timer (answers 480 and 421). */
  @Override
  public Optional<Component> toggle() {
    randomizer = randomizer.map(found -> found.toggle(draws()));
    markDirty();
    if (randomizer.isEmpty() || module().isEmpty()) {
      return Optional.empty();
    }
    String key =
        randomizer.get().running()
            ? ModuleMessages.RANDOMIZER_RUNNING
            : ModuleMessages.RANDOMIZER_STOPPED;
    return Optional.of(Component.translatable(key, module().get().displayName()));
  }

  /** `Running` or `Stopped` (answer 484). */
  @Override
  public Optional<String> stateKey() {
    return randomizer.map(found -> RandomizerScreenTexts.of(found.state()));
  }

  @Override
  public void openScreen(ServerPlayer viewer) {
    PartSpot spot = spot();
    viewer.openMenu(
        new SimpleMenuProvider(
            (id, inventory, opener) -> new RandomizerMenu(id, viewer, spot),
            item().getDescription()),
        extra -> RandomizerMenu.writeOpening(extra, viewer, spot));
  }

  /** The draws of the world of this space, on the server (answer 483). */
  public Draws draws() {
    return new RandomSourceDraws(level().map(Level::getRandom).orElseGet(RandomSource::create));
  }

  /** Keeps the randomizer after a tick or a change, and marks the space to save if it changed. */
  public void setRandomizer(SignalRandomizer next) {
    if (!randomizer.equals(Optional.of(next))) {
      randomizer = Optional.of(next);
      markDirty();
    }
  }

  /** The owner moved the output to its edge or to a channel (answers 479 and 487). */
  public void connectOutput(Connection connection) {
    output = connection;
    changed();
  }

  /**
   * On the server, a loaded randomizer fits the limits of the config of now: an interval outside
   * them goes to the nearest limit and restarts, with a warning (answers 425 and 490, rule 14).
   */
  @Override
  public void loaded(PartSpaceBlockEntity space) {
    if (module().isEmpty()) {
      return;
    }
    RandomizerLimits limits = new NeoForgeRandomizerLimits();
    if (randomizer.isEmpty()) {
      LOGGER.warn(RESTARTED, space.getBlockPos());
      randomizer = Optional.of(SignalRandomizer.placed(limits));
      markDirty();
    } else if (!randomizer.get().fits(limits)) {
      LOGGER.warn(FITTED, space.getBlockPos(), randomizer.get().interval().ticks());
      randomizer = randomizer.map(found -> found.fittedTo(limits));
      markDirty();
    }
  }
}
