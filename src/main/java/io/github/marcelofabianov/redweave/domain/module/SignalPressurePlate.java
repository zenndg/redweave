package io.github.marcelofabianov.redweave.domain.module;

import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.Collection;
import java.util.Objects;

/**
 * The rule of the Signal Pressure Plate: on while something of its mode is on it, and the strength
 * it gives while on, from 1 to 15, 15 when placed (answers 552, 604 and 621). It senses players
 * when placed. What stands on it comes from the world, as the kinds of thing found there.
 */
public record SignalPressurePlate(boolean on, Signal strength, PlateMode mode) {
  /** The weakest signal a plate gives while on (answer 552). */
  public static final int WEAKEST = 1;

  private static final String TOO_WEAK = "A pressure plate gives from 1 to 15, got %d";

  public SignalPressurePlate {
    Objects.requireNonNull(strength);
    Objects.requireNonNull(mode);
    if (!strength.isOn()) {
      throw new IllegalArgumentException(String.format(TOO_WEAK, strength.strength()));
    }
  }

  /** A new plate: off, with the strongest signal, sensing players (answers 552 and 604). */
  public static SignalPressurePlate placed() {
    return new SignalPressurePlate(false, Signal.MAX, PlateMode.PLAYERS);
  }

  /** On when something of its mode is among what stands on it, off otherwise (answer 607). */
  public SignalPressurePlate sensed(Collection<PlateMode> present) {
    return new SignalPressurePlate(present.contains(mode), strength, mode);
  }

  /** Whether something of its mode is among what stands on it. */
  public boolean senses(Collection<PlateMode> present) {
    return present.contains(mode);
  }

  /** The next mode, as the Toggle of the Configurator asks (answer 603). */
  public SignalPressurePlate toggled() {
    return withMode(mode.next());
  }

  /** Another mode, as the owner picks on the screen (answer 602). */
  public SignalPressurePlate withMode(PlateMode next) {
    return new SignalPressurePlate(on, strength, next);
  }

  /** Another strength, kept from 1 to 15, as the `−` and `+` of the screen step it (552). */
  public SignalPressurePlate withStrength(int next) {
    return new SignalPressurePlate(on, Signal.clamped(Math.max(next, WEAKEST)), mode);
  }

  /** The strength while on, nothing while off. */
  public Signal output() {
    return on ? strength : Signal.OFF;
  }
}
