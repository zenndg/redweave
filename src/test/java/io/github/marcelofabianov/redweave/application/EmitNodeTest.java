package io.github.marcelofabianov.redweave.application;

import static io.github.marcelofabianov.redweave.application.ApplicationFixture.NODE_ID;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OWNER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.Direction;
import io.github.marcelofabianov.redweave.domain.network.Face;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.DelayedOutput;
import io.github.marcelofabianov.redweave.domain.signal.OutputDelay;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import io.github.marcelofabianov.redweave.domain.signal.TestDelayLimits;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * What a node should emit before its delay: the signal of its network and its delay, or nothing
 * when it does not count, which cuts its output at once (answers 860, 862 and 874).
 */
class EmitNodeTest {
  private static final NodeId NODE = new NodeId(NODE_ID);
  private static final TestDelayLimits LIMITS = TestDelayLimits.STARTING;
  private static final OutputDelay ONE_SECOND = new OutputDelay(OutputDelay.STEP_TICKS);
  private static final ChannelColor DOOR = ChannelColor.RED;
  private static final ChannelColor LEVER = ChannelColor.BLUE;
  private static final ChannelSlot OUTPUT = new ChannelSlot(Direction.OUTPUT, Face.EAST, DOOR);
  private static final ChannelSlot INPUT = new ChannelSlot(Direction.INPUT, Face.EAST, LEVER);
  private static final Signal SEVEN = new Signal(7);

  private final ApplicationFixture fixture = new ApplicationFixture();
  private final EmitNode emitNode = new EmitNode(fixture.networks);

  private Network networkWithAnOutput() {
    fixture.ownersNetworkWithANode();
    Network network = fixture.networks.findByNode(NODE).orElseThrow();
    network.configureSlot(OWNER, NODE, DOOR.ordinal(), OUTPUT);
    network.nodeSettings().delay(OWNER, NODE, ONE_SECOND);
    return network;
  }

  @Test
  void aNodeWithAnOutputEmitsTheSignalOfItsNetworkWithItsDelay() {
    networkWithAnOutput();

    Emission emission = emitNode.of(NODE);

    assertEquals(Optional.of(Signal.OFF), emission.signal());
    assertEquals(ONE_SECOND, emission.delay());
    assertFalse(emitNode.isSilent(NODE));
  }

  @Test
  void aNodeWithoutANetworkEmitsNothing() {
    assertEquals(Emission.NONE, emitNode.of(NODE));
    assertTrue(emitNode.isSilent(NODE));
  }

  @Test
  void aNodeWithoutAnEnabledOutputEmitsNothingAndKeepsItsDelay() {
    Network network = networkWithAnOutput();
    network.disableSlot(OWNER, NODE, DOOR.ordinal());
    network.configureSlot(OWNER, NODE, LEVER.ordinal(), INPUT);

    assertEquals(new Emission(Optional.empty(), ONE_SECOND), emitNode.of(NODE));
    assertTrue(emitNode.isSilent(NODE));
  }

  @Test
  void aNodeOfANetworkTurnedOffEmitsNothing() {
    Network network = networkWithAnOutput();
    network.terminal().turnOff(OWNER);

    assertEquals(Optional.empty(), emitNode.of(NODE).signal());
    assertTrue(emitNode.isSilent(NODE));
  }

  /** Nothing to emit cuts the output at once and drops what waits (answer 862). */
  @Test
  void anEmissionWithNothingCutsTheDelayedOutputAtOnce() {
    DelayedOutput on = DelayedOutput.NONE.withDelay(ONE_SECOND).tick(SEVEN, LIMITS);
    DelayedOutput waiting = on.tick(Signal.MAX, LIMITS);

    DelayedOutput cut = new Emission(Optional.empty(), ONE_SECOND).onto(waiting);

    assertEquals(Signal.OFF, cut.output());
    assertTrue(cut.line().isEmpty());
    assertEquals(Signal.OFF, Emission.NONE.wanted());
  }

  /** A signal keeps what waits and takes the delay of today (answer 839). */
  @Test
  void anEmissionWithASignalKeepsWhatWaitsAndTakesTheDelay() {
    DelayedOutput on = DelayedOutput.NONE.withDelay(ONE_SECOND).tick(SEVEN, LIMITS);
    DelayedOutput waiting = on.tick(Signal.MAX, LIMITS);
    Emission emission = new Emission(Optional.of(Signal.MAX), OutputDelay.NONE);

    DelayedOutput taken = emission.onto(waiting);

    assertEquals(waiting.line(), taken.line());
    assertEquals(OutputDelay.NONE, taken.delay());
    assertEquals(Signal.MAX, emission.wanted());
  }
}
