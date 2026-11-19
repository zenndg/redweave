package io.github.zenndg.redweave.application;

import static io.github.zenndg.redweave.application.ApplicationFixture.NOW;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.STRANGER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zenndg.redweave.domain.network.Direction;
import io.github.zenndg.redweave.domain.network.Network;
import io.github.zenndg.redweave.domain.network.NetworkId;
import io.github.zenndg.redweave.domain.network.NetworkState;
import io.github.zenndg.redweave.domain.network.Outcome;
import io.github.zenndg.redweave.domain.network.SignalChange;
import io.github.zenndg.redweave.domain.signal.ChannelColor;
import io.github.zenndg.redweave.domain.signal.Signal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** What the owner does to the networks at a Network Terminal (terminal-de-rede.md, 131 to 166). */
class ManageNetworksTest {
  private static final String SECOND = "beta";
  private static final String FIRST = "Alpha";
  private static final String NODE_NAME = "Lever";
  private static final int ONE_NODE = 1;
  private static final int ONE_SAVE = 1;
  private static final long FROM_THE_START = 0;

  private final ApplicationFixture fixture = new ApplicationFixture();
  private final TerminalNetworks list = new TerminalNetworks(fixture.networks);
  private final TurnNetwork turn = new TurnNetwork(fixture.networks, fixture.networks);
  private final ClearComponents clear = new ClearComponents(fixture.networks, fixture.networks);
  private final DeleteNetwork delete =
      new DeleteNetwork(fixture.networks, fixture.networks, fixture.logs);

  private Network networkOf(NetworkId id) {
    return fixture.networks.find(id).orElseThrow();
  }

  /** Alphabetical, whatever the case, and only the networks of the owner (answer 138). */
  @Test
  void theListHasTheOwnersNetworksInAlphabeticalOrder() {
    networkOf(fixture.ownersNetwork()).rename(OWNER, SECOND);
    networkOf(fixture.ownersOtherNetwork()).rename(OWNER, FIRST);
    fixture.strangersNetwork();

    List<String> names = list.of(OWNER).stream().map(TerminalRow::name).toList();

    assertEquals(List.of(FIRST, SECOND), names);
  }

  /** Each row shows the state and how many nodes the network has (answer 131). */
  @Test
  void aRowHasTheStateAndTheNodes() {
    fixture.ownersNetworkWithANode();

    TerminalRow row = list.of(OWNER).get(0);

    assertEquals(NetworkState.ONLINE, row.state());
    assertEquals(ONE_NODE, row.components());
  }

  @Test
  void theOwnerTurnsANetworkOffAndOn() {
    NetworkId id = fixture.ownersNetwork();

    assertEquals(Outcome.ACCEPTED, turn.off(OWNER, id));
    assertEquals(NetworkState.OFFLINE, networkOf(id).state());
    assertEquals(Outcome.ACCEPTED, turn.on(OWNER, id));
    assertEquals(NetworkState.ONLINE, networkOf(id).state());
  }

  @Test
  void anotherPlayerCannotTurnANetworkOff() {
    NetworkId id = fixture.ownersNetwork();

    assertEquals(Outcome.NOT_OWNER, turn.off(STRANGER, id));
    assertEquals(NetworkState.ONLINE, networkOf(id).state());
  }

  @Test
  void aMissingNetworkIsUnknown() {
    assertEquals(Outcome.UNKNOWN_NETWORK, turn.off(OWNER, ApplicationFixture.MISSING_NETWORK));
    assertEquals(Outcome.UNKNOWN_NETWORK, clear.clear(OWNER, ApplicationFixture.MISSING_NETWORK));
    assertEquals(Outcome.UNKNOWN_NETWORK, delete.delete(OWNER, ApplicationFixture.MISSING_NETWORK));
  }

  /** The nodes stay in the world with no network (answer 133). */
  @Test
  void clearingTakesTheNodesOutAndSaves() {
    NetworkId id = fixture.ownersNetworkWithANode();
    int savesBefore = fixture.networks.saves();

    assertEquals(Outcome.ACCEPTED, clear.clear(OWNER, id));

    assertTrue(networkOf(id).nodes().isEmpty());
    assertEquals(savesBefore + ONE_SAVE, fixture.networks.saves());
  }

  /** An unplaced network goes for good, with its log (answers 147 and 166). */
  @Test
  void deletingAnUnplacedNetworkRemovesItAndItsLog() {
    NetworkId id = fixture.ownersNetwork();
    networkOf(id).removeController(OWNER);
    fixture.logs.record(id, aLine());

    assertEquals(Outcome.ACCEPTED, delete.delete(OWNER, id));

    assertEquals(Optional.empty(), fixture.networks.find(id));
    assertEquals(List.of(), fixture.logs.after(id, FROM_THE_START));
  }

  @Test
  void aNetworkInTheWorldIsNotDeleted() {
    NetworkId id = fixture.ownersNetwork();

    assertEquals(Outcome.NETWORK_NOT_UNPLACED, delete.delete(OWNER, id));
    assertTrue(fixture.networks.find(id).isPresent());
  }

  private static SignalChange aLine() {
    return new SignalChange(
        NOW, NODE_NAME, ChannelColor.RED, Optional.empty(), Direction.INPUT, Signal.MAX);
  }
}
