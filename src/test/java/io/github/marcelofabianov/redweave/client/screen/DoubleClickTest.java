package io.github.marcelofabianov.redweave.client.screen;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * The owner renames a node or a channel of the Controller Screen with a double click on its name,
 * so a loose click never opens the field by mistake (answer 222).
 */
class DoubleClickTest {
  private static final String LEVER = "lever";
  private static final String DOOR = "door";
  private static final long START = 1_000;
  private static final long SOON = START + DoubleClick.WINDOW_MILLIS - 1;
  private static final long LATE = START + DoubleClick.WINDOW_MILLIS + 1;

  private long now = START;
  private final DoubleClick clicks = new DoubleClick(() -> now);

  @Test
  void oneClickIsNotADoubleClick() {
    assertFalse(clicks.click(LEVER));
  }

  @Test
  void twoQuickClicksOnTheSameNameAreADoubleClick() {
    clicks.click(LEVER);
    now = SOON;

    assertTrue(clicks.click(LEVER));
  }

  @Test
  void twoSlowClicksAreNot() {
    clicks.click(LEVER);
    now = LATE;

    assertFalse(clicks.click(LEVER));
  }

  @Test
  void twoClicksOnDifferentNamesAreNot() {
    clicks.click(LEVER);
    now = SOON;

    assertFalse(clicks.click(DOOR));
  }

  /** A third quick click starts over, so three clicks do not open the field twice. */
  @Test
  void aDoubleClickStartsOver() {
    clicks.click(LEVER);
    clicks.click(LEVER);

    assertFalse(clicks.click(LEVER));
  }

  @Test
  void aClickElsewhereBreaksThePair() {
    clicks.click(LEVER);
    clicks.reset();

    assertFalse(clicks.click(LEVER));
  }
}
