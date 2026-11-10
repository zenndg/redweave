package io.github.marcelofabianov.redweave.domain.network;

import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE_POSITION;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OTHER_NODE;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OTHER_NODE_POSITION;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OVERWORLD;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.network;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import org.junit.jupiter.api.Test;

/** An output repeats its channel in the block, and emits off when it does not count. */
class OutputTest {
  private static final int SLOT = 0;
  private static final int FREE_SLOT = 1;
  private static final int PAST_THE_LAST_SLOT = 4;
  private static final ChannelColor LAMP = ChannelColor.YELLOW;
  private static final Signal LEVER = new Signal(13);

  /** 122 blocks from the lever at x = 8, inside the radius, and 138 from the lamp at x = -8. */
  private static final BlockLocation NEAR_THE_LEVER_ONLY = new BlockLocation(OVERWORLD, 130, 64, 0);

  /** NODE reads the lever into the channel, and OTHER_NODE lights the lamp with it. */
  private static Network leverAndLamp() {
    Network network = network();
    network.attachNode(OWNER, NODE, NODE_POSITION);
    network.configureSlot(
        OWNER, NODE, SLOT, new ChannelSlot(Direction.INPUT, NODE_POSITION.face(), LAMP));
    network.attachNode(OWNER, OTHER_NODE, OTHER_NODE_POSITION);
    network.configureSlot(
        OWNER,
        OTHER_NODE,
        SLOT,
        new ChannelSlot(Direction.OUTPUT, OTHER_NODE_POSITION.face(), LAMP));
    network.readInput(NODE, SLOT, LEVER);
    return network;
  }

  @Test
  void anOutputRepeatsItsChannel() {
    assertEquals(LEVER, leverAndLamp().output(OTHER_NODE, SLOT));
  }

  /** The controller moves to where the lever still reaches it, and the lamp no longer does. */
  @Test
  void anOutputOutOfRangeEmitsOff() {
    Network network = leverAndLamp();
    network.removeController(OWNER);
    network.placeController(OWNER, NEAR_THE_LEVER_ONLY);
    assertEquals(LEVER, network.channelValue(LAMP));

    assertEquals(Signal.OFF, network.output(OTHER_NODE, SLOT));
  }

  @Test
  void anOutputOfAnOfflineNetworkEmitsOff() {
    Network network = leverAndLamp();
    network.removeController(OWNER);

    assertEquals(Signal.OFF, network.output(OTHER_NODE, SLOT));
  }

  @Test
  void aSlotThatIsNotAnOutputEmitsOff() {
    Network network = leverAndLamp();

    assertEquals(Signal.OFF, network.output(NODE, SLOT));
    assertEquals(Signal.OFF, network.output(OTHER_NODE, FREE_SLOT));
    assertEquals(Signal.OFF, network.output(OTHER_NODE, PAST_THE_LAST_SLOT));
  }

  @Test
  void anUnknownNodeEmitsOff() {
    Network network = leverAndLamp();
    network.detachNode(OWNER, OTHER_NODE);

    assertEquals(Signal.OFF, network.output(OTHER_NODE, SLOT));
  }

  /** In the world a node feeds its block like a lever, so it emits its strongest output (71). */
  @Test
  void aNodeEmitsItsStrongestOutput() {
    Network network = leverAndLamp();
    network.configureSlot(
        OWNER, OTHER_NODE, SLOT, new ChannelSlot(Direction.OUTPUT, Face.UP, ChannelColor.BLACK));
    network.configureSlot(
        OWNER,
        OTHER_NODE,
        FREE_SLOT,
        new ChannelSlot(Direction.OUTPUT, OTHER_NODE_POSITION.face(), LAMP));

    assertEquals(LEVER, network.emission(OTHER_NODE));
  }

  @Test
  void aNodeWithoutOutputsEmitsOff() {
    assertEquals(Signal.OFF, leverAndLamp().emission(NODE));
  }
}
