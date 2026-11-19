package io.github.zenndg.redweave.domain.module;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zenndg.redweave.domain.signal.ChannelColor;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * A connection of a module is an edge of the plate or a channel of its network, never both, and
 * goes to the saves as `edge` or the key of the color (answers 376, 411 and 420).
 */
class ConnectionTest {
  private static final String EDGE_KEY = "edge";
  private static final String RED_KEY = "red";
  private static final String UNKNOWN_KEY = "violet";

  @Test
  void anEdgeHasNoChannel() {
    assertTrue(Connection.EDGE.isEdge());
    assertEquals(Optional.empty(), Connection.EDGE.channel());
  }

  @Test
  void aChannelConnectionHasItsColor() {
    Connection connection = Connection.on(ChannelColor.RED);

    assertFalse(connection.isEdge());
    assertEquals(Optional.of(ChannelColor.RED), connection.channel());
  }

  @Test
  void theKeysAreFixedForTheSaves() {
    assertEquals(EDGE_KEY, Connection.EDGE.key());
    assertEquals(RED_KEY, Connection.on(ChannelColor.RED).key());
  }

  @Test
  void aKeyGivesBackItsConnection() {
    assertEquals(Optional.of(Connection.EDGE), Connection.fromKey(EDGE_KEY));
    assertEquals(Optional.of(Connection.on(ChannelColor.RED)), Connection.fromKey(RED_KEY));
  }

  @Test
  void anUnknownKeyGivesNoConnection() {
    assertEquals(Optional.empty(), Connection.fromKey(UNKNOWN_KEY));
  }
}
