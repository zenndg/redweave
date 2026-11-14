package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.block.SignalInverterBlockEntity;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ContainerData;

/**
 * The live values of the Signal Inverter Screen, read from its block each time the game compares
 * them, so the diagram follows every tick. The client never writes them (rule 16).
 */
final class InverterScreenData implements ContainerData {
  private final ServerPlayer viewer;
  private final BlockPos pos;

  InverterScreenData(ServerPlayer viewer, BlockPos pos) {
    this.viewer = viewer;
    this.pos = pos;
  }

  @Override
  public int get(int index) {
    Optional<SignalInverterBlockEntity> block = block();
    if (block.isEmpty()) {
      return ModuleScreenValues.NONE;
    }
    SignalInverterBlockEntity found = block.get();
    return switch (index) {
      case InverterScreenLayout.VIEWER_IS_OWNER -> found.isOwner(viewer) ? 1 : 0;
      case InverterScreenLayout.STATE -> found.inverter().state().ordinal() + 1;
      case InverterScreenLayout.NETWORK_STATE ->
          ModuleScreenValues.networkState(viewer, pos, found);
      case InverterScreenLayout.INPUT -> ModuleScreenValues.code(found.input());
      case InverterScreenLayout.OUTPUT -> ModuleScreenValues.code(found.output());
      case InverterScreenLayout.INPUT_ON -> found.inputOn() ? 1 : 0;
      default -> ModuleScreenValues.NONE;
    };
  }

  private Optional<SignalInverterBlockEntity> block() {
    return viewer.level().getBlockEntity(pos) instanceof SignalInverterBlockEntity found
        ? Optional.of(found)
        : Optional.empty();
  }

  @Override
  public void set(int index, int value) {
    // The server reads the values from the block; the client cannot change them.
  }

  @Override
  public int getCount() {
    return InverterScreenLayout.SIZE;
  }
}
