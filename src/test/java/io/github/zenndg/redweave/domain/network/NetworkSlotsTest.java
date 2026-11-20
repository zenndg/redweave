package io.github.zenndg.redweave.domain.network;

import static io.github.zenndg.redweave.domain.network.NetworkFixture.NODE;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.NODE_POSITION;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.STRANGER;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.network;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.zenndg.redweave.domain.signal.ChannelColor;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class NetworkSlotsTest {
  private static final ChannelColor RED = ChannelColor.RED;
  private static final int FIRST_SLOT = RED.ordinal();
  private static final int PAST_THE_LAST_SLOT = Node.SLOT_COUNT;
  private static final int BEFORE_THE_FIRST_SLOT = -1;
  private static final ChannelSlot READ_RED = new ChannelSlot(Direction.INPUT, Face.EAST, RED);

  private static Network networkWithANode() {
    Network network = network();
    network.attachNode(OWNER, NODE, NODE_POSITION, Optional.empty());
    return network;
  }

  private static Optional<ChannelSlot> firstSlot(Network network) {
    return network.node(NODE).orElseThrow().slots().get(FIRST_SLOT);
  }

  @Test
  void theOwnerConfiguresASlot() {
    Network network = networkWithANode();

    assertEquals(Outcome.ACCEPTED, network.configureSlot(OWNER, NODE, FIRST_SLOT, READ_RED));
    assertEquals(Optional.of(READ_RED), firstSlot(network));
  }

  @Test
  void aSlotPastTheLimitOfTheNodeIsRefused() {
    Network network = networkWithANode();

    assertEquals(
        Outcome.INVALID_SLOT, network.configureSlot(OWNER, NODE, PAST_THE_LAST_SLOT, READ_RED));
    assertEquals(
        Outcome.INVALID_SLOT, network.configureSlot(OWNER, NODE, BEFORE_THE_FIRST_SLOT, READ_RED));
  }

  @Test
  void configuringANodeThatIsNotThereSaysSo() {
    assertEquals(Outcome.UNKNOWN_NODE, network().configureSlot(OWNER, NODE, FIRST_SLOT, READ_RED));
  }

  @Test
  void anotherPlayerCannotConfigureASlot() {
    Network network = networkWithANode();

    assertEquals(Outcome.NOT_OWNER, network.configureSlot(STRANGER, NODE, FIRST_SLOT, READ_RED));
    assertEquals(Optional.empty(), firstSlot(network));
  }

  @Test
  void theOwnerFreesASlot() {
    Network network = networkWithANode();
    network.configureSlot(OWNER, NODE, FIRST_SLOT, READ_RED);

    assertEquals(Outcome.ACCEPTED, network.clearSlot(OWNER, NODE, FIRST_SLOT));
    assertEquals(Optional.empty(), firstSlot(network));
  }

  @Test
  void anotherPlayerCannotFreeASlot() {
    Network network = networkWithANode();
    network.configureSlot(OWNER, NODE, FIRST_SLOT, READ_RED);

    assertEquals(Outcome.NOT_OWNER, network.clearSlot(STRANGER, NODE, FIRST_SLOT));
    assertEquals(Optional.of(READ_RED), firstSlot(network));
  }
}
