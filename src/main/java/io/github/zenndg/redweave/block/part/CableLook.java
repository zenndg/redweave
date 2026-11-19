package io.github.zenndg.redweave.block.part;

import java.util.Objects;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

/**
 * A piece of a Signal Cable the client draws: a model of the mod, drawn on the floor and pointing
 * north, turned so its top faces away from the side the cable rests on and it points toward the
 * direction (answers 521, 571 and 572). The client keeps the tints of the side apart from the other
 * parts of the space, as with the plates (answer 536).
 */
public record CableLook(ResourceLocation model, Direction face, Direction toward) {
  private static final String MISSING = "A cable look needs a model, a face and a direction";
  private static final String NOT_ACROSS = "A cable piece points across its side, not along it";

  public CableLook {
    Objects.requireNonNull(model, MISSING);
    Objects.requireNonNull(face, MISSING);
    Objects.requireNonNull(toward, MISSING);
    if (toward.getAxis() == face.getAxis()) {
      throw new IllegalArgumentException(NOT_ACROSS);
    }
  }
}
