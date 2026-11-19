package io.github.zenndg.redweave.block.part;

import net.minecraft.world.entity.player.Player;

/** A part that answers a click of the empty hand, as the node teaches how to open it (188). */
public interface HandPart {
  void emptyHand(Player player);

  /**
   * Whether a click with any item but the Configurator works as the empty hand, as on the Signal
   * Lever, which takes the click before the item, as a lever of the game (answer 576).
   */
  default boolean takesAnyItem() {
    return false;
  }
}
