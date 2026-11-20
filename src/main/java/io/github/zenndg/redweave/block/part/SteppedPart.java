package io.github.zenndg.redweave.block.part;

import io.github.zenndg.redweave.block.PartSpaceBlockEntity;
import net.minecraft.world.entity.Entity;

/**
 * A part that feels what walks into its space, as the Signal Pressure Plate (answer 607). The game
 * tells the space each tick an entity is inside it, so the part only looks at that entity.
 */
public interface SteppedPart {
  void steppedOn(PartSpaceBlockEntity space, Entity entity);
}
