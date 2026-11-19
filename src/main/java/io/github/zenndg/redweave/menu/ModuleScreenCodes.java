package io.github.zenndg.redweave.menu;

/**
 * The number the game sends with a click on a Signal Module Screen: the kind of the action in the 3
 * low bits and its value above them. The server checks every code it gets (rule 16).
 */
final class ModuleScreenCodes {
  private static final int KIND_BITS = 3;
  private static final int KIND_MASK = (1 << KIND_BITS) - 1;

  private ModuleScreenCodes() {}

  static int button(int kind, int value) {
    return kind | value << KIND_BITS;
  }

  static int kindOf(int button) {
    return button & KIND_MASK;
  }

  static int valueOf(int button) {
    return button >> KIND_BITS;
  }
}
