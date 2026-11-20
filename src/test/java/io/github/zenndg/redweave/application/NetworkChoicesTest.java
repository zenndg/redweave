package io.github.zenndg.redweave.application;

import static io.github.zenndg.redweave.domain.network.NetworkFixture.OWNER;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.zenndg.redweave.domain.network.Network;
import io.github.zenndg.redweave.domain.network.NetworkId;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * The networks a player can pick for a node: only their online ones, the last used on top and the
 * others in alphabetical order, each with its color, name and node count (answers 157, 202, 203).
 */
class NetworkChoicesTest {
  private static final String BASE = "Base";
  private static final String FARM = "farm";
  private static final String MINE = "Mine";
  private static final int NO_NODES = 0;
  private static final int ONE_NODE = 1;

  private final ApplicationFixture fixture = new ApplicationFixture();
  private final NetworkChoices choices = new NetworkChoices(fixture.networks, fixture.lastUsed);

  private Network network(NetworkId id) {
    return fixture.networks.find(id).orElseThrow();
  }

  private NetworkId named(NetworkId id, String name) {
    network(id).rename(OWNER, name);
    return id;
  }

  private NetworkChoice choice(NetworkId id, int nodes, boolean lastUsed) {
    Network network = network(id);
    return new NetworkChoice(id, network.name(), network.color(), nodes, lastUsed);
  }

  @Test
  void withNoNetworkThereIsNothingToPick() {
    assertEquals(List.of(), choices.of(OWNER));
  }

  @Test
  void onlyTheOnlineNetworksOfThePlayerAreListedWithTheirNodeCount() {
    NetworkId withANode = fixture.ownersNetworkWithANode();
    NetworkId unplaced = fixture.ownersOtherNetwork();
    network(unplaced).removeController(OWNER);
    fixture.strangersNetwork();

    assertEquals(List.of(choice(withANode, ONE_NODE, true)), choices.of(OWNER));
  }

  @Test
  void theLastUsedComesFirstAndTheOthersInAlphabeticalOrder() {
    NetworkId mine = named(fixture.ownersNetworkWithANode(), MINE);
    NetworkId farm = named(fixture.ownersOtherNetwork(), FARM);
    NetworkId base = named(fixture.ownersOtherNetwork(), BASE);

    List<NetworkChoice> expected =
        List.of(
            choice(mine, ONE_NODE, true),
            choice(base, NO_NODES, false),
            choice(farm, NO_NODES, false));
    assertEquals(expected, choices.of(OWNER));
  }

  /** A last used network that is no longer online is not offered, so nothing is highlighted. */
  @Test
  void aLastUsedNetworkThatIsNotOnlineIsLeftOut() {
    NetworkId mine = named(fixture.ownersNetworkWithANode(), MINE);
    NetworkId base = named(fixture.ownersOtherNetwork(), BASE);
    network(mine).removeController(OWNER);

    assertEquals(List.of(choice(base, NO_NODES, false)), choices.of(OWNER));
  }
}
