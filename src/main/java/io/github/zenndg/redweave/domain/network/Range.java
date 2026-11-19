package io.github.zenndg.redweave.domain.network;

/**
 * The reach of a Network Controller: a sphere of a radius in blocks, in the same dimension. A
 * radius of 0 reaches the whole dimension (answers 23 and 36).
 */
public record Range(int radius) {
  private static final int UNLIMITED = 0;
  private static final String NEGATIVE = "The radius of a range cannot be negative, got %d";

  public Range {
    if (radius < UNLIMITED) {
      throw new IllegalArgumentException(String.format(NEGATIVE, radius));
    }
  }

  /** Compares the squares, in long, so there is no root, no rounding and no overflow. */
  public boolean reaches(BlockLocation controller, BlockLocation node) {
    if (!controller.dimension().equals(node.dimension())) {
      return false;
    }
    if (radius == UNLIMITED) {
      return true;
    }
    long dx = (long) node.x() - controller.x();
    long dy = (long) node.y() - controller.y();
    long dz = (long) node.z() - controller.z();
    return dx * dx + dy * dy + dz * dz <= (long) radius * radius;
  }
}
