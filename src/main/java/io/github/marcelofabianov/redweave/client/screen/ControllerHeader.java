package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.client.ChannelColors;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.menu.ControllerMenu;
import io.github.marcelofabianov.redweave.menu.ControllerScreenRows.Header;
import io.github.marcelofabianov.redweave.menu.ControllerScreenTexts;
import io.github.marcelofabianov.redweave.menu.NetworkTexts;
import io.github.marcelofabianov.redweave.menu.NodeScreenTexts;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * The header of the Controller Screen: the color square and the name of the network, the Rename
 * button for the owner and the state pill, `Online · 6 components` (telas.md, answers 156, 212 and
 * 804).
 */
final class ControllerHeader {
  private static final int LEFT = 8;
  private static final int Y = 6;
  private static final int SQUARE = 16;
  private static final int SQUARE_INSET = 3;
  private static final int NAME_X = LEFT + SQUARE + 6;
  private static final int NAME_Y = 4;
  private static final int NAME_HEIGHT = 16;
  private static final int MAX_NAME = 150;
  private static final int BUTTON_GAP = 6;
  private static final int BUTTON_PADDING = 6;
  private static final int BUTTON_Y = 2;
  private static final int BUTTON_HEIGHT = 13;
  private static final int BUTTON_TEXT_Y = 3;
  private static final int FIELD_WIDTH = 150;
  private static final int PILL_RIGHT = 8;
  private static final int PILL_Y = 3;
  private static final String SEPARATOR = " · ";

  private ControllerHeader() {}

  static Box colorSquare(int left, int top) {
    return new Box(left + LEFT, top + Y, SQUARE, SQUARE);
  }

  /** Where the text field is while the owner renames the network. */
  static Box fieldBox(int left, int top) {
    return new Box(left + NAME_X - BUTTON_PADDING / 2, top + Y, FIELD_WIDTH, NAME_HEIGHT);
  }

  static Box renameBox(Font font, int left, int top, Header header) {
    int nameWidth = font.width(FittedText.fit(font, Component.literal(header.name()), MAX_NAME));
    int width = BUTTON_PADDING + font.width(renameText()) + BUTTON_PADDING;
    return new Box(
        left + NAME_X + nameWidth + BUTTON_GAP, top + Y + BUTTON_Y, width, BUTTON_HEIGHT);
  }

  static void draw(
      GuiGraphics graphics,
      Font font,
      Box window,
      ControllerMenu menu,
      Mouse mouse,
      boolean editing) {
    Header header = menu.header();
    boolean owner = header.viewerIsOwner();
    Box square = colorSquare(window.x(), window.y());
    graphics.blitSprite(
        owner && mouse.hovers(square)
            ? ControllerSprites.COLOR_SQUARE_HOVER
            : ControllerSprites.COLOR_SQUARE,
        square.x(),
        square.y(),
        square.width(),
        square.height());
    graphics.fill(
        square.x() + SQUARE_INSET,
        square.y() + SQUARE_INSET,
        square.right() - SQUARE_INSET,
        square.bottom() - SQUARE_INSET,
        ChannelColors.argb(colorOf(header)));
    if (!editing) {
      drawName(graphics, font, window, header, mouse);
    }
    StatePill.draw(
        graphics,
        font,
        window.right() - PILL_RIGHT,
        window.y() + Y + PILL_Y,
        pillText(menu),
        ScreenColors.ONLINE);
  }

  private static void drawName(
      GuiGraphics graphics, Font font, Box window, Header header, Mouse mouse) {
    String name = FittedText.fit(font, Component.literal(header.name()), MAX_NAME);
    graphics.drawString(
        font, name, window.x() + NAME_X, window.y() + Y + NAME_Y, ScreenColors.TEXT, false);
    if (!header.viewerIsOwner()) {
      return;
    }
    Box button = renameBox(font, window.x(), window.y(), header);
    graphics.blitSprite(
        mouse.hovers(button) ? NodeSprites.BUTTON_HOVER : NodeSprites.BUTTON,
        button.x(),
        button.y(),
        button.width(),
        button.height());
    graphics.drawString(
        font,
        renameText(),
        button.x() + BUTTON_PADDING,
        button.y() + BUTTON_TEXT_Y,
        ScreenColors.TEXT,
        false);
  }

  static ChannelColor colorOf(Header header) {
    ChannelColor[] colors = ChannelColor.values();
    return header.color() >= 0 && header.color() < colors.length
        ? colors[header.color()]
        : colors[0];
  }

  private static Component pillText(ControllerMenu menu) {
    return Component.translatable(NodeScreenTexts.STATE_ONLINE)
        .append(SEPARATOR)
        .append(NetworkTexts.components(menu.components()));
  }

  private static Component renameText() {
    return Component.translatable(ControllerScreenTexts.RENAME);
  }
}
