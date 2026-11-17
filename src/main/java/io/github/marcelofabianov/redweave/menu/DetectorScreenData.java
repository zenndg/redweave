package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.block.part.DetectorPart;
import io.github.marcelofabianov.redweave.block.part.ModulePart;
import io.github.marcelofabianov.redweave.block.part.PartSpot;
import java.util.Optional;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ContainerData;

/**
 * The live values of the Weather Detector Screen, read from its block each time the game compares
 * them, so the icon follows the weather. The client never writes them (rule 16).
 */
final class DetectorScreenData implements ContainerData {
  private final ServerPlayer viewer;
  private final PartSpot spot;

  DetectorScreenData(ServerPlayer viewer, PartSpot spot) {
    this.viewer = viewer;
    this.spot = spot;
  }

  @Override
  public int get(int index) {
    Optional<DetectorPart> block = block();
    if (block.isEmpty()) {
      return ModuleScreenValues.NONE;
    }
    DetectorPart found = block.get();
    if (index == DetectorScreenLayout.DELAY_SECONDS) {
      return found.delayed().delay().seconds();
    }
    return switch (index) {
      case DetectorScreenLayout.VIEWER_IS_OWNER -> found.isOwner(viewer) ? 1 : 0;
      case DetectorScreenLayout.STATE -> found.detector().state(found.weather()).ordinal() + 1;
      case DetectorScreenLayout.NETWORK_STATE ->
          ModuleScreenValues.networkState(viewer, spot, found);
      case DetectorScreenLayout.MODE -> found.detector().mode().ordinal() + 1;
      case DetectorScreenLayout.OUTPUT -> ModuleScreenValues.code(found.output());
      case DetectorScreenLayout.WEATHER -> found.weather().ordinal() + 1;
      default -> ModuleScreenValues.NONE;
    };
  }

  private Optional<DetectorPart> block() {
    return ModulePart.at(viewer.level(), spot, DetectorPart.class);
  }

  @Override
  public void set(int index, int value) {
    // The server reads the values from the block; the client cannot change them.
  }

  @Override
  public int getCount() {
    return DetectorScreenLayout.SIZE;
  }
}
