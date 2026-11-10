package io.github.marcelofabianov.redweave.client.screen;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/** Draws the controls of the slot panel with the sprites of the screen (answer 78). */
final class ControlPainter {
  private static final int SWATCH_INSET = 2;
  private static final int LABEL_Y = 3;

  private ControlPainter() {}

  static void draw(GuiGraphics graphics, Font font, Control control, NodeView view) {
    Box box = control.box();
    boolean hover = control.clickable() && view.hovers(box);
    graphics.blitSprite(
        spriteOf(control.look(), hover), box.x(), box.y(), box.width(), box.height());
    if (control.look() == Control.Look.SWATCH || control.look() == Control.Look.CHOSEN_SWATCH) {
      graphics.fill(
          box.x() + SWATCH_INSET,
          box.y() + SWATCH_INSET,
          box.right() - SWATCH_INSET,
          box.bottom() - SWATCH_INSET,
          control.color());
    }
    control.label().ifPresent(label -> drawLabel(graphics, font, control, label));
  }

  private static void drawLabel(GuiGraphics graphics, Font font, Control control, Component label) {
    Box box = control.box();
    int color = textColorOf(control.look());
    int x = box.x() + (box.width() - font.width(label)) / 2;
    graphics.drawString(font, label, x, box.y() + LABEL_Y, color, false);
  }

  private static int textColorOf(Control.Look look) {
    return switch (look) {
      case CHOSEN -> ScreenColors.ON_COPPER;
      case DISABLED -> ScreenColors.DIM;
      default -> ScreenColors.TEXT;
    };
  }

  private static ResourceLocation spriteOf(Control.Look look, boolean hover) {
    return switch (look) {
      case BUTTON, ARMS_CLEAR -> hover ? NodeSprites.BUTTON_HOVER : NodeSprites.BUTTON;
      case CHOSEN -> hover ? NodeSprites.BUTTON_SELECTED_HOVER : NodeSprites.BUTTON_SELECTED;
      case DISABLED -> NodeSprites.BUTTON_DISABLED;
      case SWATCH -> hover ? NodeSprites.SWATCH_HOVER : NodeSprites.SWATCH;
      case CHOSEN_SWATCH -> NodeSprites.SWATCH_SELECTED;
    };
  }
}
