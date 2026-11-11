package io.github.marcelofabianov.redweave.client.screen;

/** Where the mouse is, to light what it hovers. */
record Mouse(double x, double y) {
  boolean hovers(Box box) {
    return box.contains(x, y);
  }
}
