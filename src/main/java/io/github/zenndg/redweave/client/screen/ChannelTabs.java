package io.github.zenndg.redweave.client.screen;

import io.github.zenndg.redweave.client.ChannelColors;
import io.github.zenndg.redweave.domain.network.ChannelSlot;
import io.github.zenndg.redweave.domain.signal.ChannelColor;
import io.github.zenndg.redweave.menu.NodeMenu;
import java.util.Optional;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

/**
 * The row of the 16 channels of the Node Screen, each a slot of the node: its color and number, a
 * green bar when it is enabled and the copper outline when it is open in the panel (answers 224 and
 * 231). Anyone may look at any channel; only the owner changes them.
 */
final class ChannelTabs {
  static final int Y = 36;
  static final int HEIGHT = 22;

  /** The 16 tabs, 287 pixels wide, centered on the 340 of the window. */
  private static final int LEFT = 26;

  private static final int WIDTH = 17;
  private static final int GAP = 1;
  private static final int SQUARE = 9;
  private static final int SQUARE_Y = 3;
  private static final int NUMBER_Y = 13;
  private static final String NUMBER = "%02d";

  /** The font of the game counts one blank pixel after the last letter, which is not seen. */
  private static final int TRAILING_SPACE = 1;

  private static final int ROUND_UP = 1;

  private ChannelTabs() {}

  /** The width the number shows, so it centers under the square of the color. */
  private static int visibleWidth(Font font, String number) {
    return font.width(number) - TRAILING_SPACE;
  }

  static void draw(
      GuiGraphics graphics, Font font, int left, int top, NodeMenu menu, int open, Mouse mouse) {
    for (ChannelColor channel : ChannelColor.values()) {
      Box box = boxOf(left, top, channel);
      boolean enabled = menu.slot(channel.ordinal()).filter(ChannelSlot::active).isPresent();
      ResourceLocation sprite = spriteOf(channel.ordinal() == open, enabled, mouse.hovers(box));
      graphics.blitSprite(sprite, box.x(), box.y(), box.width(), box.height());
      int squareX = box.x() + (box.width() - SQUARE) / 2;
      int squareY = box.y() + SQUARE_Y;
      graphics.fill(
          squareX, squareY, squareX + SQUARE, squareY + SQUARE, ChannelColors.argb(channel));
      String number = String.format(NUMBER, channel.number());
      int numberX = box.x() + (box.width() - visibleWidth(font, number) + ROUND_UP) / 2;
      int color = enabled ? ScreenColors.TEXT : ScreenColors.DIM;
      graphics.drawString(font, number, numberX, box.y() + NUMBER_Y, color, false);
    }
  }

  private static ResourceLocation spriteOf(boolean open, boolean enabled, boolean hover) {
    if (open) {
      return NodeSprites.CHANNEL_TAB_SELECTED;
    }
    if (enabled) {
      return NodeSprites.CHANNEL_TAB_ACTIVE;
    }
    return hover ? NodeSprites.CHANNEL_TAB_HOVER : NodeSprites.CHANNEL_TAB;
  }

  static Optional<ChannelColor> at(int left, int top, double mouseX, double mouseY) {
    for (ChannelColor channel : ChannelColor.values()) {
      if (boxOf(left, top, channel).contains(mouseX, mouseY)) {
        return Optional.of(channel);
      }
    }
    return Optional.empty();
  }

  private static Box boxOf(int left, int top, ChannelColor channel) {
    return new Box(left + LEFT + channel.ordinal() * (WIDTH + GAP), top + Y, WIDTH, HEIGHT);
  }
}
