package io.github.zenndg.redweave.server;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

/**
 * The key of an enum value in the saves, in snake_case like the names of the game (answer 89). A
 * test pins every key, because renaming a constant would break the saves (rule 15).
 */
final class SaveKeys {
  private SaveKeys() {}

  static String of(Enum<?> value) {
    return value.name().toLowerCase(Locale.ROOT);
  }

  /** An unknown key, as from a damaged save, gives nothing instead of breaking the game. */
  static <E extends Enum<E>> Optional<E> parse(Class<E> type, String key) {
    return Arrays.stream(type.getEnumConstants())
        .filter(value -> of(value).equals(key))
        .findFirst();
  }
}
