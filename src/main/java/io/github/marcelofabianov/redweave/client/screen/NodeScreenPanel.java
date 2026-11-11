package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.client.ChannelColors;
import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.menu.NodeMenu;
import io.github.marcelofabianov.redweave.menu.NodeScreenTexts;
import java.util.Optional;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * The texts of the channel panel: the title, the row labels, the name of the slot and the face
 * (telas.md and answers 224 to 228).
 */
final class NodeScreenPanel {
  private static final int LABEL_OFFSET = 3;
  private static final int SQUARE = 7;
  private static final int SQUARE_Y = 7;
  private static final int TITLE_X = 18;
  private static final int FACE_NAME_X = 96;
  private static final int FACE_NAME_Y = 87;
  private static final int HINT_BOTTOM = 13;
  private static final int MESSAGE_Y = 24;
  private static final int SIDE_PADDING = 16;
  private static final int FIELD_TEXT_X = 4;
  private static final int FIELD_TEXT_Y = 3;
  private static final int PENCIL = 8;
  private static final int PENCIL_GAP = 4;
  private static final String SPACE = " ";

  private NodeScreenPanel() {}

  static void draw(
      GuiGraphics graphics, Font font, NodeView view, Box panel, int index, boolean inNetwork) {
    graphics.blitSprite(NodeSprites.PANEL, panel.x(), panel.y(), panel.width(), panel.height());
    if (!inNetwork) {
      Component message = Component.translatable(NodeScreenTexts.NOT_IN_A_NETWORK);
      text(graphics, font, message, panel, SlotEditor.LABEL_X, MESSAGE_Y, ScreenColors.LABEL);
      return;
    }
    ChannelColor channel = ChannelColor.values()[index];
    drawTitle(graphics, font, panel, channel, view.menu());
    label(graphics, font, panel, NodeScreenTexts.NAME, SlotEditor.NAME_Y);
    label(graphics, font, panel, NodeScreenTexts.STATUS, SlotEditor.STATUS_Y);
    label(graphics, font, panel, NodeScreenTexts.DIRECTION, SlotEditor.DIRECTION_Y);
    label(graphics, font, panel, NodeScreenTexts.BLOCK_FACE, FACE_NAME_Y - LABEL_OFFSET);
    label(graphics, font, panel, NodeScreenTexts.LOGIC, SlotEditor.LOGIC_Y);
    Optional<ChannelSlot> slot = view.menu().slot(index);
    slot.ifPresent(found -> drawFace(graphics, font, panel, found));
    int hintY = panel.height() - HINT_BOTTOM;
    text(graphics, font, hint(), panel, SlotEditor.LABEL_X, hintY, ScreenColors.DIM);
  }

  private static void drawTitle(
      GuiGraphics graphics, Font font, Box panel, ChannelColor channel, NodeMenu menu) {
    int x = panel.x() + SlotEditor.LABEL_X;
    int y = panel.y() + SQUARE_Y;
    graphics.fill(x, y, x + SQUARE, y + SQUARE, ChannelColors.argb(channel));
    Component title = ChannelNames.of(channel, menu.channelName(channel));
    text(graphics, font, title, panel, TITLE_X, SlotEditor.TITLE_Y, ScreenColors.TEXT);
  }

  private static void drawFace(GuiGraphics graphics, Font font, Box panel, ChannelSlot slot) {
    Component face = Component.translatable(NodeScreenTexts.face(slot.face()));
    int color = slot.active() ? ScreenColors.TEXT : ScreenColors.DIM;
    int faceY = panel.y() + FACE_NAME_Y - LABEL_OFFSET;
    graphics.drawString(font, face, panel.x() + FACE_NAME_X, faceY, color, false);
  }

  /**
   * The name of the slot in its field, or the grey hint when it has none, and the pencil for the
   * owner (answer 227). The field itself takes its place while the owner types.
   */
  static void drawName(GuiGraphics graphics, Font font, Box panel, String name, boolean owner) {
    Box box = SlotEditor.nameBox(panel);
    graphics.blitSprite(NodeSprites.NAME_FIELD, box.x(), box.y(), box.width(), box.height());
    Component shown =
        name.isEmpty()
            ? Component.translatable(NodeScreenTexts.CLICK_TO_NAME)
            : Component.literal(name);
    int color = name.isEmpty() ? ScreenColors.DIM : ScreenColors.TEXT;
    String fitted = FittedText.fit(font, shown, box.width() - 2 * FIELD_TEXT_X);
    graphics.drawString(font, fitted, box.x() + FIELD_TEXT_X, box.y() + FIELD_TEXT_Y, color, false);
    if (owner) {
      graphics.blitSprite(
          NodeSprites.ICON_EDIT, box.right() + PENCIL_GAP, box.y() + FIELD_TEXT_Y, PENCIL, PENCIL);
    }
  }

  /** The block face explains itself on hover, and the logic hint shows what comes next. */
  static Optional<Component> tooltipAt(NodeView view, Box panel, int index) {
    if (SlotEditor.isEnabled(view.menu().slot(index))
        && SlotEditor.cubeBox(panel).contains(view.mouseX(), view.mouseY())) {
      return Optional.of(Component.translatable(NodeScreenTexts.FACE_TOOLTIP));
    }
    if (view.editable() && SlotEditor.nameBox(panel).contains(view.mouseX(), view.mouseY())) {
      return Optional.of(Component.translatable(NodeScreenTexts.RENAME_TOOLTIP));
    }
    Box hint =
        new Box(panel.x(), panel.bottom() - HINT_BOTTOM - LABEL_OFFSET, panel.width(), HINT_BOTTOM);
    if (hint.contains(view.mouseX(), view.mouseY())) {
      return Optional.of(
          hint()
              .copy()
              .append(SPACE)
              .append(Component.translatable(NodeScreenTexts.LOGIC_TOOLTIP)));
    }
    return Optional.empty();
  }

  private static Component hint() {
    return Component.translatable(NodeScreenTexts.LOGIC_LATER);
  }

  private static void label(GuiGraphics graphics, Font font, Box panel, String key, int controlY) {
    graphics.drawString(
        font,
        Component.translatable(key),
        panel.x() + SlotEditor.LABEL_X,
        panel.y() + controlY + LABEL_OFFSET,
        ScreenColors.LABEL,
        false);
  }

  private static void text(
      GuiGraphics graphics, Font font, Component text, Box panel, int x, int y, int color) {
    int width = panel.width() - x - SIDE_PADDING;
    graphics.drawString(
        font, FittedText.fit(font, text, width), panel.x() + x, panel.y() + y, color, false);
  }
}
