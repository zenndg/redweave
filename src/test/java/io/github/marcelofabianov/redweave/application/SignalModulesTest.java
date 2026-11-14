package io.github.marcelofabianov.redweave.application;

import static io.github.marcelofabianov.redweave.application.ApplicationFixture.MISSING_NETWORK;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OVERWORLD;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.STRANGER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.marcelofabianov.redweave.domain.module.SignalModule;
import io.github.marcelofabianov.redweave.domain.network.BlockLocation;
import io.github.marcelofabianov.redweave.domain.network.ModuleId;
import io.github.marcelofabianov.redweave.domain.network.ModuleInput;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.NodeState;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import io.github.marcelofabianov.redweave.domain.network.SignalLog;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/**
 * The Signal Modules in the use cases: the modules of each tick reach their networks and the log,
 * only the owner chooses an online network of his, and the screen sees the state of that network
 * (answers 375, 385, 387, 392, 397 and 424).
 */
class SignalModulesTest {
  private static final String MODULE_UUID = "5f6a7b8c-9d0e-4f1a-8b2c-3d4e5f6a7b8c";
  private static final ModuleId ID = new ModuleId(UUID.fromString(MODULE_UUID));
  private static final String NAME = "Door clock";
  private static final int GROUND = 64;
  private static final int NEAR = 10;
  private static final int FAR = 5000;
  private static final BlockLocation PLATE = new BlockLocation(OVERWORLD, NEAR, GROUND, NEAR);
  private static final BlockLocation FAR_PLATE = new BlockLocation(OVERWORLD, FAR, GROUND, FAR);
  private static final ChannelColor DOOR = ChannelColor.RED;
  private static final long FROM_THE_START = 0;
  private static final int ONE = 1;

  private final ApplicationFixture fixture = new ApplicationFixture();
  private final ApplyModules applyModules =
      new ApplyModules(fixture.networks, fixture.logs, fixture.clock);
  private final ChooseModuleNetwork choose = new ChooseModuleNetwork(fixture.networks);
  private final ShowModule show = new ShowModule(fixture.networks);
  private final ReadModuleChannel read = new ReadModuleChannel(fixture.networks);

  private Network network(NetworkId id) {
    return fixture.networks.find(id).orElseThrow();
  }

  private static ModuleSighting writing(NetworkId network, Signal signal) {
    return new ModuleSighting(network, new ModuleInput(ID, PLATE, DOOR, signal), NAME);
  }

  private SignalModule moduleOn(NetworkId network) {
    SignalModule module = SignalModule.placed(ID, OWNER);
    module.choose(OWNER, network);
    return module;
  }

  @Test
  void eachNetworkGetsItsModulesAndTheOthersNone() {
    NetworkId id = fixture.ownersNetwork();
    NetworkId other = fixture.ownersOtherNetwork();

    applyModules.apply(List.of(writing(id, Signal.MAX)));

    assertEquals(Signal.MAX, network(id).channelValue(DOOR));
    assertEquals(Signal.OFF, network(other).channelValue(DOOR));
    assertEquals(ONE, network(id).modules().on());
  }

  @Test
  void theControllerScreenCountsTheModulesThatCount() {
    NetworkId id = fixture.ownersNetwork();
    ShowNetwork showNetwork = new ShowNetwork(fixture.networks);

    applyModules.apply(List.of(writing(id, Signal.OFF)));

    assertEquals(ONE, showNetwork.of(OWNER, id, node -> true).orElseThrow().modulesOn());
  }

  @Test
  void aModuleThatStopsBeingSeenGoesBackToOff() {
    NetworkId id = fixture.ownersNetwork();
    applyModules.apply(List.of(writing(id, Signal.MAX)));

    applyModules.apply(List.of());

    assertEquals(Signal.OFF, network(id).channelValue(DOOR));
  }

  @Test
  void aChangeOfWhatAModuleWritesIsALineOfTheLog() {
    NetworkId id = fixture.ownersNetwork();
    applyModules.apply(List.of(writing(id, Signal.OFF)));

    applyModules.apply(List.of(writing(id, Signal.MAX)));

    List<SignalLog.Line> lines = fixture.logs.after(id, FROM_THE_START);
    assertEquals(ONE, lines.size());
    assertEquals(NAME, lines.get(0).change().node());
    assertEquals(Signal.MAX, lines.get(0).change().value());
  }

  @Test
  void theOwnerChoosesOneOfHisOnlineNetworks() {
    NetworkId id = fixture.ownersNetwork();
    SignalModule module = SignalModule.placed(ID, OWNER);

    assertEquals(Outcome.ACCEPTED, choose.choose(OWNER, module, id));
    assertEquals(Optional.of(id), module.network());
  }

  @Test
  void aNetworkOfAnotherPlayerIsRefused() {
    NetworkId strangers = fixture.strangersNetwork();
    SignalModule module = SignalModule.placed(ID, OWNER);

    assertEquals(Outcome.NOT_OWNER, choose.choose(OWNER, module, strangers));
    assertEquals(Optional.empty(), module.network());
  }

  @Test
  void anotherPlayerCannotChooseForTheOwner() {
    NetworkId strangers = fixture.strangersNetwork();
    SignalModule module = SignalModule.placed(ID, OWNER);

    assertEquals(Outcome.NOT_OWNER, choose.choose(STRANGER, module, strangers));
  }

  @Test
  void anOfflineOrMissingNetworkIsRefused() {
    NetworkId id = fixture.ownersNetwork();
    network(id).terminal().turnOff(OWNER);
    SignalModule module = SignalModule.placed(ID, OWNER);

    assertEquals(Outcome.NETWORK_NOT_ONLINE, choose.choose(OWNER, module, id));
    assertEquals(Outcome.UNKNOWN_NETWORK, choose.choose(OWNER, module, MISSING_NETWORK));
  }

  @Test
  void theScreenSeesTheStateOfTheNetworkFromThePlate() {
    NetworkId id = fixture.ownersNetwork();
    SignalModule module = moduleOn(id);

    ModuleNetworkView view = show.show(module, PLATE);
    assertEquals(NodeState.ONLINE, view.state());
    assertEquals(Optional.of(network(id).name()), view.name());
    assertEquals(NodeState.OUT_OF_RANGE, show.show(module, FAR_PLATE).state());

    network(id).terminal().turnOff(OWNER);
    assertEquals(NodeState.OFFLINE, show.show(module, PLATE).state());
  }

  @Test
  void withNoNetworkOrAnUnplacedOneTheScreenSeesNoNetwork() {
    NetworkId id = fixture.ownersNetwork();
    assertEquals(NodeState.NO_NETWORK, show.show(SignalModule.placed(ID, OWNER), PLATE).state());

    network(id).removeController(OWNER);
    assertEquals(NodeState.NO_NETWORK, show.show(moduleOn(id), PLATE).state());
  }

  @Test
  void aDeletedNetworkIsGoneSoTheModuleCanLetItGo() {
    SignalModule module = moduleOn(MISSING_NETWORK);

    ModuleNetworkView view = show.show(module, PLATE);

    assertTrue(view.gone());
    assertEquals(NodeState.NO_NETWORK, view.state());
    assertFalse(show.show(moduleOn(fixture.ownersNetwork()), PLATE).gone());
  }

  @Test
  void aChannelIsReadOnlyWhenTheModuleCounts() {
    NetworkId id = fixture.ownersNetwork();
    applyModules.apply(List.of(writing(id, Signal.MAX)));
    SignalModule module = moduleOn(id);

    assertEquals(Signal.MAX, read.read(module, PLATE, DOOR));
    assertEquals(Signal.OFF, read.read(module, FAR_PLATE, DOOR));
    assertEquals(Signal.OFF, read.read(SignalModule.placed(ID, OWNER), PLATE, DOOR));

    network(id).terminal().turnOff(OWNER);
    assertEquals(Signal.OFF, read.read(module, PLATE, DOOR));
  }
}
