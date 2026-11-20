package io.github.zenndg.redweave.menu;

/** Where each value of the Signal Keypad Screen of the owner sits among the synced numbers. */
final class KeypadScreenLayout {
  static final int VIEWER_IS_OWNER = 0;
  static final int UNLOCKED = 1;
  static final int NETWORK_STATE = 2;
  static final int INPUT = 3;
  static final int OUTPUT = 4;
  static final int STRENGTH = 5;

  /** The open time, in whole seconds (answer 663). */
  static final int OPEN_SECONDS = 6;

  /** Whether a code is set, never the code (answer 660). */
  static final int HAS_CODE = 7;

  static final int SIZE = 8;

  private KeypadScreenLayout() {}
}
