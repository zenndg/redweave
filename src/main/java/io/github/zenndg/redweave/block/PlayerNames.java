package io.github.zenndg.redweave.block;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.GameProfileCache;

/** The name the server knows for a player, or the UUID of a player it never saw. */
public final class PlayerNames {
  private PlayerNames() {}

  public static String of(MinecraftServer server, UUID player) {
    GameProfileCache profiles = server == null ? null : server.getProfileCache();
    if (profiles == null) {
      return player.toString();
    }
    return profiles.get(player).map(GameProfile::getName).orElse(player.toString());
  }
}
