package io.github.marcelofabianov.redweave.domain.module;

import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.Objects;

/**
 * The rule of the Signal Lever: on or off, as the player leaves it, and the strength it gives while
 * on, from 1 to 15, 15 when placed (answers 548, 552 and 579). Off, its output is off; nothing
 * changes it but the player, so it has no tick of its own.
 */
public record SignalLever(boolean on, Signal strength) implements Emitter {
  private static final String TOO_WEAK = "A lever gives from 1 to 15, got %d";

  public SignalLever {
    Objects.requireNonNull(strength);
    if (!strength.isOn()) {
      throw new IllegalArgumentException(String.format(TOO_WEAK, strength.strength()));
    }
  }

  /** A new lever: off, with the strongest signal (answer 552). */
  public static SignalLever placed() {
    return new SignalLever(false, Signal.MAX);
  }

  /** On when off and off when on (answers 555 and 576). */
  public SignalLever toggle() {
    return new SignalLever(!on, strength);
  }

  /** Another strength, kept from 1 to 15, as the `−` and `+` of the screen step it (582). */
  public SignalLever withStrength(int next) {
    return new SignalLever(on, Emitter.stepped(next));
  }
}
