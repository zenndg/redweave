package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.OutputChange;
import io.github.marcelofabianov.redweave.domain.simulation.Simulator;
import java.util.List;

/**
 * The end of a tick: the simulator evaluates only the networks waiting for it and hands back the
 * faces whose signal changed, for the world to apply (rule 18 and answer 38). The output slots that
 * changed value become lines of the log (answer 243).
 */
public final class EvaluateNetworks {
  private final AllNetworksFinder finder;
  private final Simulator simulator;
  private final SignalLogs logs;
  private final ServerClock clock;

  public EvaluateNetworks(
      AllNetworksFinder finder, Simulator simulator, SignalLogs logs, ServerClock clock) {
    this.finder = finder;
    this.simulator = simulator;
    this.logs = logs;
    this.clock = clock;
  }

  public List<OutputChange> evaluate() {
    return finder.all().stream().flatMap(network -> evaluate(network).stream()).toList();
  }

  private List<OutputChange> evaluate(Network network) {
    if (!network.isPending()) {
      return List.of();
    }
    List<OutputChange> changes = simulator.evaluate(network);
    logs.outputsOf(network.id())
        .observe(network, clock.now())
        .forEach(change -> logs.record(network.id(), change));
    return changes;
  }
}
