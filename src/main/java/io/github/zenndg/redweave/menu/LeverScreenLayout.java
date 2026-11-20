package io.github.zenndg.redweave.menu;

/** Where each value of the Signal Lever Screen sits among the numbers the game keeps in sync. */
final class LeverScreenLayout {
  static final int VIEWER_IS_OWNER = 0;

  /** Whether the lever is on: 1 off and 2 on, and 0 before the first sync (answer 582). */
  static final int ON = 1;

  static final int NETWORK_STATE = 2;
  static final int OUTPUT = 3;
  static final int STRENGTH = 4;

  /** The delay of the output, in whole seconds (answer 830). */
  static final int DELAY_SECONDS = 5;

  static final int SIZE = 6;

  /** The value of `ON` for a lever turned off. */
  static final int OFF_CODE = 1;

  /** The value of `ON` for a lever turned on. */
  static final int ON_CODE = 2;

  private LeverScreenLayout() {}
}
