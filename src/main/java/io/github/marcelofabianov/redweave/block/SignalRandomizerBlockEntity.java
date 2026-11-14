package io.github.marcelofabianov.redweave.block;

import com.mojang.logging.LogUtils;
import io.github.marcelofabianov.redweave.config.NeoForgeRandomizerLimits;
import io.github.marcelofabianov.redweave.domain.module.Connection;
import io.github.marcelofabianov.redweave.domain.module.Draws;
import io.github.marcelofabianov.redweave.domain.module.RandomizerLimits;
import io.github.marcelofabianov.redweave.domain.module.SignalModule;
import io.github.marcelofabianov.redweave.domain.module.SignalRandomizer;
import io.github.marcelofabianov.redweave.menu.RandomizerMenu;
import io.github.marcelofabianov.redweave.menu.RandomizerScreenTexts;
import io.github.marcelofabianov.redweave.registry.RedweaveBlockEntities;
import io.github.marcelofabianov.redweave.registry.RedweaveBlocks;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

/**
 * What a Signal Randomizer block keeps besides its module: its randomizer and its output, all in
 * its NBT (answer 481). The rules stay in the domain; the draws come from the world (answer 483).
 */
public final class SignalRandomizerBlockEntity extends ModuleBlockEntity {
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String FITTED = "Fitted the Signal Randomizer at {} to the config: {}";
  private static final String RESTARTED = "A Signal Randomizer at {} had no valid interval";

  private Optional<SignalRandomizer> randomizer = Optional.empty();
  private Connection output = Connection.EDGE;

  public SignalRandomizerBlockEntity(BlockPos pos, BlockState state) {
    super(RedweaveBlockEntities.SIGNAL_RANDOMIZER.get(), pos, state);
  }

  /** A randomizer just placed: stopped, binary, every second, on its edge (answer 482). */
  @Override
  void place(SignalModule placed) {
    randomizer = Optional.of(SignalRandomizer.placed(new NeoForgeRandomizerLimits()));
    output = Connection.EDGE;
    setModule(placed);
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
    setChanged();
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
    viewer.openMenu(
        new SimpleMenuProvider(
            (id, inventory, opener) -> new RandomizerMenu(id, viewer, worldPosition),
            RedweaveBlocks.SIGNAL_RANDOMIZER.get().getName()),
        extra -> RandomizerMenu.writeOpening(extra, viewer, worldPosition));
  }

  /** The draws of the world of this block, on the server (answer 483). */
  public Draws draws() {
    return new RandomSourceDraws(level == null ? RandomSource.create() : level.getRandom());
  }

  /** Keeps the randomizer after a tick or a change, and marks the block to save if it changed. */
  public void setRandomizer(SignalRandomizer next) {
    if (!randomizer.equals(Optional.of(next))) {
      randomizer = Optional.of(next);
      setChanged();
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
  public void onLoad() {
    super.onLoad();
    if (level == null || level.isClientSide || module().isEmpty()) {
      return;
    }
    RandomizerLimits limits = new NeoForgeRandomizerLimits();
    if (randomizer.isEmpty()) {
      LOGGER.warn(RESTARTED, worldPosition);
      randomizer = Optional.of(SignalRandomizer.placed(limits));
      setChanged();
    } else if (!randomizer.get().fits(limits)) {
      LOGGER.warn(FITTED, worldPosition, randomizer.get().interval().ticks());
      randomizer = randomizer.map(found -> found.fittedTo(limits));
      setChanged();
    }
  }

  @Override
  protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.saveAdditional(tag, registries);
    randomizer.ifPresent(found -> RandomizerSave.writeRandomizer(tag, found));
    ModuleSave.writeConnection(tag, RandomizerSave.OUTPUT, output);
  }

  @Override
  protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.loadAdditional(tag, registries);
    randomizer = RandomizerSave.readRandomizer(tag);
    output = ModuleSave.readConnection(tag, RandomizerSave.OUTPUT);
  }
}
