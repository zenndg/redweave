package io.github.marcelofabianov.redweave.block.part;

import io.github.marcelofabianov.redweave.block.ModuleMessages;
import io.github.marcelofabianov.redweave.block.PartSpaceBlockEntity;
import io.github.marcelofabianov.redweave.domain.module.Connection;
import io.github.marcelofabianov.redweave.domain.module.Weather;
import io.github.marcelofabianov.redweave.domain.module.WeatherDetector;
import io.github.marcelofabianov.redweave.domain.module.WeatherMode;
import io.github.marcelofabianov.redweave.menu.DetectorMenu;
import io.github.marcelofabianov.redweave.menu.DetectorScreenTexts;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import io.github.marcelofabianov.redweave.server.ModuleServices;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

/**
 * The Weather Detector as a part: _docs/funcionalidades/weather-detector.md. Its mode and its
 * output are in its NBT (answer 467), and the weather of the last tick is never saved, for its
 * state (answer 468). The symbol of its top is its `mode` (answer 471). The rule stays in the
 * domain.
 */
public final class DetectorPart extends ModulePart {
  public static final String ID = "weather_detector";

  /** The looks of the top: the symbol of each mode, with the arrow lit or not (answer 471). */
  public static final List<String> LOOKS = looks();

  private WeatherDetector detector = WeatherDetector.placed();
  private Connection output = Connection.EDGE;

  /** The weather read on the last tick; not saved, as the next tick reads it again (468). */
  private Weather weather = Weather.CLEAR;

  private DetectorPart(Direction face) {
    super(face);
  }

  /** A detector on the side of its space, from its NBT, or a new one from an empty tag. */
  public static DetectorPart load(Direction face, CompoundTag tag) {
    DetectorPart part = new DetectorPart(face);
    part.loadPlate(tag);
    if (!tag.isEmpty()) {
      part.detector = new WeatherDetector(DetectorSave.readMode(tag));
      part.output = ModuleSave.readConnection(tag, DetectorSave.OUTPUT);
    }
    return part;
  }

  private static List<String> looks() {
    List<String> looks = new ArrayList<>();
    for (WeatherMode mode : WeatherMode.values()) {
      looks.add(lookOf(mode, false));
      looks.add(lookOf(mode, true));
    }
    return List.copyOf(looks);
  }

  private static String lookOf(WeatherMode mode, boolean powered) {
    return ModuleLooks.SEPARATOR + mode.key() + (powered ? ModuleLooks.ON : ModuleLooks.NONE);
  }

  @Override
  protected String id() {
    return ID;
  }

  @Override
  public Item item() {
    return RedweaveItems.WEATHER_DETECTOR.get();
  }

  @Override
  protected String lookSuffix() {
    return lookOf(detector.mode(), powered());
  }

  /** A new detector, watching the rain, on its front edge (answer 463). */
  @Override
  protected void placeRule() {
    detector = WeatherDetector.placed();
    output = Connection.EDGE;
  }

  /** It reads the weather of its dimension and gives 15 while it matches its mode (459). */
  @Override
  protected void tickRule(ModuleServices modules, PartSpaceBlockEntity space) {
    weather = weatherOf(space.getLevel());
    ModuleTicks.write(modules, space, this, detector.output(weather));
  }

  private static Weather weatherOf(Level level) {
    return Weather.of(level.dimensionType().hasSkyLight(), level.isRaining(), level.isThundering());
  }

  @Override
  protected void saveRule(CompoundTag tag) {
    DetectorSave.writeMode(tag, detector.mode());
    ModuleSave.writeConnection(tag, DetectorSave.OUTPUT, output);
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
    PartSpot spot = spot();
    viewer.openMenu(
        new SimpleMenuProvider(
            (id, inventory, opener) -> new DetectorMenu(id, viewer, spot), item().getDescription()),
        extra -> DetectorMenu.writeOpening(extra, viewer, spot));
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
}
