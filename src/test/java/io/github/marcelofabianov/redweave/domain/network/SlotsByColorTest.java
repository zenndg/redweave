package io.github.marcelofabianov.redweave.domain.network;

import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE_POSITION;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OTHER_NODE;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OTHER_NODE_POSITION;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.STRANGER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.network;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * A node has 16 slots, one per channel color, each enabled or disabled and with a name of its own
 * (answers 224 to 228).
 */
class SlotsByColorTest {
  private static final ChannelColor DOOR = ChannelColor.RED;
  private static final int DOOR_SLOT = DOOR.ordinal();
  private static final int FIRST_SLOTS = 4;
  private static final String LEVER = "Redstone from lever 1";
  private static final String BLANK = "  ";
  private static final String TOO_LONG = "x".repeat(TestLimits.STARTING.slotNameLength() + 1);
  private static final Signal READ = new Signal(9);

  private static Network networkWithNode() {
    Network network = network();
    network.attachNode(OWNER, NODE, NODE_POSITION, Optional.empty());
    return network;
  }

  private static Optional<ChannelSlot> slot(Network network, int index) {
    return network.node(NODE).orElseThrow().slots().get(index);
  }

  @Test
  void aNodeHasSixteenSlotsAllNeverEnabled() {
    List<Optional<ChannelSlot>> slots = networkWithNode().node(NODE).orElseThrow().slots();

    assertEquals(ChannelColor.values().length, slots.size());
    assertTrue(slots.stream().allMatch(Optional::isEmpty));
  }

  /** The slot of a color holds only that channel. */
  @Test
  void aSlotTakesOnlyTheChannelOfItsColor() {
    Network network = networkWithNode();
    ChannelSlot orange = new ChannelSlot(Direction.INPUT, Face.UP, ChannelColor.ORANGE);

    assertEquals(Outcome.INVALID_SLOT, network.configureSlot(OWNER, NODE, DOOR_SLOT, orange));
  }

  @Test
  void aNodeNeverHoldsSlotsOutOfTheirColor() {
    List<Optional<ChannelSlot>> slots =
        Collections.nCopies(
            ChannelColor.values().length,
            Optional.of(new ChannelSlot(Direction.INPUT, Face.UP, DOOR)));

    assertThrows(
        IllegalArgumentException.class,
        () -> new Node(NODE, Optional.empty(), NODE_POSITION, slots));
  }

  /** The first time, a slot becomes an input on the face the node sticks to (answer 228). */
  @Test
  void enablingANewSlotMakesAnInputOnTheStuckFace() {
    Network network = networkWithNode();

    assertEquals(Outcome.ACCEPTED, network.enableSlot(OWNER, NODE, DOOR_SLOT));

    ChannelSlot input = new ChannelSlot(Direction.INPUT, NODE_POSITION.face(), DOOR);
    assertEquals(Optional.of(input), slot(network, DOOR_SLOT));
  }

  /** Disabling keeps the direction, the face and the name, to enable it as it was (228). */
  @Test
  void disablingKeepsTheSlotAndEnablingBringsItBack() {
    Network network = networkWithNode();
    ChannelSlot output = new ChannelSlot(Direction.OUTPUT, Face.DOWN, DOOR);
    network.configureSlot(OWNER, NODE, DOOR_SLOT, output);
    network.renameSlot(OWNER, NODE, DOOR_SLOT, LEVER);

    assertEquals(Outcome.ACCEPTED, network.disableSlot(OWNER, NODE, DOOR_SLOT));
    ChannelSlot kept = slot(network, DOOR_SLOT).orElseThrow();
    assertFalse(kept.active());
    assertEquals(Optional.of(LEVER), kept.name());
    network.enableSlot(OWNER, NODE, DOOR_SLOT);
    assertEquals(output.named(Optional.of(LEVER)), slot(network, DOOR_SLOT).orElseThrow());
  }

  /** A disabled input does not count, and its reading is forgotten. */
  @Test
  void aDisabledInputDoesNotCount() {
    Network network = networkWithNode();
    network.enableSlot(OWNER, NODE, DOOR_SLOT);
    network.readInput(NODE, DOOR_SLOT, READ);

    network.disableSlot(OWNER, NODE, DOOR_SLOT);

    assertEquals(Signal.OFF, network.channelValue(DOOR));
    assertFalse(network.readInput(NODE, DOOR_SLOT, READ));
    assertEquals(Signal.OFF, network.slotSignal(NODE, DOOR_SLOT));
  }

  @Test
  void aDisabledOutputEmitsNothing() {
    Network network = networkWithNode();
    network.attachNode(OWNER, OTHER_NODE, OTHER_NODE_POSITION, Optional.empty());
    network.enableSlot(OWNER, OTHER_NODE, DOOR_SLOT);
    network.readInput(OTHER_NODE, DOOR_SLOT, READ);
    network.configureSlot(
        OWNER, NODE, DOOR_SLOT, new ChannelSlot(Direction.OUTPUT, NODE_POSITION.face(), DOOR));

    network.disableSlot(OWNER, NODE, DOOR_SLOT);

    assertEquals(Signal.OFF, network.output(NODE, DOOR_SLOT));
    assertEquals(Signal.OFF, network.emission(NODE));
  }

  /** The name is the slot's, blank takes it away, and the limit comes from the config (227). */
  @Test
  void theOwnerNamesASlot() {
    Network network = networkWithNode();

    assertEquals(Outcome.ACCEPTED, network.renameSlot(OWNER, NODE, DOOR_SLOT, LEVER));
    assertEquals(Optional.of(LEVER), slot(network, DOOR_SLOT).orElseThrow().name());
    assertFalse(slot(network, DOOR_SLOT).orElseThrow().active());
    assertEquals(Outcome.INVALID_NAME, network.renameSlot(OWNER, NODE, DOOR_SLOT, TOO_LONG));
    assertEquals(Outcome.ACCEPTED, network.renameSlot(OWNER, NODE, DOOR_SLOT, BLANK));
    assertEquals(Optional.empty(), slot(network, DOOR_SLOT).orElseThrow().name());
  }

  @Test
  void onlyTheOwnerChangesASlot() {
    Network network = networkWithNode();

    assertEquals(Outcome.NOT_OWNER, network.enableSlot(STRANGER, NODE, DOOR_SLOT));
    assertEquals(Outcome.NOT_OWNER, network.disableSlot(STRANGER, NODE, DOOR_SLOT));
    assertEquals(Outcome.NOT_OWNER, network.renameSlot(STRANGER, NODE, DOOR_SLOT, LEVER));
  }

  @Test
  void aSlotOutsideTheColorsIsRefused() {
    Network network = networkWithNode();
    int past = ChannelColor.values().length;

    assertEquals(Outcome.INVALID_SLOT, network.enableSlot(OWNER, NODE, past));
    assertEquals(Outcome.INVALID_SLOT, network.disableSlot(OWNER, NODE, DOOR_SLOT));
    assertEquals(Outcome.UNKNOWN_NODE, network.enableSlot(OWNER, OTHER_NODE, DOOR_SLOT));
  }

  /** The corners of the plate show the first 4 enabled channels, in color order (229). */
  @Test
  void theEnabledChannelsComeInColorOrder() {
    Network network = networkWithNode();
    for (ChannelColor color : List.of(ChannelColor.BLACK, DOOR, ChannelColor.WHITE)) {
      network.enableSlot(OWNER, NODE, color.ordinal());
    }
    network.renameSlot(OWNER, NODE, ChannelColor.ORANGE.ordinal(), LEVER);

    List<ChannelColor> enabled = network.node(NODE).orElseThrow().enabledChannels();

    assertEquals(List.of(ChannelColor.WHITE, DOOR, ChannelColor.BLACK), enabled);
    assertTrue(enabled.size() < FIRST_SLOTS);
  }
}
