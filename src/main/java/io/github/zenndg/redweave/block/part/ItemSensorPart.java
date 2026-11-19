package io.github.zenndg.redweave.block.part;

import io.github.zenndg.redweave.block.ModuleMessages;
import io.github.zenndg.redweave.block.PartSpaceBlockEntity;
import io.github.zenndg.redweave.config.NeoForgeItemSensorLimits;
import io.github.zenndg.redweave.domain.module.Connection;
import io.github.zenndg.redweave.domain.module.ItemSensor;
import io.github.zenndg.redweave.domain.signal.DelayedOutput;
import io.github.zenndg.redweave.domain.signal.OutputDelay;
import io.github.zenndg.redweave.menu.ButtonScreenTexts;
import io.github.zenndg.redweave.menu.ItemSensorMenu;
import io.github.zenndg.redweave.registry.RedweaveItems;
import io.github.zenndg.redweave.server.ModuleServices;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * The Signal Item Sensor as a part: _docs/funcionalidades/sensor-de-itens.md. A plate on a
 * container that reads it every `item_sensor.check_ticks` and gives its strength on its front edge
 * or a channel while the container holds exactly the expected count of the expected item (answers
 * 690, 692, 695 and 696). It only reads (rule 2). It is not a Signal Emitter: it has no trigger and
 * feeds only its front edge, not its block of support, which is the container (answers 632 and
 * 696). It shares their light, red off and green on (answer 697).
 */
public final class ItemSensorPart extends ModulePart implements DelayedPart {
  public static final String ID = "signal_item_sensor";

  /** The looks of the top: off, with the red light, and on, with the green one (697). */
  public static final List<String> LOOKS = List.of(ModuleLooks.NONE, ModuleLooks.ON);

  private final OutputDelaySlot delay = new OutputDelaySlot();
  private static final double RING_EDGE = 6;
  private static final double RING_FAR_EDGE = 12;
  private static final double RING_HEIGHT = 3;
  private static final RaisedShapes RING = new RaisedShapes(RING_EDGE, RING_FAR_EDGE, RING_HEIGHT);

  private ItemSensor sensor = ItemSensor.placed();
  private Connection output = Connection.EDGE;

  /** What the last read found; not saved, as a loaded sensor reads again at once. */
  private int counted;

  /** The ticks until the next read; not saved, so a loaded sensor reads at once (695). */
  private int countdown;

  private ItemSensorPart(Direction face) {
    super(face);
  }

  /** A sensor on the side of its space, from its NBT, or a new one from an empty tag. */
  public static ItemSensorPart load(Direction face, CompoundTag tag) {
    ItemSensorPart part = new ItemSensorPart(face);
    part.loadPlate(tag);
    if (!tag.isEmpty()) {
      part.readRule(tag);
    }
    return part;
  }

  /** What the Configurator copies: its settings, never what it is doing now (answer 725). */
  @Override
  protected List<String> copiedKeys() {
    return List.of(
        ItemSensorSave.ITEM,
        ItemSensorSave.COUNT,
        StrengthSave.STRENGTH,
        ItemSensorSave.OUTPUT,
        OutputDelaySlot.COPIED);
  }

  @Override
  protected void dropChannels(ConnectionMapper drop) {
    output = drop.of(output);
  }

  /** Reads its rule from its save, as it loads and as the Configurator pastes (answer 725). */
  @Override
  protected void readRule(CompoundTag tag) {
    sensor = ItemSensorSave.read(tag);
    output = ModuleSave.readConnection(tag, ItemSensorSave.OUTPUT);
    delay.load(tag, ID, sensor.strength());
  }

  @Override
  protected String id() {
    return ID;
  }

  @Override
  public Item item() {
    return RedweaveItems.SIGNAL_ITEM_SENSOR.get();
  }

  /** The plate and the ring of its window, so the ring can be aimed at too. */
  @Override
  public VoxelShape shape() {
    return Shapes.or(super.shape(), RING.of(face()));
  }

  /**
   * The light follows the sensor, never what reaches its channel (answer 697). It reads `powered`,
   * which the client receives, and not the count, which only the server knows.
   */
  @Override
  protected String lookSuffix() {
    return powered() ? ModuleLooks.ON : ModuleLooks.NONE;
  }

  /** A sensor just placed: no item yet, 1, at 15, on its front edge (answer 696). */
  @Override
  protected void placeRule() {
    sensor = ItemSensor.placed();
    output = Connection.EDGE;
    delay.place();
  }

  /**
   * Every `item_sensor.check_ticks` it reads its container, and each tick it gives what the last
   * read found, so the network is only weighed again when that changes (answers 695 and 696, rule
   * 18).
   */
  @Override
  protected void tickRule(ModuleServices modules, PartSpaceBlockEntity space) {
    if (--countdown <= 0) {
      countdown = NeoForgeItemSensorLimits.checkTicks();
      counted =
          expected()
              .map(found ->
                  ItemSensorReading.count(space.getLevel(), space.getBlockPos(), face(), found))
              .orElse(0);
    }
    if (delay.tick(sensor.output(counted), sensor.strength())) {
      markDirty();
    }
    ModuleTicks.write(modules, space, this, delay.output());
  }

  @Override
  protected void saveRule(CompoundTag tag) {
    ItemSensorSave.write(tag, sensor);
    ModuleSave.writeConnection(tag, ItemSensorSave.OUTPUT, output);
    delay.save(tag);
  }

  @Override
  public DelayedOutput delayed() {
    return delay.delayed();
  }

  @Override
  public void setDelay(OutputDelay next) {
    delay.set(next);
    changed();
  }

  public ItemSensor sensor() {
    return sensor;
  }

  /** How many of the expected item the last read found in the container. */
  public int counted() {
    return counted;
  }

  /** The item the sensor expects, if one is picked and still known to the game. */
  public Optional<Item> expected() {
    return sensor
        .item()
        .map(ResourceLocation::tryParse)
        .filter(BuiltInRegistries.ITEM::containsKey)
        .map(BuiltInRegistries.ITEM::get);
  }

  @Override
  public Connection output() {
    return output;
  }

  /** The sensor has nothing to toggle (answer 697). */
  @Override
  public Optional<Component> toggle() {
    return Optional.of(Component.translatable(ModuleMessages.ITEM_SENSOR_NOTHING_TO_TOGGLE));
  }

  /** `On` or `Off`, masculine in Portuguese as "sensor" (answer 697). */
  @Override
  public Optional<String> stateKey() {
    return Optional.of(ButtonScreenTexts.of(sensor.on(counted)));
  }

  @Override
  public void openScreen(ServerPlayer viewer) {
    PartSpot spot = spot();
    viewer.openMenu(
        new SimpleMenuProvider(
            (id, inventory, opener) -> new ItemSensorMenu(id, inventory, viewer, spot),
            item().getDescription()),
        extra -> ItemSensorMenu.writeOpening(extra, viewer, spot));
  }

  /** Keeps the sensor after a change from the screen, and reads its container at once. */
  public void setSensor(ItemSensor next) {
    sensor = next;
    countdown = 0;
    changed();
  }

  /** The owner moved the output to its edge or to a channel (answer 696). */
  public void connectOutput(Connection connection) {
    output = connection;
    changed();
  }
}
