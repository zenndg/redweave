package io.github.marcelofabianov.redweave.block;

import com.mojang.authlib.GameProfile;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.GameProfileCache;
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
      tell(player, Component.translatable(ControllerMessages.NO_NETWORK));
      return;
    }
    PlayerId clicker = new PlayerId(player.getUUID());
    if (network.get().isOwner(clicker)) {
      server.openNetwork().open(clicker, network.get().id());
      tell(player, Component.translatable(ControllerMessages.CURRENT, network.get().name()));
      return;
    }
    String owner = ownerName(player.getServer(), network.get().owner().value());
    tell(player, Component.translatable(ControllerMessages.FOREIGN, network.get().name(), owner));
  }

  static void tell(Player player, Component message) {
    player.displayClientMessage(message, true);
  }

  /** The name the server knows for the owner, or the UUID of an owner it never saw. */
  private static String ownerName(MinecraftServer minecraftServer, UUID owner) {
    GameProfileCache profiles = minecraftServer == null ? null : minecraftServer.getProfileCache();
    if (profiles == null) {
      return owner.toString();
    }
    return profiles.get(owner).map(GameProfile::getName).orElse(owner.toString());
  }
}
