package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.application.ChangeTimer;
import io.github.marcelofabianov.redweave.application.NetworkChoice;
import io.github.marcelofabianov.redweave.block.SignalTimerBlockEntity;
import io.github.marcelofabianov.redweave.config.NeoForgeTimerLimits;
import io.github.marcelofabianov.redweave.domain.module.Connection;
import io.github.marcelofabianov.redweave.domain.module.SignalModule;
import io.github.marcelofabianov.redweave.domain.module.SignalTimer;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * What the buttons of the Signal Timer Screen do on the server. Every action goes through the owner
 * of the plate: anyone else changes nothing (answers 378, 398 and 404, rules 16 and 17).
 */
final class TimerScreenActions {
  private TimerScreenActions() {}

  static void apply(Player player, BlockPos pos, TimerAction action) {
    Optional<SignalTimerBlockEntity> block = blockAt(player, pos);
    Optional<RedweaveServer> server = RedweaveServer.running();
    if (block.isEmpty() || server.isEmpty() || block.get().module().isEmpty()) {
      return;
    }
    PlayerId requester = new PlayerId(player.getUUID());
    switch (action.kind()) {
      case TOGGLE, INTERVAL, PULSE ->
          change(server.get().modules().changeTimer(), requester, block.get(), action);
      case OUTPUT -> connect(requester, block.get(), action, block.get()::connectOutput);
      case PAUSE -> connect(requester, block.get(), action, block.get()::connectPause);
      case CHOOSE -> {
        // A choice names a row of the list the menu keeps, so the menu applies it.
      }
    }
  }

  /** The owner picks one of his online networks for the module (answers 397 and 400). */
  static void choose(Player player, BlockPos pos, NetworkId network) {
    Optional<SignalTimerBlockEntity> block = blockAt(player, pos);
    Optional<RedweaveServer> server = RedweaveServer.running();
    if (block.isEmpty() || server.isEmpty() || block.get().module().isEmpty()) {
      return;
    }
    PlayerId requester = new PlayerId(player.getUUID());
    SignalModule module = block.get().module().get();
    if (server.get().modules().chooseNetwork().choose(requester, module, network)
        == Outcome.ACCEPTED) {
      block.get().changed();
    }
  }

  /** The networks the owner may pick, or none for anyone else (answers 378 and 397). */
  static List<NetworkChoice> choices(ServerPlayer player, BlockPos pos) {
    PlayerId viewer = new PlayerId(player.getUUID());
    boolean owner =
        blockAt(player, pos)
            .flatMap(SignalTimerBlockEntity::module)
            .map(module -> module.isOwner(viewer))
            .orElse(false);
    if (!owner) {
      return List.of();
    }
    return RedweaveServer.running()
        .map(server -> server.networkChoices().of(viewer))
        .orElse(List.of());
  }

  private static void change(
      ChangeTimer change, PlayerId requester, SignalTimerBlockEntity block, TimerAction action) {
    SignalModule module = block.module().get();
    Optional<SignalTimer> timer = block.timer();
    if (timer.isEmpty()) {
      return;
    }
    SignalTimer now = timer.get();
    Optional<SignalTimer> next =
        switch (action.kind()) {
          case INTERVAL ->
              change.interval(
                  requester,
                  module,
                  now,
                  now.cycle().intervalTicks() + action.ticks(),
                  new NeoForgeTimerLimits());
          case PULSE ->
              change.pulse(requester, module, now, now.cycle().pulseTicks() + action.ticks());
          default -> change.toggle(requester, module, now);
        };
    next.ifPresent(block::setTimer);
  }

  /** A channel needs the network of the module; the edge always works (answers 375 and 414). */
  private static void connect(
      PlayerId requester,
      SignalTimerBlockEntity block,
      TimerAction action,
      Consumer<Connection> keep) {
    Connection connection =
        action
            .channel()
            .map(index -> Connection.on(ChannelColor.values()[index]))
            .orElse(Connection.EDGE);
    if (block.module().get().connects(requester, connection) == Outcome.ACCEPTED) {
      keep.accept(connection);
    }
  }

  private static Optional<SignalTimerBlockEntity> blockAt(Player player, BlockPos pos) {
    return player.level().getBlockEntity(pos) instanceof SignalTimerBlockEntity found
        ? Optional.of(found)
        : Optional.empty();
  }
}
