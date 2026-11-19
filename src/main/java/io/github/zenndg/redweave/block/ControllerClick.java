package io.github.zenndg.redweave.block;

import io.github.zenndg.redweave.domain.network.Network;
import io.github.zenndg.redweave.domain.network.NetworkState;
import io.github.zenndg.redweave.menu.ControllerMenu;
import io.github.zenndg.redweave.server.RedweaveServer;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;

/**
 * A click with an empty hand on a controller opens the Controller Screen for any player, read-only
 * for anyone but the owner (answers 22 and 113). A network turned off only says so, and nothing
 * opens (answers 158 and 160).
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
    if (player instanceof ServerPlayer viewer) {
      openScreen(viewer, controller.getBlockPos(), network.get());
    }
  }

  private static void openScreen(ServerPlayer viewer, BlockPos pos, Network network) {
    viewer.openMenu(
        new SimpleMenuProvider(
            (id, inventory, opener) -> new ControllerMenu(id, viewer, pos, network.id()),
            Component.literal(network.name())),
        extra -> ControllerMenu.writeOpening(extra, viewer, pos, network.id()));
  }
}
