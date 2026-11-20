package io.github.zenndg.redweave.domain.network;

import static io.github.zenndg.redweave.domain.network.NetworkFixture.COLOR;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.CONTROLLER;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.ID;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.NAME;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.STRANGER;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.network;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zenndg.redweave.domain.signal.ChannelColor;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class NetworkTest {
  private static final String NEW_NAME = "Door";
  private static final String PADDED_NEW_NAME = "  Door  ";
  private static final String BLANK = "   ";
  private static final ChannelColor NEW_COLOR = ChannelColor.LIME;
  private static final ChannelColor DOOR_CHANNEL = ChannelColor.RED;
  private static final String LONG_CHANNEL_NAME = "Seventeen letters";
  private static final String TOO_LONG = "A name with far more than thirty-two characters";

  @Test
  void aNewNetworkKeepsItsIdOwnerNameAndColor() {
    Network network = network();

    assertEquals(ID, network.id());
    assertEquals(OWNER, network.owner());
    assertEquals(NAME, network.name());
    assertEquals(COLOR, network.color());
  }

  @Test
  void aNewNetworkIsOnlineBecauseItsControllerIsPlaced() {
    assertTrue(network().isOnline());
  }

  @Test
  void aNewNetworkHasTheSixteenChannelsInTheOrderOfTheColors() {
    assertEquals(
        Arrays.asList(ChannelColor.values()),
        network().channels().stream().map(Channel::color).toList());
  }

  @Test
  void theChannelsOfANewNetworkHaveNoNameOfTheirOwn() {
    assertTrue(network().channels().stream().allMatch(c -> c.name().equals(Optional.empty())));
  }

  @Test
  void theOwnerRenamesTheNetworkWithoutTheSpacesAtTheEnds() {
    Network network = network();

    assertEquals(Outcome.ACCEPTED, network.rename(OWNER, PADDED_NEW_NAME));
    assertEquals(NEW_NAME, network.name());
  }

  @Test
  void anotherPlayerCannotRenameTheNetwork() {
    Network network = network();

    assertEquals(Outcome.NOT_OWNER, network.rename(STRANGER, NEW_NAME));
    assertEquals(NAME, network.name());
  }

  @Test
  void aBlankNameIsRefusedAndTheNameStays() {
    Network network = network();

    assertEquals(Outcome.INVALID_NAME, network.rename(OWNER, BLANK));
    assertEquals(NAME, network.name());
  }

  @Test
  void aNameLongerThanTheLimitIsRefusedAndTheNameStays() {
    Network network = network();

    assertEquals(Outcome.INVALID_NAME, network.rename(OWNER, TOO_LONG));
    assertEquals(NAME, network.name());
  }

  @Test
  void theOwnerChangesTheColorOfTheNetwork() {
    Network network = network();

    assertEquals(Outcome.ACCEPTED, network.recolor(OWNER, NEW_COLOR));
    assertEquals(NEW_COLOR, network.color());
  }

  @Test
  void anotherPlayerCannotChangeTheColorOfTheNetwork() {
    Network network = network();

    assertEquals(Outcome.NOT_OWNER, network.recolor(STRANGER, NEW_COLOR));
    assertEquals(COLOR, network.color());
  }

  @Test
  void theOwnerNamesAChannel() {
    Network network = network();

    assertEquals(Outcome.ACCEPTED, network.renameChannel(OWNER, DOOR_CHANNEL, PADDED_NEW_NAME));
    assertEquals(Optional.of(NEW_NAME), network.channel(DOOR_CHANNEL).name());
  }

  @Test
  void aBlankChannelNameGoesBackToTheNameOfTheColor() {
    Network network = network();
    network.renameChannel(OWNER, DOOR_CHANNEL, NEW_NAME);

    assertEquals(Outcome.ACCEPTED, network.renameChannel(OWNER, DOOR_CHANNEL, BLANK));
    assertEquals(Optional.empty(), network.channel(DOOR_CHANNEL).name());
  }

  @Test
  void aChannelNameLongerThanTheLimitIsRefused() {
    Network network = network();

    assertEquals(
        Outcome.INVALID_NAME, network.renameChannel(OWNER, DOOR_CHANNEL, LONG_CHANNEL_NAME));
    assertEquals(Optional.empty(), network.channel(DOOR_CHANNEL).name());
  }

  @Test
  void anotherPlayerCannotNameAChannel() {
    Network network = network();

    assertEquals(Outcome.NOT_OWNER, network.renameChannel(STRANGER, DOOR_CHANNEL, NEW_NAME));
    assertEquals(Optional.empty(), network.channel(DOOR_CHANNEL).name());
  }

  /** The caller builds the default name (answer 35), so a bad one is a programming error. */
  @Test
  void aNetworkIsNotCreatedWithAnInvalidName() {
    assertThrows(
        IllegalArgumentException.class,
        () -> Network.create(ID, OWNER, BLANK, COLOR, CONTROLLER, TestLimits.STARTING));
  }

  /** The aggregate only changes through its operations (answer 33). */
  @Test
  void theChannelsHandedOutCannotChangeTheNetwork() {
    Network network = network();

    assertThrows(UnsupportedOperationException.class, () -> network.channels().clear());
    assertThrows(UnsupportedOperationException.class, () -> network.nodes().clear());
  }

  /** The screens open read only for anyone but the owner (answer 22). */
  @Test
  void onlyTheOwnerOwnsTheNetwork() {
    assertTrue(network().isOwner(OWNER));
    assertFalse(network().isOwner(STRANGER));
  }
}
