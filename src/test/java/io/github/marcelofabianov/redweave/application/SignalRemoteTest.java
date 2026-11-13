package io.github.marcelofabianov.redweave.application;

import static io.github.marcelofabianov.redweave.application.ApplicationFixture.MISSING_NETWORK;
import static io.github.marcelofabianov.redweave.application.ApplicationFixture.NODE_ID;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OVERWORLD;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.STRANGER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.marcelofabianov.redweave.domain.network.BlockLocation;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import io.github.marcelofabianov.redweave.domain.network.RemoteId;
import io.github.marcelofabianov.redweave.domain.network.RemoteInput;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/**
 * The Signal Remote in the use cases: the tablets of each tick, by the reach of whoever holds them;
 * the link, only by the owner; and what the item shows of its link (answers 354 to 359).
 */
class SignalRemoteTest {
  private static final NodeId NODE = new NodeId(NODE_ID);
  private static final int SLOT = 0;
  private static final int FREE_SLOT = 1;
  private static final ChannelColor CHANNEL = ChannelColor.values()[SLOT];
  private static final String TABLET_UUID = "3d4e5f6a-7b8c-4d9e-8f0a-1b2c3d4e5f6a";
  private static final RemoteId TABLET = new RemoteId(UUID.fromString(TABLET_UUID));
  private static final RemoteInput ON_SLOT = new RemoteInput(TABLET, NODE, SLOT);
  private static final int GROUND = 64;
  private static final int NEAR = 10;
  private static final int FAR = 5000;
  private static final BlockLocation NEAR_THE_CONTROLLER =
      new BlockLocation(OVERWORLD, NEAR, GROUND, NEAR);
  private static final BlockLocation FAR_AWAY = new BlockLocation(OVERWORLD, FAR, GROUND, FAR);
  private static final Signal MIDDLE = new Signal(9);
  private static final int ONE = 1;

  private final ApplicationFixture fixture = new ApplicationFixture();
  private final EnableSlot enableSlot = new EnableSlot(fixture.networks, fixture.networks);
  private final ApplyRemotes applyRemotes = new ApplyRemotes(fixture.networks);
  private final LinkRemote linkRemote = new LinkRemote(fixture.networks);
  private final ShowRemote showRemote = new ShowRemote(fixture.networks);

  private NetworkId networkWithAnInput() {
    NetworkId id = fixture.ownersNetworkWithANode();
    enableSlot.enable(OWNER, NODE, SLOT);
    network(id).settle(Map.of());
    return id;
  }

  private Network network(NetworkId id) {
    return fixture.networks.find(id).orElseThrow();
  }

  @Test
  void aTabletHeldWithinReachTurnsTheChannelOn() {
    NetworkId id = networkWithAnInput();

    applyRemotes.apply(List.of(new RemoteSighting(id, ON_SLOT, NEAR_THE_CONTROLLER)));

    assertEquals(Signal.MAX, network(id).channelValue(CHANNEL));
  }

  @Test
  void aTabletHeldOutOfReachDoesNotCount() {
    NetworkId id = networkWithAnInput();

    applyRemotes.apply(List.of(new RemoteSighting(id, ON_SLOT, FAR_AWAY)));

    assertEquals(Signal.OFF, network(id).channelValue(CHANNEL));
  }

  @Test
  void aNetworkWithNoTabletThisTickGoesBackToOff() {
    NetworkId id = networkWithAnInput();
    applyRemotes.apply(List.of(new RemoteSighting(id, ON_SLOT, NEAR_THE_CONTROLLER)));

    applyRemotes.apply(List.of());

    assertEquals(Signal.OFF, network(id).channelValue(CHANNEL));
  }

  @Test
  void aTabletOfANetworkThatIsGoneIsIgnored() {
    NetworkId id = networkWithAnInput();

    applyRemotes.apply(List.of(new RemoteSighting(MISSING_NETWORK, ON_SLOT, NEAR_THE_CONTROLLER)));

    assertEquals(Signal.OFF, network(id).channelValue(CHANNEL));
  }

  @Test
  void theControllerScreenCountsTheTabletsThatCount() {
    NetworkId id = networkWithAnInput();
    ShowNetwork show = new ShowNetwork(fixture.networks);
    applyRemotes.apply(List.of(new RemoteSighting(id, ON_SLOT, NEAR_THE_CONTROLLER)));

    assertEquals(ONE, show.of(OWNER, id, node -> true).orElseThrow().remotesOn());
  }

  @Test
  void onlyTheOwnerLinksATabletToAnEnabledInputOfAnOnlineNetwork() {
    NetworkId id = networkWithAnInput();

    assertEquals(Outcome.ACCEPTED, linkRemote.link(OWNER, id, NODE, SLOT));
    assertEquals(Outcome.NOT_OWNER, linkRemote.link(STRANGER, id, NODE, SLOT));
    assertEquals(Outcome.INVALID_SLOT, linkRemote.link(OWNER, id, NODE, FREE_SLOT));
    assertEquals(Outcome.UNKNOWN_NETWORK, linkRemote.link(OWNER, MISSING_NETWORK, NODE, SLOT));

    network(id).turnOff(OWNER);
    assertEquals(Outcome.NETWORK_NOT_ONLINE, linkRemote.link(OWNER, id, NODE, SLOT));
  }

  @Test
  void aValidLinkShowsTheNetworkTheNodeAndTheChannel() {
    NetworkId id = networkWithAnInput();

    RemoteView view = showRemote.show(id, ON_SLOT).orElseThrow();

    assertEquals(network(id).name(), view.network());
    assertEquals(network(id).color(), view.color());
    assertEquals(network(id).node(NODE).orElseThrow().displayName(), view.node());
    assertEquals(CHANNEL, view.channel());
    assertFalse(view.offline());
    assertFalse(view.othersHold());
  }

  @Test
  void theViewTellsWhenAnotherSourceHoldsTheChannel() {
    NetworkId id = networkWithAnInput();
    network(id).readInput(NODE, SLOT, MIDDLE);

    assertTrue(showRemote.show(id, ON_SLOT).orElseThrow().othersHold());
  }

  @Test
  void anOfflineNetworkKeepsTheLinkButSaysItIsOffline() {
    NetworkId id = networkWithAnInput();
    network(id).turnOff(OWNER);

    assertTrue(showRemote.show(id, ON_SLOT).orElseThrow().offline());
  }

  @Test
  void aSlotThatIsNoLongerAnInputOrANetworkThatIsGoneIsALostLink() {
    NetworkId id = networkWithAnInput();

    assertTrue(showRemote.show(id, new RemoteInput(TABLET, NODE, FREE_SLOT)).isEmpty());
    assertTrue(showRemote.show(MISSING_NETWORK, ON_SLOT).isEmpty());
  }
}
