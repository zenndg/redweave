package io.github.zenndg.redweave.client.screen;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * The pill with the state of a module under the picture on the left of its screen, as `● On`: the
 * dot and the text centered in the box (answers 448 and 465).
 */
final class ModulePill {
  static final Box BOX = new Box(17, 90, 92, 13);

  private static final int DOT_X = 5;
  private static final int DOT_Y = 4;
  private static final int TEXT_Y = 3;

  private ModulePill() {}

  static void draw(
      GuiGraphics graphics, Font font, Box box, Component text, int dotColor, int textColor) {
    graphics.blitSprite(NodeSprites.STATE_PILL, box.x(), box.y(), box.width(), box.height());
    int dotWidth = font.width(TimerButton.DOT);
    int width = DOT_X + dotWidth + font.width(text);
    int x = box.x() + (box.width() - width) / 2;
    Dots.draw(graphics, x + DOT_X, box.y() + DOT_Y, dotColor);
    graphics.drawString(font, text, x + DOT_X + dotWidth, box.y() + TEXT_Y, textColor, false);
  }
}
