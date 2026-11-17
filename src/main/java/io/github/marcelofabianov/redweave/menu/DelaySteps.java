package io.github.marcelofabianov.redweave.menu;

/**
 * The value of a `Delay` button on every module screen: `−` one second shorter and `+` one longer
 * (answer 830). The server checks every value it gets (rule 16).
 */
public final class DelaySteps {
  public static final int SHORTER = 0;
  public static final int LONGER = 1;

  private DelaySteps() {}

  public static int of(boolean longer) {
    return longer ? LONGER : SHORTER;
  }

  static boolean fits(int value) {
    return value == SHORTER || value == LONGER;
  }

  /** The seconds a step adds: one more or one less. */
  static int seconds(int value) {
    return value == LONGER ? 1 : -1;
  }
}
