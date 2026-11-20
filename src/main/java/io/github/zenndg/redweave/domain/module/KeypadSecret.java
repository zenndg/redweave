package io.github.zenndg.redweave.domain.module;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * The code of a Signal Keypad as the server keeps it: a SHA-256 hash of the code with a salt of the
 * keypad, both in hex, never the digits (answers 660 and 897). The caller gives the salt, so the
 * domain reads no random source.
 */
public record KeypadSecret(String hash, String salt) {
  private static final String ALGORITHM = "SHA-256";
  private static final int SALT_BYTES = 16;
  private static final int HASH_BYTES = 32;
  private static final int HEX_PER_BYTE = 2;
  private static final Pattern SALT = hex(SALT_BYTES);
  private static final Pattern HASH = hex(HASH_BYTES);
  private static final HexFormat HEX = HexFormat.of();
  private static final String HEX_DIGITS = "[0-9a-f]{%d}";
  private static final String INVALID = "A secret needs a hash of %d and a salt of %d hex digits";

  public KeypadSecret {
    Objects.requireNonNull(hash, invalid());
    Objects.requireNonNull(salt, invalid());
    if (!HASH.matcher(hash).matches() || !SALT.matcher(salt).matches()) {
      throw new IllegalArgumentException(invalid());
    }
  }

  /** The secret of the code with the salt. */
  public static KeypadSecret of(KeypadCode code, String salt) {
    return new KeypadSecret(HEX.formatHex(digest(code, salt)), salt);
  }

  /** Whether the code is the one kept, compared in constant time. */
  public boolean matches(KeypadCode code) {
    return MessageDigest.isEqual(digest(code, salt), HEX.parseHex(hash));
  }

  private static byte[] digest(KeypadCode code, String salt) {
    try {
      MessageDigest sha = MessageDigest.getInstance(ALGORITHM);
      sha.update(HEX.parseHex(salt));
      return sha.digest(code.digits().getBytes(StandardCharsets.UTF_8));
    } catch (NoSuchAlgorithmException missing) {
      // Every Java runtime has SHA-256: this is a broken runtime, not invalid data (rule 14).
      throw new IllegalStateException(ALGORITHM, missing);
    }
  }

  private static Pattern hex(int bytes) {
    return Pattern.compile(String.format(HEX_DIGITS, bytes * HEX_PER_BYTE));
  }

  private static String invalid() {
    return String.format(INVALID, HASH_BYTES * HEX_PER_BYTE, SALT_BYTES * HEX_PER_BYTE);
  }
}
