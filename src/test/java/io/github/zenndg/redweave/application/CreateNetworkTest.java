package io.github.zenndg.redweave.application;

import static io.github.zenndg.redweave.domain.network.NetworkFixture.CONTROLLER;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.STRANGER;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.zenndg.redweave.domain.network.Network;
import io.github.zenndg.redweave.domain.network.NetworkId;
import io.github.zenndg.redweave.domain.network.TestLimits;
import io.github.zenndg.redweave.domain.signal.ChannelColor;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CreateNetworkTest {
  private static final UUID FIRST_ID = UUID.fromString("6f1c2b0e-3a4d-4e5f-8a9b-0c1d2e3f4a5b");

  /** The first 8 characters of FIRST_ID (answer 142). */
  private static final String FIRST_NAME = "6f1c2b0e";

  private static final int CHANNELS = 16;

  /** The ceiling of network.max_per_player, so 17 networks fit (answer 145). */
  private static final int MOST_NETWORKS = 100;

  private static final int ONE_NETWORK = 1;

  private final InMemoryNetworks networks = new InMemoryNetworks();

  private CreateNetwork createNetworkWith(List<UUID> ids) {
    return createNetworkWith(ids, TestLimits.STARTING);
  }

  private CreateNetwork createNetworkWith(List<UUID> ids, TestLimits limits) {
    return new CreateNetwork(new FixedIds(ids), networks, networks, limits);
  }

  private static List<UUID> randomIds(int count) {
    List<UUID> ids = new ArrayList<>();
    for (int id = 0; id < count; id++) {
      ids.add(UUID.randomUUID());
    }
    return ids;
  }

  /** The first 8 characters of the ID, like a node (answer 142). */
  @Test
  void aNewNetworkIsNamedAfterItsId() {
    NetworkId id = createNetworkWith(List.of(FIRST_ID)).create(OWNER, CONTROLLER).orElseThrow();

    Network network = networks.find(id).orElseThrow();
    assertEquals(FIRST_NAME, network.name());
    assertEquals(OWNER, network.owner());
    assertEquals(Optional.of(CONTROLLER), network.controller());
  }

  /** The networks of one player start with different colors (answer 40). */
  @Test
  void eachNewNetworkTakesTheFirstColorTheOwnerDoesNotUse() {
    CreateNetwork createNetwork = createNetworkWith(randomIds(2));
    NetworkId first = createNetwork.create(OWNER, CONTROLLER).orElseThrow();
    NetworkId second = createNetwork.create(OWNER, CONTROLLER).orElseThrow();

    assertEquals(ChannelColor.WHITE, networks.find(first).orElseThrow().color());
    assertEquals(ChannelColor.ORANGE, networks.find(second).orElseThrow().color());
  }

  @Test
  void theColorsOfAnotherPlayerDoNotCount() {
    CreateNetwork createNetwork = createNetworkWith(randomIds(2));
    createNetwork.create(STRANGER, CONTROLLER).orElseThrow();
    NetworkId mine = createNetwork.create(OWNER, CONTROLLER).orElseThrow();

    assertEquals(ChannelColor.WHITE, networks.find(mine).orElseThrow().color());
  }

  @Test
  void withEverySixteenColorsInUseANewNetworkIsWhite() {
    CreateNetwork createNetwork =
        createNetworkWith(
            randomIds(CHANNELS + 1), TestLimits.STARTING.withNetworksPerPlayer(MOST_NETWORKS));
    for (int network = 0; network < CHANNELS; network++) {
      createNetwork.create(OWNER, CONTROLLER).orElseThrow();
    }
    NetworkId seventeenth = createNetwork.create(OWNER, CONTROLLER).orElseThrow();

    assertEquals(ChannelColor.WHITE, networks.find(seventeenth).orElseThrow().color());
  }

  /** Every network of the player counts, online, offline or unplaced (answer 145). */
  @Test
  void atTheLimitNoNetworkIsCreated() {
    CreateNetwork createNetwork =
        createNetworkWith(randomIds(2), TestLimits.STARTING.withNetworksPerPlayer(ONE_NETWORK));
    NetworkId first = createNetwork.create(OWNER, CONTROLLER).orElseThrow();
    networks.find(first).orElseThrow().removeController(OWNER);

    assertEquals(Optional.empty(), createNetwork.create(OWNER, CONTROLLER));
    assertEquals(ONE_NETWORK, networks.ownedBy(OWNER).size());
  }

  @Test
  void theNetworksOfAnotherPlayerDoNotCountForTheLimit() {
    CreateNetwork createNetwork =
        createNetworkWith(randomIds(2), TestLimits.STARTING.withNetworksPerPlayer(ONE_NETWORK));
    createNetwork.create(STRANGER, CONTROLLER);

    assertEquals(true, createNetwork.create(OWNER, CONTROLLER).isPresent());
  }
}
