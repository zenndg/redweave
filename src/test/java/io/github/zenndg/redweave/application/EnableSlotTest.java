package io.github.zenndg.redweave.application;

import static io.github.zenndg.redweave.application.ApplicationFixture.NODE_ID;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.NODE_POSITION;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.STRANGER;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.zenndg.redweave.domain.network.ChannelSlot;
import io.github.zenndg.redweave.domain.network.Direction;
import io.github.zenndg.redweave.domain.network.NetworkId;
import io.github.zenndg.redweave.domain.network.NodeId;
import io.github.zenndg.redweave.domain.network.Outcome;
import io.github.zenndg.redweave.domain.signal.ChannelColor;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** The status of a slot on the Node Screen turned to Enabled (answers 226 and 228). */
class EnableSlotTest {
  private static final NodeId NODE = new NodeId(NODE_ID);
  private static final ChannelColor DOOR = ChannelColor.RED;
  private static final int DOOR_SLOT = DOOR.ordinal();
  private static final int PAST_THE_LAST_SLOT = ChannelColor.values().length;

  private final ApplicationFixture fixture = new ApplicationFixture();
  private final EnableSlot enableSlot = new EnableSlot(fixture.networks, fixture.networks);

  private Optional<ChannelSlot> slot(NetworkId network, int index) {
    return fixture.networks.find(network).orElseThrow().node(NODE).orElseThrow().slots().get(index);
  }

  /** A node joins with every slot disabled (answer 226). */
  @Test
  void aNodeJoinsWithEverySlotNeverEnabled() {
    NetworkId network = fixture.ownersNetworkWithANode();

    for (ChannelColor color : ChannelColor.values()) {
      assertEquals(Optional.empty(), slot(network, color.ordinal()));
    }
  }

  @Test
  void enablingASlotMakesAnInputOnTheStuckFace() {
    NetworkId network = fixture.ownersNetworkWithANode();

    assertEquals(Outcome.ACCEPTED, enableSlot.enable(OWNER, NODE, DOOR_SLOT));
    ChannelSlot expected = new ChannelSlot(Direction.INPUT, NODE_POSITION.face(), DOOR);
    assertEquals(Optional.of(expected), slot(network, DOOR_SLOT));
  }

  @Test
  void anotherPlayerCannotEnableASlot() {
    NetworkId network = fixture.ownersNetworkWithANode();

    assertEquals(Outcome.NOT_OWNER, enableSlot.enable(STRANGER, NODE, DOOR_SLOT));
    assertEquals(Optional.empty(), slot(network, DOOR_SLOT));
  }

  @Test
  void aSlotPastTheLastOneIsRefused() {
    fixture.ownersNetworkWithANode();

    assertEquals(Outcome.INVALID_SLOT, enableSlot.enable(OWNER, NODE, PAST_THE_LAST_SLOT));
  }

  @Test
  void aNodeWithoutANetworkHasNoSlotToEnable() {
    assertEquals(Outcome.UNKNOWN_NODE, enableSlot.enable(OWNER, NODE, DOOR_SLOT));
  }
}
