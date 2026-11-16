package io.github.marcelofabianov.redweave.menu;

/** Where each value of the Signal Alarm Screen sits among the numbers the game keeps in sync. */
final class AlarmScreenLayout {
  static final int VIEWER_IS_OWNER = 0;

  /** Whether the alarm rings: 1 quiet and 2 ringing, and 0 before the first sync. */
  static final int RINGING = 1;

  static final int NETWORK_STATE = 2;
  static final int INPUT = 3;
  static final int LISTENERS = 4;
  static final int REPEATS = 5;
  static final int INTERVAL = 6;
  static final int DURATION = 7;
  static final int SIZE = 8;

  /** The value of `RINGING` for an alarm quiet. */
  static final int QUIET_CODE = 1;

  /** The value of `RINGING` for an alarm ringing. */
  static final int RINGING_CODE = 2;

  private AlarmScreenLayout() {}
}
