package io.github.zenndg.redweave.menu;

/**
 * Where each value of the Signal Timer Screen sits among the numbers the game keeps in sync. The
 * game sends 16 bits each, so the ticks, up to 72000, go in two halves of 15 bits (answer 384).
 */
final class TimerScreenLayout {
  static final int VIEWER_IS_OWNER = 0;
  static final int STATE = 1;
  static final int NETWORK_STATE = 2;
  static final int OUTPUT = 3;
  static final int PAUSE = 4;
  static final int INTERVAL = 5;
  static final int PULSE = 7;
  static final int ELAPSED = 9;
  static final int SIZE = 11;

  private static final int HALF_BITS = 15;
  private static final int HALF_MASK = (1 << HALF_BITS) - 1;

  private TimerScreenLayout() {}

  /** The low half of ticks at `index`, and the high half at the next one. */
  static int half(int ticks, int index, int first) {
    return index == first ? ticks & HALF_MASK : ticks >> HALF_BITS;
  }

  static int whole(int low, int high) {
    return low | high << HALF_BITS;
  }
}
