package io.github.zenndg.redweave.application;

import static io.github.zenndg.redweave.domain.network.NetworkFixture.CONTROLLER;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.STRANGER;

import io.github.zenndg.redweave.domain.network.NetworkFixture;
import io.github.zenndg.redweave.domain.network.NetworkId;
import io.github.zenndg.redweave.domain.network.NodeId;
import io.github.zenndg.redweave.domain.network.PlayerId;
import io.github.zenndg.redweave.domain.network.TestLimits;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/** The ports in memory and one network of OWNER, created as the game would create it. */
final class ApplicationFixture {
  private static final String NETWORK_UUID = "6f1c2b0e-3a4d-4e5f-8a9b-0c1d2e3f4a5b";
  private static final String SECOND_NETWORK_UUID = "7a2d3c1f-4b5e-4f60-9bac-1d2e3f4a5b6c";
  private static final String NODE_UUID = "1b2c3d4e-5f6a-4b7c-8d9e-0f1a2b3c4d5e";

  static final UUID NODE_ID = UUID.fromString(NODE_UUID);
  static final NetworkId MISSING_NETWORK = new NetworkId(UUID.fromString(SECOND_NETWORK_UUID));

  private static final TestLimits LIMITS = TestLimits.STARTING;
  private static final int LOG_SIZE = 100;

  /** The time of the server clock in every test, as the log shows it (answer 148). */
  static final LocalDateTime NOW = LocalDateTime.of(2026, 11, 11, 14, 32, 5);

  final InMemoryNetworks networks = new InMemoryNetworks();
  final InMemoryLastUsedNetworks lastUsed = new InMemoryLastUsedNetworks();
  final SignalLogs logs = new SignalLogs(() -> LOG_SIZE);
  final ServerClock clock = () -> NOW;

  /** Creates the network of OWNER, online, with its controller at CONTROLLER. */
  NetworkId ownersNetwork() {
    return networkOf(OWNER, UUID.fromString(NETWORK_UUID));
  }

  /** One more network of OWNER, online. */
  NetworkId ownersOtherNetwork() {
    return networkOf(OWNER, UUID.randomUUID());
  }

  /** The network of OWNER with NODE in it, as an input on the first free channel (171). */
  NetworkId ownersNetworkWithANode() {
    NetworkId network = ownersNetwork();
    new LinkNode(networks, networks, networks, lastUsed)
        .link(OWNER, new NodeId(NODE_ID), NetworkFixture.NODE_POSITION, network);
    return network;
  }

  NetworkId strangersNetwork() {
    return networkOf(STRANGER, UUID.randomUUID());
  }

  private NetworkId networkOf(PlayerId owner, UUID id) {
    return new CreateNetwork(new FixedIds(List.of(id)), networks, networks, LIMITS)
        .create(owner, CONTROLLER)
        .orElseThrow();
  }
}
