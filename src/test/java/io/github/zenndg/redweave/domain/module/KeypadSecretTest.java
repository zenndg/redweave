package io.github.zenndg.redweave.domain.module;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** The code kept as a SHA-256 hash with a salt of its own, never as digits (answer 897). */
class KeypadSecretTest {
  private static final KeypadCode CODE = new KeypadCode("4321");
  private static final KeypadCode OTHER = new KeypadCode("4322");
  private static final String SALT = "00112233445566778899aabbccddeeff";
  private static final String OTHER_SALT = "ffeeddccbbaa99887766554433221100";
  private static final String SHORT_SALT = "0011";
  private static final String NOT_HEX = "zz112233445566778899aabbccddeeff";
  private static final int HASH_LENGTH = 64;

  @Test
  void theRightCodeMatchesAndAnotherDoesNot() {
    KeypadSecret secret = KeypadSecret.of(CODE, SALT);

    assertTrue(secret.matches(CODE));
    assertFalse(secret.matches(OTHER));
  }

  @Test
  void itKeepsAHashAndNeverTheDigits() {
    KeypadSecret secret = KeypadSecret.of(CODE, SALT);

    assertEquals(HASH_LENGTH, secret.hash().length());
    assertFalse(secret.hash().contains(CODE.digits()));
    assertEquals(SALT, secret.salt());
  }

  @Test
  void theSameCodeWithAnotherSaltHasAnotherHash() {
    assertNotEquals(KeypadSecret.of(CODE, SALT).hash(), KeypadSecret.of(CODE, OTHER_SALT).hash());
  }

  @Test
  void aSavedSecretMatchesAsTheOneItCameFrom() {
    KeypadSecret made = KeypadSecret.of(CODE, SALT);

    assertTrue(new KeypadSecret(made.hash(), made.salt()).matches(CODE));
  }

  @Test
  void anInvalidHashOrSaltIsRefused() {
    String hash = KeypadSecret.of(CODE, SALT).hash();
    assertThrows(IllegalArgumentException.class, () -> new KeypadSecret(hash, SHORT_SALT));
    assertThrows(IllegalArgumentException.class, () -> new KeypadSecret(hash, NOT_HEX));
    assertThrows(IllegalArgumentException.class, () -> new KeypadSecret(SALT, SALT));
    assertThrows(NullPointerException.class, () -> new KeypadSecret(null, SALT));
  }
}
