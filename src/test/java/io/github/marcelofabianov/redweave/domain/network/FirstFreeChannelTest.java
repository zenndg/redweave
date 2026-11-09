package io.github.marcelofabianov.redweave.domain.network;

import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE_POSITION;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.network;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/** The first channel, in the order of the colors, with no slot in the network (answer 25). */
class FirstFreeChannelTest {
  private static final int FIRST_SLOT = 0;

  private static ChannelSlot reading(ChannelColor channel) {
    return new ChannelSlot(Direction.INPUT, NODE_POSITION.face(), channel);
  }

  /** Spreads the 16 channels over as many nodes as the slots per node need. */
  private static void useEveryChannel(Network network) {
    int slotsPerNode = TestLimits.STARTING.slotsPerNode();
    NodeId nodeId = null;
    for (ChannelColor channel : ChannelColor.values()) {
      int slot = channel.ordinal() % slotsPerNode;
      if (slot == FIRST_SLOT) {
        nodeId = new NodeId(UUID.randomUUID());
        network.attachNode(OWNER, nodeId, NODE_POSITION);
      }
      network.configureSlot(OWNER, nodeId, slot, reading(channel));
    }
  }

  @Test
  void aNewNetworkStartsWithTheFirstColor() {
    assertEquals(Optional.of(ChannelColor.WHITE), network().firstFreeChannel());
  }

  @Test
  void aChannelInUseIsSkipped() {
    Network network = network();
    network.attachNode(OWNER, NODE, NODE_POSITION);
    network.configureSlot(OWNER, NODE, FIRST_SLOT, reading(ChannelColor.WHITE));

    assertEquals(Optional.of(ChannelColor.ORANGE), network.firstFreeChannel());
  }

  @Test
  void withEveryChannelInUseThereIsNoFreeChannel() {
    Network network = network();
    useEveryChannel(network);

    assertEquals(Optional.empty(), network.firstFreeChannel());
  }
}
