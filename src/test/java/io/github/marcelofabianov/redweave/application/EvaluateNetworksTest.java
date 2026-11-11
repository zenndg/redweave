package io.github.marcelofabianov.redweave.application;

import static io.github.marcelofabianov.redweave.application.ApplicationFixture.NODE_ID;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE_POSITION;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OTHER_NODE;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OTHER_NODE_POSITION;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OWNER;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.Direction;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.OutputChange;
import io.github.marcelofabianov.redweave.domain.network.OutputFace;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import io.github.marcelofabianov.redweave.domain.simulation.Simulator;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** Once per tick, only the networks waiting for it are evaluated (rule 18 and answer 38). */
class EvaluateNetworksTest {
  private static final NodeId NODE = new NodeId(NODE_ID);
  private static final ChannelColor LEVER_CHANNEL = ChannelColor.WHITE;
  private static final int INPUT_SLOT = LEVER_CHANNEL.ordinal();
  private static final Signal LEVER = Signal.MAX;
  private static final OutputFace LAMP =
      new OutputFace(OTHER_NODE, OTHER_NODE_POSITION.block(), OTHER_NODE_POSITION.face());

  private final ApplicationFixture fixture = new ApplicationFixture();
  private final EvaluateNetworks evaluate = new EvaluateNetworks(fixture.networks, new Simulator());

  /**
   * The node of the fixture reads a lever on its input, and OTHER_NODE lights a lamp with the same
   * channel, because a node holds one slot per channel (answer 224). Each slot is set up disabled
   * and then enabled (answer 237).
   */
  private void leverAndLamp() {
    NetworkId network = fixture.ownersNetworkWithANode();
    ConfigureSlot configure = new ConfigureSlot(fixture.networks, fixture.networks);
    EnableSlot enable = new EnableSlot(fixture.networks, fixture.networks);
    configure.configure(
        OWNER,
        NODE,
        INPUT_SLOT,
        new ChannelSlot(Direction.INPUT, NODE_POSITION.face(), LEVER_CHANNEL));
    enable.enable(OWNER, NODE, INPUT_SLOT);
    fixture
        .networks
        .find(network)
        .orElseThrow()
        .attachNode(OWNER, OTHER_NODE, OTHER_NODE_POSITION, Optional.empty());
    configure.configure(
        OWNER,
        OTHER_NODE,
        INPUT_SLOT,
        new ChannelSlot(Direction.OUTPUT, OTHER_NODE_POSITION.face(), LEVER_CHANNEL));
    enable.enable(OWNER, OTHER_NODE, INPUT_SLOT);
    evaluate.evaluate();
  }

  @Test
  void aNewReadingReachesTheOutputsOnTheNextEvaluation() {
    leverAndLamp();
    new ReadInput(fixture.networks, fixture.networks).read(NODE, INPUT_SLOT, LEVER);

    assertEquals(List.of(new OutputChange(LAMP, LEVER)), evaluate.evaluate());
  }

  @Test
  void aNetworkThatIsNotWaitingIsNotEvaluatedAgain() {
    leverAndLamp();

    assertEquals(List.of(), evaluate.evaluate());
  }
}
