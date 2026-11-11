package io.github.marcelofabianov.redweave.domain.simulation;

import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE_POSITION;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OTHER_NODE;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OTHER_NODE_POSITION;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OVERWORLD;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.network;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.marcelofabianov.redweave.domain.network.BlockLocation;
import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.Direction;
import io.github.marcelofabianov.redweave.domain.network.Face;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.OutputChange;
import io.github.marcelofabianov.redweave.domain.network.OutputFace;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** NODE reads two levers into two channels, and OTHER_NODE emits them. */
class OutputFacesTest {
  private static final int FIRST_SLOT = 0;
  private static final int SECOND_SLOT = 1;
  private static final ChannelColor DOOR = ChannelColor.RED;
  private static final ChannelColor LAMP = ChannelColor.YELLOW;
  private static final Signal WEAK = new Signal(5);
  private static final Signal STRONG = new Signal(12);
  private static final Face WEST = OTHER_NODE_POSITION.face();
  private static final OutputFace WEST_FACE =
      new OutputFace(OTHER_NODE, OTHER_NODE_POSITION.block(), WEST);
  private static final OutputFace UP_FACE =
      new OutputFace(OTHER_NODE, OTHER_NODE_POSITION.block(), Face.UP);

  /** 122 blocks from the levers at x = 8, inside the radius, and 138 from the lamps at x = -8. */
  private static final BlockLocation NEAR_THE_LEVERS_ONLY =
      new BlockLocation(OVERWORLD, 130, 64, 0);

  private final Simulator simulator = new Simulator();

  private static Network twoLevers(Face doorFace, Face lampFace) {
    Network network = network();
    network.attachNode(OWNER, NODE, NODE_POSITION, Optional.empty());
    network.configureSlot(OWNER, NODE, FIRST_SLOT, input(DOOR));
    network.configureSlot(OWNER, NODE, SECOND_SLOT, input(LAMP));
    network.attachNode(OWNER, OTHER_NODE, OTHER_NODE_POSITION, Optional.empty());
    network.configureSlot(OWNER, OTHER_NODE, FIRST_SLOT, output(doorFace, DOOR));
    network.configureSlot(OWNER, OTHER_NODE, SECOND_SLOT, output(lampFace, LAMP));
    network.readInput(NODE, FIRST_SLOT, WEAK);
    network.readInput(NODE, SECOND_SLOT, STRONG);
    return network;
  }

  private static ChannelSlot input(ChannelColor channel) {
    return new ChannelSlot(Direction.INPUT, NODE_POSITION.face(), channel);
  }

  private static ChannelSlot output(Face face, ChannelColor channel) {
    return new ChannelSlot(Direction.OUTPUT, face, channel);
  }

  @Test
  void twoOutputsOnTheSameFaceEmitTheStrongest() {
    assertEquals(
        List.of(new OutputChange(WEST_FACE, STRONG)), simulator.evaluate(twoLevers(WEST, WEST)));
  }

  /** The strongest comes first here, so keeping the last one would give the weak signal. */
  @Test
  void theStrongestWinsWhicheverSlotComesFirst() {
    Network network = twoLevers(WEST, WEST);
    network.readInput(NODE, FIRST_SLOT, STRONG);
    network.readInput(NODE, SECOND_SLOT, WEAK);

    assertEquals(List.of(new OutputChange(WEST_FACE, STRONG)), simulator.evaluate(network));
  }

  @Test
  void onlyTheFaceWhoseChannelChangedIsGivenBack() {
    Network network = twoLevers(WEST, Face.UP);
    simulator.evaluate(network);
    network.readInput(NODE, FIRST_SLOT, STRONG);

    assertEquals(List.of(new OutputChange(WEST_FACE, STRONG)), simulator.evaluate(network));
  }

  @Test
  void aLampOutOfRangeTurnsOff() {
    Network network = twoLevers(WEST, Face.UP);
    simulator.evaluate(network);
    network.removeController(OWNER);
    network.placeController(OWNER, NEAR_THE_LEVERS_ONLY);

    assertEquals(
        List.of(new OutputChange(WEST_FACE, Signal.OFF), new OutputChange(UP_FACE, Signal.OFF)),
        simulator.evaluate(network));
  }
}
