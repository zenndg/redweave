package io.github.zenndg.redweave.client.screen;

import net.minecraft.client.gui.GuiGraphics;

/** The padlock beside a read-only notice (answer 85). */
final class Locks {
  static final int WIDTH = 7;
  static final int GAP = 4;
  private static final int HEIGHT = 9;
  private static final int Y = -1;

  private Locks() {}

  static void draw(GuiGraphics graphics, int x, int textY) {
    graphics.blitSprite(NodeSprites.LOCK, x, textY + Y, WIDTH, HEIGHT);
  }
}
