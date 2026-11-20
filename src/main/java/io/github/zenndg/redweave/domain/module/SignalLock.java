package io.github.zenndg.redweave.domain.module;

import io.github.zenndg.redweave.domain.signal.Signal;
import java.util.Objects;

/**
 * The rule of the Signal Lock: it gives its strength, from 1 to 15, while its input has exactly the
 * strength of its key, from 1 to 15, and nothing otherwise; an input of 0 never opens it (answers
 * 633 to 636). The `Toggle` of its owner opens it for its pulse, and `remaining` is the ticks left
 * of that pulse, 0 without one (answer 640).
 */
public record SignalLock(Signal key, Signal strength, LockPulse pulse, int remaining) {
  private static final String TOO_WEAK = "A lock %s goes from 1 to 15, got %d";
  private static final String KEY = "key";
  private static final String STRENGTH = "strength";
  private static final String NEGATIVE = "A pulse cannot have %d ticks left";

  public SignalLock {
    Objects.requireNonNull(key);
    Objects.requireNonNull(strength);
    Objects.requireNonNull(pulse);
    requireOn(key, KEY);
    requireOn(strength, STRENGTH);
    if (remaining < 0) {
      throw new IllegalArgumentException(String.format(NEGATIVE, remaining));
    }
  }

  /** A new lock: key 15, giving 15, with a pulse of 1 s (answers 636 and 640). */
  public static SignalLock placed(LockLimits limits) {
    return new SignalLock(
        Signal.MAX, Signal.MAX, LockPulse.within(LockPulse.PLACED_TICKS, limits), 0);
  }

  /** Whether the input opens it: exactly the strength of the key (answer 634). */
  public boolean opens(Signal input) {
    return input.isOn() && input.strength() == key.strength();
  }

  /** Whether it is open: by the right input or by the pulse of its `Toggle`. */
  public boolean open(Signal input) {
    return opens(input) || pulsing();
  }

  /** Its strength while open, nothing otherwise (answer 635). */
  public Signal output(Signal input) {
    return open(input) ? strength : Signal.OFF;
  }

  /** Whether the pulse of its `Toggle` is under way. */
  public boolean pulsing() {
    return remaining > 0;
  }

  /** Opens it for its whole pulse; a pulse under way does not start over, as the button. */
  public SignalLock unlock() {
    return pulsing() ? this : new SignalLock(key, strength, pulse, pulse.ticks());
  }

  /** One tick of the pulse; without one, nothing changes. */
  public SignalLock tick() {
    return pulsing() ? new SignalLock(key, strength, pulse, remaining - 1) : this;
  }

  /** Another key, kept from 1 to 15, as the `−` and `+` of the screen step it (answer 636). */
  public SignalLock withKey(int next) {
    return new SignalLock(Emitter.stepped(next), strength, pulse, remaining);
  }

  /** Another strength, kept from 1 to 15 (answer 636). */
  public SignalLock withStrength(int next) {
    return new SignalLock(key, Emitter.stepped(next), pulse, remaining);
  }

  /** Another pulse, within the limits; a pulse under way keeps the ticks it has left (640). */
  public SignalLock withPulse(int ticks, LockLimits limits) {
    return new SignalLock(key, strength, LockPulse.within(ticks, limits), remaining);
  }

  /** Whether the pulse fits the limits of the config of now (answer 425). */
  public boolean fits(LockLimits limits) {
    return pulse.fits(limits);
  }

  /** The same lock with its pulse within the limits of now (answer 425). */
  public SignalLock fittedTo(LockLimits limits) {
    return withPulse(pulse.ticks(), limits);
  }

  private static void requireOn(Signal signal, String name) {
    if (!signal.isOn()) {
      throw new IllegalArgumentException(String.format(TOO_WEAK, name, signal.strength()));
    }
  }
}
