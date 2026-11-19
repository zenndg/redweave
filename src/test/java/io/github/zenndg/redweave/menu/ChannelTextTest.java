package io.github.zenndg.redweave.menu;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.zenndg.redweave.domain.signal.ChannelColor;
import org.junit.jupiter.api.Test;

/**
 * A channel shows its number and the name its owner gave, never the name of its color, because the
 * square beside it already shows the color (answers 770 and 777).
 */
class ChannelTextTest {
  private static final String DOOR = "Door";
  private static final String NO_NAME = "";
  private static final String WHITE_NUMBER = "01";
  private static final String BLACK_NUMBER = "16";
  private static final String NUMBER_AND_DOOR = "01 Door";

  @Test
  void aChannelWithNoNameShowsOnlyItsNumber() {
    assertEquals(WHITE_NUMBER, ChannelText.of(ChannelColor.WHITE, NO_NAME));
  }

  @Test
  void theNumberHasTwoDigits() {
    assertEquals(BLACK_NUMBER, ChannelText.of(ChannelColor.BLACK, NO_NAME));
  }

  @Test
  void aNameOfItsOwnFollowsTheNumber() {
    assertEquals(NUMBER_AND_DOOR, ChannelText.of(ChannelColor.WHITE, DOOR));
  }
}
