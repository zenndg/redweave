package io.github.zenndg.redweave.menu;

/**
 * Where each value of the Signal Pressure Plate Screen sits among the numbers the game keeps in
 * sync.
 */
final class PressurePlateScreenLayout {
  static final int VIEWER_IS_OWNER = 0;

  /** Whether the plate is on: 1 off and 2 on, and 0 before the first sync. */
  static final int ON = 1;

  static final int NETWORK_STATE = 2;
  static final int OUTPUT = 3;
  static final int STRENGTH = 4;

  /** The mode, as its position plus one, and 0 before the first sync. */
  static final int MODE = 5;

  /** The delay of the output, in whole seconds (answer 830). */
  static final int DELAY_SECONDS = 6;

  static final int SIZE = 7;

  /** The value of `ON` for a plate turned off. */
  static final int OFF_CODE = 1;

  /** The value of `ON` for a plate turned on. */
  static final int ON_CODE = 2;

  private PressurePlateScreenLayout() {}
}
