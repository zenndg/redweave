package io.github.marcelofabianov.redweave.menu;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * A button of the Controller Screen is a color of the palette, and any other number from the client
 * is refused (rule 16 and answer 212).
 */
class ControllerScreenCodesTest {
  private static final int NEGATIVE = -1;
  private static final int PAST_THE_COLORS = ChannelColor.values().length;

  @Test
  void everyColorComesBackAsItWent() {
    for (ChannelColor color : ChannelColor.values()) {
      assertEquals(
          Optional.of(color), ControllerScreenCodes.colorOf(ControllerScreenCodes.button(color)));
    }
  }

  @Test
  void aNumberOutsideTheColorsIsRefused() {
    assertEquals(Optional.empty(), ControllerScreenCodes.colorOf(NEGATIVE));
    assertEquals(Optional.empty(), ControllerScreenCodes.colorOf(PAST_THE_COLORS));
  }
}
