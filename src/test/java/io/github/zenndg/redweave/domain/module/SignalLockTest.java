package io.github.zenndg.redweave.domain.module;

import static io.github.zenndg.redweave.domain.module.TestLockLimits.STARTING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zenndg.redweave.domain.signal.Signal;
import org.junit.jupiter.api.Test;

/** The rule of the Signal Lock: fechadura.md (answers 633 to 636 and 640). */
class SignalLockTest {
  private static final int KEY = 9;
  private static final int GIVES = 4;
  private static final int PULSE = 3;
  private static final int LONGEST = 200;
  private static final int TOO_LONG = 500;
  private static final int TOO_STRONG = 20;
  private static final int PLACED_PULSE = 20;
  private static final int SEVEN = 7;

  private final SignalLock lock =
      SignalLock.placed(STARTING).withKey(KEY).withStrength(GIVES).withPulse(PULSE, STARTING);

  @Test
  void aNewLockHasKeyFifteenGivesFifteenAndPulsesOneSecond() {
    SignalLock placed = SignalLock.placed(STARTING);

    assertEquals(Signal.MAX, placed.key());
    assertEquals(Signal.MAX, placed.strength());
    assertEquals(new LockPulse(PLACED_PULSE), placed.pulse());
    assertFalse(placed.pulsing());
  }

  @Test
  void onlyTheExactKeyOpensIt() {
    assertEquals(new Signal(GIVES), lock.output(new Signal(KEY)));
    assertEquals(Signal.OFF, lock.output(new Signal(KEY - 1)));
    assertEquals(Signal.OFF, lock.output(new Signal(KEY + 1)));
  }

  @Test
  void noSignalNeverOpensIt() {
    assertFalse(lock.opens(Signal.OFF));
    assertEquals(Signal.OFF, lock.output(Signal.OFF));
  }

  @Test
  void theToggleOpensItForItsPulse() {
    SignalLock open = lock.unlock();

    assertTrue(open.open(Signal.OFF));
    SignalLock after = open.tick().tick().tick();
    assertFalse(after.open(Signal.OFF));
    assertSame(after, after.tick());
  }

  @Test
  void aPulseUnderWayDoesNotStartOver() {
    SignalLock open = lock.unlock().tick();

    assertSame(open, open.unlock());
  }

  @Test
  void theKeyAndTheStrengthStayFromOneToFifteen() {
    assertEquals(Signal.MAX, lock.withKey(TOO_STRONG).key());
    assertEquals(new Signal(1), lock.withKey(0).key());
    assertEquals(new Signal(1), lock.withStrength(-1).strength());
  }

  @Test
  void thePulseStaysWithinTheLimits() {
    assertEquals(new LockPulse(LONGEST), lock.withPulse(TOO_LONG, STARTING).pulse());
    assertEquals(new LockPulse(LockPulse.SHORTEST_TICKS), lock.withPulse(0, STARTING).pulse());
  }

  @Test
  void aSavedPulseOutsideTheConfigIsFitted() {
    SignalLock saved = new SignalLock(Signal.MAX, Signal.MAX, new LockPulse(TOO_LONG), 0);

    assertFalse(saved.fits(STARTING));
    assertEquals(new LockPulse(LONGEST), saved.fittedTo(STARTING).pulse());
    assertTrue(lock.fits(STARTING));
  }

  @Test
  void anInvalidLockIsRefused() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new SignalLock(Signal.OFF, Signal.MAX, new LockPulse(PLACED_PULSE), 0));
    assertThrows(
        IllegalArgumentException.class,
        () -> new SignalLock(Signal.MAX, Signal.OFF, new LockPulse(PLACED_PULSE), 0));
    assertThrows(
        IllegalArgumentException.class,
        () -> new SignalLock(Signal.MAX, Signal.MAX, new LockPulse(PLACED_PULSE), -1));
    assertThrows(IllegalArgumentException.class, () -> new LockPulse(1));
    assertEquals(new Signal(SEVEN), lock.withKey(SEVEN).key());
  }
}
