package io.github.marcelofabianov.redweave.menu;

/**
 * Where each value of the Weather Detector Screen sits among the numbers the game keeps in sync.
 */
final class DetectorScreenLayout {
  static final int VIEWER_IS_OWNER = 0;
  static final int STATE = 1;
  static final int NETWORK_STATE = 2;
  static final int MODE = 3;
  static final int OUTPUT = 4;

  /** The weather of the last tick, for the icon of the screen (answer 470). */
  static final int WEATHER = 5;

  static final int SIZE = 6;

  private DetectorScreenLayout() {}
}
