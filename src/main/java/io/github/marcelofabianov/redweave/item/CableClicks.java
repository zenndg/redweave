package io.github.marcelofabianov.redweave.item;

import io.github.marcelofabianov.redweave.block.PartSpaceBlockEntity;
import io.github.marcelofabianov.redweave.block.PartSpaces;
import io.github.marcelofabianov.redweave.block.part.CablePart;
import io.github.marcelofabianov.redweave.block.part.PartMessages;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * What the Configurator does on a Signal Cable on the server: the cable has no screen and no state,
 * so only the Wrench does something, picking it up with its color (answers 525 and 526).
 */
final class CableClicks {
  private static final boolean NOT_THROWN = false;

  private CableClicks() {}

  /** Only the Wrench takes the click; Open, Copy and Toggle do nothing on a cable (answer 525). */
  static boolean takes(ConfiguratorMode mode) {
    return mode == ConfiguratorMode.WRENCH;
  }

  /**
   * Picks the cable up, as its item with its color, to the inventory, or to the ground when it is
   * full; in survival only its owner does, and in creative anyone (answers 525 and 526).
   */
  static Component pickUp(Player player, PartSpaceBlockEntity space, CablePart cable) {
    if (!player.isCreative() && !cable.mayBreak(player)) {
      return Component.translatable(PartMessages.CABLE_NOT_OWNER);
    }
    ItemStack item = cable.drop();
    PartSpaces.pickUp(space, cable);
    if (!player.getInventory().add(item)) {
      player.drop(item, NOT_THROWN);
    }
    return Component.translatable(PartMessages.CABLE_PICKED_UP);
  }
}
