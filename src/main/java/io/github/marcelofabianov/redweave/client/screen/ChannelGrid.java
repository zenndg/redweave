package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.client.ChannelColors;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.menu.ControllerMenu;
import io.github.marcelofabianov.redweave.menu.ControllerScreenRows.ChannelLine;
import io.github.marcelofabianov.redweave.menu.ControllerScreenTexts;
import java.util.Optional;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * The Signals tab: the 16 channels in two columns of 8, each with its color, number and name, how
 * many inputs and outputs use it and its value. Unused channels are dimmer, and the owner renames a
 * channel with a click on its name (rede-e-canais.md).
 */
final class ChannelGrid {
  private static final int ROWS_PER_COLUMN = 8;
  private static final int COLUMNS = 2;
  private static final int ROW_HEIGHT = 17;
  private static final int ROW_STEP = 18;
  private static final int COLUMN_GAP = 4;
  private static final int SWATCH_X = 5;
  private static final int SWATCH_Y = 4;
  private static final int SWATCH = 8;
  private static final int NAME_X = 17;
  private static final int TEXT_Y = 5;
  private static final int VALUE_RIGHT = 6;
  private static final int USE_RIGHT = 24;
  private static final int NAME_WIDTH = 74;
  private static final int NAME_FIELD_Y = 2;
  private static final int NAME_FIELD_HEIGHT = 14;

  private ChannelGrid() {}

  static void draw(GuiGraphics graphics, Font font, Box area, ControllerMenu menu, Mouse mouse) {
    for (ChannelColor channel : ChannelColor.values()) {
      if (channel.ordinal() < menu.channels().size()) {
        drawRow(graphics, font, rowBox(area, channel), channel, menu);
      }
    }
  }

  private static void drawRow(
      GuiGraphics graphics, Font font, Box box, ChannelColor channel, ControllerMenu menu) {
    ChannelLine line = menu.channels().get(channel.ordinal());
    graphics.blitSprite(ControllerSprites.CHANNEL_ROW, box.x(), box.y(), box.width(), box.height());
    int swatchX = box.x() + SWATCH_X;
    int swatchY = box.y() + SWATCH_Y;
    graphics.fill(
        swatchX, swatchY, swatchX + SWATCH, swatchY + SWATCH, ChannelColors.argb(channel));
    int color = line.inUse() ? ScreenColors.TEXT : ScreenColors.DIM;
    int y = box.y() + TEXT_Y;
    String name = FittedText.fit(font, ChannelNames.of(channel, line.name()), NAME_WIDTH);
    graphics.drawString(font, name, box.x() + NAME_X, y, color, false);
    String value = String.valueOf(menu.channelValue(channel));
    int valueX = box.right() - VALUE_RIGHT - font.width(value);
    graphics.drawString(font, value, valueX, y, color, false);
    if (line.inUse()) {
      Component use =
          Component.translatable(ControllerScreenTexts.SLOT_USE, line.inputs(), line.outputs());
      int useX = box.right() - USE_RIGHT - font.width(use);
      graphics.drawString(font, use, useX, y, ScreenColors.LABEL, false);
    }
  }

  /** Where a channel is: white to gray on the left, light gray to black on the right. */
  static Box rowBox(Box area, ChannelColor channel) {
    int column = channel.ordinal() / ROWS_PER_COLUMN;
    int row = channel.ordinal() % ROWS_PER_COLUMN;
    int width = (area.width() - COLUMN_GAP * (COLUMNS - 1)) / COLUMNS;
    return new Box(
        area.x() + column * (width + COLUMN_GAP), area.y() + row * ROW_STEP, width, ROW_HEIGHT);
  }

  /** The channel whose name is under the mouse, which its owner clicks to rename. */
  static Optional<ChannelColor> nameAt(Box area, double mouseX, double mouseY) {
    for (ChannelColor channel : ChannelColor.values()) {
      if (nameBox(rowBox(area, channel)).contains(mouseX, mouseY)) {
        return Optional.of(channel);
      }
    }
    return Optional.empty();
  }

  /** Where the name of a channel row is; the field covers only the name, not the number. */
  static Box nameBox(Box row) {
    return new Box(row.x() + NAME_X, row.y() + NAME_FIELD_Y, NAME_WIDTH, NAME_FIELD_HEIGHT);
  }
}
