package io.github.marcelofabianov.redweave.domain.module;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * The rule of the Signal Lamp: its light is the strength of its intensity channel, it gives the
 * strength of its screen while lit, and its control turns it off while held or at each pulse
 * (answers 650, 651 and 656 to 658).
 */
class SignalLampTest {
  private static final int SEVEN = 7;
  private static final int NINE = 9;
  private static final int TOO_STRONG = 20;
  private static final Signal INTENSITY = new Signal(NINE);
  private static final String HOLD_KEY = "hold";
  private static final String PULSE_KEY = "pulse";
  private static final String UNKNOWN_KEY = "blink";

  @Test
  void aPlacedLampGivesFifteenInHold() {
    SignalLamp placed = SignalLamp.placed();

    assertEquals(Signal.MAX, placed.strength());
    assertEquals(LampMode.HOLD, placed.mode());
    assertFalse(placed.switchedOff());
  }

  @Test
  void itsLightIsTheIntensityAndItGivesItsStrength() {
    SignalLamp lamp = SignalLamp.placed().withStrength(SEVEN);

    assertEquals(INTENSITY, lamp.light(INTENSITY, false));
    assertEquals(new Signal(SEVEN), lamp.output(INTENSITY, false));
  }

  @Test
  void withNoIntensityItIsDarkAndGivesNothing() {
    SignalLamp lamp = SignalLamp.placed();

    assertEquals(Signal.OFF, lamp.light(Signal.OFF, false));
    assertEquals(Signal.OFF, lamp.output(Signal.OFF, false));
  }

  @Test
  void inHoldTheControlKeepsItOffOnlyWhileOn() {
    SignalLamp lamp = SignalLamp.placed().control(true);

    assertTrue(lamp.off(true));
    assertEquals(Signal.OFF, lamp.output(INTENSITY, true));
    assertFalse(lamp.control(false).off(false));
  }

  @Test
  void inPulseEachPulseThatStartsSwitchesIt() {
    SignalLamp pulse = SignalLamp.placed().withMode(LampMode.PULSE);

    SignalLamp first = pulse.control(true);
    assertTrue(first.off(true));
    assertTrue(first.control(true).off(true));
    SignalLamp released = first.control(false);
    assertTrue(released.off(false));
    assertFalse(released.control(true).off(true));
  }

  @Test
  void theToggleSwitchesItByHand() {
    SignalLamp off = SignalLamp.placed().toggle();

    assertTrue(off.off(false));
    assertFalse(off.toggle().off(false));
  }

  @Test
  void theStrengthStaysFromOneToFifteen() {
    SignalLamp lamp = SignalLamp.placed();

    assertEquals(Signal.MAX, lamp.withStrength(TOO_STRONG).strength());
    assertEquals(new Signal(Emitter.WEAKEST), lamp.withStrength(0).strength());
  }

  @Test
  void aLampNeverHoldsANoSignalStrength() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new SignalLamp(Signal.OFF, LampMode.HOLD, false, false));
    assertThrows(NullPointerException.class, () -> new SignalLamp(Signal.MAX, null, false, false));
    assertThrows(
        NullPointerException.class, () -> new SignalLamp(null, LampMode.HOLD, false, false));
  }

  @Test
  void theModesHaveTheirKeys() {
    assertEquals(HOLD_KEY, LampMode.HOLD.key());
    assertEquals(Optional.of(LampMode.PULSE), LampMode.fromKey(PULSE_KEY));
    assertTrue(LampMode.fromKey(UNKNOWN_KEY).isEmpty());
  }
}
