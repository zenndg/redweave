package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.application.NetworkChoice;
import io.github.marcelofabianov.redweave.block.ModuleBlockEntity;
import io.github.marcelofabianov.redweave.domain.module.Connection;
import io.github.marcelofabianov.redweave.domain.module.SignalModule;
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
 * What the buttons every Signal Module Screen has do on the server: the network choice and the
 * connections. Every action goes through the owner of the plate: anyone else changes nothing
 * (answers 378, 397, 400 and 414, rules 16 and 17).
 */
final class ModuleScreenActions {
  private ModuleScreenActions() {}

  /** The owner picks one of his online networks for the module (answers 397 and 400). */
  static void choose(Player player, BlockPos pos, NetworkId network) {
    Optional<ModuleBlockEntity> block = blockAt(player, pos);
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
            .flatMap(ModuleBlockEntity::module)
            .map(module -> module.isOwner(viewer))
            .orElse(false);
    if (!owner) {
      return List.of();
    }
    return RedweaveServer.running()
        .map(server -> server.networkChoices().of(viewer))
        .orElse(List.of());
  }

  /** A channel needs the network of the module; the edge always works (answers 375 and 414). */
  static void connect(
      PlayerId requester,
      ModuleBlockEntity block,
      Optional<Integer> channel,
      Consumer<Connection> keep) {
    Connection connection =
        channel.map(index -> Connection.on(ChannelColor.values()[index])).orElse(Connection.EDGE);
    if (block.module().get().connects(requester, connection) == Outcome.ACCEPTED) {
      keep.accept(connection);
    }
  }

  private static Optional<ModuleBlockEntity> blockAt(Player player, BlockPos pos) {
    return player.level().getBlockEntity(pos) instanceof ModuleBlockEntity found
        ? Optional.of(found)
        : Optional.empty();
  }
}
