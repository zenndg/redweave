package io.github.marcelofabianov.redweave.menu;

/**
 * Where each live value of the Node Screen sits among the data slots of the menu. The slots go up
 * to 9, the most a node has with upgrades (answer 66).
 */
final class NodeScreenLayout {
  static final int MAX_SLOTS = 9;
  static final int SLOT_COUNT = 0;
  static final int STATE = 1;
  static final int VIEWER_IS_OWNER = 2;
  static final int FIRST_SLOT = 3;
  static final int FIRST_SIGNAL = FIRST_SLOT + MAX_SLOTS;
  static final int SIZE = FIRST_SIGNAL + MAX_SLOTS;

  private NodeScreenLayout() {}
}
