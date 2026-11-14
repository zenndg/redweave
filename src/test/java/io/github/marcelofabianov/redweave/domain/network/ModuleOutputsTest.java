package io.github.marcelofabianov.redweave.domain.network;

import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OVERWORLD;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.network;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/**
 * Each change of what a module that counts writes into a channel is a line of the log, as an output
 * of a node, with the name of the module (answers 244, 392, 396 and 424).
 */
class ModuleOutputsTest {
  private static final LocalDateTime NOW = LocalDateTime.of(2026, 11, 14, 10, 50, 0);
  private static final String TIMER_UUID = "5f6a7b8c-9d0e-4f1a-8b2c-3d4e5f6a7b8c";
  private static final ModuleId TIMER = new ModuleId(UUID.fromString(TIMER_UUID));
  private static final String NAME = "Door clock";
  private static final int GROUND = 64;
  private static final int NEAR = 10;
  private static final int FAR = 500;
  private static final BlockLocation NEAR_PLATE = new BlockLocation(OVERWORLD, NEAR, GROUND, NEAR);
  private static final BlockLocation FAR_PLATE = new BlockLocation(OVERWORLD, FAR, GROUND, FAR);
  private static final ChannelColor DOOR = ChannelColor.RED;
  private static final ChannelColor LAMP = ChannelColor.BLUE;
  private static final String GATE = "Gate";

  private static Map<ModuleInput, String> writing(ChannelColor channel, Signal signal) {
    return Map.of(new ModuleInput(TIMER, NEAR_PLATE, channel, signal), NAME);
  }

  private static SignalChange line(ChannelColor channel, Signal value) {
    return new SignalChange(NOW, NAME, channel, Optional.empty(), Direction.OUTPUT, value);
  }

  private static ModuleOutputs looked(Network network, Map<ModuleInput, String> modules) {
    ModuleOutputs outputs = new ModuleOutputs();
    outputs.observe(network, modules, NOW);
    return outputs;
  }

  @Test
  void theFirstLookOnlyLearns() {
    assertEquals(List.of(), new ModuleOutputs().observe(network(), writing(DOOR, Signal.MAX), NOW));
  }

  @Test
  void aModuleThatTurnsOnIsALine() {
    Network network = network();
    ModuleOutputs outputs = looked(network, writing(DOOR, Signal.OFF));

    assertEquals(
        List.of(line(DOOR, Signal.MAX)), outputs.observe(network, writing(DOOR, Signal.MAX), NOW));
  }

  @Test
  void theSameValueAgainIsNoLine() {
    Network network = network();
    ModuleOutputs outputs = looked(network, writing(DOOR, Signal.MAX));

    assertEquals(List.of(), outputs.observe(network, writing(DOOR, Signal.MAX), NOW));
  }

  @Test
  void aModuleThatLeavesWhileOnGoesBackToOff() {
    Network network = network();
    ModuleOutputs outputs = looked(network, writing(DOOR, Signal.MAX));

    assertEquals(List.of(line(DOOR, Signal.OFF)), outputs.observe(network, Map.of(), NOW));
  }

  @Test
  void anotherChannelIsOffOnTheOldAndOnOnTheNew() {
    Network network = network();
    ModuleOutputs outputs = looked(network, writing(DOOR, Signal.MAX));

    List<SignalChange> lines = outputs.observe(network, writing(LAMP, Signal.MAX), NOW);

    assertEquals(2, lines.size());
    assertTrue(lines.contains(line(DOOR, Signal.OFF)));
    assertTrue(lines.contains(line(LAMP, Signal.MAX)));
  }

  @Test
  void aModuleOutOfRangeWritesNothing() {
    Network network = network();
    ModuleOutputs outputs = looked(network, writing(DOOR, Signal.OFF));
    Map<ModuleInput, String> far =
        Map.of(new ModuleInput(TIMER, FAR_PLATE, DOOR, Signal.MAX), NAME);

    assertEquals(List.of(), outputs.observe(network, far, NOW));
  }

  @Test
  void aNetworkThatIsNotOnlineWritesNothing() {
    Network network = network();
    ModuleOutputs outputs = looked(network, writing(DOOR, Signal.MAX));

    network.terminal().turnOff(OWNER);

    assertEquals(
        List.of(line(DOOR, Signal.OFF)), outputs.observe(network, writing(DOOR, Signal.MAX), NOW));
  }

  @Test
  void theLineTakesTheNameOfTheChannelOfTheMoment() {
    Network network = network();
    network.renameChannel(OWNER, DOOR, GATE);
    ModuleOutputs outputs = looked(network, writing(DOOR, Signal.OFF));

    SignalChange expected =
        new SignalChange(NOW, NAME, DOOR, Optional.of(GATE), Direction.OUTPUT, Signal.MAX);
    assertEquals(List.of(expected), outputs.observe(network, writing(DOOR, Signal.MAX), NOW));
  }
}
