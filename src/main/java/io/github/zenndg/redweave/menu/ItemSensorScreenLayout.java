package io.github.zenndg.redweave.menu;

/**
 * Where each value of the Signal Item Sensor Screen sits among the numbers the game keeps in sync.
 * The game sends 16 bits each, so the item, the count and what was counted go in two halves, as the
 * ticks of the timer (answer 384).
 */
final class ItemSensorScreenLayout {
  static final int VIEWER_IS_OWNER = 0;

  /** Whether the sensor is on: 1 off and 2 on, and 0 before the first sync. */
  static final int ON = 1;

  static final int NETWORK_STATE = 2;
  static final int OUTPUT = 3;
  static final int STRENGTH = 4;

  /** The expected item, as its number in the registry plus one, and 0 for none. */
  static final int ITEM = 5;

  static final int COUNT = 7;
  static final int COUNTED = 9;

  /** The delay of the output, in whole seconds (answer 830). */
  static final int DELAY_SECONDS = 11;

  static final int SIZE = 12;

  /** The value of `ON` for a sensor off. */
  static final int OFF_CODE = 1;

  /** The value of `ON` for a sensor on. */
  static final int ON_CODE = 2;

  private ItemSensorScreenLayout() {}

  /** The first number of the pair a value at `index` belongs to. */
  static int firstOf(int index) {
    if (index >= COUNTED) {
      return COUNTED;
    }
    return index >= COUNT ? COUNT : ITEM;
  }
}
