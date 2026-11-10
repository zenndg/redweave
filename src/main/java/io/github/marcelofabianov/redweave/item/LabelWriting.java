package io.github.marcelofabianov.redweave.item;

import io.github.marcelofabianov.redweave.domain.network.NodeCopy;
import io.github.marcelofabianov.redweave.registry.RedweaveDataComponents;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Writes one empty Label of the other hand, which goes to the inventory, or to the ground when it
 * is full. A written Label there is never written over, so no label is lost by mistake (118).
 */
final class LabelWriting {
  private LabelWriting() {}

  static boolean write(Player player, NodeCopy copy) {
    ItemStack other = player.getOffhandItem();
    if (!other.is(RedweaveItems.LABEL.get())
        || other.has(RedweaveDataComponents.NODE_SLOTS.get())) {
      return false;
    }
    ItemStack written = other.split(1);
    written.set(RedweaveDataComponents.NODE_SLOTS.get(), copy);
    if (!player.getInventory().add(written)) {
      player.drop(written, false);
    }
    return true;
  }
}
