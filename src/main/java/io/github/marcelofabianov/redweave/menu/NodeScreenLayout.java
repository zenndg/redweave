package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.domain.network.Node;

/**
 * Where each live value of the Node Screen sits among the data slots of the menu: a node has one
 * slot per channel color (answer 224).
 */
final class NodeScreenLayout {
  static final int MAX_SLOTS = Node.SLOT_COUNT;
  static final int SLOT_COUNT = 0;
  static final int STATE = 1;
  static final int VIEWER_IS_OWNER = 2;
  static final int VIEWER_PLACED = 3;
  static final int FIRST_SLOT = 4;
  static final int FIRST_SIGNAL = FIRST_SLOT + MAX_SLOTS;
  static final int SIZE = FIRST_SIGNAL + MAX_SLOTS;

  private NodeScreenLayout() {}
}
