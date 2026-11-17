package io.github.marcelofabianov.redweave.client;

import io.github.marcelofabianov.redweave.block.part.LampPart;
import io.github.marcelofabianov.redweave.domain.signal.Signal;

/**
 * The colors of a Signal Lamp: its core is white, bright by its light, from 40% to 100%, and 25%
 * while dark; its band has the color of its intensity channel, dark while the lamp is, and gray
 * without a channel (answers 654, 657, 800, 821 and 822). It reads only what the client receives of
 * the lamp, its channel and its light.
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

  private static final int WHITE = 0xFFFFFFFF;
  private static final int BAND_SLOT = 1;
  private static final float FULL = 1;

  /** The core, slot 0, or the band, slot 1, as the model tints them. */
  static int of(LampPart lamp, int slot) {
    return slot == BAND_SLOT ? band(lamp) : scaled(WHITE, brightness(lamp.light()));
  }

  private static int band(LampPart lamp) {
    if (lamp.color().isEmpty()) {
      return ChannelColors.FREE;
    }
    int color = ChannelColors.argb(lamp.color().get());
    return scaled(color, lamp.light() > 0 ? FULL : DARK);
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
