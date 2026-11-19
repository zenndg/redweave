package io.github.zenndg.redweave.application;

import io.github.zenndg.redweave.domain.module.SignalModule;
import io.github.zenndg.redweave.domain.network.ModuleEntry;
import io.github.zenndg.redweave.domain.network.ModuleId;
import io.github.zenndg.redweave.domain.network.Network;
import io.github.zenndg.redweave.domain.network.NetworkId;
import io.github.zenndg.redweave.domain.network.Outcome;
import java.util.Optional;

/**
 * A loaded Signal Module brings its entry in its network up to date, on each tick, and saves only
 * what changed (rule 18). An old module, with no mark, or one that chose another network, registers
 * and leaves the old one; one marked with its network but out of the register was taken out by
 * `Clear components` and lets go (answers 803, 805, 806, 810, 811 and 817).
 */
public final class SyncModule {
  private final NetworkFinder finder;
  private final NetworkSaver saver;

  public SyncModule(NetworkFinder finder, NetworkSaver saver) {
    this.finder = finder;
    this.saver = saver;
  }

  public ModuleSync sync(SignalModule module, Optional<NetworkId> registeredIn, ModuleEntry entry) {
    Optional<NetworkId> chosen = module.network();
    if (chosen.equals(registeredIn) && chosen.isPresent()) {
      return refresh(chosen.get(), entry);
    }
    registeredIn.ifPresent(old -> forget(old, entry.id()));
    return chosen
        .flatMap(finder::find)
        .map(network -> join(network, module, entry))
        .orElse(ModuleSync.NONE);
  }

  /** A module that went, broken or picked up, leaves the network of its mark (answer 806). */
  public void leave(Optional<NetworkId> registeredIn, ModuleId module) {
    registeredIn.ifPresent(network -> forget(network, module));
  }

  /** Marked with its network: up to date, or taken out by `Clear components` (answer 811). */
  private ModuleSync refresh(NetworkId id, ModuleEntry entry) {
    Optional<Network> network = finder.find(id);
    if (network.isEmpty()) {
      return ModuleSync.NONE;
    }
    Optional<ModuleEntry> known = network.get().register().entry(entry.id());
    if (known.isEmpty()) {
      return ModuleSync.CLEARED;
    }
    if (!known.get().equals(entry)) {
      network.get().register().register(network.get().owner(), entry);
      saver.save(network.get());
    }
    return ModuleSync.in(id);
  }

  /** Only a module of the owner of the network joins it (rule 17). */
  private ModuleSync join(Network network, SignalModule module, ModuleEntry entry) {
    if (network.register().register(module.owner(), entry) != Outcome.ACCEPTED) {
      return ModuleSync.NONE;
    }
    saver.save(network);
    return ModuleSync.in(network.id());
  }

  private void forget(NetworkId old, ModuleId module) {
    finder.find(old).filter(network -> network.register().forget(module)).ifPresent(saver::save);
  }
}
