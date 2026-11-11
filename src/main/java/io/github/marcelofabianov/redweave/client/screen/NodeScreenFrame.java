package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.menu.NodeMenu;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/** The window of the Node Screen: the metal frame, the title and the state. No side tabs (233). */
final class NodeScreenFrame {
  private static final int TITLE_X = 10;
  private static final int TITLE_Y = 9;
  private static final int PILL_Y = 6;
  private static final int PILL_RIGHT_PADDING = 10;

  private NodeScreenFrame() {}

  static void draw(
      GuiGraphics graphics,
      Font font,
      int left,
      int top,
      int width,
      int height,
      Component title,
      NodeMenu menu) {
    graphics.blitSprite(NodeSprites.WINDOW, left, top, width, height);
    graphics.drawString(font, title, left + TITLE_X, top + TITLE_Y, ScreenColors.TEXT, false);
    StatePill.draw(graphics, font, left + width - PILL_RIGHT_PADDING, top + PILL_Y, menu.state());
  }
}
