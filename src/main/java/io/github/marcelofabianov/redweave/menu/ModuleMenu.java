package io.github.marcelofabianov.redweave.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

/** The menu of a Signal Module screen, which takes a new name only from its own plate (404). */
public interface ModuleMenu {
  BlockPos pos();

  boolean stillValid(Player player);
}
