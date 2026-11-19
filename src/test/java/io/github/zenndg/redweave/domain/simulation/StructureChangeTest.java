package io.github.zenndg.redweave.domain.simulation;

import static io.github.zenndg.redweave.domain.network.NetworkFixture.CONTROLLER;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.NODE;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.NODE_POSITION;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.OTHER_NODE;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.OTHER_NODE_POSITION;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.network;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import io.github.zenndg.redweave.domain.network.ChannelSlot;
import io.github.zenndg.redweave.domain.network.Direction;
import io.github.zenndg.redweave.domain.network.Face;
import io.github.zenndg.redweave.domain.network.Network;
import io.github.zenndg.redweave.domain.network.OutputChange;
import io.github.zenndg.redweave.domain.network.OutputFace;
import io.github.zenndg.redweave.domain.signal.ChannelColor;
import io.github.zenndg.redweave.domain.signal.Signal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** A change to the structure can change a signal, so it makes the network wait (answer 38). */
class StructureChangeTest {
  private static final ChannelColor LAMP = ChannelColor.YELLOW;
  private static final ChannelColor SECOND_LAMP = ChannelColor.RED;
  private static final int SLOT = LAMP.ordinal();
  private static final int SECOND_SLOT = SECOND_LAMP.ordinal();
  private static final Signal LEVER = new Signal(13);
  private static final String NEW_NAME = "Door";
  private static final OutputFace LAMP_FACE =
      new OutputFace(OTHER_NODE, OTHER_NODE_POSITION.block(), OTHER_NODE_POSITION.face());
  private static final OutputFace SECOND_LAMP_FACE =
      new OutputFace(OTHER_NODE, OTHER_NODE_POSITION.block(), Face.UP);

  private final Simulator simulator = new Simulator();

  /** Same scene as SimulatorTest, already evaluated once, so nothing waits. */
  private Network settledLeverAndLamp() {
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
    simulator.evaluate(network);
    return network;
  }

  @Test
  void removingTheControllerTurnsTheLampOff() {
    Network network = settledLeverAndLamp();
    network.removeController(OWNER);

    assertEquals(List.of(new OutputChange(LAMP_FACE, Signal.OFF)), simulator.evaluate(network));
  }

  @Test
  void placingTheControllerAgainTurnsTheLampBackOn() {
    Network network = settledLeverAndLamp();
    network.removeController(OWNER);
    simulator.evaluate(network);
    network.placeController(OWNER, CONTROLLER);

    assertEquals(List.of(new OutputChange(LAMP_FACE, LEVER)), simulator.evaluate(network));
  }

  @Test
  void detachingTheLampNodeTurnsItsFaceOff() {
    Network network = settledLeverAndLamp();
    network.detachNode(OWNER, OTHER_NODE);

    assertEquals(List.of(new OutputChange(LAMP_FACE, Signal.OFF)), simulator.evaluate(network));
  }

  @Test
  void freeingTheLampSlotTurnsItsFaceOff() {
    Network network = settledLeverAndLamp();
    network.clearSlot(OWNER, OTHER_NODE, SLOT);

    assertEquals(List.of(new OutputChange(LAMP_FACE, Signal.OFF)), simulator.evaluate(network));
  }

  /** A node holds one slot per channel, so the new output repeats a second lever (224). */
  @Test
  void aNewOutputStartsEmitting() {
    Network network = settledLeverAndLamp();
    network.configureSlot(
        OWNER,
        NODE,
        SECOND_SLOT,
        new ChannelSlot(Direction.INPUT, NODE_POSITION.face(), SECOND_LAMP));
    network.readInput(NODE, SECOND_SLOT, LEVER);
    simulator.evaluate(network);
    network.configureSlot(
        OWNER, OTHER_NODE, SECOND_SLOT, new ChannelSlot(Direction.OUTPUT, Face.UP, SECOND_LAMP));

    assertEquals(List.of(new OutputChange(SECOND_LAMP_FACE, LEVER)), simulator.evaluate(network));
  }

  @Test
  void namesAndColorsChangeNoSignal() {
    Network network = settledLeverAndLamp();
    network.rename(OWNER, NEW_NAME);
    network.recolor(OWNER, ChannelColor.LIME);
    network.renameChannel(OWNER, LAMP, NEW_NAME);
    network.nodeSettings().rename(OWNER, NODE, NEW_NAME);

    assertFalse(network.isPending());
  }

  /** The block already has nothing, so telling it again would be a useless update. */
  @Test
  void detachingALampThatWasOffChangesNothing() {
    Network network = settledLeverAndLamp();
    network.readInput(NODE, SLOT, Signal.OFF);
    simulator.evaluate(network);
    network.detachNode(OWNER, OTHER_NODE);

    assertEquals(List.of(), simulator.evaluate(network));
  }
}
