package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.menu.ControllerMenu;
import io.github.marcelofabianov.redweave.menu.ControllerScreenRows.NodeLine;
import io.github.marcelofabianov.redweave.menu.ControllerScreenRows.SlotLine;
import io.github.marcelofabianov.redweave.menu.ControllerScreenTexts;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * The Nodes tab: one row per node, in the order they joined (answer 216), with the name, the signal
 * and, for the owner, the position (answers 149 and 179). A click opens the slots of a row, one row
 * at a time (answer 218), and the owner renames a node with a click on its name (answer 217). The
 * mouse wheel scrolls when the rows do not fit.
 */
final class NodeTable {
  private static final int NONE = -1;
  private static final int HEADER_HEIGHT = 14;
  private static final int HEADER_GAP = 2;
  private static final int ROW_HEIGHT = 20;
  private static final int SLOT_HEIGHT = 12;
  private static final int GAP = 1;
  private static final int TEXT_Y = 6;
  private static final int SLOT_TEXT_Y = 2;
  private static final int HEADER_TEXT_Y = 3;
  private static final int ICON_X = 5;
  private static final int ICON_Y = 6;
  private static final int ICON_SIZE = 8;
  private static final int NAME_X = 16;
  private static final int NAME_FIELD_Y = 3;
  private static final int NAME_FIELD_HEIGHT = 14;
  private static final int SIGNAL_X = 150;
  private static final int POSITION_X = 196;
  private static final int COLUMN_GAP = 6;
  private static final int SLOT_INDENT = 14;
  private static final int SLOT_ICON_X = 4;
  private static final int SLOT_ICON_Y = 2;
  private static final int SLOT_SWATCH_X = 15;
  private static final int SLOT_SWATCH_Y = 3;
  private static final int SLOT_SWATCH = 6;
  private static final int SLOT_TEXT_X = 25;
  private static final String POSITION = "%d, %d, %d";

  private int scroll;
  private int expanded = NONE;

  /**
   * One line of the list: a node row, or a line under the open row, with one of its enabled slots
   * or, with none, the notice `No active channel` (answer 234).
   */
  record Item(int row, Optional<SlotLine> slot, boolean under) {
    int height() {
      return under ? SLOT_HEIGHT : ROW_HEIGHT;
    }
  }

  /** An item where it is drawn now. */
  record Placed(Item item, Box box) {}

  void draw(GuiGraphics graphics, Font font, Box area, ControllerMenu menu, Mouse mouse) {
    drawHeader(graphics, font, area, menu.header().viewerIsOwner());
    if (menu.nodes().isEmpty()) {
      Component empty = Component.translatable(ControllerScreenTexts.NO_NODES);
      int x = area.x() + (area.width() - font.width(empty)) / 2;
      int y = area.y() + area.height() / 2;
      graphics.drawString(font, empty, x, y, ScreenColors.LABEL, false);
      return;
    }
    for (Placed placed : placed(area, menu)) {
      if (placed.item().under()) {
        drawUnder(graphics, font, placed, menu);
      } else {
        drawRow(graphics, font, placed, menu, mouse);
      }
    }
  }

  private static void drawHeader(GuiGraphics graphics, Font font, Box area, boolean owner) {
    graphics.blitSprite(
        ControllerSprites.TABLE_HEADER, area.x(), area.y(), area.width(), HEADER_HEIGHT);
    int y = area.y() + HEADER_TEXT_Y;
    label(graphics, font, ControllerScreenTexts.COLUMN_NODE, area.x() + NAME_X, y);
    label(graphics, font, ControllerScreenTexts.COLUMN_SIGNAL, area.x() + SIGNAL_X, y);
    if (owner) {
      label(graphics, font, ControllerScreenTexts.COLUMN_POSITION, area.x() + POSITION_X, y);
    }
  }

  private static void label(GuiGraphics graphics, Font font, String key, int x, int y) {
    graphics.drawString(font, Component.translatable(key), x, y, ScreenColors.LABEL, false);
  }

  private void drawRow(
      GuiGraphics graphics, Font font, Placed placed, ControllerMenu menu, Mouse mouse) {
    Box box = placed.box();
    int row = placed.item().row();
    NodeLine node = menu.nodes().get(row);
    boolean open = row == expanded;
    graphics.blitSprite(
        spriteOf(open, mouse.hovers(box)), box.x(), box.y(), box.width(), box.height());
    graphics.blitSprite(
        open ? ControllerSprites.ICON_COLLAPSE : ControllerSprites.ICON_EXPAND,
        box.x() + ICON_X,
        box.y() + ICON_Y,
        ICON_SIZE,
        ICON_SIZE);
    int color = NodeStates.colorOf(menu.nodeState(row));
    int y = box.y() + TEXT_Y;
    String name =
        FittedText.fit(font, Component.literal(node.name()), SIGNAL_X - NAME_X - COLUMN_GAP);
    graphics.drawString(font, name, box.x() + NAME_X, y, color, false);
    graphics.drawString(
        font, String.valueOf(menu.nodeSignal(row)), box.x() + SIGNAL_X, y, color, false);
    node.position()
        .ifPresent(pos ->
            graphics.drawString(
                font,
                String.format(POSITION, pos.getX(), pos.getY(), pos.getZ()),
                box.x() + POSITION_X,
                y,
                color,
                false));
  }

  private static ResourceLocation spriteOf(boolean open, boolean hover) {
    if (open) {
      return ControllerSprites.NODE_ROW_EXPANDED;
    }
    return hover ? ControllerSprites.NODE_ROW_HOVER : ControllerSprites.NODE_ROW;
  }

  private static void drawUnder(
      GuiGraphics graphics, Font font, Placed placed, ControllerMenu menu) {
    Box box = placed.box();
    graphics.blitSprite(ControllerSprites.SLOT_LINE, box.x(), box.y(), box.width(), box.height());
    Optional<SlotLine> found = placed.item().slot();
    if (found.isEmpty()) {
      Component none = Component.translatable(ControllerScreenTexts.NO_ACTIVE_CHANNEL);
      graphics.drawString(
          font, none, box.x() + SLOT_ICON_X, box.y() + SLOT_TEXT_Y, ScreenColors.DIM, false);
      return;
    }
    SlotLine slot = found.get();
    graphics.blitSprite(
        SlotLines.isInput(slot) ? ControllerSprites.ICON_INPUT : ControllerSprites.ICON_OUTPUT,
        box.x() + SLOT_ICON_X,
        box.y() + SLOT_ICON_Y,
        ICON_SIZE,
        ICON_SIZE);
    int swatchX = box.x() + SLOT_SWATCH_X;
    int swatchY = box.y() + SLOT_SWATCH_Y;
    graphics.fill(
        swatchX, swatchY, swatchX + SLOT_SWATCH, swatchY + SLOT_SWATCH, SlotLines.argb(slot));
    graphics.drawString(
        font,
        SlotLines.text(slot, menu),
        box.x() + SLOT_TEXT_X,
        box.y() + SLOT_TEXT_Y,
        ScreenColors.LABEL,
        false);
  }

  /** The items in view, from the first scrolled one down to the bottom of the area. */
  List<Placed> placed(Box area, ControllerMenu menu) {
    List<Item> items = items(menu);
    scroll = Mth.clamp(scroll, 0, Math.max(0, items.size() - 1));
    List<Placed> placed = new ArrayList<>();
    int y = area.y() + HEADER_HEIGHT + HEADER_GAP;
    for (int index = scroll; index < items.size(); index++) {
      Item item = items.get(index);
      if (y + item.height() > area.bottom()) {
        break;
      }
      int indent = item.under() ? SLOT_INDENT : 0;
      placed.add(
          new Placed(item, new Box(area.x() + indent, y, area.width() - indent, item.height())));
      y += item.height() + GAP;
    }
    return placed;
  }

  private List<Item> items(ControllerMenu menu) {
    if (expanded >= menu.nodes().size()) {
      expanded = NONE;
    }
    List<Item> items = new ArrayList<>();
    for (int row = 0; row < menu.nodes().size(); row++) {
      items.add(new Item(row, Optional.empty(), false));
      if (row == expanded) {
        int open = row;
        List<SlotLine> slots = menu.nodes().get(row).slots();
        slots.forEach(slot -> items.add(new Item(open, Optional.of(slot), true)));
        if (slots.isEmpty()) {
          items.add(new Item(open, Optional.empty(), true));
        }
      }
    }
    return items;
  }

  /** The node row under the mouse, if any. */
  Optional<Placed> rowAt(Box area, ControllerMenu menu, double mouseX, double mouseY) {
    return placed(area, menu).stream()
        .filter(placed -> !placed.item().under())
        .filter(placed -> placed.box().contains(mouseX, mouseY))
        .findFirst();
  }

  /** Where the name of a row is, which its owner clicks to rename (answer 217). */
  static Box nameBox(Box row) {
    return new Box(
        row.x() + NAME_X - NAME_FIELD_Y,
        row.y() + NAME_FIELD_Y,
        SIGNAL_X - NAME_X,
        NAME_FIELD_HEIGHT);
  }

  /** Opens a row and closes the one open before, or closes it when it was open (218). */
  void toggle(int row) {
    expanded = expanded == row ? NONE : row;
  }

  void scroll(double amount) {
    scroll -= (int) Math.signum(amount);
    scroll = Math.max(0, scroll);
  }
}
