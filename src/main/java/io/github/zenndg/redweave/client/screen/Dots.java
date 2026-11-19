package io.github.zenndg.redweave.client.screen;

import net.minecraft.client.gui.GuiGraphics;

/** The small colored dot of the states, a white sprite tinted with the color. */
final class Dots {
  static final int SIZE = 5;

  private Dots() {}

  static void draw(GuiGraphics graphics, int x, int y, int color) {
    graphics.setColor(Channels.red(color), Channels.green(color), Channels.blue(color), 1);
    graphics.blitSprite(NodeSprites.DOT, x, y, SIZE, SIZE);
    graphics.setColor(1, 1, 1, 1);
  }
}
