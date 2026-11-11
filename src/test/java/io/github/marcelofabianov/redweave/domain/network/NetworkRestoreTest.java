package io.github.marcelofabianov.redweave.domain.network;

import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.COLOR;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.CONTROLLER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.ID;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NAME;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE_POSITION;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OTHER_NODE;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OWNER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * The save is checked at the border, so a snapshot that breaks the rules is a programming error.
 * The limits of the config only hold for new changes (answers 90, 92 and 93).
 */
class NetworkRestoreTest {
  private static final int SLOT = 0;
  private static final int UPGRADED_SLOTS = 9;
  private static final int MISSING_SLOT = 8;
  private static final int LONG_NAME_LENGTH = 40;
  private static final String LETTER = "x";
  private static final String BLANK = "  ";
  private static final String PADDED_NAME = " Network ";
  private static final String CHANNEL_NAME = "Door";
  private static final String NODE_NAME = "Front lamp";
  private static final ChannelColor CHANNEL = ChannelColor.RED;
  private static final Signal READ = new Signal(7);

  private static Node node(Direction direction) {
    List<Optional<ChannelSlot>> slots =
        List.of(
            Optional.of(new ChannelSlot(direction, Face.EAST, CHANNEL)),
            Optional.empty(),
            Optional.empty(),
            Optional.empty());
    return new Node(NODE, Optional.empty(), NODE_POSITION, slots);
  }

  private static Node named(Optional<String> name) {
    Node input = node(Direction.INPUT);
    return new Node(input.id(), name, input.position(), input.slots());
  }

  private static NetworkSnapshot snapshot(
      String name, List<Channel> channels, List<Node> nodes, List<SlotReading> readings) {
    return new NetworkSnapshot(
        ID, OWNER, name, COLOR, Optional.of(CONTROLLER), false, channels, nodes, readings);
  }

  private static NetworkSnapshot withNodes(List<Node> nodes, List<SlotReading> readings) {
    return snapshot(NAME, List.of(), nodes, readings);
  }

  private static void assertRejected(NetworkSnapshot snapshot) {
    assertThrows(
        IllegalArgumentException.class, () -> Network.restore(snapshot, TestLimits.STARTING));
  }

  @Test
  void aNodeKeepsItsSlotsWhenTheConfigNowAllowsFewer() {
    Node upgraded =
        new Node(
            NODE,
            Optional.empty(),
            NODE_POSITION,
            Collections.nCopies(UPGRADED_SLOTS, Optional.empty()));

    Network restored =
        Network.restore(withNodes(List.of(upgraded), List.of()), TestLimits.STARTING);

    assertEquals(UPGRADED_SLOTS, restored.node(NODE).orElseThrow().slots().size());
  }

  @Test
  void aNameKeepsItsLengthWhenTheConfigNowAllowsLess() {
    String longName = LETTER.repeat(LONG_NAME_LENGTH);

    Network restored =
        Network.restore(snapshot(longName, List.of(), List.of(), List.of()), TestLimits.STARTING);

    assertEquals(longName, restored.name());
  }

  @Test
  void aBlankOrPaddedNameIsRejected() {
    assertRejected(snapshot(BLANK, List.of(), List.of(), List.of()));
    assertRejected(snapshot(PADDED_NAME, List.of(), List.of(), List.of()));
  }

  @Test
  void aChannelTwiceOrWithABlankNameIsRejected() {
    Channel named = new Channel(CHANNEL, Optional.of(CHANNEL_NAME));
    assertRejected(snapshot(NAME, List.of(named, named), List.of(), List.of()));
    assertRejected(
        snapshot(NAME, List.of(new Channel(CHANNEL, Optional.of(BLANK))), List.of(), List.of()));
  }

  @Test
  void theSameNodeTwiceIsRejected() {
    Node input = node(Direction.INPUT);

    assertRejected(withNodes(List.of(input, input), List.of()));
  }

  @Test
  void aReadingOutsideAnInputSlotIsRejected() {
    SlotReading reading = new SlotReading(NODE, SLOT, READ);
    SlotReading missingSlot = new SlotReading(NODE, MISSING_SLOT, READ);

    assertRejected(withNodes(List.of(), List.of(reading)));
    assertRejected(withNodes(List.of(node(Direction.OUTPUT)), List.of(reading)));
    assertRejected(withNodes(List.of(node(Direction.INPUT)), List.of(missingSlot)));
  }

  @Test
  void aReadingOfAnInputSlotIsKept() {
    SlotReading reading = new SlotReading(NODE, SLOT, READ);

    Network restored =
        Network.restore(
            withNodes(List.of(node(Direction.INPUT)), List.of(reading)), TestLimits.STARTING);

    assertEquals(READ, restored.channelValue(CHANNEL));
  }

  @Test
  void aNodeKeepsItsNameAndANameLongerThanTheConfigNowAllows() {
    String longName = LETTER.repeat(LONG_NAME_LENGTH);
    Node lamp = named(Optional.of(NODE_NAME));
    Node longNamed = new Node(OTHER_NODE, Optional.of(longName), NODE_POSITION, lamp.slots());

    Network restored =
        Network.restore(withNodes(List.of(lamp, longNamed), List.of()), TestLimits.STARTING);

    assertEquals(NODE_NAME, restored.node(NODE).orElseThrow().displayName());
    assertEquals(longName, restored.node(OTHER_NODE).orElseThrow().displayName());
  }

  @Test
  void aNodeWithABlankOrPaddedNameIsRejected() {
    assertRejected(withNodes(List.of(named(Optional.of(BLANK))), List.of()));
    assertRejected(withNodes(List.of(named(Optional.of(PADDED_NAME))), List.of()));
  }
}
