package io.github.zenndg.redweave.domain.module;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zenndg.redweave.domain.signal.Signal;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * The rule of the Signal Pressure Plate: off, at 15 and sensing players when placed, on only while
 * something of its mode stands on it, the Toggle cycling the modes, and the strength from 1 to 15
 * (answers 552, 603, 604, 607 and 621). A Signal Remote turns it on for a pulse, on top of what
 * stands on it (answers 783 and 793).
 */
class SignalPressurePlateTest {
  private static final int SEVEN = 7;
  private static final int TOO_STRONG = 20;
  private static final String HOSTILE = "hostile";
  private static final String UNKNOWN = "everything";
  private static final int PULSE = 20;
  private static final int SHORT_PULSE = 2;
  private static final int NEGATIVE = -1;

  @Test
  void aPlacedPlateIsOffAtTheStrongestSignalSensingPlayers() {
    SignalPressurePlate placed = SignalPressurePlate.placed();

    assertFalse(placed.on());
    assertEquals(Signal.MAX, placed.strength());
    assertEquals(PlateMode.PLAYERS, placed.mode());
    assertEquals(Signal.OFF, placed.output());
  }

  @Test
  void somethingOfItsModeTurnsItOn() {
    SignalPressurePlate on = SignalPressurePlate.placed().sensed(Set.of(PlateMode.PLAYERS));

    assertTrue(on.on());
    assertEquals(Signal.MAX, on.output());
  }

  @Test
  void somethingOfAnotherModeLeavesItOff() {
    SignalPressurePlate plate = SignalPressurePlate.placed();

    assertFalse(plate.senses(List.of(PlateMode.PASSIVE, PlateMode.HOSTILE, PlateMode.ITEMS)));
    assertFalse(plate.sensed(List.of(PlateMode.PASSIVE, PlateMode.ITEMS)).on());
  }

  @Test
  void nothingOnItTurnsItOff() {
    SignalPressurePlate on = SignalPressurePlate.placed().sensed(Set.of(PlateMode.PLAYERS));

    assertFalse(on.sensed(Set.of()).on());
  }

  @Test
  void theToggleCyclesTheModes() {
    SignalPressurePlate plate = SignalPressurePlate.placed();

    assertEquals(PlateMode.PASSIVE, plate.toggled().mode());
    assertEquals(PlateMode.HOSTILE, plate.toggled().toggled().mode());
    assertEquals(PlateMode.ITEMS, plate.toggled().toggled().toggled().mode());
    assertEquals(PlateMode.PLAYERS, plate.toggled().toggled().toggled().toggled().mode());
  }

  @Test
  void whileOnItGivesItsStrength() {
    SignalPressurePlate on =
        SignalPressurePlate.placed().withStrength(SEVEN).sensed(Set.of(PlateMode.PLAYERS));

    assertEquals(new Signal(SEVEN), on.output());
  }

  @Test
  void theStrengthStaysFromOneToFifteen() {
    SignalPressurePlate plate = SignalPressurePlate.placed();

    assertEquals(Signal.MAX, plate.withStrength(TOO_STRONG).strength());
    assertEquals(new Signal(SignalPressurePlate.WEAKEST), plate.withStrength(0).strength());
  }

  @Test
  void aPlateCannotGiveNothingWhileOn() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new SignalPressurePlate(true, Signal.OFF, PlateMode.HOSTILE));
  }

  @Test
  void theKeysReadBack() {
    assertEquals(HOSTILE, PlateMode.HOSTILE.key());
    assertEquals(PlateMode.HOSTILE, PlateMode.fromKey(HOSTILE).orElseThrow());
    assertTrue(PlateMode.fromKey(UNKNOWN).isEmpty());
  }

  @Test
  void aPulseOfTheRemoteTurnsItOnWithNothingOnIt() {
    SignalPressurePlate pulsed = SignalPressurePlate.placed().pulsed(PULSE);

    assertTrue(pulsed.on());
    assertEquals(PULSE, pulsed.pulse());
    assertEquals(Signal.MAX, pulsed.output());
  }

  @Test
  void thePulseEndsAfterItsTicks() {
    SignalPressurePlate plate = SignalPressurePlate.placed().pulsed(SHORT_PULSE).tick();

    assertTrue(plate.on());
    assertFalse(plate.tick().on());
    assertEquals(0, plate.tick().pulse());
  }

  /** Something of its mode on it keeps it on after the pulse ends (answer 783). */
  @Test
  void whatStandsOnItKeepsItOnAfterThePulse() {
    SignalPressurePlate plate =
        SignalPressurePlate.placed()
            .pulsed(SHORT_PULSE)
            .sensed(Set.of(PlateMode.PLAYERS))
            .tick()
            .tick();

    assertTrue(plate.on());
    assertEquals(0, plate.pulse());
  }

  /** Nothing on it does not cut the pulse short, as the remote holds it on (answer 783). */
  @Test
  void nothingOnItLeavesThePulseOn() {
    SignalPressurePlate plate = SignalPressurePlate.placed().pulsed(PULSE).sensed(Set.of());

    assertTrue(plate.on());
    assertFalse(plate.stood());
  }

  @Test
  void aClickDuringThePulseStartsItAgain() {
    SignalPressurePlate plate = SignalPressurePlate.placed().pulsed(PULSE).tick().pulsed(PULSE);

    assertEquals(PULSE, plate.pulse());
  }

  @Test
  void anOffPlateStaysTheSameOnATick() {
    SignalPressurePlate placed = SignalPressurePlate.placed();

    assertEquals(placed, placed.tick());
  }

  @Test
  void theModeAndTheStrengthKeepThePulse() {
    SignalPressurePlate plate =
        SignalPressurePlate.placed().pulsed(PULSE).toggled().withStrength(SEVEN);

    assertEquals(PULSE, plate.pulse());
  }

  @Test
  void aPulseCannotBeNegative() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new SignalPressurePlate(false, Signal.MAX, PlateMode.PLAYERS, NEGATIVE));
  }
}
