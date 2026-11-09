package io.github.marcelofabianov.redweave.domain.network;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class NetworkIdTest {
  private static final UUID FIRST = UUID.fromString("6f1c2b0e-3a4d-4e5f-8a9b-0c1d2e3f4a5b");
  private static final UUID SECOND = UUID.fromString("0a1b2c3d-4e5f-4a6b-8c7d-9e0f1a2b3c4d");

  @Test
  void twoIdsWithTheSameValueAreTheSameNetwork() {
    assertEquals(new NetworkId(FIRST), new NetworkId(FIRST));
  }

  @Test
  void twoIdsWithDifferentValuesAreDifferentNetworks() {
    assertNotEquals(new NetworkId(FIRST), new NetworkId(SECOND));
  }

  @Test
  void rejectsAMissingValue() {
    assertThrows(NullPointerException.class, () -> new NetworkId(null));
  }
}
