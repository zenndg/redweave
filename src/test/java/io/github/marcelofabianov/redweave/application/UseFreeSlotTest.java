package io.github.marcelofabianov.redweave.application;

import static io.github.marcelofabianov.redweave.application.ApplicationFixture.NODE_ID;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE_POSITION;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.STRANGER;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.Direction;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** "Click to use" on a free slot of the Node Screen (answer 83). */
class UseFreeSlotTest {
  private static final NodeId NODE = new NodeId(NODE_ID);
  private static final int USED_SLOT = 0;
  private static final int FREE_SLOT = 1;
  private static final int PAST_THE_LAST_SLOT = 4;

  private final ApplicationFixture fixture = new ApplicationFixture();
  private final UseFreeSlot useFreeSlot = new UseFreeSlot(fixture.networks, fixture.networks);

  private Optional<ChannelSlot> slot(NetworkId network, int index) {
    return fixture.networks.find(network).orElseThrow().node(NODE).orElseThrow().slots().get(index);
  }

  /** The node took white when placed, so the next free channel is orange. */
  @Test
  void aFreeSlotBecomesAnInputOnTheFirstFreeChannel() {
    NetworkId network = fixture.ownersNetworkWithANode();

    assertEquals(Outcome.ACCEPTED, useFreeSlot.use(OWNER, NODE, FREE_SLOT));
    ChannelSlot expected =
        new ChannelSlot(Direction.INPUT, NODE_POSITION.face(), ChannelColor.ORANGE);
    assertEquals(Optional.of(expected), slot(network, FREE_SLOT));
  }

  /** Only a free slot shows "Click to use", so a used one is never overwritten by it. */
  @Test
  void aSlotInUseIsNotOverwritten() {
    NetworkId network = fixture.ownersNetworkWithANode();
    Optional<ChannelSlot> before = slot(network, USED_SLOT);

    assertEquals(Outcome.INVALID_SLOT, useFreeSlot.use(OWNER, NODE, USED_SLOT));
    assertEquals(before, slot(network, USED_SLOT));
  }

  @Test
  void anotherPlayerCannotUseASlot() {
    NetworkId network = fixture.ownersNetworkWithANode();

    assertEquals(Outcome.NOT_OWNER, useFreeSlot.use(STRANGER, NODE, FREE_SLOT));
    assertEquals(Optional.empty(), slot(network, FREE_SLOT));
  }

  @Test
  void aSlotPastTheLastOneIsRefused() {
    fixture.ownersNetworkWithANode();

    assertEquals(Outcome.INVALID_SLOT, useFreeSlot.use(OWNER, NODE, PAST_THE_LAST_SLOT));
  }

  @Test
  void aNodeWithoutANetworkHasNoSlotToUse() {
    assertEquals(Outcome.UNKNOWN_NODE, useFreeSlot.use(OWNER, NODE, FREE_SLOT));
  }
}
