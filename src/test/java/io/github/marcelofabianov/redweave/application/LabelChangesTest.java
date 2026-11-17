package io.github.marcelofabianov.redweave.application;

import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OTHER_NODE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.marcelofabianov.redweave.domain.network.NodeId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** Only what changed goes to the player who holds the Configurator (answer 269 and rule 18). */
class LabelChangesTest {
  private static final String LAMP = "Lamp";
  private static final String DOOR = "Door";

  private static NodeLabel label(String name) {
    return new NodeLabel(name, Optional.empty(), Optional.empty());
  }

  @Test
  void theFirstLookSendsEveryLabel() {
    LabelChanges<NodeId, NodeLabel> changes = new LabelChanges<>();

    LabelChanges.Delta<NodeId, NodeLabel> delta = changes.next(Map.of(NODE, label(LAMP)));

    assertEquals(Map.of(NODE, label(LAMP)), delta.changed());
    assertTrue(delta.gone().isEmpty());
  }

  @Test
  void aLabelThatStaysTheSameIsNotSentAgain() {
    LabelChanges<NodeId, NodeLabel> changes = new LabelChanges<>();
    changes.next(Map.of(NODE, label(LAMP)));

    assertTrue(changes.next(Map.of(NODE, label(LAMP))).isEmpty());
  }

  @Test
  void aChangedLabelIsSentAndALabelGoneIsTold() {
    LabelChanges<NodeId, NodeLabel> changes = new LabelChanges<>();
    changes.next(Map.of(NODE, label(LAMP), OTHER_NODE, label(DOOR)));

    LabelChanges.Delta<NodeId, NodeLabel> delta = changes.next(Map.of(NODE, label(DOOR)));

    assertEquals(Map.of(NODE, label(DOOR)), delta.changed());
    assertEquals(List.<NodeId>of(OTHER_NODE), delta.gone());
  }

  /** A node that only left the reach is still a change to send. */
  @Test
  void aLabelThatOnlyLeftIsAChange() {
    LabelChanges<NodeId, NodeLabel> changes = new LabelChanges<>();
    changes.next(Map.of(NODE, label(LAMP)));

    LabelChanges.Delta<NodeId, NodeLabel> delta = changes.next(Map.of());

    assertTrue(delta.changed().isEmpty());
    assertEquals(List.<NodeId>of(NODE), delta.gone());
    assertTrue(!delta.isEmpty());
  }
}
