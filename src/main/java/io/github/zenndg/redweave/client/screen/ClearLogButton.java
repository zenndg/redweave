package io.github.zenndg.redweave.client.screen;

import io.github.zenndg.redweave.menu.ControllerScreenTexts;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * The red `Clear log` button in the corner of the Log tab, only for the owner. The first click
 * turns it into `Click again to clear`, and the second one asks the server (answers 264 and 265).
 */
final class ClearLogButton {
  /** The strip under the rows of the log that holds the button. */
  static final int STRIP = 16;

  private static final int HEIGHT = 14;
  private static final int PADDING = 6;
  private static final int TEXT_Y = 3;

  private boolean armed;

  void draw(GuiGraphics graphics, Font font, Box area, Mouse mouse) {
    Box box = box(font, area);
    graphics.blitSprite(spriteOf(mouse.hovers(box)), box.x(), box.y(), box.width(), box.height());
    Component text = text();
    int x = box.x() + (box.width() - font.width(text)) / 2;
    graphics.drawString(font, text, x, box.y() + TEXT_Y, ScreenColors.TEXT, false);
  }

  /** Whether this click asks the server to clear: only the second one on the button. */
  boolean click(Font font, Box area, double mouseX, double mouseY) {
    if (!box(font, area).contains(mouseX, mouseY)) {
      armed = false;
      return false;
    }
    boolean confirmed = armed;
    armed = !armed;
    return confirmed;
  }

  /** Whether the click fell on the button, so the screen does not take it as anything else. */
  boolean contains(Font font, Box area, double mouseX, double mouseY) {
    return box(font, area).contains(mouseX, mouseY);
  }

  /** The rows of the log stay above the strip of the button. */
  static Box rowsOf(Box area) {
    return new Box(area.x(), area.y(), area.width(), area.height() - STRIP);
  }

  private Box box(Font font, Box area) {
    int width = font.width(text()) + 2 * PADDING;
    return new Box(area.right() - width, area.bottom() - HEIGHT, width, HEIGHT);
  }

  private Component text() {
    return Component.translatable(
        armed ? ControllerScreenTexts.CONFIRM_CLEAR_LOG : ControllerScreenTexts.CLEAR_LOG);
  }

  private ResourceLocation spriteOf(boolean hover) {
    if (armed) {
      return TerminalSprites.BUTTON_CONFIRM;
    }
    return hover ? TerminalSprites.BUTTON_DANGER_HOVER : TerminalSprites.BUTTON_DANGER;
  }
}
