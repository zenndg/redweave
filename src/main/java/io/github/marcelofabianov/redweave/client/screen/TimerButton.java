package io.github.marcelofabianov.redweave.client.screen;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * A button of the Signal Timer Screen, with the sprites of the Node Screen: copper when chosen and
 * grey when it cannot be pressed, as for whoever does not own the plate, who still sees what is
 * chosen, dimmed (answer 378).
 */
record TimerButton(Box box, Component label, Look look, Runnable click) {
  enum Look {
    BUTTON,
    CHOSEN,
    LOCKED,
    DISABLED
  }

  /** The dot before a state, as `● Running` or `● On`. */
  static final String DOT = "● ";

  private static final int TEXT_Y = 3;

  boolean clickable() {
    return look == Look.BUTTON || look == Look.CHOSEN;
  }

  void draw(GuiGraphics graphics, Font font, Mouse mouse) {
    boolean hover = clickable() && mouse.hovers(box);
    graphics.blitSprite(spriteOf(hover), box.x(), box.y(), box.width(), box.height());
    int x = box.x() + (box.width() - font.width(label)) / 2;
    graphics.drawString(font, label, x, box.y() + TEXT_Y, textColor(), false);
  }

  private ResourceLocation spriteOf(boolean hover) {
    return switch (look) {
      case CHOSEN -> hover ? NodeSprites.BUTTON_SELECTED_HOVER : NodeSprites.BUTTON_SELECTED;
      case LOCKED -> NodeSprites.BUTTON_SELECTED;
      case DISABLED -> NodeSprites.BUTTON_DISABLED;
      case BUTTON -> hover ? NodeSprites.BUTTON_HOVER : NodeSprites.BUTTON;
    };
  }

  private int textColor() {
    return switch (look) {
      case CHOSEN -> ScreenColors.ON_COPPER;
      case LOCKED, DISABLED -> ScreenColors.DIM;
      case BUTTON -> ScreenColors.TEXT;
    };
  }
}
