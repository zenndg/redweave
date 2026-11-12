package io.github.marcelofabianov.redweave.domain.network;

import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** The kind of a node, from its enabled slots, for the Type column of the lists (283 and 337). */
class NodeKindTest {
  private static final BlockLocation BLOCK = new BlockLocation(NetworkFixture.OVERWORLD, 0, 0, 0);
  private static final boolean DISABLED = false;
  private static final ChannelSlot INPUT =
      new ChannelSlot(Direction.INPUT, Face.EAST, ChannelColor.RED);
  private static final ChannelSlot OTHER_INPUT =
      new ChannelSlot(Direction.INPUT, Face.UP, ChannelColor.LIME);
  private static final ChannelSlot OUTPUT =
      new ChannelSlot(Direction.OUTPUT, Face.EAST, ChannelColor.BLUE);
  private static final ChannelSlot DISABLED_OUTPUT =
      new ChannelSlot(Direction.OUTPUT, Face.EAST, ChannelColor.WHITE, DISABLED);
  private static final ChannelSlot DISABLED_INPUT =
      new ChannelSlot(Direction.INPUT, Face.EAST, ChannelColor.WHITE, DISABLED);

  private static Node node(ChannelSlot... slots) {
    Node node =
        new Node(
            NODE,
            Optional.empty(),
            new NodePosition(BLOCK, Face.EAST),
            Collections.nCopies(Node.SLOT_COUNT, Optional.empty()));
    for (ChannelSlot slot : slots) {
      node = node.withSlot(slot.channel().ordinal(), Optional.of(slot));
    }
    return node;
  }

  @Test
  void aNodeWithNoSlotIsNone() {
    assertEquals(NodeKind.NONE, node().kind());
  }

  @Test
  void aNodeWithOnlyDisabledSlotsIsNone() {
    assertEquals(NodeKind.NONE, node(DISABLED_INPUT).kind());
  }

  @Test
  void aNodeWithOnlyEnabledInputsIsInput() {
    assertEquals(NodeKind.INPUT, node(INPUT, OTHER_INPUT).kind());
  }

  @Test
  void aNodeWithOnlyEnabledOutputsIsOutput() {
    assertEquals(NodeKind.OUTPUT, node(OUTPUT).kind());
  }

  @Test
  void aNodeWithEnabledInputsAndOutputsIsBoth() {
    assertEquals(NodeKind.BOTH, node(INPUT, OUTPUT).kind());
  }

  @Test
  void aDisabledSlotDoesNotChangeTheKind() {
    assertEquals(NodeKind.INPUT, node(INPUT, DISABLED_OUTPUT).kind());
  }
}
