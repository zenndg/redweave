package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.menu.NodeMenu;
import io.github.marcelofabianov.redweave.menu.NodeScreenTexts;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/** The window of the Node Screen: the metal frame, the tab, the title and the state. */
final class NodeScreenFrame {
  private static final int TITLE_X = 10;
  private static final int TITLE_Y = 9;
  private static final int PILL_Y = 6;
  private static final int PILL_RIGHT_PADDING = 10;
  private static final int TAB_Y = 26;
  private static final int TAB_OVERLAP = 3;
  private static final int TAB_WIDTH = 44;
  private static final int TAB_HEIGHT = 18;
  private static final int TAB_TEXT_X = 8;
  private static final int TAB_TEXT_Y = 5;

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
    drawTab(graphics, font, left + width - TAB_OVERLAP, top + TAB_Y);
    graphics.blitSprite(NodeSprites.WINDOW, left, top, width, height);
    graphics.drawString(font, title, left + TITLE_X, top + TITLE_Y, ScreenColors.TEXT, false);
    StatePill.draw(
        graphics,
        font,
        left + width - PILL_RIGHT_PADDING,
        top + PILL_Y,
        menu.state(),
        menu.networkName());
  }

  /** Only Slots for now: the other tabs arrive with their content (answer 80). */
  private static void drawTab(GuiGraphics graphics, Font font, int x, int y) {
    graphics.blitSprite(NodeSprites.TAB_SELECTED, x, y, TAB_WIDTH, TAB_HEIGHT);
    Component tab = Component.translatable(NodeScreenTexts.TAB_SLOTS);
    graphics.drawString(font, tab, x + TAB_TEXT_X, y + TAB_TEXT_Y, ScreenColors.TEXT, false);
  }

  static void label(GuiGraphics graphics, Font font, int x, int y) {
    Component label = Component.translatable(NodeScreenTexts.CHANNEL_SLOTS);
    graphics.drawString(font, label, x, y, ScreenColors.LABEL, false);
  }
}
