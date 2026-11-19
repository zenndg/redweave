package io.github.zenndg.redweave.block;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

/** The line above the hotbar, where the blocks answer a click until their screens arrive (52). */
final class ActionBar {
  private ActionBar() {}

  static void tell(Player player, Component message) {
    player.displayClientMessage(message, true);
  }
}
