package io.github.marcelofabianov.redweave.application;

import static io.github.marcelofabianov.redweave.application.ApplicationFixture.NODE_ID;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.STRANGER;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.Direction;
import io.github.marcelofabianov.redweave.domain.network.Face;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/**
 * The Node Screen changes a slot of the node it shows, in the one network of the node: the
 * direction and face, the status and the name (answers 224 to 228).
 */
class ChangeSlotTest {
  private static final ChannelColor LAMP = ChannelColor.YELLOW;
  private static final int SLOT = LAMP.ordinal();
  private static final NodeId NODE = new NodeId(NODE_ID);
  private static final NodeId UNKNOWN = new NodeId(UUID.randomUUID());
  private static final ChannelSlot LIGHT_THE_LAMP =
      new ChannelSlot(Direction.OUTPUT, Face.UP, LAMP);

  private final ApplicationFixture fixture = new ApplicationFixture();
  private final ConfigureSlot configureSlot = new ConfigureSlot(fixture.networks, fixture.networks);
  private final DisableSlot disableSlot = new DisableSlot(fixture.networks, fixture.networks);
  private final EnableSlot enableSlot = new EnableSlot(fixture.networks, fixture.networks);

  /** The network of OWNER with the node in it, and how many saves that took. */
  private int placedNode() {
    fixture.ownersNetworkWithANode();
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
    assertEquals(Optional.of(LIGHT_THE_LAMP.enabled(false)), theSlot());
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
    assertEquals(Outcome.UNKNOWN_NODE, disableSlot.disable(OWNER, UNKNOWN, SLOT));
  }

  @Test
  void theOwnerDisablesASlotWhichKeepsItsConfiguration() {
    placedNode();
    configureSlot.configure(OWNER, NODE, SLOT, LIGHT_THE_LAMP);
    enableSlot.enable(OWNER, NODE, SLOT);
    int saves = fixture.networks.saves();

    assertEquals(Outcome.ACCEPTED, disableSlot.disable(OWNER, NODE, SLOT));
    assertEquals(Optional.of(LIGHT_THE_LAMP.enabled(false)), theSlot());
    assertEquals(saves + 1, fixture.networks.saves());
  }

  /** An enabled slot is locked: the owner disables it to set it up (answer 237). */
  @Test
  void anEnabledSlotIsNotConfigured() {
    placedNode();
    enableSlot.enable(OWNER, NODE, SLOT);

    assertEquals(Outcome.SLOT_ENABLED, configureSlot.configure(OWNER, NODE, SLOT, LIGHT_THE_LAMP));
  }

  /** The screen sends only the direction and the face, so the status stays (answer 228). */
  @Test
  void configuringKeepsTheStatus() {
    placedNode();

    configureSlot.configure(OWNER, NODE, SLOT, LIGHT_THE_LAMP);

    assertEquals(Optional.of(LIGHT_THE_LAMP.enabled(false)), theSlot());
  }
}
