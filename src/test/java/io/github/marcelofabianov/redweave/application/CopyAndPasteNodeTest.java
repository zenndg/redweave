package io.github.marcelofabianov.redweave.application;

import static io.github.marcelofabianov.redweave.application.ApplicationFixture.NODE_ID;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.STRANGER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.CopiedSlot;
import io.github.marcelofabianov.redweave.domain.network.Direction;
import io.github.marcelofabianov.redweave.domain.network.Face;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.NodeCopy;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/** The Configurator copies a node and pastes it on others (answers 105 and 106). */
class CopyAndPasteNodeTest {
  private static final NodeId NODE = new NodeId(NODE_ID);
  private static final String UNPLACED_UUID = "3d4e5f6a-7b8c-4d9e-8f0a-1b2c3d4e5f6a";
  private static final NodeId UNPLACED = new NodeId(UUID.fromString(UNPLACED_UUID));
  private static final int SLOT = 0;
  private static final int SECOND_SLOT = 1;
  private static final int PAST_THE_END = 5;
  private static final int PASTED = 2;
  private static final Signal READ = new Signal(9);
  private static final ChannelSlot OWN_BLOCK =
      new ChannelSlot(Direction.OUTPUT, Face.WEST, ChannelColor.RED);
  private static final ChannelSlot NEIGHBOR =
      new ChannelSlot(Direction.INPUT, Face.NORTH, ChannelColor.BLUE);
  private static final NodeCopy COPY =
      new NodeCopy(
          Face.WEST,
          List.of(
              new CopiedSlot(SLOT, OWN_BLOCK),
              new CopiedSlot(SECOND_SLOT, NEIGHBOR),
              new CopiedSlot(PAST_THE_END, NEIGHBOR)));

  private final ApplicationFixture fixture = new ApplicationFixture();
  private final CopyNode copyNode = new CopyNode(fixture.networks);
  private final PasteNode pasteNode = new PasteNode(fixture.networks, fixture.networks);

  private Network network() {
    return fixture.networks.findByNode(NODE).orElseThrow();
  }

  @Test
  void aCopyHoldsTheSlotsOfTheNode() {
    fixture.ownersNetworkWithANode();

    NodeCopy copy = copyNode.copy(NODE).orElseThrow();

    assertEquals(network().node(NODE).map(NodeCopy::of), Optional.of(copy));
  }

  @Test
  void aNodeWithoutANetworkHasNothingToCopy() {
    assertEquals(Optional.empty(), copyNode.copy(UNPLACED));
  }

  @Test
  void pastingReplacesTheSlotsAndCountsTheOnesLeftOut() {
    fixture.ownersNetworkWithANode();

    PasteResult result = pasteNode.paste(OWNER, NODE, COPY);

    assertEquals(new PasteResult(Outcome.ACCEPTED, PASTED, 1), result);
    ChannelSlot followed = new ChannelSlot(Direction.OUTPUT, Face.EAST, ChannelColor.RED);
    assertEquals(
        List.of(Optional.of(followed), Optional.of(NEIGHBOR), Optional.empty(), Optional.empty()),
        network().node(NODE).orElseThrow().slots());
  }

  @Test
  void pastingDropsTheReadingsOfTheNode() {
    fixture.ownersNetworkWithANode();
    ChannelColor channel =
        network().node(NODE).orElseThrow().slots().get(SLOT).orElseThrow().channel();
    network().readInput(NODE, SLOT, READ);
    NodeCopy itself = copyNode.copy(NODE).orElseThrow();

    pasteNode.paste(OWNER, NODE, itself);

    assertEquals(Signal.OFF, network().channelValue(channel));
  }

  @Test
  void anotherPlayerDoesNotPaste() {
    fixture.ownersNetworkWithANode();
    var before = network().node(NODE).orElseThrow();

    PasteResult result = pasteNode.paste(STRANGER, NODE, COPY);

    assertEquals(Outcome.NOT_OWNER, result.outcome());
    assertEquals(before, network().node(NODE).orElseThrow());
  }

  @Test
  void aNodeWithoutANetworkRefusesThePaste() {
    assertEquals(Outcome.UNKNOWN_NODE, pasteNode.paste(OWNER, UNPLACED, COPY).outcome());
  }

  @Test
  void aPasteIsSaved() {
    fixture.ownersNetworkWithANode();

    pasteNode.paste(OWNER, NODE, COPY);

    assertTrue(fixture.networks.saves() > 0);
  }
}
