package io.github.zenndg.redweave.domain.module;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zenndg.redweave.domain.network.PlayerId;
import io.github.zenndg.redweave.domain.signal.Signal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/**
 * The rule of the Signal Keypad: the right code, with power on its input, opens it for its open
 * time; a wrong one counts for the player, never for the owner; a wait refuses any try; and a
 * linked card opens it as the code (answers 659 to 667, 900, 904 to 908, 913 and 914).
 */
class SignalKeypadTest {
  private static final String THIEF_UUID = "5e6f7a8b-9c0d-4e1f-8a2b-3c4d5e6f7a8b";
  private static final String CARD_UUID = "6f7a8b9c-0d1e-4f2a-9b3c-4d5e6f7a8b9c";
  private static final PlayerId THIEF = new PlayerId(UUID.fromString(THIEF_UUID));
  private static final KeypadCode CODE = new KeypadCode("2580");
  private static final KeypadCode WRONG = new KeypadCode("0000");
  private static final String SALT = "0123456789abcdef0123456789abcdef";
  private static final CardId CARD = new CardId(UUID.fromString(CARD_UUID));
  private static final KeypadLimits LIMITS = TestKeypadLimits.STARTING;
  private static final long NOW = 500;
  private static final int PLACED_OPEN = 60;
  private static final int SEVEN = 7;
  private static final int TOO_STRONG = 20;
  private static final int TOO_SHORT = 1;
  private static final int MIN_OPEN = 20;
  private static final int FREE = 3;
  private static final int FIRST_WAIT = 100;
  private static final int RESET = 12000;
  private static final boolean POWERED = true;
  private static final boolean OWNER = true;

  private static final SignalKeypad CODED = SignalKeypad.placed().withCode(CODE, SALT);

  private static KeypadEntry enter(SignalKeypad keypad, KeypadCode code) {
    return keypad.enter(new KeypadTry(THIEF, !OWNER, code, POWERED, NOW), LIMITS);
  }

  private static SignalKeypad missed(int times) {
    SignalKeypad keypad = CODED;
    for (int miss = 0; miss < times; miss++) {
      keypad = enter(keypad, WRONG).keypad();
    }
    return keypad;
  }

  @Test
  void aPlacedKeypadIsLockedWithNoCodeAndGivesFifteenForThreeSeconds() {
    SignalKeypad placed = SignalKeypad.placed();

    assertFalse(placed.unlocked());
    assertTrue(placed.secret().isEmpty());
    assertEquals(Signal.MAX, placed.strength());
    assertEquals(PLACED_OPEN, placed.openTicks());
    assertEquals(Signal.OFF, placed.output());
  }

  @Test
  void withNoPowerOrNoCodeItDoesNotOpen() {
    KeypadTry unpowered = new KeypadTry(THIEF, !OWNER, CODE, !POWERED, NOW);

    assertEquals(KeypadEntry.Kind.NO_POWER, CODED.enter(unpowered, LIMITS).kind());
    assertEquals(KeypadEntry.Kind.NO_CODE, enter(SignalKeypad.placed(), CODE).kind());
  }

  @Test
  void theRightCodeOpensItForItsOpenTime() {
    KeypadEntry entry = enter(CODED, CODE);

    assertEquals(KeypadEntry.Kind.GRANTED, entry.kind());
    assertTrue(entry.keypad().unlocked());
    assertEquals(PLACED_OPEN, entry.keypad().remaining());
    assertEquals(Signal.MAX, entry.keypad().output());
  }

  @Test
  void aWrongCodeCountsAndWarnsTheOwnerAfterTheFreeTries() {
    KeypadEntry first = enter(CODED, WRONG);
    KeypadEntry past = enter(missed(FREE), WRONG);

    assertEquals(KeypadEntry.Kind.WRONG, first.kind());
    assertEquals(1, first.misses());
    assertFalse(first.warnsOwner());
    assertFalse(first.keypad().unlocked());
    assertEquals(FREE + 1, past.misses());
    assertTrue(past.warnsOwner());
  }

  @Test
  void duringTheWaitNotEvenTheRightCodeIsChecked() {
    KeypadEntry entry = enter(missed(FREE + 1), CODE);

    assertEquals(KeypadEntry.Kind.WAIT, entry.kind());
    assertEquals(FIRST_WAIT, entry.waitTicks());
    assertFalse(entry.keypad().unlocked());
  }

  @Test
  void theRightCodeClearsTheTriesOfThePlayer() {
    SignalKeypad passed = enter(missed(FREE), CODE).keypad();

    assertEquals(0, passed.attempts().count(THIEF, NOW, LIMITS));
  }

  @Test
  void theMissesOfTheOwnerDoNotCount() {
    KeypadEntry entry = CODED.enter(new KeypadTry(THIEF, OWNER, WRONG, POWERED, NOW), LIMITS);

    assertEquals(KeypadEntry.Kind.WRONG, entry.kind());
    assertEquals(0, entry.misses());
    assertFalse(entry.warnsOwner());
    assertTrue(entry.keypad().attempts().byPlayer().isEmpty());
  }

  @Test
  void aNewCodeClearsTheTriesOfEveryone() {
    SignalKeypad changed = missed(FREE + 1).withCode(WRONG, SALT);

    assertTrue(changed.attempts().byPlayer().isEmpty());
    assertEquals(KeypadEntry.Kind.GRANTED, enter(changed, WRONG).kind());
  }

  @Test
  void itClosesWhenItsOpenTimeRunsOut() {
    SignalKeypad open = CODED.toggle();

    SignalKeypad closing = open;
    for (int tick = 0; tick < PLACED_OPEN; tick++) {
      assertTrue(closing.unlocked());
      closing = closing.tick();
    }
    assertFalse(closing.unlocked());
    assertFalse(closing.tick().unlocked());
  }

  @Test
  void theToggleOpensItForItsOpenTime() {
    assertEquals(PLACED_OPEN, SignalKeypad.placed().toggle().remaining());
  }

  @Test
  void aLinkedCardOpensItAsTheCodeWithPower() {
    SignalKeypad linked = CODED.link(CARD, TestCardLimits.STARTING).orElseThrow();

    assertEquals(KeypadEntry.Kind.GRANTED, linked.useCard(CARD, POWERED).kind());
    assertEquals(KeypadEntry.Kind.NO_POWER, linked.useCard(CARD, !POWERED).kind());
    assertEquals(KeypadEntry.Kind.NOT_LINKED, CODED.useCard(CARD, POWERED).kind());
    assertFalse(linked.revoke(CARD).cards().accepts(CARD));
  }

  @Test
  void itsStrengthAndOpenTimeStayWithinTheirLimits() {
    SignalKeypad keypad = SignalKeypad.placed().withStrength(SEVEN).withOpen(TOO_SHORT, LIMITS);

    assertEquals(new Signal(SEVEN), keypad.toggle().output());
    assertEquals(MIN_OPEN, keypad.openTicks());
    assertEquals(Signal.MAX, keypad.withStrength(TOO_STRONG).strength());
  }

  @Test
  void anInvalidKeypadIsRefused() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new SignalKeypad(
                Signal.OFF,
                PLACED_OPEN,
                0,
                Optional.empty(),
                KeypadAttempts.NONE,
                KeypadCards.NONE));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new SignalKeypad(
                Signal.MAX,
                PLACED_OPEN,
                -1,
                Optional.empty(),
                KeypadAttempts.NONE,
                KeypadCards.NONE));
  }

  @Test
  void anOpenTimeOfNoTicksIsRefused() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new SignalKeypad(
                Signal.MAX, 0, 0, Optional.empty(), KeypadAttempts.NONE, KeypadCards.NONE));
  }

  @Test
  void prunedDropsTheTriesTheResetCleared() {
    assertTrue(missed(FREE).pruned(NOW + RESET, LIMITS).attempts().byPlayer().isEmpty());
    assertEquals(FREE, missed(FREE).pruned(NOW, LIMITS).attempts().count(THIEF, NOW, LIMITS));
  }
}
