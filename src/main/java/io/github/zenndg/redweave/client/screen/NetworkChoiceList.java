package io.github.zenndg.redweave.client.screen;

import io.github.zenndg.redweave.client.ChannelColors;
import io.github.zenndg.redweave.domain.signal.ChannelColor;
import io.github.zenndg.redweave.menu.NetworkTexts;
import io.github.zenndg.redweave.menu.NodeScreenChoices;
import io.github.zenndg.redweave.menu.NodeScreenTexts;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

/**
 * The network choice of the Node Screen: _docs/imagens/telas/tela-do-no-escolha-de-rede.png. Each
 * row has the color, the name and the node count, and the last used one is copper (answers 199, 202
 * and 203). It scrolls when the rows do not fit.
 */
final class NetworkChoiceList {
  private static final int LABEL_Y = 4;
  private static final int ROWS_Y = 16;
  private static final int ROW_HEIGHT = 22;
  private static final int ROW_GAP = 2;
  private static final int ROW_STEP = ROW_HEIGHT + ROW_GAP;
  private static final int SIDE = 4;
  private static final int SWATCH_X = 8;
  private static final int SWATCH_SIZE = 8;
  private static final int NAME_X = 22;
  private static final int TEXT_Y = 7;
  private static final int COUNT_RIGHT = 8;
  private static final int NAME_GAP = 6;
  private static final int ICON_SIZE = 16;
  private static final int ICON_Y = 26;
  private static final int EMPTY_TEXT_Y = 50;
  private static final int LINE_HEIGHT = 11;

  private int first;

  void draw(
      GuiGraphics graphics,
      Font font,
      Box area,
      List<NodeScreenChoices.Row> rows,
      double mx,
      double my) {
    graphics.blitSprite(NodeSprites.PANEL, area.x(), area.y(), area.width(), area.height());
    Component label = Component.translatable(NodeScreenTexts.CHOOSE_NETWORK);
    graphics.drawString(
        font, label, area.x() + SIDE, area.y() + LABEL_Y, ScreenColors.LABEL, false);
    if (rows.isEmpty()) {
      drawEmpty(graphics, font, area);
      return;
    }
    first = Math.max(0, Math.min(first, rows.size() - visible(area)));
    for (int shown = 0; shown < Math.min(visible(area), rows.size() - first); shown++) {
      Box row = rowBox(area, shown);
      drawRow(graphics, font, row, rows.get(first + shown), row.contains(mx, my));
    }
  }

  /** The position in the list of the row under the mouse, as the server keeps it. */
  Optional<Integer> rowAt(Box area, int count, double mouseX, double mouseY) {
    for (int shown = 0; shown < Math.min(visible(area), count - first); shown++) {
      if (rowBox(area, shown).contains(mouseX, mouseY)) {
        return Optional.of(first + shown);
      }
    }
    return Optional.empty();
  }

  void scroll(double amount, Box area, int count) {
    int step = amount > 0 ? -1 : 1;
    first = Math.max(0, Math.min(first + step, count - visible(area)));
  }

  private static int visible(Box area) {
    return Math.max(1, (area.height() - ROWS_Y) / ROW_STEP);
  }

  private static Box rowBox(Box area, int shown) {
    return new Box(
        area.x() + SIDE, area.y() + ROWS_Y + shown * ROW_STEP, area.width() - 2 * SIDE, ROW_HEIGHT);
  }

  private static void drawRow(
      GuiGraphics graphics, Font font, Box row, NodeScreenChoices.Row choice, boolean hover) {
    graphics.blitSprite(
        spriteOf(choice.lastUsed(), hover), row.x(), row.y(), row.width(), row.height());
    int swatchY = row.y() + (row.height() - SWATCH_SIZE) / 2;
    graphics.fill(
        row.x() + SWATCH_X,
        swatchY,
        row.x() + SWATCH_X + SWATCH_SIZE,
        swatchY + SWATCH_SIZE,
        colorOf(choice.color()));
    Component count = countOf(choice.components());
    int countX = row.right() - COUNT_RIGHT - font.width(count);
    graphics.drawString(font, count, countX, row.y() + TEXT_Y, ScreenColors.LABEL, false);
    int nameWidth = countX - NAME_GAP - (row.x() + NAME_X);
    String name = FittedText.fit(font, Component.literal(choice.name()), nameWidth);
    graphics.drawString(font, name, row.x() + NAME_X, row.y() + TEXT_Y, ScreenColors.TEXT, false);
  }

  /** The empty list tells how to make a network (answer 199 and pillar 5 of ux.md). */
  private static void drawEmpty(GuiGraphics graphics, Font font, Box area) {
    int center = area.x() + area.width() / 2;
    graphics.blitSprite(
        NodeSprites.ICON_NO_NETWORK,
        center - ICON_SIZE / 2,
        area.y() + ICON_Y,
        ICON_SIZE,
        ICON_SIZE);
    Component text = Component.translatable(NodeScreenTexts.NO_NETWORK_ONLINE);
    List<FormattedCharSequence> lines = font.split(text, area.width() - 2 * SIDE);
    for (int line = 0; line < lines.size(); line++) {
      FormattedCharSequence shown = lines.get(line);
      graphics.drawString(
          font,
          shown,
          center - font.width(shown) / 2,
          area.y() + EMPTY_TEXT_Y + line * LINE_HEIGHT,
          ScreenColors.LABEL,
          false);
    }
  }

  private static Component countOf(int components) {
    return NetworkTexts.components(components);
  }

  private static int colorOf(int color) {
    ChannelColor[] colors = ChannelColor.values();
    return color >= 0 && color < colors.length
        ? ChannelColors.argb(colors[color])
        : ScreenColors.NO_NETWORK;
  }

  private static ResourceLocation spriteOf(boolean lastUsed, boolean hover) {
    if (lastUsed) {
      return hover ? NodeSprites.NETWORK_ROW_LAST_USED_HOVER : NodeSprites.NETWORK_ROW_LAST_USED;
    }
    return hover ? NodeSprites.NETWORK_ROW_HOVER : NodeSprites.NETWORK_ROW;
  }
}
