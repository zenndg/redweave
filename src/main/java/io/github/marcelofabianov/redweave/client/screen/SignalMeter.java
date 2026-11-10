package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.domain.signal.Signal;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

/**
 * The 15-bar meter: the bars grow in height, a signal of 0 is all off, and from 12 they glow
 * (_docs/conceito/visual.md, section Sinal).
 */
final class SignalMeter {
  static final int BARS = Signal.MAX.strength();
  private static final int BAR_WIDTH = 4;
  private static final int STEP = 6;
  static final int WIDTH = BARS * STEP - (STEP - BAR_WIDTH);
  private static final int SHORTEST = 5;
  private static final int TALLEST = 14;
  private static final int GLOW_FROM = 12;

  private SignalMeter() {}

  /** Draws the bars with their feet on the baseline given. */
  static void draw(GuiGraphics graphics, int x, int baseline, int signal) {
    for (int bar = 1; bar <= BARS; bar++) {
      int height = SHORTEST + (TALLEST - SHORTEST) * (bar - 1) / (BARS - 1);
      graphics.blitSprite(
          spriteOf(bar, signal), x + (bar - 1) * STEP, baseline - height, BAR_WIDTH, height);
    }
  }

  private static ResourceLocation spriteOf(int bar, int signal) {
    if (bar > signal) {
      return NodeSprites.BAR_OFF;
    }
    return signal >= GLOW_FROM ? NodeSprites.BAR_GLOW : NodeSprites.BAR_ON;
  }
}
