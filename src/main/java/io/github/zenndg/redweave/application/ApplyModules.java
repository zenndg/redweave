package io.github.zenndg.redweave.application;

import io.github.zenndg.redweave.domain.network.ModuleInput;
import io.github.zenndg.redweave.domain.network.Network;
import io.github.zenndg.redweave.domain.network.NetworkId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Each tick, the loaded Signal Modules reach their networks. Every network gets its whole set, an
 * empty one too, so a module that is gone or unloaded goes back to 0, and what a module writes that
 * changed becomes a line of the log (answers 386, 387, 392 and 396). Nothing is saved.
 */
public final class ApplyModules {
  private final AllNetworksFinder finder;
  private final SignalLogs logs;
  private final ServerClock clock;

  public ApplyModules(AllNetworksFinder finder, SignalLogs logs, ServerClock clock) {
    this.finder = finder;
    this.logs = logs;
    this.clock = clock;
  }

  public void apply(List<ModuleSighting> sightings) {
    Map<NetworkId, Map<ModuleInput, String>> byNetwork = new HashMap<>();
    sightings.forEach(sighting ->
        byNetwork
            .computeIfAbsent(sighting.network(), id -> new HashMap<>())
            .put(sighting.input(), sighting.name()));
    for (Network network : finder.all()) {
      Map<ModuleInput, String> modules = byNetwork.getOrDefault(network.id(), Map.of());
      network.modules().apply(modules.keySet());
      logs.modulesOf(network.id())
          .observe(network, modules, clock.now())
          .forEach(change -> logs.record(network.id(), change));
    }
  }
}
