package io.github.zenndg.redweave.application;

import static io.github.zenndg.redweave.application.ApplicationFixture.NODE_ID;
import static io.github.zenndg.redweave.application.ApplicationFixture.NOW;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.OWNER;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.zenndg.redweave.domain.network.Direction;
import io.github.zenndg.redweave.domain.network.NetworkId;
import io.github.zenndg.redweave.domain.network.NodeId;
import io.github.zenndg.redweave.domain.network.Outcome;
import io.github.zenndg.redweave.domain.network.SignalChange;
import io.github.zenndg.redweave.domain.network.SignalLog;
import io.github.zenndg.redweave.domain.signal.ChannelColor;
import io.github.zenndg.redweave.domain.signal.Signal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** A node reads the redstone of its block into the channel, and the reading is saved (37). */
class ReadInputTest {
  private static final NodeId NODE = new NodeId(NODE_ID);
  private static final ChannelColor CHANNEL = ChannelColor.WHITE;
  private static final int FIRST_SLOT = CHANNEL.ordinal();
  private static final Signal LEVER = Signal.MAX;
  private static final long FROM_THE_START = 0;
  private static final int ONE_LINE = 1;

  /** The default name of the node of the fixture: the first 8 characters of its ID (142). */
  private static final String NODE_NAME = "1b2c3d4e";

  private final ApplicationFixture fixture = new ApplicationFixture();
  private final ReadInput readInput =
      new ReadInput(fixture.networks, fixture.networks, fixture.logs, fixture.clock);

  /** The network of the fixture, with an input enabled on the first slot of its node. */
  private NetworkId networkWithAnInput() {
    NetworkId network = fixture.ownersNetworkWithANode();
    new EnableSlot(fixture.networks, fixture.networks).enable(OWNER, NODE, FIRST_SLOT);
    return network;
  }

  @Test
  void aReadingGoesIntoTheChannelAndIsSaved() {
    NetworkId network = networkWithAnInput();
    int savesBefore = fixture.networks.saves();

    assertEquals(Outcome.ACCEPTED, readInput.read(NODE, FIRST_SLOT, LEVER));
    assertEquals(LEVER, fixture.networks.find(network).orElseThrow().channelValue(CHANNEL));
    assertEquals(savesBefore + 1, fixture.networks.saves());
  }

  /** Only a change waits for a tick and is saved, so a steady lever costs nothing (rule 18). */
  @Test
  void theSameReadingAgainChangesNothing() {
    networkWithAnInput();
    readInput.read(NODE, FIRST_SLOT, LEVER);
    int savesBefore = fixture.networks.saves();

    readInput.read(NODE, FIRST_SLOT, LEVER);

    assertEquals(savesBefore, fixture.networks.saves());
  }

  private List<SignalChange> logOf(NetworkId network) {
    return fixture.logs.after(network, FROM_THE_START).stream()
        .map(SignalLog.Line::change)
        .toList();
  }

  /** A new reading is a line of the log, with the time of the server (answers 147 and 148). */
  @Test
  void aNewReadingIsALineOfTheLog() {
    NetworkId network = networkWithAnInput();

    readInput.read(NODE, FIRST_SLOT, LEVER);

    SignalChange line =
        new SignalChange(NOW, NODE_NAME, CHANNEL, Optional.empty(), Direction.INPUT, LEVER);
    assertEquals(List.of(line), logOf(network));
  }

  @Test
  void theSameReadingAgainIsNoNewLine() {
    NetworkId network = networkWithAnInput();
    readInput.read(NODE, FIRST_SLOT, LEVER);

    readInput.read(NODE, FIRST_SLOT, LEVER);

    assertEquals(ONE_LINE, logOf(network).size());
  }

  @Test
  void aNodeWithoutANetworkHasNothingToRead() {
    assertEquals(Outcome.UNKNOWN_NODE, readInput.read(NODE, FIRST_SLOT, LEVER));
  }
}
