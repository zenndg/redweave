package io.github.marcelofabianov.redweave.application;

import static io.github.marcelofabianov.redweave.application.ApplicationFixture.MISSING_NETWORK;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.STRANGER;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** The Controller Screen renames the network, changes its color and names the channels. */
class ChangeNetworkTest {
  private static final String NEW_NAME = "Door";
  private static final ChannelColor NEW_COLOR = ChannelColor.LIME;
  private static final ChannelColor DOOR_CHANNEL = ChannelColor.RED;

  private final ApplicationFixture fixture = new ApplicationFixture();
  private final RenameNetwork renameNetwork = new RenameNetwork(fixture.networks, fixture.networks);
  private final RecolorNetwork recolorNetwork =
      new RecolorNetwork(fixture.networks, fixture.networks);
  private final RenameChannel renameChannel = new RenameChannel(fixture.networks, fixture.networks);

  private Network network(NetworkId id) {
    return fixture.networks.find(id).orElseThrow();
  }

  @Test
  void theOwnerRenamesTheNetworkAndItIsSaved() {
    NetworkId id = fixture.ownersNetwork();
    int saves = fixture.networks.saves();

    assertEquals(Outcome.ACCEPTED, renameNetwork.rename(OWNER, id, NEW_NAME));
    assertEquals(NEW_NAME, network(id).name());
    assertEquals(saves + 1, fixture.networks.saves());
  }

  @Test
  void theOwnerChangesTheColorAndItIsSaved() {
    NetworkId id = fixture.ownersNetwork();
    int saves = fixture.networks.saves();

    assertEquals(Outcome.ACCEPTED, recolorNetwork.recolor(OWNER, id, NEW_COLOR));
    assertEquals(NEW_COLOR, network(id).color());
    assertEquals(saves + 1, fixture.networks.saves());
  }

  @Test
  void theOwnerNamesAChannelAndItIsSaved() {
    NetworkId id = fixture.ownersNetwork();
    int saves = fixture.networks.saves();

    assertEquals(Outcome.ACCEPTED, renameChannel.rename(OWNER, id, DOOR_CHANNEL, NEW_NAME));
    assertEquals(Optional.of(NEW_NAME), network(id).channel(DOOR_CHANNEL).name());
    assertEquals(saves + 1, fixture.networks.saves());
  }

  @Test
  void aRefusedChangeIsNotSaved() {
    NetworkId id = fixture.ownersNetwork();
    int saves = fixture.networks.saves();

    assertEquals(Outcome.NOT_OWNER, renameNetwork.rename(STRANGER, id, NEW_NAME));
    assertEquals(Outcome.NOT_OWNER, recolorNetwork.recolor(STRANGER, id, NEW_COLOR));
    assertEquals(Outcome.NOT_OWNER, renameChannel.rename(STRANGER, id, DOOR_CHANNEL, NEW_NAME));
    assertEquals(saves, fixture.networks.saves());
  }

  @Test
  void aNetworkThatDoesNotExistSaysSo() {
    assertEquals(Outcome.UNKNOWN_NETWORK, renameNetwork.rename(OWNER, MISSING_NETWORK, NEW_NAME));
    assertEquals(
        Outcome.UNKNOWN_NETWORK, recolorNetwork.recolor(OWNER, MISSING_NETWORK, NEW_COLOR));
    assertEquals(
        Outcome.UNKNOWN_NETWORK,
        renameChannel.rename(OWNER, MISSING_NETWORK, DOOR_CHANNEL, NEW_NAME));
  }
}
