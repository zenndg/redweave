package io.github.zenndg.redweave.menu;

/**
 * Where each value of the Signal Randomizer Screen sits among the numbers the game keeps in sync.
 * The interval goes in two halves, as the ticks of the timer (answer 384).
 */
final class RandomizerScreenLayout {
  static final int VIEWER_IS_OWNER = 0;
  static final int STATE = 1;
  static final int NETWORK_STATE = 2;
  static final int MODE = 3;
  static final int OUTPUT = 4;

  /** The value on the output, for the big number of the screen (answer 487). */
  static final int VALUE = 5;

  static final int INTERVAL = 6;

  /** The delay of the output, in whole seconds (answer 830). */
  static final int DELAY_SECONDS = 8;

  static final int SIZE = 9;

  private RandomizerScreenLayout() {}
}
