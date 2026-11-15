package io.github.marcelofabianov.redweave.menu;

/**
 * Where each value of the Signal Button Screen sits among the numbers the game keeps in sync. The
 * on time goes in two halves, as the ticks of the timer (answer 384).
 */
final class ButtonScreenLayout {
  static final int VIEWER_IS_OWNER = 0;

  /** Whether the button is on: 1 off and 2 on, and 0 before the first sync, as the lever. */
  static final int ON = 1;

  static final int NETWORK_STATE = 2;
  static final int OUTPUT = 3;
  static final int STRENGTH = 4;
  static final int TIME = 5;
  static final int SIZE = 7;

  /** The value of `ON` for a button released. */
  static final int OFF_CODE = 1;

  /** The value of `ON` for a button pressed. */
  static final int ON_CODE = 2;

  private ButtonScreenLayout() {}
}
