package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.menu.NodeMenu;

/** What every part of the Node Screen needs in a frame: the menu, the mouse, and who looks. */
record NodeView(NodeMenu menu, double mouseX, double mouseY, boolean editable) {
  boolean hovers(Box box) {
    return editable && box.contains(mouseX, mouseY);
  }
}
