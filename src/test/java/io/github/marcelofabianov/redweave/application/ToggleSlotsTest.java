package io.github.marcelofabianov.redweave.application;

import static io.github.marcelofabianov.redweave.application.ApplicationFixture.NODE_ID;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.STRANGER;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.Direction;
import io.github.marcelofabianov.redweave.domain.network.Face;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * The Toggle mode of the Configurator enables every configured channel of a node with a click and
 * disables them with Shift + click; a channel never configured stays as it is (answer 241).
 */
class ToggleSlotsTest {
  private static final NodeId NODE = new NodeId(NODE_ID);
  private static final ChannelColor DOOR = ChannelColor.RED;
  private static final ChannelColor LAMP = ChannelColor.BLUE;
  private static final ChannelColor NEVER = ChannelColor.LIME;
  private static final int CONFIGURED = 2;
  private static final int NONE = 0;

  private final ApplicationFixture fixture = new ApplicationFixture();
  private final ToggleSlots toggle = new ToggleSlots(fixture.networks, fixture.networks);

  /** A node with two channels set up and left disabled (answer 237). */
  private Network networkWithTwoDisabledChannels() {
    NetworkId id = fixture.ownersNetworkWithANode();
    Network network = fixture.networks.find(id).orElseThrow();
    for (ChannelColor channel : new ChannelColor[] {DOOR, LAMP}) {
      network.setUpSlot(OWNER, NODE, channel.ordinal(), Direction.INPUT, Face.UP);
    }
    return network;
  }

  private static Optional<ChannelSlot> slot(Network network, ChannelColor channel) {
    return network.node(NODE).orElseThrow().slots().get(channel.ordinal());
  }

  @Test
  void aClickEnablesEveryConfiguredChannel() {
    Network network = networkWithTwoDisabledChannels();

    assertEquals(new ToggleResult(Outcome.ACCEPTED, CONFIGURED), toggle.toggle(OWNER, NODE, true));
    assertEquals(true, slot(network, DOOR).orElseThrow().active());
    assertEquals(true, slot(network, LAMP).orElseThrow().active());
    assertEquals(Optional.empty(), slot(network, NEVER));
  }

  @Test
  void shiftAndClickDisablesThemAll() {
    Network network = networkWithTwoDisabledChannels();
    toggle.toggle(OWNER, NODE, true);

    assertEquals(new ToggleResult(Outcome.ACCEPTED, CONFIGURED), toggle.toggle(OWNER, NODE, false));
    assertEquals(false, slot(network, DOOR).orElseThrow().active());
  }

  @Test
  void onlyTheOwnerToggles() {
    Network network = networkWithTwoDisabledChannels();

    assertEquals(new ToggleResult(Outcome.NOT_OWNER, NONE), toggle.toggle(STRANGER, NODE, true));
    assertEquals(false, slot(network, DOOR).orElseThrow().active());
  }

  @Test
  void aNodeWithoutANetworkHasNothingToToggle() {
    assertEquals(new ToggleResult(Outcome.UNKNOWN_NODE, NONE), toggle.toggle(OWNER, NODE, true));
  }
}
