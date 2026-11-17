package io.github.marcelofabianov.redweave.domain.module;

import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.Collection;
import java.util.Objects;

/**
 * The rule of the Signal Pressure Plate: on while something of its mode is on it, or while the
 * pulse of a Signal Remote lasts, and the strength it gives while on, from 1 to 15, 15 when placed
 * (answers 552, 604, 621, 783 and 793). It senses players when placed. What stands on it comes from
 * the world, as the kinds of thing found there; the pulse is the ticks left of it.
 */
public record SignalPressurePlate(boolean stood, Signal strength, PlateMode mode, int pulse)
    implements Emitter {
  private static final String TOO_WEAK = "A pressure plate gives from 1 to 15, got %d";
  private static final String NEGATIVE_PULSE = "A pulse cannot have %d ticks left";
  private static final int NO_PULSE = 0;

  public SignalPressurePlate {
    Objects.requireNonNull(strength);
    Objects.requireNonNull(mode);
    if (!strength.isOn()) {
      throw new IllegalArgumentException(String.format(TOO_WEAK, strength.strength()));
    }
    if (pulse < NO_PULSE) {
      throw new IllegalArgumentException(String.format(NEGATIVE_PULSE, pulse));
    }
  }

  /** A plate with no pulse of a Signal Remote, as the saves before it had (answer 793). */
  public SignalPressurePlate(boolean stood, Signal strength, PlateMode mode) {
    this(stood, strength, mode, NO_PULSE);
  }

  /** A new plate: off, with the strongest signal, sensing players (answers 552 and 604). */
  public static SignalPressurePlate placed() {
    return new SignalPressurePlate(false, Signal.MAX, PlateMode.PLAYERS);
  }

  /** On while something of its mode stands on it or the pulse lasts (answers 607 and 783). */
  @Override
  public boolean on() {
    return stood || pulse > NO_PULSE;
  }

  /** What stands on it now: something of its mode or not; the pulse goes on (answer 607). */
  public SignalPressurePlate sensed(Collection<PlateMode> present) {
    return new SignalPressurePlate(present.contains(mode), strength, mode, pulse);
  }

  /** Whether something of its mode is among what stands on it. */
  public boolean senses(Collection<PlateMode> present) {
    return present.contains(mode);
  }

  /** The click of a Signal Remote: on for the ticks of its pulse, again if on (answer 783). */
  public SignalPressurePlate pulsed(int ticks) {
    return new SignalPressurePlate(stood, strength, mode, ticks);
  }

  /** One tick of the pulse: the last one ends it. With no pulse, nothing changes. */
  public SignalPressurePlate tick() {
    return pulse == NO_PULSE ? this : new SignalPressurePlate(stood, strength, mode, pulse - 1);
  }

  /** The next mode, as the Toggle of the Configurator asks (answer 603). */
  public SignalPressurePlate toggled() {
    return withMode(mode.next());
  }

  /** Another mode, as the owner picks on the screen (answer 602). */
  public SignalPressurePlate withMode(PlateMode next) {
    return new SignalPressurePlate(stood, strength, next, pulse);
  }

  /** Another strength, kept from 1 to 15, as the `−` and `+` of the screen step it (552). */
  public SignalPressurePlate withStrength(int next) {
    return new SignalPressurePlate(stood, Emitter.stepped(next), mode, pulse);
  }
}
