package io.github.marcelofabianov.redweave.application;

import static io.github.marcelofabianov.redweave.application.ApplicationFixture.NODE_ID;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE_POSITION;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OWNER;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.Direction;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.OutputChange;
import io.github.marcelofabianov.redweave.domain.network.OutputFace;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import io.github.marcelofabianov.redweave.domain.simulation.Simulator;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Once per tick, only the networks waiting for it are evaluated (rule 18 and answer 38). */
class EvaluateNetworksTest {
  private static final NodeId NODE = new NodeId(NODE_ID);
  private static final int INPUT_SLOT = 0;
  private static final int OUTPUT_SLOT = 1;
  private static final Signal LEVER = Signal.MAX;
  private static final OutputFace LAMP =
      new OutputFace(NODE, NODE_POSITION.block(), NODE_POSITION.face());

  private final ApplicationFixture fixture = new ApplicationFixture();
  private final EvaluateNetworks evaluate = new EvaluateNetworks(fixture.networks, new Simulator());

  /** The node of the fixture reads a lever on its input and lights a lamp on its output. */
  private void leverAndLamp() {
    fixture.ownersNetworkWithANode();
    ChannelSlot output =
        new ChannelSlot(Direction.OUTPUT, NODE_POSITION.face(), ChannelColor.WHITE);
    new ConfigureSlot(fixture.networks, fixture.networks)
        .configure(OWNER, NODE, OUTPUT_SLOT, output);
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
