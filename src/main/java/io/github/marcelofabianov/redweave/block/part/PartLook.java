package io.github.marcelofabianov.redweave.block.part;

import java.util.Objects;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

/**
 * What the client draws for a part: a model of the mod, drawn on the floor, turned so its top
 * points to `facing`, as the blockstates of the plates did (answers 534 and 536).
 */
public record PartLook(ResourceLocation model, Direction facing) {
  private static final String MISSING = "A part look needs a model and a facing";

  public PartLook {
    Objects.requireNonNull(model, MISSING);
    Objects.requireNonNull(facing, MISSING);
  }
}
