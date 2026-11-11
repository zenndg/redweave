package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.client.ChannelColors;
import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.Direction;
import io.github.marcelofabianov.redweave.menu.NodeMenu;
import io.github.marcelofabianov.redweave.menu.NodeScreenTexts;
import java.util.Optional;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * The channel slots on the left: one row per slot, with the color, the direction and the signal. A
 * free slot is dashed. With more slots than fit, the mouse wheel scrolls (telas.md).
 */
final class SlotList {
  static final int ROW_HEIGHT = 24;
  static final int VISIBLE_ROWS = 4;
  private static final int GAP = 3;
  private static final int SWATCH_X = 6;
  private static final int SWATCH_Y = 7;
  private static final int SWATCH_SIZE = 10;
  private static final int TEXT_X = 21;
  private static final int FIRST_LINE = 4;
  private static final int SECOND_LINE = 13;
  private static final int TEXT_RIGHT_PADDING = 5;
  private static final int SCROLL_BAR_WIDTH = 2;

  private int scroll;

  /** The height the list takes on the screen. */
  static int height() {
    return VISIBLE_ROWS * (ROW_HEIGHT + GAP) - GAP;
  }

  void draw(GuiGraphics graphics, Font font, NodeView view, Box area, int selected) {
    int count = view.menu().slotCount();
    scroll = Mth.clamp(scroll, 0, Math.max(0, count - VISIBLE_ROWS));
    for (int row = 0; row < Math.min(VISIBLE_ROWS, count); row++) {
      int index = scroll + row;
      drawRow(graphics, font, view, rowBox(area, row), index, index == selected);
    }
    if (count > VISIBLE_ROWS) {
      drawScrollBar(graphics, area, count);
    }
  }

  /** The slot under the mouse, if any. */
  Optional<Integer> slotAt(Box area, int count, double mouseX, double mouseY) {
    for (int row = 0; row < Math.min(VISIBLE_ROWS, count); row++) {
      if (rowBox(area, row).contains(mouseX, mouseY)) {
        return Optional.of(scroll + row);
      }
    }
    return Optional.empty();
  }

  void scroll(double amount) {
    scroll -= (int) Math.signum(amount);
  }

  /** Keeps the selected slot in sight. */
  void reveal(int index) {
    if (index < scroll) {
      scroll = index;
    } else if (index >= scroll + VISIBLE_ROWS) {
      scroll = index - VISIBLE_ROWS + 1;
    }
  }

  private static Box rowBox(Box area, int row) {
    return new Box(area.x(), area.y() + row * (ROW_HEIGHT + GAP), area.width(), ROW_HEIGHT);
  }

  private static void drawRow(
      GuiGraphics graphics, Font font, NodeView view, Box box, int index, boolean selected) {
    NodeMenu menu = view.menu();
    Optional<ChannelSlot> slot = menu.slot(index);
    ResourceLocation sprite = spriteOf(slot.isPresent(), selected, view.hovers(box));
    graphics.blitSprite(sprite, box.x(), box.y(), box.width(), box.height());
    int textWidth = box.width() - TEXT_X - TEXT_RIGHT_PADDING;
    if (slot.isEmpty()) {
      drawFree(graphics, font, box, textWidth, view.editable());
      return;
    }
    drawSwatch(graphics, box, slot.get());
    Component first = ChannelNames.of(slot.get().channel(), menu.channelName(slot.get().channel()));
    Component second =
        Component.translatable(
            NodeScreenTexts.SLOT_LINE, directionOf(slot.get()), menu.signal(index));
    text(graphics, font, first, box, FIRST_LINE, textWidth, ScreenColors.TEXT);
    text(graphics, font, second, box, SECOND_LINE, textWidth, ScreenColors.LABEL);
  }

  private static void drawFree(
      GuiGraphics graphics, Font font, Box box, int textWidth, boolean editable) {
    Component first = Component.translatable(NodeScreenTexts.FREE_SLOT);
    text(graphics, font, first, box, FIRST_LINE, textWidth, ScreenColors.TEXT);
    if (editable) {
      Component second = Component.translatable(NodeScreenTexts.CLICK_TO_USE);
      text(graphics, font, second, box, SECOND_LINE, textWidth, ScreenColors.LABEL);
    }
  }

  private static void drawSwatch(GuiGraphics graphics, Box box, ChannelSlot slot) {
    int x = box.x() + SWATCH_X;
    int y = box.y() + SWATCH_Y;
    graphics.fill(x - 1, y - 1, x + SWATCH_SIZE + 1, y + SWATCH_SIZE + 1, ScreenColors.SWATCH_EDGE);
    graphics.fill(x, y, x + SWATCH_SIZE, y + SWATCH_SIZE, ChannelColors.argb(slot.channel()));
  }

  private static void text(
      GuiGraphics graphics, Font font, Component text, Box box, int line, int width, int color) {
    String shown = FittedText.fit(font, text, width);
    graphics.drawString(font, shown, box.x() + TEXT_X, box.y() + line, color, false);
  }

  static Component directionOf(ChannelSlot slot) {
    String key =
        slot.direction() == Direction.INPUT ? NodeScreenTexts.INPUT : NodeScreenTexts.OUTPUT;
    return Component.translatable(key);
  }

  private static ResourceLocation spriteOf(boolean used, boolean selected, boolean hover) {
    if (!used) {
      return hover ? NodeSprites.SLOT_FREE_HOVER : NodeSprites.SLOT_FREE;
    }
    if (selected) {
      return NodeSprites.SLOT_SELECTED;
    }
    return hover ? NodeSprites.SLOT_HOVER : NodeSprites.SLOT;
  }

  private void drawScrollBar(GuiGraphics graphics, Box area, int count) {
    int x = area.right() + 1;
    int barHeight = area.height() * VISIBLE_ROWS / count;
    int top = area.y() + (area.height() - barHeight) * scroll / (count - VISIBLE_ROWS);
    graphics.fill(x, area.y(), x + SCROLL_BAR_WIDTH, area.bottom(), ScreenColors.SWATCH_EDGE);
    graphics.fill(x, top, x + SCROLL_BAR_WIDTH, top + barHeight, ScreenColors.DIM);
  }
}
