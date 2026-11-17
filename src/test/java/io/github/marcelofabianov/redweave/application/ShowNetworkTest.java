package io.github.marcelofabianov.redweave.application;

import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE_POSITION;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OTHER_NODE;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OTHER_NODE_POSITION;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.STRANGER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.DefaultNames;
import io.github.marcelofabianov.redweave.domain.network.Direction;
import io.github.marcelofabianov.redweave.domain.network.ModuleChannel;
import io.github.marcelofabianov.redweave.domain.network.ModuleEntry;
import io.github.marcelofabianov.redweave.domain.network.ModuleId;
import io.github.marcelofabianov.redweave.domain.network.ModuleInput;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.NodeKind;
import io.github.marcelofabianov.redweave.domain.network.NodeState;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/**
 * What the Controller Screen shows of a network: the header, the node list in the order the nodes
 * joined, with only the enabled slots, the 16 channels and the counts of the footer (telas.md,
 * answers 149, 179, 216 and 234).
 */
class ShowNetworkTest {
  private static final LoadedNodes ALL_LOADED = node -> true;
  private static final LoadedNodes ONLY_NODE_LOADED = node -> node.equals(NODE_POSITION);
  private static final int CHANNELS = ChannelColor.values().length;
  private static final int NONE = 0;
  private static final int ONE = 1;
  private static final ChannelColor DOOR = ChannelColor.RED;
  private static final int FIRST_SLOT = DOOR.ordinal();
  private static final int SECOND_SLOT = DOOR.ordinal();
  private static final String DOOR_NAME = "Main door";
  private static final String LEVER = "Lever";
  private static final int DISABLED_SLOT = ChannelColor.ORANGE.ordinal();
  private static final Signal READ = new Signal(12);

  private static final String TIMER_UUID = "8a9b0c1d-2e3f-4a4b-9c5d-6e7f8a9b0c1d";
  private static final ModuleId TIMER = new ModuleId(UUID.fromString(TIMER_UUID));
  private static final String TIMER_KIND = "signal_timer";
  private static final String TIMER_NAME = "Door clock";
  private static final ModuleEntry TIMER_ENTRY =
      new ModuleEntry(
          TIMER,
          TIMER_KIND,
          Optional.of(TIMER_NAME),
          NODE_POSITION,
          List.of(new ModuleChannel(DOOR, Direction.OUTPUT)));
  private static final ModuleEntry UNNAMED =
      new ModuleEntry(TIMER, TIMER_KIND, Optional.empty(), NODE_POSITION, List.of());

  private final ApplicationFixture fixture = new ApplicationFixture();
  private final ShowNetwork show = new ShowNetwork(fixture.networks);

  private Network network(NetworkId id) {
    return fixture.networks.find(id).orElseThrow();
  }

  /** NODE reads READ on DOOR; OTHER_NODE emits DOOR, and is out of the loaded chunks. */
  private NetworkId networkWithTwoNodes() {
    NetworkId id = fixture.ownersNetworkWithANode();
    Network network = network(id);
    network.attachNode(OWNER, OTHER_NODE, OTHER_NODE_POSITION, Optional.empty());
    network.configureSlot(
        OWNER, NODE, FIRST_SLOT, new ChannelSlot(Direction.INPUT, NODE_POSITION.face(), DOOR));
    network.configureSlot(
        OWNER,
        OTHER_NODE,
        SECOND_SLOT,
        new ChannelSlot(Direction.OUTPUT, OTHER_NODE_POSITION.face(), DOOR));
    network.readInput(NODE, FIRST_SLOT, READ);
    network.enableSlot(OWNER, NODE, DISABLED_SLOT);
    network.disableSlot(OWNER, NODE, DISABLED_SLOT);
    network.renameNode(OWNER, NODE, LEVER);
    network.renameChannel(OWNER, DOOR, DOOR_NAME);
    return id;
  }

  @Test
  void anUnknownNetworkShowsNothing() {
    assertEquals(Optional.empty(), show.of(OWNER, ApplicationFixture.MISSING_NETWORK, ALL_LOADED));
  }

  @Test
  void theHeaderShowsTheNameTheColorAndTheOwner() {
    NetworkId id = fixture.ownersNetwork();
    Network network = network(id);

    NetworkView view = show.of(OWNER, id, ALL_LOADED).orElseThrow();

    assertEquals(network.name(), view.name());
    assertEquals(network.color(), view.color());
    assertEquals(OWNER, view.owner());
    assertEquals(network.state(), view.state());
    assertTrue(view.viewerIsOwner());
  }

  @Test
  void theNodesComeInTheOrderTheyJoinedWithTheirNameSignalAndState() {
    NetworkId id = networkWithTwoNodes();

    List<NodeRow> rows = show.of(OWNER, id, ONLY_NODE_LOADED).orElseThrow().nodes();

    NodeRow lever =
        new NodeRow(
            NODE,
            LEVER,
            READ,
            NodeState.ONLINE,
            NodeKind.INPUT,
            Optional.of(NODE_POSITION),
            List.of(new SlotRow(FIRST_SLOT, DOOR, Direction.INPUT)));
    NodeRow other =
        new NodeRow(
            OTHER_NODE,
            network(id).node(OTHER_NODE).orElseThrow().displayName(),
            READ,
            NodeState.UNLOADED,
            NodeKind.OUTPUT,
            Optional.of(OTHER_NODE_POSITION),
            List.of(new SlotRow(SECOND_SLOT, DOOR, Direction.OUTPUT)));
    assertEquals(List.of(lever, other), rows);
  }

  /** The position stays on the server for anyone but the owner (answer 149). */
  @Test
  void anotherPlayerSeesNoPosition() {
    NetworkId id = networkWithTwoNodes();

    NetworkView view = show.of(STRANGER, id, ALL_LOADED).orElseThrow();

    assertFalse(view.viewerIsOwner());
    assertTrue(view.nodes().stream().allMatch(row -> row.position().isEmpty()));
  }

  @Test
  void theSixteenChannelsShowTheirNameValueAndSlotCounts() {
    NetworkId id = networkWithTwoNodes();

    List<ChannelRow> channels = show.of(OWNER, id, ALL_LOADED).orElseThrow().channels();

    assertEquals(CHANNELS, channels.size());
    assertEquals(
        new ChannelRow(DOOR, Optional.of(DOOR_NAME), READ, ONE, ONE), channels.get(DOOR.ordinal()));
    assertEquals(
        new ChannelRow(ChannelColor.ORANGE, Optional.empty(), Signal.OFF, NONE, NONE),
        channels.get(ChannelColor.ORANGE.ordinal()));
  }

  @Test
  void theFooterCountsTheChannelsInUseAndTheNodesByState() {
    NetworkId id = networkWithTwoNodes();

    NetworkView view = show.of(OWNER, id, ONLY_NODE_LOADED).orElseThrow();

    assertEquals(ONE, view.channelsInUse());
    assertEquals(ONE, view.nodesIn(NodeState.UNLOADED));
    assertEquals(NONE, view.nodesIn(NodeState.OUT_OF_RANGE));
  }

  /** The modules come after the nodes, with their kind, state and signal (answers 809 and 814). */
  @Test
  void theModulesAreListedWithTheirKindStateAndSignal() {
    NetworkId id = fixture.ownersNetwork();
    Network network = network(id);
    network.register().register(OWNER, TIMER_ENTRY);
    network.modules().apply(Set.of(new ModuleInput(TIMER, NODE_POSITION.block(), DOOR, READ)));

    ModuleRow row = show.of(OWNER, id, ALL_LOADED).orElseThrow().modules().get(0);

    assertEquals(TIMER, row.id());
    assertEquals(TIMER_NAME, row.name());
    assertEquals(TIMER_KIND, row.kind());
    assertEquals(Optional.of(READ), row.signal());
    assertEquals(NodeState.ONLINE, row.state());
    assertEquals(Optional.of(NODE_POSITION), row.position());
    assertEquals(TIMER_ENTRY.channels(), row.channels());
  }

  /** Without its own name, a module shows the default one, and others never see where it is. */
  @Test
  void aStrangerSeesNoPlaceOfAModule() {
    NetworkId id = fixture.ownersNetwork();
    network(id).register().register(OWNER, UNNAMED);

    ModuleRow row = show.of(STRANGER, id, ALL_LOADED).orElseThrow().modules().get(0);

    assertEquals(Optional.empty(), row.position());
    assertEquals(DefaultNames.of(TIMER.value()), row.name());
    assertEquals(Optional.empty(), row.signal());
  }
}
