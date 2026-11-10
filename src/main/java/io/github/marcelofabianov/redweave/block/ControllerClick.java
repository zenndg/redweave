package io.github.marcelofabianov.redweave.block;

import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

/**
 * A click with an empty hand on a controller. The owner makes its network the current one, and the
 * others only see whose it is (answers 25 and 52).
 */
final class ControllerClick {
  private ControllerClick() {}

  static void show(RedweaveServer server, NetworkControllerBlockEntity controller, Player player) {
    Optional<Network> network = controller.network().flatMap(server::network);
    if (network.isEmpty()) {
      ActionBar.tell(player, Component.translatable(ControllerMessages.NO_NETWORK));
      return;
    }
    PlayerId clicker = new PlayerId(player.getUUID());
    if (network.get().isOwner(clicker)) {
      server.openNetwork().open(clicker, network.get().id());
      ActionBar.tell(
          player, Component.translatable(ControllerMessages.CURRENT, network.get().name()));
      return;
    }
    String owner = PlayerNames.of(player.getServer(), network.get().owner().value());
    ActionBar.tell(
        player, Component.translatable(ControllerMessages.FOREIGN, network.get().name(), owner));
  }
}
