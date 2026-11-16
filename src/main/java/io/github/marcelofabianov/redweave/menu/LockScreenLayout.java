package io.github.marcelofabianov.redweave.menu;

/**
 * Where each value of the Signal Lock Screen sits among the numbers the game keeps in sync. The key
 * goes only to its owner; anyone else gets 0 and sees `?` (answer 637).
 */
final class LockScreenLayout {
  static final int VIEWER_IS_OWNER = 0;

  /** Whether the lock is open: 1 locked and 2 unlocked, and 0 before the first sync. */
  static final int OPEN = 1;

  static final int NETWORK_STATE = 2;
  static final int INPUT = 3;
  static final int OUTPUT = 4;
  static final int KEY = 5;
  static final int STRENGTH = 6;
  static final int PULSE = 7;
  static final int SIZE = 8;

  /** The value of `OPEN` for a lock locked. */
  static final int LOCKED_CODE = 1;

  /** The value of `OPEN` for a lock unlocked. */
  static final int UNLOCKED_CODE = 2;

  /** The key a player who does not own the lock receives: none (answer 637). */
  static final int HIDDEN_KEY = 0;

  private LockScreenLayout() {}
}
