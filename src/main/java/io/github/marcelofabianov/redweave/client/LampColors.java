package io.github.marcelofabianov.redweave.client;

import io.github.marcelofabianov.redweave.block.part.LampPart;
import io.github.marcelofabianov.redweave.domain.signal.Signal;

/**
 * The color of the diffuser of a Signal Lamp: the color of its intensity channel, bright by its
 * light and dim while dark, and gray without a channel (answers 650, 654 and 657). It reads only
 * what the client receives of the lamp, its channel and its light.
 */
final class LampColors {
  private static final float DARK = 0.25F;
  private static final float LIT_FLOOR = 0.4F;
  private static final float LIT_RANGE = 1 - LIT_FLOOR;
  private static final int ALPHA = 0xFF000000;
  private static final int BYTE = 0xFF;
  private static final int RED_SHIFT = 16;
  private static final int GREEN_SHIFT = 8;

  private LampColors() {}

  static int of(LampPart lamp) {
    if (lamp.color().isEmpty()) {
      return ChannelColors.FREE;
    }
    int color = ChannelColors.argb(lamp.color().get());
    return scaled(color, brightness(lamp.light()));
  }

  private static float brightness(int light) {
    if (light <= 0) {
      return DARK;
    }
    return LIT_FLOOR + LIT_RANGE * light / Signal.MAX.strength();
  }

  private static int scaled(int argb, float factor) {
    int red = Math.round(((argb >> RED_SHIFT) & BYTE) * factor);
    int green = Math.round(((argb >> GREEN_SHIFT) & BYTE) * factor);
    int blue = Math.round((argb & BYTE) * factor);
    return ALPHA | (red << RED_SHIFT) | (green << GREEN_SHIFT) | blue;
  }
}
