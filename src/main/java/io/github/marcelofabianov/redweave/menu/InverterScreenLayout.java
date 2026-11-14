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

  static final int SIZE = 6;

  private InverterScreenLayout() {}
}
