package io.github.zenndg.redweave.client.screen;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** The cube of the Node Screen picks the face under the mouse with this test (answer 79). */
class QuadsTest {
  private static final double SIDE = 10;
  private static final double INSIDE = 5;
  private static final double OUTSIDE = 11;

  /** A square seen from the front, going around clockwise on the screen. */
  private static double[][] square() {
    return new double[][] {{0, 0}, {SIDE, 0}, {SIDE, SIDE}, {0, SIDE}};
  }

  /** The same square seen from the back: the corners go around the other way. */
  private static double[][] reversed() {
    double[][] square = square();
    double[][] reversed = new double[square.length][];
    for (int corner = 0; corner < square.length; corner++) {
      reversed[corner] = square[square.length - 1 - corner];
    }
    return reversed;
  }

  @Test
  void aPointInsideIsFoundWhicheverWayTheCornersGo() {
    assertTrue(Quads.contains(square(), INSIDE, INSIDE));
    assertTrue(Quads.contains(reversed(), INSIDE, INSIDE));
  }

  @Test
  void aPointOutsideIsNot() {
    assertFalse(Quads.contains(square(), OUTSIDE, INSIDE));
    assertFalse(Quads.contains(square(), INSIDE, -INSIDE));
  }
}
