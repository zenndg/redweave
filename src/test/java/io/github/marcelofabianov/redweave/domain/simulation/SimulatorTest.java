package io.github.marcelofabianov.redweave.domain.simulation;

import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE_POSITION;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OTHER_NODE;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OTHER_NODE_POSITION;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.network;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.Direction;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.OutputChange;
import io.github.marcelofabianov.redweave.domain.network.OutputFace;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** Each network is evaluated at most once a tick, and only when a signal of it changed. */
class SimulatorTest {
  private static final ChannelColor LAMP = ChannelColor.YELLOW;
  private static final int SLOT = LAMP.ordinal();
  private static final Signal LEVER = new Signal(13);
  private static final Signal LEVER_LOWER = new Signal(6);
  private static final OutputFace LAMP_FACE =
      new OutputFace(OTHER_NODE, OTHER_NODE_POSITION.block(), OTHER_NODE_POSITION.face());

  private final Simulator simulator = new Simulator();

  /** NODE reads the lever into the channel, and OTHER_NODE lights the lamp with it. */
  private static Network leverAndLamp() {
    Network network = network();
    network.attachNode(OWNER, NODE, NODE_POSITION, Optional.empty());
    network.configureSlot(
        OWNER, NODE, SLOT, new ChannelSlot(Direction.INPUT, NODE_POSITION.face(), LAMP));
    network.attachNode(OWNER, OTHER_NODE, OTHER_NODE_POSITION, Optional.empty());
    network.configureSlot(
        OWNER,
        OTHER_NODE,
        SLOT,
        new ChannelSlot(Direction.OUTPUT, OTHER_NODE_POSITION.face(), LAMP));
    network.readInput(NODE, SLOT, LEVER);
    return network;
  }

  @Test
  void theFirstEvaluationAppliesEveryOutput() {
    assertEquals(List.of(new OutputChange(LAMP_FACE, LEVER)), simulator.evaluate(leverAndLamp()));
  }

  @Test
  void aSecondEvaluationInTheSameTickChangesNothing() {
    Network network = leverAndLamp();
    simulator.evaluate(network);

    assertEquals(List.of(), simulator.evaluate(network));
  }

  @Test
  void aReadingThatChangesTheInputChangesTheOutput() {
    Network network = leverAndLamp();
    simulator.evaluate(network);
    network.readInput(NODE, SLOT, LEVER_LOWER);

    assertEquals(List.of(new OutputChange(LAMP_FACE, LEVER_LOWER)), simulator.evaluate(network));
  }

  @Test
  void theSameReadingAgainLeavesTheNetworkAlone() {
    Network network = leverAndLamp();
    simulator.evaluate(network);
    network.readInput(NODE, SLOT, LEVER);

    assertFalse(network.isPending());
    assertEquals(List.of(), simulator.evaluate(network));
  }

  /** After loading, the blocks may hold anything, so the first tick applies even an off face. */
  @Test
  void theFirstEvaluationAppliesAnOffFaceToo() {
    Network network = leverAndLamp();
    network.readInput(NODE, SLOT, Signal.OFF);

    assertEquals(List.of(new OutputChange(LAMP_FACE, Signal.OFF)), simulator.evaluate(network));
  }
}
