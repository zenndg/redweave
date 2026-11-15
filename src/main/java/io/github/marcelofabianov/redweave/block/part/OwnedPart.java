package io.github.marcelofabianov.redweave.block.part;

import io.github.marcelofabianov.redweave.block.PartSpaceBlockEntity;
import net.minecraft.world.entity.player.Player;

/** A part only its owner breaks in survival; in creative, anyone (answers 74, 394 and 526). */
public interface OwnedPart {
  /** Whether the player may break it, as both sides see it, to show the breaking. */
  boolean mayBreak(Player player);

  /** The last word of the server on breaking, whatever the client shows (rule 16). */
  boolean breakBy(PartSpaceBlockEntity space, Player player);
}
