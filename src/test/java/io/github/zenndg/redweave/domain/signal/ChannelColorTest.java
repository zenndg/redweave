package io.github.zenndg.redweave.domain.signal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** The order and the numbers come from the table of channels in _docs/conceito/visual.md. */
class ChannelColorTest {
  private static final int CHANNELS = 16;
  private static final int FIRST = 1;
  private static final int RED_NUMBER = 15;
  private static final String SEPARATOR = ",";
  private static final String FIRST_KEYS = "white,orange,magenta,light_blue,yellow,lime,pink,gray";
  private static final String LAST_KEYS = "light_gray,cyan,purple,blue,brown,green,red,black";
  private static final String UNKNOWN_KEY = "crimson";

  @Test
  void thereIsOneChannelForEachWoolColor() {
    assertEquals(CHANNELS, ChannelColor.values().length);
  }

  @Test
  void theChannelsFollowTheOrderOfTheGame() {
    assertEquals(FIRST, ChannelColor.WHITE.number());
    assertEquals(RED_NUMBER, ChannelColor.RED.number());
    assertEquals(CHANNELS, ChannelColor.BLACK.number());
  }

  /** The keys go to the saves, so they never change (AGENTS.md, rule 15). */
  @Test
  void theKeysAreTheSavedNamesOfTheColors() {
    List<String> expected = Arrays.asList((FIRST_KEYS + SEPARATOR + LAST_KEYS).split(SEPARATOR));
    assertEquals(expected, Arrays.stream(ChannelColor.values()).map(ChannelColor::key).toList());
  }

  @Test
  void aKeyGivesBackItsColor() {
    for (ChannelColor color : ChannelColor.values()) {
      assertEquals(Optional.of(color), ChannelColor.fromKey(color.key()));
    }
  }

  @Test
  void anUnknownKeyGivesNoColor() {
    assertEquals(Optional.empty(), ChannelColor.fromKey(UNKNOWN_KEY));
  }
}
