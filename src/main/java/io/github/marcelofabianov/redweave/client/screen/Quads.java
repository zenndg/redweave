package io.github.marcelofabianov.redweave.client.screen;

/**
 * Whether a point of the screen falls inside a convex quad, seen from the front or the back. The
 * corners come as screen coordinates, {x, y}, going around the quad.
 */
final class Quads {
  private Quads() {}

  static boolean contains(double[][] corners, double x, double y) {
    boolean anyPositive = false;
    boolean anyNegative = false;
    for (int corner = 0; corner < corners.length; corner++) {
      double[] from = corners[corner];
      double[] to = corners[(corner + 1) % corners.length];
      double cross = (to[0] - from[0]) * (y - from[1]) - (to[1] - from[1]) * (x - from[0]);
      anyPositive |= cross > 0;
      anyNegative |= cross < 0;
    }
    return !(anyPositive && anyNegative);
  }
}
