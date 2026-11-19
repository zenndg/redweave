package io.github.marcelofabianov.redweave.client.screen;

import java.util.Optional;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * The diagram A → T → B on the left of the Signal Flip-Flop Screen: each side lit in red when it
 * has a signal, and in the middle a copper box with the `T` of the plate (answers 884 and 888).
 * Drawn pixel by pixel, at the scale of the window, as the diagram of the Signal Inverter.
 */
final class FlipFlopDiagram {
  private static final int A_X = 20;
  private static final int B_X = 105;
  private static final int CENTER_Y = 60;
  private static final int RADIUS = 5;
  private static final double RING_INNER = RADIUS - 1.1;
  private static final double EDGE = RADIUS + 0.2;
  private static final int BOX_X = 50;
  private static final int BOX_SIZE = 20;
  private static final int BOX_HALF = BOX_SIZE / 2;
  private static final int LETTER_Y = 69;
  private static final int GLYPH_Y = 4;
  private static final int BOX_FILL = 0xFF4A4F57;
  private static final int NO_PAINT = 0;
  private static final String A = "A";
  private static final String B = "B";
  private static final String T = "T";

  private FlipFlopDiagram() {}

  static void draw(
      GuiGraphics graphics, Font font, int left, int top, boolean inputOn, Optional<Boolean> on) {
    int y = top + CENTER_Y;
    boolean outputOn = on.orElse(false);
    line(graphics, left + A_X + RADIUS + 1, left + BOX_X, y, inputOn);
    line(graphics, left + BOX_X + BOX_SIZE + 1, left + B_X - RADIUS, y, outputOn);
    box(graphics, font, left + BOX_X, y);
    circle(graphics, left + A_X, y, inputOn);
    circle(graphics, left + B_X, y, outputOn);
    letter(graphics, font, left + A_X, top + LETTER_Y, A);
    letter(graphics, font, left + B_X, top + LETTER_Y, B);
  }

  private static void line(GuiGraphics graphics, int from, int to, int y, boolean on) {
    graphics.fill(from, y, to, y + 1, on ? ScreenColors.REDSTONE : ScreenColors.SEPARATOR);
  }

  /** The body of the flip-flop, gray with a copper outline and the `T` of the plate (888). */
  private static void box(GuiGraphics graphics, Font font, int x, int y) {
    int top = y - BOX_HALF;
    graphics.fill(x, top, x + BOX_SIZE + 1, top + BOX_SIZE + 1, ScreenColors.COPPER);
    graphics.fill(x + 1, top + 1, x + BOX_SIZE, top + BOX_SIZE, BOX_FILL);
    Component glyph = Component.literal(T);
    int glyphX = x + (BOX_SIZE - font.width(glyph)) / 2 + 1;
    graphics.drawString(font, glyph, glyphX, y - GLYPH_Y, ScreenColors.COPPER, false);
  }

  /** A copper ring around (x, y), filled in red when lit; a lit side has no copper ring. */
  private static void circle(GuiGraphics graphics, int x, int y, boolean on) {
    int fill = on ? ScreenColors.REDSTONE : NO_PAINT;
    int reach = (int) Math.ceil(EDGE);
    for (int dy = -reach; dy <= reach; dy++) {
      for (int dx = -reach; dx <= reach; dx++) {
        double distance = Math.sqrt(dx * dx + dy * dy);
        int color = distance > RING_INNER ? ScreenColors.COPPER : fill;
        if (distance <= EDGE && color != NO_PAINT) {
          int painted = fill == NO_PAINT ? color : fill;
          graphics.fill(x + dx, y + dy, x + dx + 1, y + dy + 1, painted);
        }
      }
    }
  }

  private static void letter(GuiGraphics graphics, Font font, int x, int y, String letter) {
    Component text = Component.literal(letter);
    graphics.drawString(font, text, x - font.width(text) / 2 + 1, y, ScreenColors.LABEL, false);
  }
}
