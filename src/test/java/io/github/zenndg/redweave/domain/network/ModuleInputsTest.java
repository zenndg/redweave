package io.github.zenndg.redweave.domain.network;

import static io.github.zenndg.redweave.domain.network.NetworkFixture.NODE;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.NODE_POSITION;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.OVERWORLD;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.network;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zenndg.redweave.domain.signal.ChannelColor;
import io.github.zenndg.redweave.domain.signal.Signal;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/**
 * A Signal Module writes straight into a channel, with no node: each tick, the network gets the
 * loaded modules that write into one, which count in the range of the controller measured from the
 * plate, with the strongest rule (answers 375, 385, 386, 387 and 424).
 */
class ModuleInputsTest {
  private static final ChannelColor DOOR = ChannelColor.RED;
  private static final ChannelColor LAMP = ChannelColor.BLUE;
  private static final int SLOT = DOOR.ordinal();
  private static final Signal MIDDLE = new Signal(9);
  private static final int GROUND = 64;
  private static final int NEAR = 10;
  private static final int FAR = 500;
  private static final BlockLocation NEAR_PLATE = new BlockLocation(OVERWORLD, NEAR, GROUND, NEAR);
  private static final BlockLocation FAR_PLATE = new BlockLocation(OVERWORLD, FAR, GROUND, FAR);
  private static final String TIMER_UUID = "5f6a7b8c-9d0e-4f1a-8b2c-3d4e5f6a7b8c";
  private static final String OTHER_TIMER_UUID = "6a7b8c9d-0e1f-4a2b-9c3d-4e5f6a7b8c9d";
  private static final ModuleId TIMER = new ModuleId(UUID.fromString(TIMER_UUID));
  private static final ModuleId OTHER_TIMER = new ModuleId(UUID.fromString(OTHER_TIMER_UUID));
  private static final ModuleInput DOOR_ON = new ModuleInput(TIMER, NEAR_PLATE, DOOR, Signal.MAX);
  private static final ModuleInput DOOR_OFF = new ModuleInput(TIMER, NEAR_PLATE, DOOR, Signal.OFF);
  private static final int NONE = 0;
  private static final int ONE = 1;
  private static final int TWO = 2;

  private static Network settled() {
    Network network = network();
    network.settle(Map.of());
    return network;
  }

  @Test
  void aModuleThatIsOnPutsItsSignalOnItsChannel() {
    Network network = settled();

    network.modules().apply(Set.of(DOOR_ON));

    assertEquals(Signal.MAX, network.channelValue(DOOR));
    assertEquals(Signal.OFF, network.channelValue(LAMP));
    assertTrue(network.isPending());
  }

  @Test
  void theModuleJoinsTheReadingsWithTheStrongestRule() {
    Network network = settled();
    network.attachNode(OWNER, NODE, NODE_POSITION, Optional.empty());
    network.configureSlot(
        OWNER, NODE, SLOT, new ChannelSlot(Direction.INPUT, NODE_POSITION.face(), DOOR));
    network.readInput(NODE, SLOT, MIDDLE);

    network.modules().apply(Set.of(DOOR_ON));
    assertEquals(Signal.MAX, network.channelValue(DOOR));

    network.modules().apply(Set.of(DOOR_OFF));
    assertEquals(MIDDLE, network.channelValue(DOOR));
  }

  @Test
  void aModuleThatLeavesTheSetGoesBackToOff() {
    Network network = settled();
    network.modules().apply(Set.of(DOOR_ON));

    network.modules().apply(Set.of());

    assertEquals(Signal.OFF, network.channelValue(DOOR));
  }

  @Test
  void aModuleThatChangesNoChannelLeavesTheNetworkWaitingForNothing() {
    Network network = settled();

    network.modules().apply(Set.of(DOOR_OFF));

    assertFalse(network.isPending());
  }

  @Test
  void theSameModulesAgainChangeNothing() {
    Network network = settled();
    network.modules().apply(Set.of(DOOR_ON));
    network.settle(Map.of());

    network.modules().apply(Set.of(DOOR_ON));

    assertFalse(network.isPending());
  }

  @Test
  void aModuleOutOfRangeDoesNotCount() {
    Network network = settled();

    network.modules().apply(Set.of(new ModuleInput(TIMER, FAR_PLATE, DOOR, Signal.MAX)));

    assertEquals(Signal.OFF, network.channelValue(DOOR));
    assertEquals(NONE, network.modules().on());
  }

  @Test
  void aNetworkThatIsNotOnlineIgnoresItsModules() {
    Network network = settled();
    network.modules().apply(Set.of(DOOR_ON));

    network.terminal().turnOff(OWNER);

    assertEquals(Signal.OFF, network.channelValue(DOOR));
    assertEquals(NONE, network.modules().on());
  }

  @Test
  void theModulesThatCountAreCountedOnOrOff() {
    Network network = settled();
    ModuleInput otherOff = new ModuleInput(OTHER_TIMER, NEAR_PLATE, LAMP, Signal.OFF);

    network.modules().apply(Set.of(DOOR_OFF));
    assertEquals(ONE, network.modules().on());

    network.modules().apply(Set.of(DOOR_ON, otherOff));
    assertEquals(TWO, network.modules().on());
  }

  @Test
  void theReachIsTheRangeOfTheControllerFromThePlate() {
    Network network = settled();

    assertTrue(network.modules().reaches(NEAR_PLATE));
    assertFalse(network.modules().reaches(FAR_PLATE));
  }

  @Test
  void aModuleInputNeedsEveryPart() {
    assertThrows(
        NullPointerException.class, () -> new ModuleInput(null, NEAR_PLATE, DOOR, Signal.MAX));
    assertThrows(NullPointerException.class, () -> new ModuleInput(TIMER, null, DOOR, Signal.MAX));
    assertThrows(
        NullPointerException.class, () -> new ModuleInput(TIMER, NEAR_PLATE, null, Signal.MAX));
    assertThrows(NullPointerException.class, () -> new ModuleInput(TIMER, NEAR_PLATE, DOOR, null));
  }

  @Test
  void aModuleIdNeedsAValue() {
    assertThrows(NullPointerException.class, () -> new ModuleId(null));
  }
}
