package io.github.marcelofabianov.redweave.client.screen;

/** The parts of an ARGB color, from 0 to 1, to tint a white sprite. */
final class Channels {
  private static final int BYTE = 0xFF;
  private static final float MAX = 255.0F;
  private static final int RED_SHIFT = 16;
  private static final int GREEN_SHIFT = 8;

  private Channels() {}

  static float red(int argb) {
    return (argb >> RED_SHIFT & BYTE) / MAX;
  }

  static float green(int argb) {
    return (argb >> GREEN_SHIFT & BYTE) / MAX;
  }

  static float blue(int argb) {
    return (argb & BYTE) / MAX;
  }
}
