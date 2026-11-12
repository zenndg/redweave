package io.github.marcelofabianov.redweave.domain.network;

import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE_POSITION;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OTHER_NODE;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OTHER_NODE_POSITION;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.network;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** Each output slot that changes the value it emits is a line of the log (answers 243 to 246). */
class SlotOutputsTest {
  private static final ChannelColor LAMP = ChannelColor.YELLOW;
  private static final int SLOT = LAMP.ordinal();
  private static final Signal LEVER = new Signal(13);
  private static final Signal DIMMER = new Signal(5);
  private static final String LAMP_NODE = "Lamp";
  private static final String NEW_LAMP_NODE = "Hall lamp";
  private static final String MAIN_LIGHT = "Main light";
  private static final LocalDateTime NOON = LocalDateTime.of(2026, 11, 11, 12, 0, 0);

  private final SlotOutputs outputs = new SlotOutputs();

  /** NODE reads the lever into the channel, and the node named Lamp emits it. */
  private static Network leverAndLamp() {
    Network network = network();
    network.attachNode(OWNER, NODE, NODE_POSITION, Optional.empty());
    network.configureSlot(
        OWNER, NODE, SLOT, new ChannelSlot(Direction.INPUT, NODE_POSITION.face(), LAMP));
    network.attachNode(OWNER, OTHER_NODE, OTHER_NODE_POSITION, Optional.of(LAMP_NODE));
    network.configureSlot(
        OWNER,
        OTHER_NODE,
        SLOT,
        new ChannelSlot(Direction.OUTPUT, OTHER_NODE_POSITION.face(), LAMP));
    network.readInput(NODE, SLOT, LEVER);
    return network;
  }

  private static SignalChange lamp(Signal value) {
    return new SignalChange(NOON, LAMP_NODE, LAMP, Optional.empty(), Direction.OUTPUT, value);
  }

  /** After loading, the faces are applied again, and that is no change (answer 244). */
  @Test
  void theFirstLookRecordsNothing() {
    assertEquals(List.of(), outputs.observe(leverAndLamp(), NOON));
  }

  @Test
  void anOutputThatChangesIsALine() {
    Network network = leverAndLamp();
    outputs.observe(network, NOON);

    network.readInput(NODE, SLOT, DIMMER);

    assertEquals(List.of(lamp(DIMMER)), outputs.observe(network, NOON));
  }

  @Test
  void anOutputThatKeepsItsValueIsNoLine() {
    Network network = leverAndLamp();
    outputs.observe(network, NOON);

    assertEquals(List.of(), outputs.observe(network, NOON));
  }

  /** An offline network emits off, and the fall to 0 is a line too (answer 243). */
  @Test
  void anOutputFallingToOffIsALine() {
    Network network = leverAndLamp();
    outputs.observe(network, NOON);

    network.turnOff(OWNER);

    assertEquals(List.of(lamp(Signal.OFF)), outputs.observe(network, NOON));
  }

  /** A disabled slot emits 0, so disabling and enabling are changes (answer 246). */
  @Test
  void disablingAndEnablingAnOutputAreLines() {
    Network network = leverAndLamp();
    outputs.observe(network, NOON);

    network.disableSlot(OWNER, OTHER_NODE, SLOT);
    assertEquals(List.of(lamp(Signal.OFF)), outputs.observe(network, NOON));

    network.enableSlot(OWNER, OTHER_NODE, SLOT);
    assertEquals(List.of(lamp(LEVER)), outputs.observe(network, NOON));
  }

  /** The line keeps the names of the moment it happened (answer 245). */
  @Test
  void aLineTakesTheNamesOfTheMoment() {
    Network network = leverAndLamp();
    outputs.observe(network, NOON);
    network.renameNode(OWNER, OTHER_NODE, NEW_LAMP_NODE);
    network.renameChannel(OWNER, LAMP, MAIN_LIGHT);

    network.readInput(NODE, SLOT, DIMMER);

    SignalChange expected =
        new SignalChange(
            NOON, NEW_LAMP_NODE, LAMP, Optional.of(MAIN_LIGHT), Direction.OUTPUT, DIMMER);
    assertEquals(List.of(expected), outputs.observe(network, NOON));
  }
}
