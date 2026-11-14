package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.block.BlockLocations;
import io.github.marcelofabianov.redweave.block.SignalTimerBlockEntity;
import io.github.marcelofabianov.redweave.domain.module.Connection;
import io.github.marcelofabianov.redweave.domain.module.SignalModule;
import io.github.marcelofabianov.redweave.domain.module.SignalTimer;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ContainerData;

/**
 * The live values of the Signal Timer Screen, read from its block each time the game compares them,
 * so the clock moves every tick. The client never writes them (rule 16).
 */
final class TimerScreenData implements ContainerData {
  private final ServerPlayer viewer;
  private final BlockPos pos;

  TimerScreenData(ServerPlayer viewer, BlockPos pos) {
    this.viewer = viewer;
    this.pos = pos;
  }

  @Override
  public int get(int index) {
    Optional<SignalTimerBlockEntity> block = block();
    if (block.isEmpty()) {
      return TimerScreenLayout.NONE;
    }
    return switch (index) {
      case TimerScreenLayout.VIEWER_IS_OWNER -> block.get().isOwner(viewer) ? 1 : 0;
      case TimerScreenLayout.NETWORK_STATE -> networkState(block.get());
      case TimerScreenLayout.OUTPUT -> code(block.get().output());
      case TimerScreenLayout.PAUSE -> code(block.get().pause());
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

  /** The state of the network seen from the plate, as a node, or none (answer 385). */
  private int networkState(SignalTimerBlockEntity block) {
    Optional<SignalModule> module = block.module().filter(found -> found.network().isPresent());
    Optional<RedweaveServer> server = RedweaveServer.running();
    if (module.isEmpty() || server.isEmpty()) {
      return TimerScreenLayout.NONE;
    }
    return server
            .get()
            .modules()
            .show()
            .show(module.get(), BlockLocations.of(viewer.level(), pos))
            .state()
            .ordinal()
        + 1;
  }

  static int code(Connection connection) {
    return connection.channel().map(color -> color.ordinal() + 1).orElse(TimerAction.EDGE);
  }

  private Optional<SignalTimerBlockEntity> block() {
    return viewer.level().getBlockEntity(pos) instanceof SignalTimerBlockEntity found
        ? Optional.of(found)
        : Optional.empty();
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
