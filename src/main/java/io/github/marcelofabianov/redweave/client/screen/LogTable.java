package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.client.ChannelColors;
import io.github.marcelofabianov.redweave.domain.network.Direction;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.menu.ChannelNames;
import io.github.marcelofabianov.redweave.menu.ControllerMenu;
import io.github.marcelofabianov.redweave.menu.ControllerScreenLog.LogLine;
import io.github.marcelofabianov.redweave.menu.ControllerScreenTexts;
import io.github.marcelofabianov.redweave.menu.NodeScreenTexts;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

/**
 * The Log tab: one thin row per change, newest on top, as `2026-11-11 14:32:05 Iron Door 15 Red
 * Output 12`, with the date and time of the server in grey and the value on the right (answers 147,
 * 148 and 214). The mouse wheel scrolls when the rows do not fit.
 */
final class LogTable {
  private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
  private static final int NANOS = 0;
  private static final int ROW_HEIGHT = 12;
  private static final int GAP = 1;
  private static final int TEXT_Y = 2;
  private static final int TIME_X = 4;
  private static final int NODE_X = 116;
  private static final int SWATCH_X = 184;
  private static final int SWATCH_Y = 3;
  private static final int SWATCH = 6;
  private static final int CHANNEL_X = 194;
  private static final int DIRECTION_X = 262;
  private static final int VALUE_RIGHT = 4;
  private static final int COLUMN_GAP = 6;

  private int scroll;

  void draw(GuiGraphics graphics, Font font, Box area, ControllerMenu menu) {
    List<LogLine> lines = menu.log();
    if (lines.isEmpty()) {
      Component empty = Component.translatable(ControllerScreenTexts.NO_LOG);
      int x = area.x() + (area.width() - font.width(empty)) / 2;
      graphics.drawString(font, empty, x, area.y() + area.height() / 2, ScreenColors.LABEL, false);
      return;
    }
    scroll = Mth.clamp(scroll, 0, Math.max(0, lines.size() - 1));
    int y = area.y();
    for (int index = scroll; index < lines.size() && y + ROW_HEIGHT <= area.bottom(); index++) {
      drawRow(graphics, font, new Box(area.x(), y, area.width(), ROW_HEIGHT), lines.get(index));
      y += ROW_HEIGHT + GAP;
    }
  }

  private static void drawRow(GuiGraphics graphics, Font font, Box box, LogLine line) {
    graphics.blitSprite(ControllerSprites.LOG_ROW, box.x(), box.y(), box.width(), box.height());
    int y = box.y() + TEXT_Y;
    graphics.drawString(font, timeOf(line), box.x() + TIME_X, y, ScreenColors.LABEL, false);
    String node =
        FittedText.fit(font, Component.literal(line.node()), SWATCH_X - NODE_X - COLUMN_GAP);
    graphics.drawString(font, node, box.x() + NODE_X, y, ScreenColors.TEXT, false);
    ChannelColor channel = channelOf(line);
    int swatchX = box.x() + SWATCH_X;
    int swatchY = box.y() + SWATCH_Y;
    graphics.fill(
        swatchX, swatchY, swatchX + SWATCH, swatchY + SWATCH, ChannelColors.argb(channel));
    String shown =
        FittedText.fit(
            font,
            ChannelNames.of(channel, line.channelName()),
            DIRECTION_X - CHANNEL_X - COLUMN_GAP);
    graphics.drawString(font, shown, box.x() + CHANNEL_X, y, ScreenColors.TEXT, false);
    graphics.drawString(
        font, directionOf(line), box.x() + DIRECTION_X, y, ScreenColors.TEXT, false);
    String value = String.valueOf(line.value());
    int valueX = box.right() - VALUE_RIGHT - font.width(value);
    graphics.drawString(font, value, valueX, y, ScreenColors.TEXT, false);
  }

  /** The time the server wrote as seconds of its own clock, shown as it was there (148). */
  private static String timeOf(LogLine line) {
    return LocalDateTime.ofEpochSecond(line.time(), NANOS, ZoneOffset.UTC).format(TIME);
  }

  /** The numbers come from the server, so one outside the colors shows the first one. */
  private static ChannelColor channelOf(LogLine line) {
    ChannelColor[] colors = ChannelColor.values();
    return line.channel() >= 0 && line.channel() < colors.length
        ? colors[line.channel()]
        : colors[0];
  }

  private static Component directionOf(LogLine line) {
    boolean input = line.direction() == Direction.INPUT.ordinal();
    return Component.translatable(input ? NodeScreenTexts.INPUT : NodeScreenTexts.OUTPUT);
  }

  void scroll(double amount) {
    scroll -= (int) Math.signum(amount);
    scroll = Math.max(0, scroll);
  }
}
