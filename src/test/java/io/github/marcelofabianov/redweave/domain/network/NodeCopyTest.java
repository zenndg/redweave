package io.github.marcelofabianov.redweave.domain.network;

import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OTHER_NODE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * The Configurator copies the slots of a node and pastes them on others. A slot on the face the
 * node sticks to follows that face, and every slot fits, one per color (105, 106 and 224).
 */
class NodeCopyTest {
  private static final int NEGATIVE = -1;
  private static final BlockLocation BLOCK = new BlockLocation(NetworkFixture.OVERWORLD, 0, 0, 0);
  private static final ChannelSlot OWN_BLOCK =
      new ChannelSlot(Direction.INPUT, Face.EAST, ChannelColor.RED);
  private static final ChannelSlot NEIGHBOR =
      new ChannelSlot(Direction.OUTPUT, Face.UP, ChannelColor.BLUE);
  private static final ChannelSlot LAST =
      new ChannelSlot(Direction.INPUT, Face.DOWN, ChannelColor.BLACK);
  private static final int OWN_BLOCK_SLOT = OWN_BLOCK.channel().ordinal();
  private static final int NEIGHBOR_SLOT = NEIGHBOR.channel().ordinal();
  private static final int LAST_SLOT = LAST.channel().ordinal();
  private static final int FREE_SLOT = ChannelColor.LIME.ordinal();

  private static Node node(NodeId id, Face attached) {
    return new Node(
        id,
        Optional.empty(),
        new NodePosition(BLOCK, attached),
        Collections.nCopies(Node.SLOT_COUNT, Optional.empty()));
  }

  private static Node source() {
    return node(NODE, Face.EAST)
        .withSlot(OWN_BLOCK_SLOT, Optional.of(OWN_BLOCK))
        .withSlot(NEIGHBOR_SLOT, Optional.of(NEIGHBOR))
        .withSlot(LAST_SLOT, Optional.of(LAST));
  }

  private static List<Optional<ChannelSlot>> slots(ChannelSlot... inUse) {
    List<Optional<ChannelSlot>> slots =
        new ArrayList<>(Collections.nCopies(Node.SLOT_COUNT, Optional.empty()));
    for (ChannelSlot slot : inUse) {
      slots.set(slot.channel().ordinal(), Optional.of(slot));
    }
    return slots;
  }

  @Test
  void aCopyKeepsTheSlotsInUseAndTheAttachedFace() {
    NodeCopy copy = NodeCopy.of(source());

    assertEquals(Face.EAST, copy.attached());
    assertEquals(
        List.of(
            new CopiedSlot(NEIGHBOR_SLOT, NEIGHBOR),
            new CopiedSlot(OWN_BLOCK_SLOT, OWN_BLOCK),
            new CopiedSlot(LAST_SLOT, LAST)),
        copy.slots());
  }

  @Test
  void aSlotOnTheAttachedFaceFollowsTheFaceOfTheTarget() {
    Pasted pasted = NodeCopy.of(source()).onto(node(OTHER_NODE, Face.NORTH));

    ChannelSlot moved = new ChannelSlot(Direction.INPUT, Face.NORTH, ChannelColor.RED);
    assertEquals(slots(moved, NEIGHBOR, LAST), pasted.slots());
  }

  /** A disabled slot on the attached face keeps its status (answers 230 and 277). */
  @Test
  void aSlotOnTheAttachedFaceKeepsItsStatus() {
    ChannelSlot disabled = OWN_BLOCK.enabled(false);
    Node source = node(NODE, Face.EAST).withSlot(OWN_BLOCK_SLOT, Optional.of(disabled));

    Pasted pasted = NodeCopy.of(source).onto(node(OTHER_NODE, Face.NORTH));

    ChannelSlot moved = new ChannelSlot(Direction.INPUT, Face.NORTH, ChannelColor.RED, false);
    assertEquals(slots(moved), pasted.slots());
  }

  /** Every node has one slot per color, so each copied slot fits the target (answer 224). */
  @Test
  void everyCopiedSlotFitsTheTarget() {
    Pasted pasted = NodeCopy.of(source()).onto(node(OTHER_NODE, Face.EAST));

    assertEquals(slots(OWN_BLOCK, NEIGHBOR, LAST), pasted.slots());
    assertEquals(NodeCopy.of(source()).slots().size(), pasted.inUse());
  }

  @Test
  void pastingFreesTheSlotsTheCopyDoesNotHave() {
    ChannelSlot onTheFreeSlot = new ChannelSlot(Direction.INPUT, Face.EAST, ChannelColor.LIME);
    Node target = node(OTHER_NODE, Face.EAST).withSlot(FREE_SLOT, Optional.of(onTheFreeSlot));

    Pasted pasted = NodeCopy.of(source()).onto(target);

    assertEquals(Optional.empty(), pasted.slots().get(FREE_SLOT));
  }

  @Test
  void aCopiedSlotHasNoNegativeIndex() {
    assertThrows(IllegalArgumentException.class, () -> new CopiedSlot(NEGATIVE, OWN_BLOCK));
  }

  /** A copied slot sits at the place of its color, so it always fits a node (answer 224). */
  @Test
  void aCopiedSlotSitsAtItsColor() {
    assertThrows(IllegalArgumentException.class, () -> new CopiedSlot(LAST_SLOT, OWN_BLOCK));
  }

  @Test
  void aCopyHasEachIndexOnce() {
    List<CopiedSlot> repeated =
        List.of(
            new CopiedSlot(OWN_BLOCK_SLOT, OWN_BLOCK),
            new CopiedSlot(OWN_BLOCK_SLOT, OWN_BLOCK.enabled(false)));

    assertThrows(IllegalArgumentException.class, () -> new NodeCopy(Face.EAST, repeated));
  }
}
