package io.github.marcelofabianov.redweave.application;

import static io.github.marcelofabianov.redweave.application.ApplicationFixture.NODE_ID;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE_POSITION;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.STRANGER;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.Direction;
import io.github.marcelofabianov.redweave.domain.network.Face;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/** The Node Screen changes a slot of the node it shows, in the one network of the node. */
class ChangeSlotTest {
  private static final int SLOT = 1;
  private static final NodeId NODE = new NodeId(NODE_ID);
  private static final NodeId UNKNOWN = new NodeId(UUID.randomUUID());
  private static final ChannelSlot LIGHT_THE_LAMP =
      new ChannelSlot(Direction.OUTPUT, Face.UP, ChannelColor.YELLOW);

  private final ApplicationFixture fixture = new ApplicationFixture();
  private final ConfigureSlot configureSlot = new ConfigureSlot(fixture.networks, fixture.networks);
  private final ClearSlot clearSlot = new ClearSlot(fixture.networks, fixture.networks);

  /** The network of OWNER with the node placed, and how many saves that took. */
  private int placedNode() {
    fixture.ownersNetwork();
    new PlaceNode(
            new FixedIds(List.of(NODE_ID)), fixture.current, fixture.networks, fixture.networks)
        .place(OWNER, NODE_POSITION);
    return fixture.networks.saves();
  }

  private Optional<ChannelSlot> theSlot() {
    return fixture
        .networks
        .findByNode(NODE)
        .orElseThrow()
        .node(NODE)
        .orElseThrow()
        .slots()
        .get(SLOT);
  }

  @Test
  void theOwnerConfiguresASlotAndItIsSaved() {
    int saves = placedNode();

    assertEquals(Outcome.ACCEPTED, configureSlot.configure(OWNER, NODE, SLOT, LIGHT_THE_LAMP));
    assertEquals(Optional.of(LIGHT_THE_LAMP), theSlot());
    assertEquals(saves + 1, fixture.networks.saves());
  }

  @Test
  void aRefusedChangeIsNotSaved() {
    int saves = placedNode();

    assertEquals(Outcome.NOT_OWNER, configureSlot.configure(STRANGER, NODE, SLOT, LIGHT_THE_LAMP));
    assertEquals(saves, fixture.networks.saves());
  }

  @Test
  void aNodeWithoutANetworkSaysSo() {
    assertEquals(
        Outcome.UNKNOWN_NODE, configureSlot.configure(OWNER, UNKNOWN, SLOT, LIGHT_THE_LAMP));
    assertEquals(Outcome.UNKNOWN_NODE, clearSlot.clear(OWNER, UNKNOWN, SLOT));
  }

  @Test
  void theOwnerFreesASlotAndItIsSaved() {
    placedNode();
    configureSlot.configure(OWNER, NODE, SLOT, LIGHT_THE_LAMP);
    int saves = fixture.networks.saves();

    assertEquals(Outcome.ACCEPTED, clearSlot.clear(OWNER, NODE, SLOT));
    assertEquals(Optional.empty(), theSlot());
    assertEquals(saves + 1, fixture.networks.saves());
  }
}
