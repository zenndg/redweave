package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.client.ChannelColors;
import io.github.marcelofabianov.redweave.domain.network.NetworkState;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.menu.NodeScreenTexts;
import io.github.marcelofabianov.redweave.menu.TerminalScreenRows.NetworkLine;
import io.github.marcelofabianov.redweave.menu.TerminalScreenTexts;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

/**
 * The list of the Network Terminal: one row per network, in the order the server sent, with the
 * color, the name, the state and the nodes (answers 131 and 138). The mouse wheel scrolls it, and
 * without networks it tells how to make one (answer 256).
 */
final class TerminalList {
  private static final int SIDE = 4;
  private static final int ROW_HEIGHT = 22;
  private static final int ROW_GAP = 2;
  private static final int ROW_STEP = ROW_HEIGHT + ROW_GAP;
  private static final int FIELD_INSET = 2;
  private static final int FIELD_Y = 3;
  private static final int FIELD_HEIGHT = 16;
  private static final int SWATCH_X = 8;
  private static final int SWATCH = 8;
  private static final int NAME_X = 22;
  private static final int NAME_WIDTH = 110;
  private static final int PILL_RIGHT = 210;
  private static final int PILL_Y = 4;
  private static final int TEXT_Y = 7;
  private static final int COUNT_RIGHT = 8;
  private static final int LINE_HEIGHT = 11;
  private static final int SINGLE = 1;

  private int first;

  void draw(GuiGraphics graphics, Font font, Box area, List<NetworkLine> rows, Selection chosen) {
    if (rows.isEmpty()) {
      drawEmpty(graphics, font, area);
      return;
    }
    first = Math.max(0, Math.min(first, rows.size() - visible(area)));
    for (int shown = 0; shown < Math.min(visible(area), rows.size() - first); shown++) {
      int row = first + shown;
      drawRow(graphics, font, rowBox(area, shown), rows.get(row), chosen.is(row));
    }
  }

  /** The row under the mouse, by its position in the list the server keeps. */
  Optional<Integer> rowAt(Box area, int count, double mouseX, double mouseY) {
    for (int shown = 0; shown < Math.min(visible(area), count - first); shown++) {
      if (rowBox(area, shown).contains(mouseX, mouseY)) {
        return Optional.of(first + shown);
      }
    }
    return Optional.empty();
  }

  /** Where the name of a row is drawn, for the double click that renames it (answer 252). */
  Optional<Box> nameBox(Box area, int row) {
    int shown = row - first;
    if (shown < 0 || shown >= visible(area)) {
      return Optional.empty();
    }
    Box box = rowBox(area, shown);
    return Optional.of(
        new Box(box.x() + NAME_X - FIELD_INSET, box.y() + FIELD_Y, NAME_WIDTH, FIELD_HEIGHT));
  }

  void scroll(double amount, Box area, int count) {
    int step = amount > 0 ? -1 : 1;
    first = Math.max(0, Math.min(first + step, count - visible(area)));
  }

  private static int visible(Box area) {
    return Math.max(1, (area.height() - SIDE) / ROW_STEP);
  }

  private static Box rowBox(Box area, int shown) {
    return new Box(
        area.x() + SIDE, area.y() + SIDE + shown * ROW_STEP, area.width() - 2 * SIDE, ROW_HEIGHT);
  }

  private static void drawRow(
      GuiGraphics graphics, Font font, Box row, NetworkLine line, boolean selected) {
    ResourceLocation sprite =
        selected ? TerminalSprites.NETWORK_ROW_SELECTED : NodeSprites.NETWORK_ROW;
    graphics.blitSprite(sprite, row.x(), row.y(), row.width(), row.height());
    int swatchY = row.y() + (row.height() - SWATCH) / 2;
    int swatchX = row.x() + SWATCH_X;
    graphics.fill(swatchX, swatchY, swatchX + SWATCH, swatchY + SWATCH, colorOf(line.color()));
    String name = FittedText.fit(font, Component.literal(line.name()), NAME_WIDTH);
    graphics.drawString(font, name, row.x() + NAME_X, row.y() + TEXT_Y, ScreenColors.TEXT, false);
    NetworkState state = stateOf(line.state());
    StatePill.draw(
        graphics, font, row.x() + PILL_RIGHT, row.y() + PILL_Y, textOf(state), colorOf(state));
    Component count = nodesOf(line.nodes());
    int countX = row.right() - COUNT_RIGHT - font.width(count);
    graphics.drawString(font, count, countX, row.y() + TEXT_Y, ScreenColors.LABEL, false);
  }

  private static void drawEmpty(GuiGraphics graphics, Font font, Box area) {
    Component text = Component.translatable(TerminalScreenTexts.NO_NETWORKS);
    List<FormattedCharSequence> lines = font.split(text, area.width() - 2 * SIDE);
    int y = area.y() + (area.height() - lines.size() * LINE_HEIGHT) / 2;
    for (FormattedCharSequence line : lines) {
      int x = area.x() + (area.width() - font.width(line)) / 2;
      graphics.drawString(font, line, x, y, ScreenColors.LABEL, false);
      y += LINE_HEIGHT;
    }
  }

  /** The numbers come from the server, so one outside the states shows the safest one. */
  static NetworkState stateOf(int state) {
    NetworkState[] states = NetworkState.values();
    return state >= 0 && state < states.length ? states[state] : NetworkState.OFFLINE;
  }

  private static Component textOf(NetworkState state) {
    return Component.translatable(
        switch (state) {
          case ONLINE -> NodeScreenTexts.STATE_ONLINE;
          case OFFLINE -> NodeScreenTexts.STATE_OFFLINE;
          case UNPLACED -> TerminalScreenTexts.STATE_UNPLACED;
        });
  }

  /** Online in green, offline in grey and unplaced in amber, as on the mockup (answer 252). */
  private static int colorOf(NetworkState state) {
    return switch (state) {
      case ONLINE -> ScreenColors.ONLINE;
      case OFFLINE -> ScreenColors.OFFLINE;
      case UNPLACED -> ScreenColors.UNLOADED;
    };
  }

  private static int colorOf(int color) {
    ChannelColor[] colors = ChannelColor.values();
    return color >= 0 && color < colors.length
        ? ChannelColors.argb(colors[color])
        : ScreenColors.NO_NETWORK;
  }

  private static Component nodesOf(int nodes) {
    String key = nodes == SINGLE ? NodeScreenTexts.NODE_COUNT_ONE : NodeScreenTexts.NODE_COUNT_MANY;
    return Component.translatable(key, nodes);
  }

  /** The selected row, which only the client keeps. */
  record Selection(Optional<Integer> row) {
    boolean is(int other) {
      return row.filter(selected -> selected == other).isPresent();
    }
  }
}
