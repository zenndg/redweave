package io.github.marcelofabianov.redweave.domain.module;

import static io.github.marcelofabianov.redweave.domain.module.TestAlarmLimits.STARTING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** The rule of the Signal Alarm: alarme.md (answers 645, 646, 648 and 649). */
class SignalAlarmTest {
  private static final int REPEATS = 3;
  private static final int INTERVAL = 20;
  private static final int DURATION = 100;
  private static final int SHORT_DURATION = 30;
  private static final int TOO_MANY = 50;
  private static final int TOO_LONG = 9000;
  private static final int TOO_SHORT = 1;
  private static final int MAX_REPEATS = 5;
  private static final int MAX_DURATION = 600;
  private static final int LONG_INTERVAL = 40;

  private final SignalAlarm alarm = SignalAlarm.placed(STARTING);

  @Test
  void aNewAlarmIsQuietAndHeardByItsOwnerOnly() {
    assertFalse(alarm.ringing());
    assertEquals(AlarmListeners.OWNER, alarm.settings().listeners());
    assertEquals(REPEATS, alarm.settings().repeats());
    assertEquals(INTERVAL, alarm.settings().intervalTicks());
    assertEquals(DURATION, alarm.settings().durationTicks());
  }

  @Test
  void aRisingInputRingsAtOnceAndAtEachIntervalUpToItsRings() {
    int rings = 0;
    SignalAlarm now = alarm;
    for (int tick = 0; tick < DURATION; tick++) {
      SignalAlarm.Step step = now.tick(true);
      rings += step.ring() ? 1 : 0;
      now = step.alarm();
    }
    assertEquals(REPEATS, rings);
    assertFalse(now.ringing());
  }

  @Test
  void itStopsAtTheEndOfItsDurationEvenWithTheInputOn() {
    SignalAlarm now =
        alarm.withSettings(
            alarm.settings().withRepeats(1, STARTING).withDuration(SHORT_DURATION, STARTING));
    for (int tick = 0; tick < SHORT_DURATION; tick++) {
      now = now.tick(true).alarm();
    }
    assertFalse(now.ringing());
    assertFalse(now.tick(true).ring());
    assertFalse(now.tick(true).alarm().ringing());
  }

  @Test
  void itRingsAgainOnlyAfterTheInputTurnsOffAndOn() {
    SignalAlarm now = alarm;
    for (int tick = 0; tick < DURATION; tick++) {
      now = now.tick(true).alarm();
    }
    SignalAlarm off = now.tick(false).alarm();
    SignalAlarm.Step again = off.tick(true);

    assertTrue(again.ring());
    assertTrue(again.alarm().ringing());
  }

  @Test
  void anInputAlwaysOffNeverRings() {
    SignalAlarm.Step step = alarm.tick(false);

    assertFalse(step.ring());
    assertEquals(alarm, step.alarm());
  }

  @Test
  void silencedItStopsUntilTheInputTurnsOff() {
    SignalAlarm ringing = alarm.tick(true).alarm();
    SignalAlarm silenced = ringing.silenced();

    assertFalse(silenced.ringing());
    assertFalse(silenced.tick(true).ring());
    assertTrue(silenced.tick(false).alarm().tick(true).ring());
  }

  @Test
  void theSettingsStayWithinTheLimits() {
    AlarmSettings settings =
        alarm
            .settings()
            .withRepeats(TOO_MANY, STARTING)
            .withInterval(TOO_SHORT, STARTING)
            .withDuration(TOO_LONG, STARTING)
            .withListeners(AlarmListeners.RANGE);

    assertEquals(MAX_REPEATS, settings.repeats());
    assertEquals(INTERVAL, settings.intervalTicks());
    assertEquals(MAX_DURATION, settings.durationTicks());
    assertEquals(AlarmListeners.RANGE, settings.listeners());
    assertEquals(1, settings.withRepeats(0, STARTING).repeats());
  }

  @Test
  void savedSettingsOutsideANewConfigAreFitted() {
    AlarmSettings saved = new AlarmSettings(AlarmListeners.NETWORK, TOO_MANY, INTERVAL, TOO_LONG);

    assertFalse(saved.fits(STARTING));
    assertTrue(saved.fittedTo(STARTING).fits(STARTING));
    assertTrue(alarm.settings().fits(STARTING));
  }

  /** More rings or a longer interval stretch the duration, so every ring sounds (737). */
  @Test
  void theDurationFollowsItsRings() {
    AlarmSettings longer = alarm.settings().withInterval(LONG_INTERVAL, STARTING);

    assertEquals(REPEATS * LONG_INTERVAL, longer.durationTicks());
    assertEquals(
        REPEATS * LONG_INTERVAL, longer.withDuration(SHORT_DURATION, STARTING).durationTicks());
    assertEquals(REPEATS * LONG_INTERVAL, longer.ringsTicks());
    assertEquals(DURATION, alarm.settings().withRepeats(MAX_REPEATS, STARTING).durationTicks());
  }

  @Test
  void theListenersCycleInTurn() {
    assertEquals(AlarmListeners.NETWORK, AlarmListeners.OWNER.next());
    assertEquals(AlarmListeners.RANGE, AlarmListeners.NETWORK.next());
    assertEquals(AlarmListeners.OWNER, AlarmListeners.RANGE.next());
  }

  @Test
  void invalidNumbersAreRefused() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new AlarmSettings(AlarmListeners.OWNER, 0, INTERVAL, DURATION));
    assertThrows(
        IllegalArgumentException.class,
        () -> new AlarmSettings(AlarmListeners.OWNER, REPEATS, TOO_SHORT, DURATION));
    assertThrows(
        IllegalArgumentException.class,
        () -> new AlarmSettings(AlarmListeners.OWNER, REPEATS, INTERVAL, TOO_SHORT));
    assertThrows(
        IllegalArgumentException.class, () -> new SignalAlarm(alarm.settings(), -1, 0, 0, false));
    assertThrows(
        IllegalArgumentException.class, () -> new SignalAlarm(alarm.settings(), 0, -1, 0, false));
    assertThrows(
        IllegalArgumentException.class, () -> new SignalAlarm(alarm.settings(), 0, 0, -1, false));
  }
}
