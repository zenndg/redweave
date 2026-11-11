package io.github.marcelofabianov.redweave.client.screen;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/** Draws the controls of the channel panel with the sprites of the screen (answers 78 and 231). */
final class ControlPainter {
  private static final int LABEL_Y = 3;
  private static final int ICON_X = 5;
  private static final int ICON_Y = 4;
  private static final int ICON_GAP = 4;

  private ControlPainter() {}

  static void draw(GuiGraphics graphics, Font font, Control control, NodeView view) {
    Box box = control.box();
    boolean hover = control.clickable() && view.hovers(box);
    graphics.blitSprite(
        spriteOf(control.look(), hover), box.x(), box.y(), box.width(), box.height());
    control.label().ifPresent(label -> drawLabel(graphics, font, control, label));
  }

  private static void drawLabel(GuiGraphics graphics, Font font, Control control, Component label) {
    Box box = control.box();
    int color = textColorOf(control.look());
    if (isStatus(control.look())) {
      ResourceLocation icon =
          control.look() == Control.Look.STATUS_ON
              ? NodeSprites.ICON_ACTIVE
              : NodeSprites.ICON_INACTIVE;
      graphics.blitSprite(icon, box.x() + ICON_X, box.y() + ICON_Y, Dots.SIZE, Dots.SIZE);
      int x = box.x() + ICON_X + Dots.SIZE + ICON_GAP;
      graphics.drawString(font, label, x, box.y() + LABEL_Y, color, false);
      return;
    }
    int x = box.x() + (box.width() - font.width(label)) / 2;
    graphics.drawString(font, label, x, box.y() + LABEL_Y, color, false);
  }

  private static boolean isStatus(Control.Look look) {
    return look == Control.Look.STATUS_ON || look == Control.Look.STATUS_OFF;
  }

  private static int textColorOf(Control.Look look) {
    return switch (look) {
      case CHOSEN -> ScreenColors.ON_COPPER;
      case DISABLED -> ScreenColors.DIM;
      case STATUS_ON -> ScreenColors.ONLINE;
      case STATUS_OFF -> ScreenColors.LABEL;
      case BUTTON -> ScreenColors.TEXT;
    };
  }

  private static ResourceLocation spriteOf(Control.Look look, boolean hover) {
    return switch (look) {
      case BUTTON -> hover ? NodeSprites.BUTTON_HOVER : NodeSprites.BUTTON;
      case CHOSEN -> hover ? NodeSprites.BUTTON_SELECTED_HOVER : NodeSprites.BUTTON_SELECTED;
      case DISABLED -> NodeSprites.BUTTON_DISABLED;
      case STATUS_ON -> hover ? NodeSprites.STATUS_HOVER : NodeSprites.STATUS_ENABLED;
      case STATUS_OFF -> hover ? NodeSprites.STATUS_HOVER : NodeSprites.STATUS_DISABLED;
    };
  }
}
