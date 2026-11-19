package io.github.zenndg.redweave.domain.signal;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

/**
 * The 16 wool colors, in the order of the game. Each one is a channel of the network, and the order
 * decides the first free channel (answer 25).
 */
public enum ChannelColor {
  WHITE,
  ORANGE,
  MAGENTA,
  LIGHT_BLUE,
  YELLOW,
  LIME,
  PINK,
  GRAY,
  LIGHT_GRAY,
  CYAN,
  PURPLE,
  BLUE,
  BROWN,
  GREEN,
  RED,
  BLACK;

  /** The number shown on the screens, from 1 to 16. */
  public int number() {
    return ordinal() + 1;
  }

  /**
   * The name saved in the world and in the items, the same as the dye of the game. A test pins
   * every key, because renaming a constant would break the saves (AGENTS.md, rule 15).
   */
  public String key() {
    return name().toLowerCase(Locale.ROOT);
  }

  /** An unknown key, as from a damaged save, gives no color instead of breaking the game. */
  public static Optional<ChannelColor> fromKey(String key) {
    return Arrays.stream(values()).filter(color -> color.key().equals(key)).findFirst();
  }
}
