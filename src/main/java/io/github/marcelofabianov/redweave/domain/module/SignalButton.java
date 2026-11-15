package io.github.marcelofabianov.redweave.domain.module;

import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.Objects;

/**
 * The rule of the Signal Button: pressed, it turns on and gives its strength, from 1 to 15, for its
 * on time, and then turns off on its own (answers 548, 552 and 556). Pressing it while on does
 * nothing, and the time does not start over (answer 590). `remaining` is the ticks left while on,
 * saved so a reload goes on from there (answer 591), and 0 while off.
 */
public record SignalButton(boolean on, Signal strength, ButtonTime time, int remaining) {
  /** The weakest signal a button gives while on (answer 552). */
  public static final int WEAKEST = 1;

  private static final String TOO_WEAK = "A button gives from 1 to 15, got %d";
  private static final String INVALID_REMAINING = "%d ticks left on a button that is %s";
  private static final String ON = "on";
  private static final String OFF = "off";

  public SignalButton {
    Objects.requireNonNull(strength);
    Objects.requireNonNull(time);
    if (!strength.isOn()) {
      throw new IllegalArgumentException(String.format(TOO_WEAK, strength.strength()));
    }
    if (on != remaining > 0) {
      throw new IllegalArgumentException(
          String.format(INVALID_REMAINING, remaining, on ? ON : OFF));
    }
  }

  /** A new button: off, with the strongest signal and 1 s of on time (answers 552 and 556). */
  public static SignalButton placed(ButtonLimits limits) {
    return new SignalButton(
        false, Signal.MAX, ButtonTime.within(ButtonTime.PLACED_TICKS, limits), 0);
  }

  /** On for its whole on time; already on, nothing changes (answers 555, 576 and 590). */
  public SignalButton press() {
    return on ? this : new SignalButton(true, strength, time, time.ticks());
  }

  /** One tick of the on time: the last one turns it off. Off, nothing changes. */
  public SignalButton tick() {
    if (!on) {
      return this;
    }
    int left = remaining - 1;
    return new SignalButton(left > 0, strength, time, left);
  }

  /** Another strength, kept from 1 to 15, as the `−` and `+` of the screen step it. */
  public SignalButton withStrength(int next) {
    return new SignalButton(on, Signal.clamped(Math.max(next, WEAKEST)), time, remaining);
  }

  /** Another on time, within the limits; a press under way keeps the ticks it has left. */
  public SignalButton withTime(int ticks, ButtonLimits limits) {
    return new SignalButton(on, strength, ButtonTime.within(ticks, limits), remaining);
  }

  /** Whether the on time fits the limits of the config of now (answer 425). */
  public boolean fits(ButtonLimits limits) {
    return time.fits(limits);
  }

  /** The same button with its on time within the limits of now (answer 425). */
  public SignalButton fittedTo(ButtonLimits limits) {
    return new SignalButton(on, strength, time.fittedTo(limits), remaining);
  }

  /** The strength while on, nothing while off. */
  public Signal output() {
    return on ? strength : Signal.OFF;
  }
}
