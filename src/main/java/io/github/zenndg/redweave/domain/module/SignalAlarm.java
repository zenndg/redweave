package io.github.zenndg.redweave.domain.module;

import java.util.Objects;

/**
 * The rule of the Signal Alarm: when its input turns on, it rings for its duration, once every
 * interval up to its rings, and then stops, even with the input still on; it rings again only after
 * the input turns off and on (answers 646 and 648). The `Toggle` silences it until the input turns
 * off (answer 649). `remaining` is the ticks left of the alarm, `ringsLeft` the rings it still has,
 * `untilRing` the ticks to the next ring, and `wasOn` the input of the last tick.
 */
public record SignalAlarm(
    AlarmSettings settings, int remaining, int ringsLeft, int untilRing, boolean wasOn) {
  private static final String MISSING = "An alarm needs its settings";
  private static final String NEGATIVE = "An alarm cannot count below 0: %d, %d, %d";

  public SignalAlarm {
    Objects.requireNonNull(settings, MISSING);
    if (remaining < 0 || ringsLeft < 0 || untilRing < 0) {
      throw new IllegalArgumentException(String.format(NEGATIVE, remaining, ringsLeft, untilRing));
    }
  }

  /** A new alarm, quiet, with the settings of one just placed. */
  public static SignalAlarm placed(AlarmLimits limits) {
    return new SignalAlarm(AlarmSettings.placed(limits), 0, 0, 0, false);
  }

  /** Whether it is ringing now: within its duration (answer 649). */
  public boolean ringing() {
    return remaining > 0;
  }

  /**
   * One tick with the input on or off: a rising input starts the alarm, and while it lasts it rings
   * when the interval comes, up to its rings.
   */
  public Step tick(boolean inputOn) {
    SignalAlarm now = inputOn && !wasOn ? started() : this;
    if (!now.ringing()) {
      return new Step(now.withInput(inputOn), false);
    }
    boolean ring = now.untilRing == 0 && now.ringsLeft > 0;
    int rings = ring ? now.ringsLeft - 1 : now.ringsLeft;
    int until = Math.max((ring ? settings.intervalTicks() : now.untilRing) - 1, 0);
    return new Step(new SignalAlarm(settings, now.remaining - 1, rings, until, inputOn), ring);
  }

  /** Silences it until its input turns off: no more rings, and the light goes dark (649). */
  public SignalAlarm silenced() {
    return new SignalAlarm(settings, 0, 0, 0, wasOn);
  }

  /** Other settings; an alarm under way keeps what it has left. */
  public SignalAlarm withSettings(AlarmSettings next) {
    return new SignalAlarm(next, remaining, ringsLeft, untilRing, wasOn);
  }

  private SignalAlarm started() {
    return new SignalAlarm(settings, settings.durationTicks(), settings.repeats(), 0, wasOn);
  }

  private SignalAlarm withInput(boolean inputOn) {
    return inputOn == wasOn
        ? this
        : new SignalAlarm(settings, remaining, ringsLeft, untilRing, inputOn);
  }

  /** The alarm after a tick, and whether it rang on that tick. */
  public record Step(SignalAlarm alarm, boolean ring) {}
}
