package io.github.zenndg.redweave.client.screen;

/** A rectangle of the screen, in GUI pixels, that knows whether the mouse is over it. */
record Box(int x, int y, int width, int height) {
  boolean contains(double mouseX, double mouseY) {
    return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
  }

  int right() {
    return x + width;
  }

  int bottom() {
    return y + height;
  }
}
