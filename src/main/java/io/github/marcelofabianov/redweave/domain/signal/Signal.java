package io.github.marcelofabianov.redweave.domain.signal;

/** A redstone signal, from 0 to 15, the same range as the game (AGENTS.md, rule 5). */
public record Signal(int strength) {
  private static final int MIN_STRENGTH = 0;
  private static final int MAX_STRENGTH = 15;
  private static final String OUT_OF_RANGE = "A signal goes from 0 to 15, got %d";

  /** No signal, like a redstone wire with no source. */
  public static final Signal OFF = new Signal(MIN_STRENGTH);

  /** The strongest signal, like a redstone block. */
  public static final Signal MAX = new Signal(MAX_STRENGTH);

  public Signal {
    if (strength < MIN_STRENGTH || strength > MAX_STRENGTH) {
      throw new IllegalArgumentException(String.format(OUT_OF_RANGE, strength));
    }
  }

  /**
   * The closest valid signal, for a value read from the world or another mod, which must not break
   * the game (AGENTS.md, rule 14). The caller logs it when the value was out of range.
   */
  public static Signal clamped(int strength) {
    return new Signal(Math.clamp(strength, MIN_STRENGTH, MAX_STRENGTH));
  }

  public boolean isOn() {
    return strength > MIN_STRENGTH;
  }

  /** Two sources on the same line keep the strongest, as two torches on one redstone wire. */
  public Signal strongest(Signal other) {
    return other.strength > strength ? other : this;
  }
}
