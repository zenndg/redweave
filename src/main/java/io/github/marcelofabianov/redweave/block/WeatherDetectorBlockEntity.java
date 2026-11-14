package io.github.marcelofabianov.redweave.block;

import io.github.marcelofabianov.redweave.domain.module.Connection;
import io.github.marcelofabianov.redweave.domain.module.SignalModule;
import io.github.marcelofabianov.redweave.domain.module.Weather;
import io.github.marcelofabianov.redweave.domain.module.WeatherDetector;
import io.github.marcelofabianov.redweave.menu.DetectorMenu;
import io.github.marcelofabianov.redweave.menu.DetectorScreenTexts;
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

/**
 * What a Weather Detector block keeps besides its module: its mode and its output, in its NBT
 * (answer 467), and the weather of the last tick, never saved, for its state. The rule stays in the
 * domain.
 */
public final class WeatherDetectorBlockEntity extends ModuleBlockEntity {
  private WeatherDetector detector = WeatherDetector.placed();
  private Connection output = Connection.EDGE;

  /** The weather read on the last tick; not saved, as the next tick reads it again (468). */
  private Weather weather = Weather.CLEAR;

  public WeatherDetectorBlockEntity(BlockPos pos, BlockState state) {
    super(RedweaveBlockEntities.WEATHER_DETECTOR.get(), pos, state);
  }

  /** A new detector, watching the rain, on its front edge (answer 463). */
  @Override
  void place(SignalModule placed) {
    detector = WeatherDetector.placed();
    output = Connection.EDGE;
    setModule(placed);
  }

  public WeatherDetector detector() {
    return detector;
  }

  public Weather weather() {
    return weather;
  }

  @Override
  public Connection output() {
    return output;
  }

  /** The next mode, said with the name of the module, as `<name>: Thunder` (answer 462). */
  @Override
  public Optional<Component> toggle() {
    detector = detector.toggled();
    changed();
    return module()
        .map(found ->
            Component.translatable(
                ModuleMessages.DETECTOR_MODE,
                found.displayName(),
                Component.translatable(DetectorScreenTexts.of(detector.mode()))));
  }

  /** `On`, `Off` or `No weather` (answer 465). */
  @Override
  public Optional<String> stateKey() {
    return Optional.of(DetectorScreenTexts.of(detector.state(weather)));
  }

  @Override
  public void openScreen(ServerPlayer viewer) {
    viewer.openMenu(
        new SimpleMenuProvider(
            (id, inventory, opener) -> new DetectorMenu(id, viewer, worldPosition),
            RedweaveBlocks.WEATHER_DETECTOR.get().getName()),
        extra -> DetectorMenu.writeOpening(extra, viewer, worldPosition));
  }

  /** The weather of this tick, for the state and the screen. */
  void read(Weather now) {
    weather = now;
  }

  /** The owner picked a mode on the screen (answer 470). */
  public void changeTo(WeatherDetector next) {
    detector = next;
    changed();
  }

  /** The owner moved the output to its edge or to a channel (answers 461 and 470). */
  public void connectOutput(Connection connection) {
    output = connection;
    changed();
  }

  @Override
  protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.saveAdditional(tag, registries);
    DetectorSave.writeMode(tag, detector.mode());
    ModuleSave.writeConnection(tag, DetectorSave.OUTPUT, output);
  }

  @Override
  protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.loadAdditional(tag, registries);
    detector = new WeatherDetector(DetectorSave.readMode(tag));
    output = ModuleSave.readConnection(tag, DetectorSave.OUTPUT);
  }
}
