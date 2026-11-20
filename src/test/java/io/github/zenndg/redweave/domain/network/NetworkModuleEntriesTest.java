package io.github.zenndg.redweave.domain.network;

import static io.github.zenndg.redweave.domain.network.NetworkFixture.NODE;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.NODE_POSITION;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.OTHER_NODE_POSITION;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.STRANGER;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.network;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zenndg.redweave.domain.signal.ChannelColor;
import io.github.zenndg.redweave.domain.signal.Signal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/**
 * The Signal Modules a network knows, so its components count with the chunks unloaded: the owner
 * registers each one with what the list shows, it leaves when it goes, and the Network Terminal
 * clears them with the nodes (answers 802, 803, 806, 807 and 810).
 */
class NetworkModuleEntriesTest {
  private static final String TIMER_UUID = "8a9b0c1d-2e3f-4a4b-9c5d-6e7f8a9b0c1d";
  private static final ModuleId TIMER = new ModuleId(UUID.fromString(TIMER_UUID));
  private static final String LEVER_UUID = "9b0c1d2e-3f4a-4b5c-8d6e-7f8a9b0c1d2e";
  private static final ModuleId LEVER = new ModuleId(UUID.fromString(LEVER_UUID));
  private static final String TIMER_KIND = "signal_timer";
  private static final String LEVER_KIND = "signal_lever";
  private static final String DOOR_CLOCK = "Door clock";
  private static final String BLANK = "  ";
  private static final int ONE = 1;
  private static final int TWO = 2;
  private static final int THREE = 3;
  private static final boolean LOADED = true;
  private static final boolean UNLOADED = false;
  private static final int FAR = 500;
  private static final int SEA = 64;
  private static final BlockLocation FAR_CONTROLLER =
      new BlockLocation(NetworkFixture.OVERWORLD, FAR, SEA, 0);
  private static final BlockLocation PLATE = NODE_POSITION.block();
  private static final Signal SEVEN = new Signal(7);
  private static final ModuleChannel RED_OUT =
      new ModuleChannel(ChannelColor.RED, Direction.OUTPUT);

  private static ModuleEntry timer() {
    return new ModuleEntry(TIMER, TIMER_KIND, Optional.empty(), NODE_POSITION, List.of(RED_OUT));
  }

  private static ModuleEntry lever() {
    return new ModuleEntry(LEVER, LEVER_KIND, Optional.empty(), OTHER_NODE_POSITION, List.of());
  }

  @Test
  void theOwnerRegistersAModule() {
    Network network = network();

    assertEquals(Outcome.ACCEPTED, network.register().register(OWNER, timer()));
    assertEquals(Optional.of(timer()), network.register().entry(TIMER));
    assertEquals(List.of(timer()), network.register().entries());
  }

  @Test
  void aStrangerRegistersNothing() {
    Network network = network();

    assertEquals(Outcome.NOT_OWNER, network.register().register(STRANGER, timer()));
    assertTrue(network.register().entries().isEmpty());
  }

  /** Registered again, the module keeps its place and takes the new name and channels (810). */
  @Test
  void aModuleRegisteredAgainIsUpdated() {
    Network network = network();
    network.register().register(OWNER, timer());
    network.register().register(OWNER, lever());
    ModuleEntry renamed =
        new ModuleEntry(TIMER, TIMER_KIND, Optional.of(DOOR_CLOCK), NODE_POSITION, List.of());

    network.register().register(OWNER, renamed);

    assertEquals(List.of(renamed, lever()), network.register().entries());
  }

  @Test
  void aForgottenModuleLeaves() {
    Network network = network();
    network.register().register(OWNER, timer());

    assertTrue(network.register().forget(TIMER));
    assertFalse(network.register().forget(TIMER));
    assertTrue(network.register().entry(TIMER).isEmpty());
  }

  /** The components are the nodes and the registered modules (answer 802). */
  @Test
  void theComponentsCountTheNodesAndTheModules() {
    Network network = network();
    network.attachNode(OWNER, NODE, NODE_POSITION, Optional.empty());
    network.register().register(OWNER, timer());
    network.register().register(OWNER, lever());

    assertEquals(THREE, network.components());
  }

  /** `Clear components` takes the nodes and the modules out (answer 807). */
  @Test
  void clearingTakesTheModulesOutWithTheNodes() {
    Network network = network();
    network.attachNode(OWNER, NODE, NODE_POSITION, Optional.empty());
    network.register().register(OWNER, timer());

    assertEquals(Outcome.ACCEPTED, network.terminal().clearComponents(OWNER));
    assertEquals(0, network.components());
  }

  @Test
  void aStrangerClearsNothing() {
    Network network = network();
    network.register().register(OWNER, timer());

    assertEquals(Outcome.NOT_OWNER, network.terminal().clearComponents(STRANGER));
    assertEquals(ONE, network.components());
  }

  @Test
  void theModulesGoToTheSnapshotAndComeBack() {
    Network network = network();
    network.register().register(OWNER, timer());
    network.register().register(OWNER, lever());

    Network restored = Network.restore(network.snapshot(), TestLimits.STARTING);

    assertEquals(List.of(timer(), lever()), restored.register().entries());
    assertEquals(TWO, restored.components());
  }

  @Test
  void anEntryNeedsAKindAndAKeptName() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new ModuleEntry(TIMER, BLANK, Optional.empty(), NODE_POSITION, List.of()));
    assertThrows(
        IllegalArgumentException.class,
        () -> new ModuleEntry(TIMER, TIMER_KIND, Optional.of(BLANK), NODE_POSITION, List.of()));
  }

  /** A module shows its state as a node: loaded, unloaded, out of range or offline (809). */
  @Test
  void aRegisteredModuleHasTheStateOfANode() {
    Network network = network();
    network.register().register(OWNER, timer());

    assertEquals(NodeState.ONLINE, network.register().state(TIMER, LOADED));
    assertEquals(NodeState.UNLOADED, network.register().state(TIMER, UNLOADED));
    assertEquals(NodeState.NO_NETWORK, network.register().state(LEVER, LOADED));
    network.terminal().turnOff(OWNER);
    assertEquals(NodeState.OFFLINE, network.register().state(TIMER, LOADED));
  }

  @Test
  void aModuleOutOfTheRadiusIsOutOfRange() {
    Network network = network();
    network.register().register(OWNER, timer());
    network.removeController(OWNER);
    network.placeController(OWNER, FAR_CONTROLLER);

    assertEquals(NodeState.OUT_OF_RANGE, network.register().state(TIMER, LOADED));
  }

  /** The signal of a module is what it writes on its channel this tick, or none (814). */
  @Test
  void theSignalOfAModuleIsWhatItWritesThisTick() {
    Network network = network();
    network.register().register(OWNER, timer());

    assertEquals(Optional.empty(), network.register().signal(TIMER));
    network.modules().apply(Set.of(new ModuleInput(TIMER, PLATE, ChannelColor.RED, SEVEN)));
    assertEquals(Optional.of(SEVEN), network.register().signal(TIMER));
  }
}
