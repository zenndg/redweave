package io.github.marcelofabianov.redweave.domain.network;

import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OTHER_NODE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * The Configurator copies the slots of a node and pastes them on others. A slot on the face the
 * node sticks to follows that face, and a slot past the end of the target is left out (105, 106).
 */
class NodeCopyTest {
  private static final int SLOTS = 4;
  private static final int FEWER_SLOTS = 2;
  private static final int LAST_SLOT = 3;
  private static final int NEGATIVE = -1;
  private static final BlockLocation BLOCK = new BlockLocation(NetworkFixture.OVERWORLD, 0, 0, 0);
  private static final ChannelSlot OWN_BLOCK =
      new ChannelSlot(Direction.INPUT, Face.EAST, ChannelColor.RED);
  private static final ChannelSlot NEIGHBOR =
      new ChannelSlot(Direction.OUTPUT, Face.UP, ChannelColor.BLUE);
  private static final ChannelSlot LAST =
      new ChannelSlot(Direction.INPUT, Face.DOWN, ChannelColor.LIME);

  private static Node node(NodeId id, Face attached, int slotCount) {
    return new Node(
        id, new NodePosition(BLOCK, attached), Collections.nCopies(slotCount, Optional.empty()));
  }

  private static Node source() {
    return node(NODE, Face.EAST, SLOTS)
        .withSlot(0, Optional.of(OWN_BLOCK))
        .withSlot(1, Optional.of(NEIGHBOR))
        .withSlot(LAST_SLOT, Optional.of(LAST));
  }

  @Test
  void aCopyKeepsTheSlotsInUseAndTheAttachedFace() {
    NodeCopy copy = NodeCopy.of(source());

    assertEquals(Face.EAST, copy.attached());
    assertEquals(
        List.of(
            new CopiedSlot(0, OWN_BLOCK),
            new CopiedSlot(1, NEIGHBOR),
            new CopiedSlot(LAST_SLOT, LAST)),
        copy.slots());
  }

  @Test
  void aSlotOnTheAttachedFaceFollowsTheFaceOfTheTarget() {
    Pasted pasted = NodeCopy.of(source()).onto(node(OTHER_NODE, Face.NORTH, SLOTS));

    ChannelSlot moved = new ChannelSlot(Direction.INPUT, Face.NORTH, ChannelColor.RED);
    assertEquals(
        List.of(Optional.of(moved), Optional.of(NEIGHBOR), Optional.empty(), Optional.of(LAST)),
        pasted.slots());
    assertEquals(0, pasted.leftOut());
  }

  @Test
  void slotsPastTheEndOfTheTargetAreLeftOut() {
    Pasted pasted = NodeCopy.of(source()).onto(node(OTHER_NODE, Face.EAST, FEWER_SLOTS));

    assertEquals(List.of(Optional.of(OWN_BLOCK), Optional.of(NEIGHBOR)), pasted.slots());
    assertEquals(1, pasted.leftOut());
  }

  @Test
  void pastingFreesTheSlotsTheCopyDoesNotHave() {
    Node target = node(OTHER_NODE, Face.EAST, SLOTS).withSlot(2, Optional.of(OWN_BLOCK));

    Pasted pasted = NodeCopy.of(source()).onto(target);

    assertEquals(Optional.empty(), pasted.slots().get(2));
  }

  @Test
  void aCopiedSlotHasNoNegativeIndex() {
    assertThrows(IllegalArgumentException.class, () -> new CopiedSlot(NEGATIVE, OWN_BLOCK));
  }

  @Test
  void aCopyHasEachIndexOnce() {
    List<CopiedSlot> repeated = List.of(new CopiedSlot(0, OWN_BLOCK), new CopiedSlot(0, LAST));

    assertThrows(IllegalArgumentException.class, () -> new NodeCopy(Face.EAST, repeated));
  }
}
