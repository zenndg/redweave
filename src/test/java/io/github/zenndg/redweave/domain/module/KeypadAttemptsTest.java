package io.github.zenndg.redweave.domain.module;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zenndg.redweave.domain.network.PlayerId;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/**
 * The wrong tries of each player on a Signal Keypad: the free ones, the wait that doubles up to its
 * cap, and the counts that go back to 0, all on the time of the world (answers 664, 665, 907, 913
 * and 915).
 */
class KeypadAttemptsTest {
  private static final String THIEF_UUID = "1a2b3c4d-5e6f-4a7b-8c9d-0e1f2a3b4c5d";
  private static final String GUEST_UUID = "2b3c4d5e-6f7a-4b8c-9d0e-1f2a3b4c5d6e";
  private static final PlayerId THIEF = new PlayerId(UUID.fromString(THIEF_UUID));
  private static final PlayerId GUEST = new PlayerId(UUID.fromString(GUEST_UUID));
  private static final KeypadLimits LIMITS = TestKeypadLimits.STARTING;
  private static final long START = 1000;
  private static final int FIRST_WAIT = 100;
  private static final int SECOND_WAIT = 200;
  private static final int THIRD_WAIT = 400;
  private static final int MAX_WAIT = 6000;
  private static final int FREE = 3;
  private static final int THIRD_PAST = 3;
  private static final int MANY = 12;
  private static final int HALF_WAIT = 50;
  private static final int FREE_WINDOW = 1200;
  private static final int RESET = 12000;

  private static KeypadAttempts missed(PlayerId player, int times) {
    KeypadAttempts attempts = KeypadAttempts.NONE;
    for (int miss = 0; miss < times; miss++) {
      attempts = attempts.miss(player, START, LIMITS);
    }
    return attempts;
  }

  @Test
  void theFreeTriesDoNotWait() {
    KeypadAttempts attempts = missed(THIEF, FREE);

    assertEquals(FREE, attempts.count(THIEF, START, LIMITS));
    assertEquals(0, attempts.waitTicks(THIEF, START, LIMITS));
  }

  @Test
  void theFirstMissAfterTheFreeOnesWaitsAndEachMoreDoubles() {
    assertEquals(FIRST_WAIT, missed(THIEF, FREE + 1).waitTicks(THIEF, START, LIMITS));
    assertEquals(SECOND_WAIT, missed(THIEF, FREE + 2).waitTicks(THIEF, START, LIMITS));
    assertEquals(THIRD_WAIT, missed(THIEF, FREE + THIRD_PAST).waitTicks(THIEF, START, LIMITS));
  }

  @Test
  void theWaitStopsAtItsCap() {
    assertEquals(MAX_WAIT, missed(THIEF, MANY).waitTicks(THIEF, START, LIMITS));
  }

  @Test
  void theWaitGoesDownWithTheTimeOfTheWorld() {
    KeypadAttempts attempts = missed(THIEF, FREE + 1);

    assertEquals(HALF_WAIT, attempts.waitTicks(THIEF, START + HALF_WAIT, LIMITS));
    assertEquals(0, attempts.waitTicks(THIEF, START + FIRST_WAIT, LIMITS));
  }

  @Test
  void theFreeTriesComeBackAfterTheWindowWithNoMiss() {
    KeypadAttempts attempts = missed(THIEF, FREE).miss(THIEF, START + FREE_WINDOW, LIMITS);

    assertEquals(1, attempts.count(THIEF, START + FREE_WINDOW, LIMITS));
  }

  @Test
  void afterTheFreeTriesTheWindowDoesNotClearTheCount() {
    long later = START + FREE_WINDOW;
    KeypadAttempts attempts = missed(THIEF, FREE + 1).miss(THIEF, later, LIMITS);

    assertEquals(FREE + 2, attempts.count(THIEF, later, LIMITS));
  }

  @Test
  void tenMinutesWithNoTryClearTheCount() {
    long later = START + RESET;
    KeypadAttempts attempts = missed(THIEF, MANY);

    assertEquals(0, attempts.count(THIEF, later, LIMITS));
    assertEquals(0, attempts.waitTicks(THIEF, later, LIMITS));
    assertEquals(1, attempts.miss(THIEF, later, LIMITS).count(THIEF, later, LIMITS));
  }

  @Test
  void eachPlayerHasHisOwnCount() {
    KeypadAttempts attempts = missed(THIEF, MANY);

    assertEquals(0, attempts.count(GUEST, START, LIMITS));
    assertEquals(0, attempts.waitTicks(GUEST, START, LIMITS));
  }

  @Test
  void theRightCodeClearsTheCountOfThePlayer() {
    KeypadAttempts attempts = missed(THIEF, MANY).miss(GUEST, START, LIMITS).pass(THIEF);

    assertEquals(0, attempts.count(THIEF, START, LIMITS));
    assertEquals(1, attempts.count(GUEST, START, LIMITS));
  }

  @Test
  void prunedDropsThePlayersWithNoTryForTenMinutes() {
    KeypadAttempts attempts =
        missed(THIEF, MANY).miss(GUEST, START + RESET, LIMITS).pruned(START + RESET, LIMITS);

    assertEquals(1, attempts.byPlayer().size());
    assertTrue(attempts.byPlayer().containsKey(GUEST));
  }

  @Test
  void aTryNeedsAPositiveCountAndTick() {
    assertThrows(IllegalArgumentException.class, () -> new KeypadAttempt(0, START));
    assertThrows(IllegalArgumentException.class, () -> new KeypadAttempt(1, -1));
  }
}
