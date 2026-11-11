package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.menu.NodeMenu;
import io.github.marcelofabianov.redweave.menu.NodeScreenTexts;
import java.util.Optional;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/** The texts of the slot panel: the title, the row labels and what each control shows. */
final class NodeScreenPanel {
  private static final int LABEL_OFFSET = 3;
  private static final int FACE_NAME_X = 96;
  private static final int FACE_NAME_Y = 51;
  private static final int CHANNEL_NAME_Y = 88;
  private static final int HINT_BOTTOM = 13;
  private static final int MESSAGE_Y = 24;
  private static final int SIDE_PADDING = 16;
  private static final String SPACE = " ";

  private NodeScreenPanel() {}

  static void draw(
      GuiGraphics graphics, Font font, NodeView view, Box panel, int index, boolean inNetwork) {
    graphics.blitSprite(NodeSprites.PANEL, panel.x(), panel.y(), panel.width(), panel.height());
    if (!inNetwork) {
      text(
          graphics,
          font,
          Component.translatable(NodeScreenTexts.NOT_IN_A_NETWORK),
          panel,
          SlotEditor.LABEL_X,
          MESSAGE_Y,
          ScreenColors.LABEL);
      return;
    }
    Component title = Component.translatable(NodeScreenTexts.SLOT, index + 1);
    graphics.drawString(
        font,
        title,
        panel.x() + SlotEditor.LABEL_X,
        panel.y() + SlotEditor.TITLE_Y,
        ScreenColors.TEXT,
        false);
    view.menu().slot(index).ifPresent(slot -> drawRows(graphics, font, panel, slot, view.menu()));
  }

  private static void drawRows(
      GuiGraphics graphics, Font font, Box panel, ChannelSlot slot, NodeMenu menu) {
    label(graphics, font, panel, NodeScreenTexts.DIRECTION, SlotEditor.DIRECTION_Y);
    label(graphics, font, panel, NodeScreenTexts.BLOCK_FACE, FACE_NAME_Y - LABEL_OFFSET);
    label(graphics, font, panel, NodeScreenTexts.CHANNEL, SlotEditor.CHANNEL_Y);
    label(graphics, font, panel, NodeScreenTexts.LOGIC, SlotEditor.LOGIC_Y);
    Component face = Component.translatable(NodeScreenTexts.face(slot.face()));
    int faceY = panel.y() + FACE_NAME_Y - LABEL_OFFSET;
    graphics.drawString(font, face, panel.x() + FACE_NAME_X, faceY, ScreenColors.TEXT, false);
    int nameWidth = SlotEditor.CONTROL_X - SlotEditor.LABEL_X - LABEL_OFFSET;
    Component channel = ChannelNames.of(slot.channel(), menu.channelName(slot.channel()));
    text(
        graphics,
        font,
        channel,
        panel,
        SlotEditor.LABEL_X,
        CHANNEL_NAME_Y,
        ScreenColors.TEXT,
        nameWidth);
    int hintY = panel.height() - HINT_BOTTOM;
    text(graphics, font, hint(), panel, SlotEditor.LABEL_X, hintY, ScreenColors.DIM);
  }

  /** The block face explains itself on hover, and the logic hint shows what comes next. */
  static Optional<Component> tooltipAt(Font font, NodeView view, Box panel, int index) {
    if (view.menu().slot(index).isEmpty()) {
      return Optional.empty();
    }
    if (SlotEditor.cubeBox(panel).contains(view.mouseX(), view.mouseY())) {
      return Optional.of(Component.translatable(NodeScreenTexts.FACE_TOOLTIP));
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
    text(graphics, font, text, panel, x, y, color, panel.width() - x - SIDE_PADDING);
  }

  private static void text(
      GuiGraphics graphics,
      Font font,
      Component text,
      Box panel,
      int x,
      int y,
      int color,
      int width) {
    graphics.drawString(
        font, FittedText.fit(font, text, width), panel.x() + x, panel.y() + y, color, false);
  }
}
