package io.github.zenndg.redweave.server;

import io.github.zenndg.redweave.application.KeypadSalts;
import java.security.SecureRandom;
import java.util.HexFormat;

/** A new random salt of 16 bytes for each code of a Signal Keypad, in hex (answer 897). */
final class SecureKeypadSalts implements KeypadSalts {
  private static final int BYTES = 16;

  private final SecureRandom random = new SecureRandom();

  @Override
  public String next() {
    byte[] salt = new byte[BYTES];
    random.nextBytes(salt);
    return HexFormat.of().formatHex(salt);
  }
}
