package io.github.marcelofabianov.redweave.block.part;

import net.minecraft.world.entity.player.Player;

/** A part that answers a click of the empty hand, as the node teaches how to open it (188). */
public interface HandPart {
  void emptyHand(Player player);
}
