package io.github.marcelofabianov.redweave.domain.network;

import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE_POSITION;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.STRANGER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.network;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.OutputDelay;
import io.github.marcelofabianov.redweave.domain.signal.TestDelayLimits;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * The output delay of a node: of the whole node, set by the owner of its network a second at a
 * time, within the config (answers 859 to 861, 864 and 870).
 */
class NodeSettingsTest {
  private static final TestDelayLimits LIMITS = TestDelayLimits.STARTING;
  private static final OutputDelay ONE_SECOND = new OutputDelay(OutputDelay.STEP_TICKS);
  private static final OutputDelay LONGEST = new OutputDelay(LIMITS.maxOutputDelayTicks());
  private static final int LONGER = 1;
  private static final int SHORTER = -1;
  private static final int RED = ChannelColor.RED.ordinal();
  private static final String DOOR = "Door";

  private static Network withNode() {
    Network network = network();
    network.attachNode(OWNER, NODE, NODE_POSITION, Optional.empty());
    return network;
  }

  private static OutputDelay delayOf(Network network) {
    return network.node(NODE).orElseThrow().delay();
  }

  @Test
  void aNewNodeHasNoDelay() {
    assertEquals(OutputDelay.NONE, delayOf(withNode()));
  }

  @Test
  void theOwnerStepsTheDelayOneSecondAtATime() {
    Network network = withNode();

    assertEquals(Outcome.ACCEPTED, network.nodeSettings().stepDelay(OWNER, NODE, LONGER, LIMITS));
    assertEquals(ONE_SECOND, delayOf(network));
    assertEquals(Outcome.ACCEPTED, network.nodeSettings().stepDelay(OWNER, NODE, SHORTER, LIMITS));
    assertEquals(OutputDelay.NONE, delayOf(network));
  }

  @Test
  void aStepStaysWithinTheConfig() {
    Network network = withNode();
    network.nodeSettings().delay(OWNER, NODE, LONGEST);

    network.nodeSettings().stepDelay(OWNER, NODE, LONGER, LIMITS);

    assertEquals(LONGEST, delayOf(network));
    network.nodeSettings().delay(OWNER, NODE, OutputDelay.NONE);
    network.nodeSettings().stepDelay(OWNER, NODE, SHORTER, LIMITS);
    assertEquals(OutputDelay.NONE, delayOf(network));
  }

  @Test
  void anotherPlayerChangesNoDelay() {
    Network network = withNode();

    assertEquals(
        Outcome.NOT_OWNER, network.nodeSettings().stepDelay(STRANGER, NODE, LONGER, LIMITS));
    assertEquals(Outcome.NOT_OWNER, network.nodeSettings().delay(STRANGER, NODE, ONE_SECOND));
    assertEquals(OutputDelay.NONE, delayOf(network));
  }

  @Test
  void aNodeOutOfTheNetworkHasNoDelayToChange() {
    Network network = network();

    assertEquals(
        Outcome.UNKNOWN_NODE, network.nodeSettings().stepDelay(OWNER, NODE, LONGER, LIMITS));
    assertEquals(
        Outcome.NOT_OWNER, network.nodeSettings().stepDelay(STRANGER, NODE, LONGER, LIMITS));
    assertEquals(Outcome.UNKNOWN_NODE, network.nodeSettings().delay(OWNER, NODE, ONE_SECOND));
  }

  /** The delay is of the node, so its name and its slots change without touching it (861). */
  @Test
  void theDelayStaysWhenTheNameAndTheSlotsChange() {
    Network network = withNode();
    network.nodeSettings().delay(OWNER, NODE, ONE_SECOND);

    network.nodeSettings().rename(OWNER, NODE, DOOR);
    network.configureSlot(
        OWNER, NODE, RED, new ChannelSlot(Direction.OUTPUT, Face.EAST, ChannelColor.RED));

    assertEquals(ONE_SECOND, delayOf(network));
  }

  /** The delay changes no signal, so the network does not wait for an evaluation (rule 18). */
  @Test
  void aNewDelayDoesNotMakeTheNetworkPending() {
    Network network = withNode();
    network.settle(Map.of());

    network.nodeSettings().stepDelay(OWNER, NODE, LONGER, LIMITS);

    assertFalse(network.isPending());
  }

  @Test
  void theDelayGoesToTheSavesWithTheNode() {
    Network network = withNode();
    network.nodeSettings().delay(OWNER, NODE, ONE_SECOND);

    Network loaded = Network.restore(network.snapshot(), TestLimits.STARTING);

    assertEquals(ONE_SECOND, delayOf(loaded));
  }

  /** Only a node with an enabled output emits, the rest is cut off at once (answer 862). */
  @Test
  void aNodeEmitsOnlyWithAnEnabledOutput() {
    Network network = withNode();
    assertFalse(network.node(NODE).orElseThrow().emits());

    network.configureSlot(
        OWNER, NODE, RED, new ChannelSlot(Direction.OUTPUT, Face.EAST, ChannelColor.RED));
    assertTrue(network.node(NODE).orElseThrow().emits());

    network.disableSlot(OWNER, NODE, RED);
    assertFalse(network.node(NODE).orElseThrow().emits());
  }
}
