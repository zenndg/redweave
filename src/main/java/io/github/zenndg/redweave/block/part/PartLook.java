package io.github.zenndg.redweave.block.part;

import java.util.Objects;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

/**
 * What the client draws for a part: a model of the mod, drawn on the floor and turned to its place
 * as the blockstates of the plates did, and the side of the space it rests on, which keeps its
 * tints apart from the other parts (answers 534 and 536).
 */
public record PartLook(ResourceLocation model, Direction face, PlateTurn turn) {
  private static final String MISSING = "A part look needs a model, a face and a turn";

  public PartLook {
    Objects.requireNonNull(model, MISSING);
    Objects.requireNonNull(face, MISSING);
    Objects.requireNonNull(turn, MISSING);
  }
}
