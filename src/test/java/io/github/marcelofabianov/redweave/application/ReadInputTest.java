package io.github.marcelofabianov.redweave.application;

import static io.github.marcelofabianov.redweave.application.ApplicationFixture.NODE_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import org.junit.jupiter.api.Test;

/** A node reads the redstone of its block into the channel, and the reading is saved (37). */
class ReadInputTest {
  private static final NodeId NODE = new NodeId(NODE_ID);
  private static final int FIRST_SLOT = 0;
  private static final Signal LEVER = Signal.MAX;

  private final ApplicationFixture fixture = new ApplicationFixture();
  private final ReadInput readInput = new ReadInput(fixture.networks, fixture.networks);

  @Test
  void aReadingGoesIntoTheChannelAndIsSaved() {
    NetworkId network = fixture.ownersNetworkWithANode();
    int savesBefore = fixture.networks.saves();

    assertEquals(Outcome.ACCEPTED, readInput.read(NODE, FIRST_SLOT, LEVER));
    assertEquals(
        LEVER, fixture.networks.find(network).orElseThrow().channelValue(ChannelColor.WHITE));
    assertEquals(savesBefore + 1, fixture.networks.saves());
  }

  /** Only a change waits for a tick and is saved, so a steady lever costs nothing (rule 18). */
  @Test
  void theSameReadingAgainChangesNothing() {
    fixture.ownersNetworkWithANode();
    readInput.read(NODE, FIRST_SLOT, LEVER);
    int savesBefore = fixture.networks.saves();

    readInput.read(NODE, FIRST_SLOT, LEVER);

    assertEquals(savesBefore, fixture.networks.saves());
  }

  @Test
  void aNodeWithoutANetworkHasNothingToRead() {
    assertEquals(Outcome.UNKNOWN_NODE, readInput.read(NODE, FIRST_SLOT, LEVER));
  }
}
