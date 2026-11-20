package io.github.zenndg.redweave.menu;

import io.github.zenndg.redweave.block.part.ModulePart;
import io.github.zenndg.redweave.block.part.PartSpot;
import io.github.zenndg.redweave.block.part.TimerPart;
import io.github.zenndg.redweave.domain.module.SignalTimer;
import java.util.Optional;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ContainerData;

/**
 * The live values of the Signal Timer Screen, read from its block each time the game compares them,
 * so the clock moves every tick. The client never writes them (rule 16).
 */
final class TimerScreenData implements ContainerData {
  private final ServerPlayer viewer;
  private final PartSpot spot;

  TimerScreenData(ServerPlayer viewer, PartSpot spot) {
    this.viewer = viewer;
    this.spot = spot;
  }

  @Override
  public int get(int index) {
    Optional<TimerPart> block = block();
    if (block.isEmpty()) {
      return ModuleScreenValues.NONE;
    }
    return switch (index) {
      case TimerScreenLayout.VIEWER_IS_OWNER -> block.get().isOwner(viewer) ? 1 : 0;
      case TimerScreenLayout.NETWORK_STATE ->
          ModuleScreenValues.networkState(viewer, spot, block.get());
      case TimerScreenLayout.OUTPUT -> ModuleScreenValues.code(block.get().output());
      case TimerScreenLayout.PAUSE -> ModuleScreenValues.code(block.get().pause());
      default -> block.get().timer().map(timer -> ofTimer(timer, index)).orElse(0);
    };
  }

  private static int ofTimer(SignalTimer timer, int index) {
    if (index == TimerScreenLayout.STATE) {
      return timer.state().ordinal() + 1;
    }
    if (index < TimerScreenLayout.PULSE) {
      return TimerScreenLayout.half(
          timer.cycle().intervalTicks(), index, TimerScreenLayout.INTERVAL);
    }
    if (index < TimerScreenLayout.ELAPSED) {
      return TimerScreenLayout.half(timer.cycle().pulseTicks(), index, TimerScreenLayout.PULSE);
    }
    return TimerScreenLayout.half(timer.elapsedTicks(), index, TimerScreenLayout.ELAPSED);
  }

  private Optional<TimerPart> block() {
    return ModulePart.at(viewer.level(), spot, TimerPart.class);
  }

  @Override
  public void set(int index, int value) {
    // The server reads the values from the block; the client cannot change them.
  }

  @Override
  public int getCount() {
    return TimerScreenLayout.SIZE;
  }
}
