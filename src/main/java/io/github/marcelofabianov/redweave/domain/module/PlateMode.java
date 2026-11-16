package io.github.marcelofabianov.redweave.domain.module;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

/**
 * What a Signal Pressure Plate senses, one at a time: players that are not spectators, passive
 * living mobs, hostile ones, or items on the ground (answer 621). Arrows and other entities are
 * none of them. The order is the one of the Toggle, and the key is part of the save (answer 608,
 * rule 15).
 */
public enum PlateMode {
  PLAYERS,
  PASSIVE,
  HOSTILE,
  ITEMS;

  public String key() {
    return name().toLowerCase(Locale.ROOT);
  }

  public static Optional<PlateMode> fromKey(String key) {
    return Arrays.stream(values()).filter(mode -> mode.key().equals(key)).findFirst();
  }

  /** The mode after this one, for the Toggle: players, passive, hostile, items (621). */
  PlateMode next() {
    return values()[(ordinal() + 1) % values().length];
  }
}
