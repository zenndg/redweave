package io.github.marcelofabianov.redweave.domain.network;

import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE_POSITION;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.network;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** A reading belongs to the slot as it was configured, so a change of the slot drops it. */
class StaleReadingTest {
  private static final int SLOT = 0;
  private static final ChannelColor OLD_CHANNEL = ChannelColor.RED;
  private static final ChannelColor NEW_CHANNEL = ChannelColor.BLUE;
  private static final Signal READ = new Signal(13);

  private static ChannelSlot reading(ChannelColor channel) {
    return new ChannelSlot(Direction.INPUT, NODE_POSITION.face(), channel);
  }

  private static Network networkThatRead() {
    Network network = network();
    network.attachNode(OWNER, NODE, NODE_POSITION, Optional.empty());
    network.configureSlot(OWNER, NODE, SLOT, reading(OLD_CHANNEL));
    network.readInput(NODE, SLOT, READ);
    return network;
  }

  @Test
  void movingASlotToAnotherChannelDoesNotCarryTheOldReading() {
    Network network = networkThatRead();
    network.configureSlot(OWNER, NODE, SLOT, reading(NEW_CHANNEL));

    assertEquals(Signal.OFF, network.channelValue(OLD_CHANNEL));
    assertEquals(Signal.OFF, network.channelValue(NEW_CHANNEL));
  }

  @Test
  void freeingASlotDropsItsReading() {
    Network network = networkThatRead();
    network.clearSlot(OWNER, NODE, SLOT);
    network.configureSlot(OWNER, NODE, SLOT, reading(OLD_CHANNEL));

    assertEquals(Signal.OFF, network.channelValue(OLD_CHANNEL));
  }

  @Test
  void detachingANodeDropsItsReadings() {
    Network network = networkThatRead();
    network.detachNode(OWNER, NODE);
    network.attachNode(OWNER, NODE, NODE_POSITION, Optional.empty());
    network.configureSlot(OWNER, NODE, SLOT, reading(OLD_CHANNEL));

    assertEquals(Signal.OFF, network.channelValue(OLD_CHANNEL));
  }
}
