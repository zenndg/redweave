package io.github.zenndg.redweave.application;

import static io.github.zenndg.redweave.domain.network.NetworkFixture.NODE_POSITION;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.STRANGER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zenndg.redweave.domain.module.SignalModule;
import io.github.zenndg.redweave.domain.network.ModuleEntry;
import io.github.zenndg.redweave.domain.network.ModuleId;
import io.github.zenndg.redweave.domain.network.Network;
import io.github.zenndg.redweave.domain.network.NetworkId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/**
 * Each loaded Signal Module brings its entry in the network up to date: an old one registers, a
 * changed one updates, one taken out by `Clear components` lets go of the network, and one that
 * moved leaves the old network (answers 803, 805, 806, 810, 811 and 817).
 */
class SyncModuleTest {
  private static final String ID_UUID = "8a9b0c1d-2e3f-4a4b-9c5d-6e7f8a9b0c1d";
  private static final ModuleId ID = new ModuleId(UUID.fromString(ID_UUID));
  private static final String KIND = "signal_timer";
  private static final String DOOR_CLOCK = "Door clock";

  private final ApplicationFixture fixture = new ApplicationFixture();
  private final SyncModule sync = new SyncModule(fixture.networks, fixture.networks);

  private static ModuleEntry entry(Optional<String> name) {
    return new ModuleEntry(ID, KIND, name, NODE_POSITION, List.of());
  }

  private static SignalModule moduleOn(NetworkId network) {
    SignalModule module = SignalModule.placed(ID, OWNER);
    module.choose(OWNER, network);
    return module;
  }

  private Network network(NetworkId id) {
    return fixture.networks.find(id).orElseThrow();
  }

  /** A module saved before the register, with its network and no mark, registers (805). */
  @Test
  void aModuleWithNoMarkRegisters() {
    NetworkId network = fixture.ownersNetwork();

    ModuleSync result = sync.sync(moduleOn(network), Optional.empty(), entry(Optional.empty()));

    assertEquals(Optional.of(network), result.registeredIn());
    assertFalse(result.cleared());
    assertEquals(Optional.of(entry(Optional.empty())), network(network).register().entry(ID));
  }

  @Test
  void aChangedModuleUpdatesItsEntry() {
    NetworkId network = fixture.ownersNetwork();
    SignalModule module = moduleOn(network);
    sync.sync(module, Optional.empty(), entry(Optional.empty()));

    sync.sync(module, Optional.of(network), entry(Optional.of(DOOR_CLOCK)));

    assertEquals(
        Optional.of(entry(Optional.of(DOOR_CLOCK))), network(network).register().entry(ID));
  }

  /** Nothing changed, nothing is saved, so the tick costs no write (rule 18). */
  @Test
  void anUnchangedModuleSavesNothing() {
    NetworkId network = fixture.ownersNetwork();
    SignalModule module = moduleOn(network);
    sync.sync(module, Optional.empty(), entry(Optional.empty()));
    int saves = fixture.networks.saves();

    ModuleSync result = sync.sync(module, Optional.of(network), entry(Optional.empty()));

    assertEquals(saves, fixture.networks.saves());
    assertEquals(Optional.of(network), result.registeredIn());
  }

  /** Marked with this network and out of its register, `Clear components` took it out (811). */
  @Test
  void aModuleTakenOutLetsGoOfTheNetwork() {
    NetworkId network = fixture.ownersNetwork();
    SignalModule module = moduleOn(network);

    ModuleSync result = sync.sync(module, Optional.of(network), entry(Optional.empty()));

    assertTrue(result.cleared());
    assertTrue(result.registeredIn().isEmpty());
    assertTrue(network(network).register().entry(ID).isEmpty());
  }

  /** A module that chose another network leaves the old one and joins the new (817). */
  @Test
  void aModuleThatMovedLeavesTheOldNetwork() {
    NetworkId old = fixture.ownersNetwork();
    NetworkId other = fixture.ownersOtherNetwork();
    sync.sync(moduleOn(old), Optional.empty(), entry(Optional.empty()));

    ModuleSync result = sync.sync(moduleOn(other), Optional.of(old), entry(Optional.empty()));

    assertTrue(network(old).register().entry(ID).isEmpty());
    assertTrue(network(other).register().entry(ID).isPresent());
    assertEquals(Optional.of(other), result.registeredIn());
  }

  @Test
  void aModuleWithNoNetworkLeavesItsOldOne() {
    NetworkId old = fixture.ownersNetwork();
    sync.sync(moduleOn(old), Optional.empty(), entry(Optional.empty()));

    ModuleSync result =
        sync.sync(SignalModule.placed(ID, OWNER), Optional.of(old), entry(Optional.empty()));

    assertTrue(network(old).register().entry(ID).isEmpty());
    assertTrue(result.registeredIn().isEmpty());
    assertFalse(result.cleared());
  }

  /** A network that is gone registers nothing; the tick lets go of it on its own. */
  @Test
  void aModuleOfAGoneNetworkRegistersNothing() {
    ModuleSync result =
        sync.sync(
            moduleOn(ApplicationFixture.MISSING_NETWORK),
            Optional.empty(),
            entry(Optional.empty()));

    assertTrue(result.registeredIn().isEmpty());
    assertFalse(result.cleared());
  }

  /** A module of another player never joins the network of the owner (rule 17). */
  @Test
  void aModuleOfAStrangerJoinsNothing() {
    NetworkId network = fixture.ownersNetwork();
    SignalModule module = SignalModule.placed(ID, STRANGER);
    module.choose(STRANGER, network);
    int saves = fixture.networks.saves();

    ModuleSync result = sync.sync(module, Optional.empty(), entry(Optional.empty()));

    assertTrue(result.registeredIn().isEmpty());
    assertEquals(saves, fixture.networks.saves());
  }

  /** Broken or picked up, the module leaves the network it is registered in (answer 806). */
  @Test
  void aModuleThatWentLeavesItsNetwork() {
    NetworkId network = fixture.ownersNetwork();
    sync.sync(moduleOn(network), Optional.empty(), entry(Optional.empty()));

    sync.leave(Optional.of(network), ID);
    sync.leave(Optional.empty(), ID);

    assertTrue(network(network).register().entry(ID).isEmpty());
  }
}
