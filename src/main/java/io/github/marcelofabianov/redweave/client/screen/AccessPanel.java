package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.menu.ControllerScreenRows.Header;
import io.github.marcelofabianov.redweave.menu.ControllerScreenTexts;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/** The Access tab: the owner of the network and the notice (rede-e-canais.md, answer 22). */
final class AccessPanel {
  private static final int PADDING = 8;
  private static final int FIELD_Y = 20;
  private static final int FIELD_WIDTH = 160;
  private static final int FIELD_HEIGHT = 16;
  private static final int FIELD_TEXT_X = 6;
  private static final int FIELD_TEXT_Y = 4;
  private static final int NOTICE_Y = 46;

  private AccessPanel() {}

  static void draw(GuiGraphics graphics, Font font, Box area, Header header) {
    int x = area.x() + PADDING;
    Component owner = Component.translatable(ControllerScreenTexts.OWNER);
    graphics.drawString(font, owner, x, area.y() + PADDING, ScreenColors.LABEL, false);
    int fieldY = area.y() + FIELD_Y;
    graphics.blitSprite(NodeSprites.NAME_FIELD, x, fieldY, FIELD_WIDTH, FIELD_HEIGHT);
    String name =
        FittedText.fit(font, Component.literal(header.owner()), FIELD_WIDTH - 2 * FIELD_TEXT_X);
    graphics.drawString(
        font, name, x + FIELD_TEXT_X, fieldY + FIELD_TEXT_Y, ScreenColors.TEXT, false);
    int noticeY = area.y() + NOTICE_Y;
    Locks.draw(graphics, x, noticeY);
    Component notice = Component.translatable(ControllerScreenTexts.READ_ONLY);
    graphics.drawString(
        font, notice, x + Locks.WIDTH + Locks.GAP, noticeY, ScreenColors.LABEL, false);
  }
}
