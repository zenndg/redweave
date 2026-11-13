package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.client.ChannelColors;
import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.menu.ChannelNames;
import io.github.marcelofabianov.redweave.menu.NodeMenu;
import io.github.marcelofabianov.redweave.menu.NodeScreenTexts;
import java.util.Optional;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * The texts of the channel panel: on the left, the title, the row labels and the signal of the
 * chosen channel; on the right, the block face (telas.md and answers 224 to 228 and 346). The slot
 * has no name field (answer 277).
 */
final class NodeScreenPanel {
  private static final int LABEL_OFFSET = 3;
  private static final int SQUARE = 7;
  private static final int SQUARE_Y = 7;
  private static final int TITLE_X = 18;
  private static final int FACE_TEXT_GAP = 8;
  private static final int FACE_TEXT_X =
      SlotEditor.RIGHT_COLUMN_X + SlotEditor.CUBE_SIZE + FACE_TEXT_GAP;

  /** The name of the face sits in the middle of the height of the cube (answer 372). */
  private static final int FACE_NAME_Y = 34;

  private static final int DIVIDER_X = SlotEditor.RIGHT_COLUMN_X - 10;
  private static final int DIVIDER_MARGIN = 4;
  private static final int NUMBER_GAP = 6;
  private static final int BASELINE = 12;
  private static final int MESSAGE_Y = 24;
  private static final int SIDE_PADDING = 16;

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
    label(graphics, font, panel, NodeScreenTexts.STATUS, SlotEditor.STATUS_Y);
    label(graphics, font, panel, NodeScreenTexts.DIRECTION, SlotEditor.DIRECTION_Y);
    label(graphics, font, panel, NodeScreenTexts.SIGNAL, SlotEditor.SIGNAL_Y);
    drawDivider(graphics, panel);
    drawSignal(graphics, font, view, panel, index);
    Optional<ChannelSlot> slot = view.menu().slot(index);
    slot.ifPresent(found -> drawFace(graphics, font, panel, found));
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
    text(graphics, font, face, panel, FACE_TEXT_X, FACE_NAME_Y, color);
  }

  /** The line between the columns, so the cube reads as its own part (answer 346). */
  private static void drawDivider(GuiGraphics graphics, Box panel) {
    int x = panel.x() + DIVIDER_X;
    graphics.fill(
        x,
        panel.y() + DIVIDER_MARGIN,
        x + 1,
        panel.bottom() - DIVIDER_MARGIN,
        ScreenColors.SEPARATOR);
  }

  /** The 15-bar meter of the chosen channel, 0 when it is disabled (answers 224 and 346). */
  private static void drawSignal(
      GuiGraphics graphics, Font font, NodeView view, Box panel, int index) {
    int strength = SlotEditor.isEnabled(view.menu().slot(index)) ? view.menu().signal(index) : 0;
    int x = panel.x() + SlotEditor.CONTROL_X;
    int y = panel.y() + SlotEditor.SIGNAL_Y;
    SignalMeter.draw(graphics, x, y + BASELINE, strength);
    int numberX = x + SignalMeter.WIDTH + NUMBER_GAP;
    graphics.drawString(
        font, String.valueOf(strength), numberX, y + LABEL_OFFSET, ScreenColors.TEXT, false);
  }

  /** The block face explains itself on hover. */
  static Optional<Component> tooltipAt(NodeView view, Box panel, int index) {
    boolean settable = view.editable() && !SlotEditor.isEnabled(view.menu().slot(index));
    if (settable && SlotEditor.cubeBox(panel).contains(view.mouseX(), view.mouseY())) {
      return Optional.of(Component.translatable(NodeScreenTexts.FACE_TOOLTIP));
    }
    return Optional.empty();
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
