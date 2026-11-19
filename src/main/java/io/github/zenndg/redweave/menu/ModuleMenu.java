package io.github.zenndg.redweave.menu;

import io.github.zenndg.redweave.block.part.PartSpot;
import net.minecraft.world.entity.player.Player;

/**
 * The menu of a Signal Module screen, which takes a new name only from its own plate and keeps the
 * names and the network choice of its module in sync (answers 400 and 404).
 */
public interface ModuleMenu {
  PartSpot spot();

  boolean stillValid(Player player);

  ModuleScreenSync sync();
}
