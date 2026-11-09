package io.github.marcelofabianov.redweave.domain.network;

import java.util.Optional;

/** The rule of every name: no spaces at the ends, not empty and within the limit (answer 34). */
final class Names {
  private Names() {}

  static Optional<String> valid(String name, int maxLength) {
    String trimmed = name.strip();
    if (trimmed.isEmpty() || trimmed.length() > maxLength) {
      return Optional.empty();
    }
    return Optional.of(trimmed);
  }
}
