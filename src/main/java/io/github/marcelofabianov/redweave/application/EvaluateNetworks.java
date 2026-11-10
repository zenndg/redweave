package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.OutputChange;
import io.github.marcelofabianov.redweave.domain.simulation.Simulator;
import java.util.List;

/**
 * The end of a tick: the simulator evaluates only the networks waiting for it and hands back the
 * faces whose signal changed, for the world to apply (rule 18 and answer 38).
 */
public final class EvaluateNetworks {
  private final AllNetworksFinder finder;
  private final Simulator simulator;

  public EvaluateNetworks(AllNetworksFinder finder, Simulator simulator) {
    this.finder = finder;
    this.simulator = simulator;
  }

  public List<OutputChange> evaluate() {
    return finder.all().stream().flatMap(network -> simulator.evaluate(network).stream()).toList();
  }
}
