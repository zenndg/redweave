package io.github.marcelofabianov.redweave.domain.network;

import java.util.Optional;

/** The rule of every name: no spaces at the ends, not empty and within the limit (answer 34). */
public final class Names {
  private Names() {}

  public static Optional<String> valid(String name, int maxLength) {
    String trimmed = name.strip();
    if (trimmed.isEmpty() || trimmed.length() > maxLength) {
      return Optional.empty();
    }
    return Optional.of(trimmed);
  }

  /**
   * Whether a name given before is still whole: not empty and with no spaces at the ends. Its
   * length is not checked, because the limit of the config only holds for new names (answer 93).
   */
  static boolean isKept(String name) {
    return valid(name, name.length()).filter(name::equals).isPresent();
  }
}
