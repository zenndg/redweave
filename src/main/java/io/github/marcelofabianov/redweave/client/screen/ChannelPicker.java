package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.client.ChannelColors;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import java.util.Optional;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

/**
 * The 16 channels of the network in two rows of 8, each with its color and number, where a
 * connection of a module picks its channel (answers 400 and 404).
 */
final class ChannelPicker {
  private static final int COLUMNS = 8;
  private static final int CELL_WIDTH = 22;
  private static final int CELL_HEIGHT = 14;
  private static final int GAP = 1;
  private static final int SQUARE = 5;
  private static final int SQUARE_X = 3;
  private static final int SQUARE_Y = 4;
  private static final int NUMBER_X = 10;
  private static final int NUMBER_Y = 3;
  private static final String NUMBER = "%02d";

  private ChannelPicker() {}

  static void draw(
      GuiGraphics graphics,
      Font font,
      int x,
      int y,
      Optional<ChannelColor> chosen,
      Mouse mouse,
      boolean editable) {
    for (ChannelColor channel : ChannelColor.values()) {
      Box cell = cellOf(x, y, channel);
      boolean picked = chosen.equals(Optional.of(channel));
      boolean hover = editable && mouse.hovers(cell);
      graphics.blitSprite(
          picked
              ? NodeSprites.SWATCH_SELECTED
              : hover ? NodeSprites.SWATCH_HOVER : NodeSprites.SWATCH,
          cell.x(),
          cell.y(),
          cell.width(),
          cell.height());
      int squareX = cell.x() + SQUARE_X;
      int squareY = cell.y() + SQUARE_Y;
      graphics.fill(
          squareX, squareY, squareX + SQUARE, squareY + SQUARE, ChannelColors.argb(channel));
      String number = String.format(NUMBER, channel.ordinal() + 1);
      int color = editable || picked ? ScreenColors.TEXT : ScreenColors.DIM;
      graphics.drawString(font, number, cell.x() + NUMBER_X, cell.y() + NUMBER_Y, color, false);
    }
  }

  /** The channel under the mouse, if any. */
  static Optional<ChannelColor> at(int x, int y, double mouseX, double mouseY) {
    for (ChannelColor channel : ChannelColor.values()) {
      if (cellOf(x, y, channel).contains(mouseX, mouseY)) {
        return Optional.of(channel);
      }
    }
    return Optional.empty();
  }

  private static Box cellOf(int x, int y, ChannelColor channel) {
    int column = channel.ordinal() % COLUMNS;
    int row = channel.ordinal() / COLUMNS;
    return new Box(
        x + column * (CELL_WIDTH + GAP), y + row * (CELL_HEIGHT + GAP), CELL_WIDTH, CELL_HEIGHT);
  }
}
