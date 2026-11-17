package io.github.marcelofabianov.redweave.menu;

/** Where each value of the Signal Inverter Screen sits among the numbers the game keeps in sync. */
final class InverterScreenLayout {
  static final int VIEWER_IS_OWNER = 0;
  static final int STATE = 1;
  static final int NETWORK_STATE = 2;
  static final int INPUT = 3;
  static final int OUTPUT = 4;

  /** Whether the input had a signal on the last tick, for the diagram (answer 451). */
  static final int INPUT_ON = 5;

  /** The strength, from 1 to 15 (answer 834). */
  static final int STRENGTH = 6;

  /** The delay of the output, in whole seconds (answer 830). */
  static final int DELAY_SECONDS = 7;

  static final int SIZE = 8;

  private InverterScreenLayout() {}
}
