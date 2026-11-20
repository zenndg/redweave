package io.github.zenndg.redweave.domain.module;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/** The Signal Cards a keypad accepts, up to the limit of the config (answers 669 and 904). */
class KeypadCardsTest {
  private static final String CARD_UUID = "3c4d5e6f-7a8b-4c9d-8e0f-1a2b3c4d5e6f";
  private static final String OTHER_UUID = "4d5e6f7a-8b9c-4d0e-9f1a-2b3c4d5e6f7a";
  private static final CardId CARD = new CardId(UUID.fromString(CARD_UUID));
  private static final CardId OTHER = new CardId(UUID.fromString(OTHER_UUID));
  private static final CardLimits ONE = new TestCardLimits(1);

  @Test
  void aLinkedCardIsAcceptedAndAnotherIsNot() {
    KeypadCards cards = KeypadCards.NONE.link(CARD, TestCardLimits.STARTING).orElseThrow();

    assertTrue(cards.accepts(CARD));
    assertFalse(cards.accepts(OTHER));
  }

  @Test
  void aCardLinkedAgainIsKeptOnce() {
    KeypadCards cards = KeypadCards.NONE.link(CARD, ONE).orElseThrow();

    assertEquals(List.of(CARD), cards.link(CARD, ONE).orElseThrow().ids());
  }

  @Test
  void noCardIsLinkedPastTheLimit() {
    KeypadCards full = KeypadCards.NONE.link(CARD, ONE).orElseThrow();

    assertTrue(full.link(OTHER, ONE).isEmpty());
  }

  @Test
  void theOwnerRevokesACard() {
    KeypadCards cards = KeypadCards.NONE.link(CARD, TestCardLimits.STARTING).orElseThrow();

    assertFalse(cards.revoke(CARD).accepts(CARD));
  }

  @Test
  void aCardNeedsAnId() {
    assertThrows(NullPointerException.class, () -> new CardId(null));
    assertThrows(NullPointerException.class, () -> new KeypadCards(null));
  }
}
