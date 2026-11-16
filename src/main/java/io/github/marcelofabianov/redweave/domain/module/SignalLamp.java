package io.github.marcelofabianov.redweave.domain.module;

import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.Objects;

/**
 * The rule of the Signal Lamp (answers 650 to 658): its light is the strength of its intensity
 * channel, and while lit it gives the strength chosen on its screen, from 1 to 15. Its optional
 * control turns it off: in `HOLD` while the control has a signal, and in `PULSE` at each pulse that
 * starts. The `Toggle` of its owner turns it off or on again by hand, as a pulse of the control.
 * `controlWasOn` keeps the control of the last tick, so a pulse is seen only when it starts.
 */
public record SignalLamp(
    Signal strength, LampMode mode, boolean switchedOff, boolean controlWasOn) {
  private static final String TOO_WEAK = "A lamp gives from 1 to 15, got %d";

  public SignalLamp {
    Objects.requireNonNull(strength);
    Objects.requireNonNull(mode);
    if (!strength.isOn()) {
      throw new IllegalArgumentException(String.format(TOO_WEAK, strength.strength()));
    }
  }

  /** A new lamp: giving 15, in `HOLD`, not switched off (answers 651 and 656). */
  public static SignalLamp placed() {
    return new SignalLamp(Signal.MAX, LampMode.HOLD, false, false);
  }

  /** The control of this tick: in `PULSE`, a pulse that starts turns it off or on (656). */
  public SignalLamp control(boolean on) {
    boolean starts = on && !controlWasOn;
    boolean flips = mode == LampMode.PULSE && starts;
    return new SignalLamp(strength, mode, flips != switchedOff, on);
  }

  /** The `Toggle` of the owner: off or on again by hand, as a pulse of the control (658). */
  public SignalLamp toggle() {
    return new SignalLamp(strength, mode, !switchedOff, controlWasOn);
  }

  /** Whether the control keeps it off now: switched off, or held by the control (656 and 657). */
  public boolean off(boolean controlOn) {
    return switchedOff || (mode == LampMode.HOLD && controlOn);
  }

  /** The light, the strength of the intensity channel, or none while off (answers 650 and 657). */
  public Signal light(Signal intensity, boolean controlOn) {
    return off(controlOn) ? Signal.OFF : intensity;
  }

  /** While lit, the strength of its screen; nothing while dark (answers 651 and 657). */
  public Signal output(Signal intensity, boolean controlOn) {
    return light(intensity, controlOn).isOn() ? strength : Signal.OFF;
  }

  /** Another strength, kept from 1 to 15, as the `−` and `+` of the screen step it (651). */
  public SignalLamp withStrength(int next) {
    return new SignalLamp(Emitter.stepped(next), mode, switchedOff, controlWasOn);
  }

  /** The other mode of the control, as the screen switches it (answer 656). */
  public SignalLamp withMode(LampMode next) {
    return new SignalLamp(strength, next, switchedOff, controlWasOn);
  }
}
