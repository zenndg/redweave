package io.github.zenndg.redweave.domain.module;

/**
 * The weather a Weather Detector reads from the dimension of its plate, which the caller gives: the
 * domain does not read the game (answers 458 and 464).
 */
public enum Weather {
  CLEAR,
  RAIN,
  THUNDER,

  /** A dimension with no sky light, as the Nether and the End, where the weather never moves. */
  NONE;

  /**
   * The weather from what the game says. The game shows no storm without rain, so thunder alone
   * reads as a clear sky.
   */
  public static Weather of(boolean hasWeather, boolean raining, boolean thundering) {
    if (!hasWeather) {
      return NONE;
    }
    if (!raining) {
      return CLEAR;
    }
    return thundering ? THUNDER : RAIN;
  }
}
