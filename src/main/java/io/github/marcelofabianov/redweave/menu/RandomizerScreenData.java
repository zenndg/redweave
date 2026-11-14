package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.block.SignalRandomizerBlockEntity;
import io.github.marcelofabianov.redweave.domain.module.SignalRandomizer;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ContainerData;

/**
 * The live values of the Signal Randomizer Screen, read from its block each time the game compares
 * them, so the big number follows every draw. The client never writes them (rule 16).
 */
final class RandomizerScreenData implements ContainerData {
  private final ServerPlayer viewer;
  private final BlockPos pos;

  RandomizerScreenData(ServerPlayer viewer, BlockPos pos) {
    this.viewer = viewer;
    this.pos = pos;
  }

  @Override
  public int get(int index) {
    Optional<SignalRandomizerBlockEntity> block = block();
    if (block.isEmpty()) {
      return ModuleScreenValues.NONE;
    }
    SignalRandomizerBlockEntity found = block.get();
    return switch (index) {
      case RandomizerScreenLayout.VIEWER_IS_OWNER -> found.isOwner(viewer) ? 1 : 0;
      case RandomizerScreenLayout.NETWORK_STATE ->
          ModuleScreenValues.networkState(viewer, pos, found);
      case RandomizerScreenLayout.OUTPUT -> ModuleScreenValues.code(found.output());
      default -> found.randomizer().map(randomizer -> of(randomizer, index)).orElse(0);
    };
  }

  private static int of(SignalRandomizer randomizer, int index) {
    return switch (index) {
      case RandomizerScreenLayout.STATE -> randomizer.state().ordinal() + 1;
      case RandomizerScreenLayout.MODE -> randomizer.mode().ordinal() + 1;
      case RandomizerScreenLayout.VALUE -> randomizer.output().strength();
      default ->
          TimerScreenLayout.half(
              randomizer.interval().ticks(), index, RandomizerScreenLayout.INTERVAL);
    };
  }

  private Optional<SignalRandomizerBlockEntity> block() {
    return viewer.level().getBlockEntity(pos) instanceof SignalRandomizerBlockEntity found
        ? Optional.of(found)
        : Optional.empty();
  }

  @Override
  public void set(int index, int value) {
    // The server reads the values from the block; the client cannot change them.
  }

  @Override
  public int getCount() {
    return RandomizerScreenLayout.SIZE;
  }
}
