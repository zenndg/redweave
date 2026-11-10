package io.github.marcelofabianov.redweave.block;

import io.github.marcelofabianov.redweave.domain.network.Face;
import net.minecraft.core.Direction;

/** Translates the faces of the domain to the directions of the game, which share the order. */
public final class Faces {
  private Faces() {}

  public static Face of(Direction direction) {
    return Face.valueOf(direction.name());
  }

  public static Direction toDirection(Face face) {
    return Direction.valueOf(face.name());
  }
}
