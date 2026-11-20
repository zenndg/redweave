package io.github.zenndg.redweave.application;

import static io.github.zenndg.redweave.application.ApplicationFixture.NODE_ID;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.STRANGER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zenndg.redweave.domain.module.CardId;
import io.github.zenndg.redweave.domain.module.KeypadCode;
import io.github.zenndg.redweave.domain.module.KeypadLimits;
import io.github.zenndg.redweave.domain.module.SignalKeypad;
import io.github.zenndg.redweave.domain.module.SignalModule;
import io.github.zenndg.redweave.domain.network.ModuleId;
import io.github.zenndg.redweave.domain.signal.Signal;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/** Only the owner changes a Signal Keypad (answers 663, 669, 905 and 915). */
class ChangeKeypadTest {
  private static final int SEVEN = 7;
  private static final int FIVE_SECONDS = 100;
  private static final String SALT = "abcdef0123456789abcdef0123456789";
  private static final String CARD_UUID = "2f3a4b5c-6d7e-4f8a-9b0c-1d2e3f4a5b6c";
  private static final CardId CARD = new CardId(UUID.fromString(CARD_UUID));
  private static final KeypadCode CODE = new KeypadCode("1984");
  private static final KeypadLimits LIMITS = new Limits();

  private final ChangeKeypad change = new ChangeKeypad(() -> SALT);
  private final SignalModule module = SignalModule.placed(new ModuleId(NODE_ID), OWNER);
  private final SignalKeypad keypad = SignalKeypad.placed();

  @Test
  void theOwnerChangesTheStrengthAndTheOpenTime() {
    assertEquals(
        new Signal(SEVEN), change.strength(OWNER, module, keypad, SEVEN).orElseThrow().strength());
    assertEquals(
        FIVE_SECONDS,
        change.open(OWNER, module, keypad, FIVE_SECONDS, LIMITS).orElseThrow().openTicks());
  }

  @Test
  void theOwnerSetsACodeWithANewSalt() {
    SignalKeypad coded = change.code(OWNER, module, keypad, CODE).orElseThrow();

    assertEquals(SALT, coded.secret().orElseThrow().salt());
    assertTrue(coded.secret().orElseThrow().matches(CODE));
  }

  @Test
  void theOwnerRevokesACard() {
    SignalKeypad linked = keypad.link(CARD, () -> 1).orElseThrow();

    assertFalse(change.revoke(OWNER, module, linked, CARD).orElseThrow().cards().accepts(CARD));
  }

  @Test
  void aStrangerChangesNothing() {
    assertTrue(change.strength(STRANGER, module, keypad, SEVEN).isEmpty());
    assertTrue(change.open(STRANGER, module, keypad, FIVE_SECONDS, LIMITS).isEmpty());
    assertTrue(change.code(STRANGER, module, keypad, CODE).isEmpty());
    assertTrue(change.revoke(STRANGER, module, keypad, CARD).isEmpty());
  }

  /** The starting limits of _docs/limites.md (answer 915). */
  private static final class Limits implements KeypadLimits {
    private static final int MIN_OPEN = 20;
    private static final int MAX_OPEN = 600;
    private static final int FREE_WINDOW = 1200;
    private static final int FREE_TRIES = 3;
    private static final int FIRST_WAIT = 100;
    private static final int MAX_WAIT = 6000;
    private static final int RESET = 12000;

    @Override
    public int minIntervalTicks() {
      return MIN_OPEN;
    }

    @Override
    public int maxIntervalTicks() {
      return MAX_OPEN;
    }

    @Override
    public int freeWindowTicks() {
      return FREE_WINDOW;
    }

    @Override
    public int freeTries() {
      return FREE_TRIES;
    }

    @Override
    public int firstWaitTicks() {
      return FIRST_WAIT;
    }

    @Override
    public int maxWaitTicks() {
      return MAX_WAIT;
    }

    @Override
    public int resetTicks() {
      return RESET;
    }
  }
}
