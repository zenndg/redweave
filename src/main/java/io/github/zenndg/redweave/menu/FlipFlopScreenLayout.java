package io.github.zenndg.redweave.menu;

/**
 * Where each value of the Signal Flip-Flop Screen sits among the numbers the game keeps in sync.
 */
final class FlipFlopScreenLayout {
  static final int VIEWER_IS_OWNER = 0;

  /** The state kept: {@link #OFF_CODE} or {@link #ON_CODE}, 0 before the first sync (890). */
  static final int STATE = 1;

  static final int NETWORK_STATE = 2;
  static final int INPUT = 3;
  static final int OUTPUT = 4;

  /** Whether the last reading of the input had a signal, for the diagram (answer 884). */
  static final int INPUT_ON = 5;

  /** The strength, from 1 to 15 (answer 880). */
  static final int STRENGTH = 6;

  /** The delay of the output, in whole seconds (answer 830). */
  static final int DELAY_SECONDS = 7;

  static final int SIZE = 8;

  static final int OFF_CODE = 1;
  static final int ON_CODE = 2;

  private FlipFlopScreenLayout() {}
}
