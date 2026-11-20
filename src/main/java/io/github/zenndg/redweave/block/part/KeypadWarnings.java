package io.github.zenndg.redweave.block.part;

import io.github.zenndg.redweave.block.ModuleMessages;
import io.github.zenndg.redweave.config.NeoForgeKeypadLimits;
import io.github.zenndg.redweave.domain.module.KeypadReport;
import io.github.zenndg.redweave.domain.module.SignalModule;
import io.github.zenndg.redweave.domain.network.PlayerId;
import io.github.zenndg.redweave.server.ModuleServices;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

/**
 * Tells the owner of a Signal Keypad about a miss past the free tries: a line in the chat when
 * online, or one more miss in the summary the owner gets when joining (answers 666, 903 and 911).
 */
public final class KeypadWarnings {
  private KeypadWarnings() {}

  static void missed(
      MinecraftServer server,
      ModuleServices modules,
      SignalModule keypad,
      ServerPlayer player,
      int misses) {
    ServerPlayer owner = server.getPlayerList().getPlayer(keypad.owner().value());
    if (owner != null) {
      owner.sendSystemMessage(
          line(player.getGameProfile().getName(), keypad.displayName(), misses));
      return;
    }
    int cap = new NeoForgeKeypadLimits().maxOfflineReports();
    modules
        .keypadReports()
        .missed(
            keypad.owner(), keypad.id(), keypad.displayName(), new PlayerId(player.getUUID()), cap);
  }

  /** The summary of the misses while the owner was away, line by line, then cleared (903). */
  public static void joined(MinecraftServer server, ModuleServices modules, ServerPlayer owner) {
    for (KeypadReport report : modules.keypadReports().take(new PlayerId(owner.getUUID()))) {
      String player = nameOf(server, report.lastPlayer());
      owner.sendSystemMessage(line(player, report.name(), report.misses()));
    }
  }

  private static Component line(String player, String keypad, int misses) {
    return Component.translatable(ModuleMessages.KEYPAD_MISSED, player, keypad, misses);
  }

  /** The name of the player the server knows, or the start of the UUID. */
  private static String nameOf(MinecraftServer server, PlayerId player) {
    return server
        .getProfileCache()
        .get(player.value())
        .map(profile -> profile.getName())
        .orElse(player.value().toString());
  }
}
