package io.github.marcelofabianov.redweave.menu;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;
import org.junit.jupiter.api.Test;

/** A button of the Network Terminal carries an action and a row, and nothing else (rule 17). */
class TerminalScreenCodesTest {
  private static final int ROWS = 5;
  private static final int NEGATIVE = -1;

  @Test
  void everyActionOfEveryRowComesBackAsItWent() {
    for (int row = 0; row < ROWS; row++) {
      for (TerminalAction action : TerminalAction.values()) {
        TerminalScreenCodes.Click click = new TerminalScreenCodes.Click(row, action);
        assertEquals(
            Optional.of(click), TerminalScreenCodes.clickOf(TerminalScreenCodes.button(click)));
      }
    }
  }

  @Test
  void aNegativeNumberIsRefused() {
    assertEquals(Optional.empty(), TerminalScreenCodes.clickOf(NEGATIVE));
  }
}
