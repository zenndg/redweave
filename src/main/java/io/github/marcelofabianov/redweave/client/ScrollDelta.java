package io.github.marcelofabianov.redweave.client;

/**
 * The turn of the wheel, as the hotbar of the game reads it in {@code MouseHandler.onScroll}: the
 * vertical wheel, or, when it is still, the horizontal one inverted. On macOS, Shift turns the
 * vertical wheel into a horizontal one, and without this the mode would never switch (answer 351).
 */
final class ScrollDelta {
  private static final double STILL = 0;

  private ScrollDelta() {}

  static double of(double horizontal, double vertical) {
    return vertical != STILL ? vertical : STILL - horizontal;
  }
}
