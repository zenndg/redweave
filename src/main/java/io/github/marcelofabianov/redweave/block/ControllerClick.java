package io.github.marcelofabianov.redweave.block;

import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.NetworkState;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

/**
 * A click with an empty hand on a controller shows every player the network and its owner, until
 * the Controller Screen of issue #20 (answers 52 and 201). A network turned off only says so (160).
 */
final class ControllerClick {
  private ControllerClick() {}

  static void show(RedweaveServer server, NetworkControllerBlockEntity controller, Player player) {
    Optional<Network> network = controller.network().flatMap(server::network);
    if (network.isEmpty()) {
      ActionBar.tell(player, Component.translatable(ControllerMessages.NO_NETWORK));
      return;
    }
    if (network.get().state() == NetworkState.OFFLINE) {
      ActionBar.tell(player, ControllerMessages.offline(network.get().name()));
      return;
    }
    String owner = PlayerNames.of(player.getServer(), network.get().owner().value());
    ActionBar.tell(
        player, Component.translatable(ControllerMessages.OWNED_BY, network.get().name(), owner));
  }
}
