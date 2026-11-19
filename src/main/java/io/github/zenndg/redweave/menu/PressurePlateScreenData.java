package io.github.zenndg.redweave.menu;

import io.github.zenndg.redweave.block.part.ModulePart;
import io.github.zenndg.redweave.block.part.PartSpot;
import io.github.zenndg.redweave.block.part.PressurePlatePart;
import java.util.Optional;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ContainerData;

/**
 * The live values of the Signal Pressure Plate Screen, read from its part each time the game
 * compares them, so the state follows what steps on it while the screen is open. The client never
 * writes them (rule 16).
 */
final class PressurePlateScreenData implements ContainerData {
  private final ServerPlayer viewer;
  private final PartSpot spot;

  PressurePlateScreenData(ServerPlayer viewer, PartSpot spot) {
    this.viewer = viewer;
    this.spot = spot;
  }

  @Override
  public int get(int index) {
    Optional<PressurePlatePart> part = ModulePart.at(viewer.level(), spot, PressurePlatePart.class);
    if (part.isEmpty()) {
      return ModuleScreenValues.NONE;
    }
    PressurePlatePart found = part.get();
    if (index == PressurePlateScreenLayout.DELAY_SECONDS) {
      return found.delayed().delay().seconds();
    }
    return switch (index) {
      case PressurePlateScreenLayout.VIEWER_IS_OWNER -> found.isOwner(viewer) ? 1 : 0;
      case PressurePlateScreenLayout.ON ->
          found.plate().on()
              ? PressurePlateScreenLayout.ON_CODE
              : PressurePlateScreenLayout.OFF_CODE;
      case PressurePlateScreenLayout.NETWORK_STATE ->
          ModuleScreenValues.networkState(viewer, spot, found);
      case PressurePlateScreenLayout.OUTPUT -> ModuleScreenValues.code(found.output());
      case PressurePlateScreenLayout.MODE -> found.plate().mode().ordinal() + 1;
      default -> found.plate().strength().strength();
    };
  }

  @Override
  public void set(int index, int value) {
    // The server reads the values from the part; the client cannot change them.
  }

  @Override
  public int getCount() {
    return PressurePlateScreenLayout.SIZE;
  }
}
