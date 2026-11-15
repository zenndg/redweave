package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.block.part.LeverPart;
import io.github.marcelofabianov.redweave.block.part.ModulePart;
import io.github.marcelofabianov.redweave.block.part.PartSpot;
import java.util.Optional;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ContainerData;

/**
 * The live values of the Signal Lever Screen, read from its part each time the game compares them,
 * so the state follows a click of the hand while the screen is open. The client never writes them
 * (rule 16).
 */
final class LeverScreenData implements ContainerData {
  private final ServerPlayer viewer;
  private final PartSpot spot;

  LeverScreenData(ServerPlayer viewer, PartSpot spot) {
    this.viewer = viewer;
    this.spot = spot;
  }

  @Override
  public int get(int index) {
    Optional<LeverPart> part = ModulePart.at(viewer.level(), spot, LeverPart.class);
    if (part.isEmpty()) {
      return ModuleScreenValues.NONE;
    }
    LeverPart found = part.get();
    return switch (index) {
      case LeverScreenLayout.VIEWER_IS_OWNER -> found.isOwner(viewer) ? 1 : 0;
      case LeverScreenLayout.ON ->
          found.lever().on() ? LeverScreenLayout.ON_CODE : LeverScreenLayout.OFF_CODE;
      case LeverScreenLayout.NETWORK_STATE -> ModuleScreenValues.networkState(viewer, spot, found);
      case LeverScreenLayout.OUTPUT -> ModuleScreenValues.code(found.output());
      default -> found.lever().strength().strength();
    };
  }

  @Override
  public void set(int index, int value) {
    // The server reads the values from the part; the client cannot change them.
  }

  @Override
  public int getCount() {
    return LeverScreenLayout.SIZE;
  }
}
