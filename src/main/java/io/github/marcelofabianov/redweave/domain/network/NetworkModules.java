package io.github.marcelofabianov.redweave.domain.network;

import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.Map;
import java.util.Set;

/**
 * The Signal Module side of a network: the modules that write into its channels each tick and how
 * many count now. It keeps the state in the `ModuleInputs` part (answers 375, 387 and 388).
 */
public final class NetworkModules {
  private final ModuleInputs inputs;
  private final NetworkSignals signals;
  private final Emissions emissions;

  NetworkModules(ModuleInputs inputs, NetworkSignals signals, Emissions emissions) {
    this.inputs = inputs;
    this.signals = signals;
    this.emissions = emissions;
  }

  /** The modules of this tick; the network is pending only when a channel changes (rule 18). */
  public void apply(Set<ModuleInput> modules) {
    Map<ChannelColor, Signal> before = signals.channelValues();
    if (inputs.replace(modules) && !before.equals(signals.channelValues())) {
      emissions.markPending();
    }
  }

  /** Whether a plate there is in the range of the controller, as a node (answer 385). */
  public boolean reaches(BlockLocation plate) {
    return signals.reaches(plate);
  }

  /** How many modules count now, for the Controller Screen; none when not online (424). */
  public int on() {
    return signals.modulesOn();
  }
}
