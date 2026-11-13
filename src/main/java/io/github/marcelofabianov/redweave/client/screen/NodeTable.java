package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.domain.network.NodeKind;
import io.github.marcelofabianov.redweave.domain.network.NodeState;
import io.github.marcelofabianov.redweave.menu.ControllerKinds;
import io.github.marcelofabianov.redweave.menu.ControllerMenu;
import io.github.marcelofabianov.redweave.menu.ControllerScreenRows.NodeLine;
import io.github.marcelofabianov.redweave.menu.ControllerScreenRows.SlotLine;
import io.github.marcelofabianov.redweave.menu.ControllerScreenTexts;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * The Nodes tab: one row per node, in the order they joined (answer 216), with the name, the kind,
 * the signal and the state, only the state in its color (answers 179, 335 to 337). A click opens a
 * row, one at a time (answer 218): the position, for the owner, and then the enabled slots (answers
 * 149, 234 and 340). The owner renames a node with a click on its name (answer 217). The mouse
 * wheel scrolls when the rows do not fit.
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
  private static final int TYPE_X = 140;
  private static final int SIGNAL_X = 190;
  private static final int STATE_X = 228;
  private static final int COLUMN_GAP = 6;
  private static final int RIGHT_PADDING = 4;
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

  /** What a line of the list shows: a node, or, under the open one, one of its lines. */
  enum Line {
    NODE,
    POSITION,
    SLOT,
    NO_CHANNEL
  }

  /** One line of the list, with the row of its node and, on a slot line, the slot. */
  record Item(int row, Line line, Optional<SlotLine> slot) {
    boolean under() {
      return line != Line.NODE;
    }

    int height() {
      return under() ? SLOT_HEIGHT : ROW_HEIGHT;
    }
  }

  /** An item where it is drawn now. */
  record Placed(Item item, Box box) {}

  void draw(GuiGraphics graphics, Font font, Box area, ControllerMenu menu, Mouse mouse) {
    drawHeader(graphics, font, area);
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

  private static void drawHeader(GuiGraphics graphics, Font font, Box area) {
    graphics.blitSprite(
        ControllerSprites.TABLE_HEADER, area.x(), area.y(), area.width(), HEADER_HEIGHT);
    int y = area.y() + HEADER_TEXT_Y;
    label(graphics, font, ControllerScreenTexts.COLUMN_NODE, area.x() + NAME_X, y);
    label(graphics, font, ControllerScreenTexts.COLUMN_TYPE, area.x() + TYPE_X, y);
    label(graphics, font, ControllerScreenTexts.COLUMN_SIGNAL, area.x() + SIGNAL_X, y);
    label(graphics, font, ControllerScreenTexts.COLUMN_STATE, area.x() + STATE_X, y);
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
    cell(graphics, font, Component.literal(node.name()), box, NAME_X, TYPE_X);
    cell(graphics, font, kindOf(node.nodeKind()), box, TYPE_X, SIGNAL_X);
    cell(
        graphics,
        font,
        Component.literal(String.valueOf(menu.nodeSignal(row))),
        box,
        SIGNAL_X,
        STATE_X);
    drawState(graphics, font, box, menu.nodeState(row));
  }

  /** A text from one column, cut to fit before the next, in the normal color (answer 336). */
  private static void cell(
      GuiGraphics graphics, Font font, Component text, Box box, int column, int next) {
    String shown = FittedText.fit(font, text, next - column - COLUMN_GAP);
    graphics.drawString(font, shown, box.x() + column, box.y() + TEXT_Y, ScreenColors.TEXT, false);
  }

  /** The state in text and in its color, so it never depends on the color alone (answer 284). */
  private static void drawState(GuiGraphics graphics, Font font, Box box, NodeState state) {
    Optional<NodeState> shown = Optional.of(state);
    int width = box.width() - STATE_X - RIGHT_PADDING;
    String text = FittedText.fit(font, StatePill.textOf(shown), width);
    graphics.drawString(
        font, text, box.x() + STATE_X, box.y() + TEXT_Y, StatePill.colorOf(shown), false);
  }

  static Component kindOf(NodeKind kind) {
    return Component.translatable(ControllerKinds.keyOf(kind));
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
    switch (placed.item().line()) {
      case POSITION -> drawPosition(graphics, font, box, menu.nodes().get(placed.item().row()));
      case SLOT ->
          placed.item().slot().ifPresent(slot -> drawSlot(graphics, font, box, slot, menu));
      default -> {
        Component none = Component.translatable(ControllerScreenTexts.NO_ACTIVE_CHANNEL);
        dim(graphics, font, none, box);
      }
    }
  }

  /** The block of the node, only in the rows the owner receives (answers 149 and 340). */
  private static void drawPosition(GuiGraphics graphics, Font font, Box box, NodeLine node) {
    node.position()
        .ifPresent(pos ->
            dim(
                graphics,
                font,
                Component.translatable(ControllerScreenTexts.POSITION_LINE, positionOf(pos)),
                box));
  }

  private static String positionOf(BlockPos pos) {
    return String.format(POSITION, pos.getX(), pos.getY(), pos.getZ());
  }

  private static void dim(GuiGraphics graphics, Font font, Component text, Box box) {
    graphics.drawString(
        font, text, box.x() + SLOT_ICON_X, box.y() + SLOT_TEXT_Y, ScreenColors.DIM, false);
  }

  private static void drawSlot(
      GuiGraphics graphics, Font font, Box box, SlotLine slot, ControllerMenu menu) {
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
      items.add(new Item(row, Line.NODE, Optional.empty()));
      if (row == expanded) {
        items.addAll(openLines(row, menu.nodes().get(row)));
      }
    }
    return items;
  }

  /** Under the open row: the position for the owner, then each enabled slot (234 and 340). */
  private static List<Item> openLines(int row, NodeLine node) {
    List<Item> lines = new ArrayList<>();
    if (node.position().isPresent()) {
      lines.add(new Item(row, Line.POSITION, Optional.empty()));
    }
    node.slots().forEach(slot -> lines.add(new Item(row, Line.SLOT, Optional.of(slot))));
    if (node.slots().isEmpty()) {
      lines.add(new Item(row, Line.NO_CHANNEL, Optional.empty()));
    }
    return lines;
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
        TYPE_X - NAME_X,
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
