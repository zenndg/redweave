package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.domain.module.InverterState;
import java.util.Optional;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * The diagram A → NOT → B on the left of the Signal Inverter Screen: each side lit in red when it
 * has a signal, the copper triangle with the bubble of the NOT in the middle, and B dark gray while
 * burnt out (answers 448, 451 and 452). Drawn pixel by pixel, at the scale of the window.
 */
final class InverterDiagram {
  private static final int A_X = 20;
  private static final int B_X = 105;
  private static final int CENTER_Y = 60;
  private static final int RADIUS = 5;
  private static final double RING_INNER = RADIUS - 1.1;
  private static final double EDGE = RADIUS + 0.2;
  private static final int TRIANGLE_X = 42;
  private static final int TRIANGLE_WIDTH = 18;
  private static final int TRIANGLE_HALF = 9;
  private static final int BUBBLE_RADIUS = 2;
  private static final int BUBBLE_X = TRIANGLE_X + TRIANGLE_WIDTH + BUBBLE_RADIUS + 1;
  private static final double BUBBLE_INNER = BUBBLE_RADIUS - 1.1;
  private static final double BUBBLE_EDGE = BUBBLE_RADIUS + 0.2;
  private static final int LETTER_Y = 69;
  private static final int TRIANGLE_FILL = 0xFF4A4F57;
  private static final int NO_PAINT = 0;
  private static final String A = "A";
  private static final String B = "B";

  private InverterDiagram() {}

  static void draw(
      GuiGraphics graphics,
      Font font,
      int left,
      int top,
      boolean inputOn,
      Optional<InverterState> state) {
    int y = top + CENTER_Y;
    boolean outputOn = state.equals(Optional.of(InverterState.ON));
    boolean burnt = state.equals(Optional.of(InverterState.BURNT_OUT));
    line(graphics, left + A_X + RADIUS + 1, left + TRIANGLE_X, y, inputOn);
    line(graphics, left + BUBBLE_X + BUBBLE_RADIUS + 1, left + B_X - RADIUS, y, outputOn);
    triangle(graphics, left + TRIANGLE_X, y);
    circle(graphics, left + BUBBLE_X, y, BUBBLE_INNER, BUBBLE_EDGE, NO_PAINT);
    circle(graphics, left + A_X, y, RING_INNER, EDGE, inputOn ? ScreenColors.REDSTONE : NO_PAINT);
    circle(graphics, left + B_X, y, RING_INNER, EDGE, fillOf(outputOn, burnt));
    letter(graphics, font, left + A_X, top + LETTER_Y, A);
    letter(graphics, font, left + B_X, top + LETTER_Y, B);
  }

  private static int fillOf(boolean on, boolean burnt) {
    if (burnt) {
      return ScreenColors.BURNT;
    }
    return on ? ScreenColors.REDSTONE : NO_PAINT;
  }

  private static void line(GuiGraphics graphics, int from, int to, int y, boolean on) {
    graphics.fill(from, y, to, y + 1, on ? ScreenColors.REDSTONE : ScreenColors.SEPARATOR);
  }

  /** The body of the NOT, pointing to B, gray with a copper outline. */
  private static void triangle(GuiGraphics graphics, int x, int y) {
    for (int column = 0; column <= TRIANGLE_WIDTH; column++) {
      int half = Math.round(TRIANGLE_HALF * (1 - (float) column / TRIANGLE_WIDTH));
      graphics.fill(x + column, y - half, x + column + 1, y + half + 1, ScreenColors.COPPER);
      if (column > 0 && half > 0) {
        graphics.fill(x + column, y - half + 1, x + column + 1, y + half, TRIANGLE_FILL);
      }
    }
  }

  /** A copper ring around (x, y), filled with a color, or empty for none. */
  private static void circle(
      GuiGraphics graphics, int x, int y, double inner, double edge, int fill) {
    int reach = (int) Math.ceil(edge);
    for (int dy = -reach; dy <= reach; dy++) {
      for (int dx = -reach; dx <= reach; dx++) {
        double distance = Math.sqrt(dx * dx + dy * dy);
        int color = distance > inner ? ScreenColors.COPPER : fill;
        if (distance <= edge && color != NO_PAINT) {
          graphics.fill(x + dx, y + dy, x + dx + 1, y + dy + 1, ringOf(color, fill));
        }
      }
    }
  }

  /** A side lit or burnt has no copper ring: the whole circle takes its color. */
  private static int ringOf(int color, int fill) {
    return fill == NO_PAINT ? color : fill;
  }

  private static void letter(GuiGraphics graphics, Font font, int x, int y, String letter) {
    Component text = Component.literal(letter);
    graphics.drawString(font, text, x - font.width(text) / 2 + 1, y, ScreenColors.LABEL, false);
  }
}
