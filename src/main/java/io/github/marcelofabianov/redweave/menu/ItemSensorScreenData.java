package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.block.part.ItemSensorPart;
import io.github.marcelofabianov.redweave.block.part.ModulePart;
import io.github.marcelofabianov.redweave.block.part.PartSpot;
import java.util.Optional;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ContainerData;

/**
 * The live values of the Signal Item Sensor Screen, read from its part each time the game compares
 * them, so the count follows the container while the screen is open (answer 696). The client never
 * writes them (rule 16).
 */
final class ItemSensorScreenData implements ContainerData {
  private final ServerPlayer viewer;
  private final PartSpot spot;

  ItemSensorScreenData(ServerPlayer viewer, PartSpot spot) {
    this.viewer = viewer;
    this.spot = spot;
  }

  @Override
  public int get(int index) {
    Optional<ItemSensorPart> part = ModulePart.at(viewer.level(), spot, ItemSensorPart.class);
    if (part.isEmpty()) {
      return ModuleScreenValues.NONE;
    }
    ItemSensorPart found = part.get();
    return switch (index) {
      case ItemSensorScreenLayout.VIEWER_IS_OWNER -> found.isOwner(viewer) ? 1 : 0;
      case ItemSensorScreenLayout.ON ->
          found.sensor().on(found.counted())
              ? ItemSensorScreenLayout.ON_CODE
              : ItemSensorScreenLayout.OFF_CODE;
      case ItemSensorScreenLayout.NETWORK_STATE ->
          ModuleScreenValues.networkState(viewer, spot, found);
      case ItemSensorScreenLayout.OUTPUT -> ModuleScreenValues.code(found.output());
      case ItemSensorScreenLayout.STRENGTH -> found.sensor().strength().strength();
      default -> halfOf(found, index);
    };
  }

  /** A half of the item, the count or what was counted. */
  private static int halfOf(ItemSensorPart part, int index) {
    int first = ItemSensorScreenLayout.firstOf(index);
    return TimerScreenLayout.half(wholeOf(part, first), index, first);
  }

  private static int wholeOf(ItemSensorPart part, int first) {
    return switch (first) {
      case ItemSensorScreenLayout.ITEM ->
          part.expected().map(item -> BuiltInRegistries.ITEM.getId(item) + 1).orElse(0);
      case ItemSensorScreenLayout.COUNT -> part.sensor().count();
      default -> part.counted();
    };
  }

  @Override
  public void set(int index, int value) {
    // The server reads the values from the part; the client cannot change them.
  }

  @Override
  public int getCount() {
    return ItemSensorScreenLayout.SIZE;
  }
}
