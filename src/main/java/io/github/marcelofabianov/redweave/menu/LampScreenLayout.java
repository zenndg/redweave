package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.domain.module.LampMode;

/** Where each value of the Signal Lamp Screen sits among the numbers the game keeps in sync. */
final class LampScreenLayout {
  static final int VIEWER_IS_OWNER = 0;

  /** Whether the lamp gives its signal: 1 dark and 2 lit, and 0 before the first sync. */
  static final int LIT = 1;

  static final int NETWORK_STATE = 2;
  static final int INTENSITY = 3;
  static final int CONTROL = 4;
  static final int MODE = 5;
  static final int STRENGTH = 6;
  static final int LIGHT = 7;
  static final int SIZE = 8;

  static final int DARK_CODE = 1;
  static final int LIT_CODE = 2;

  /** How many modes the control has (answer 656). */
  static final int MODES = LampMode.values().length;

  private LampScreenLayout() {}
}
