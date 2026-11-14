package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.block.WeatherDetectorBlockEntity;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ContainerData;

/**
 * The live values of the Weather Detector Screen, read from its block each time the game compares
 * them, so the icon follows the weather. The client never writes them (rule 16).
 */
final class DetectorScreenData implements ContainerData {
  private final ServerPlayer viewer;
  private final BlockPos pos;

  DetectorScreenData(ServerPlayer viewer, BlockPos pos) {
    this.viewer = viewer;
    this.pos = pos;
  }

  @Override
  public int get(int index) {
    Optional<WeatherDetectorBlockEntity> block = block();
    if (block.isEmpty()) {
      return ModuleScreenValues.NONE;
    }
    WeatherDetectorBlockEntity found = block.get();
    return switch (index) {
      case DetectorScreenLayout.VIEWER_IS_OWNER -> found.isOwner(viewer) ? 1 : 0;
      case DetectorScreenLayout.STATE -> found.detector().state(found.weather()).ordinal() + 1;
      case DetectorScreenLayout.NETWORK_STATE ->
          ModuleScreenValues.networkState(viewer, pos, found);
      case DetectorScreenLayout.MODE -> found.detector().mode().ordinal() + 1;
      case DetectorScreenLayout.OUTPUT -> ModuleScreenValues.code(found.output());
      case DetectorScreenLayout.WEATHER -> found.weather().ordinal() + 1;
      default -> ModuleScreenValues.NONE;
    };
  }

  private Optional<WeatherDetectorBlockEntity> block() {
    return viewer.level().getBlockEntity(pos) instanceof WeatherDetectorBlockEntity found
        ? Optional.of(found)
        : Optional.empty();
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
