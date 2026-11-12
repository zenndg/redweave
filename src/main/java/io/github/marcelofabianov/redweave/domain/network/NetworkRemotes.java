package io.github.marcelofabianov.redweave.domain.network;

import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.Map;
import java.util.Set;

/**
 * The Signal Remote side of a network: the tablets that count each tick, which slot takes one and
 * how many count now. It keeps the state in the `RemoteInputs` part (answers 27 and 360).
 */
public final class NetworkRemotes {
  private final RemoteInputs inputs;
  private final NetworkSignals signals;
  private final Nodes nodes;
  private final Emissions emissions;

  NetworkRemotes(RemoteInputs inputs, NetworkSignals signals, Nodes nodes, Emissions emissions) {
    this.inputs = inputs;
    this.signals = signals;
    this.nodes = nodes;
    this.emissions = emissions;
  }

  /** The tablets that count this tick; the network is pending only when a channel changes. */
  public void apply(Set<RemoteInput> tablets) {
    Map<ChannelColor, Signal> before = signals.channelValues();
    if (inputs.replace(tablets) && !before.equals(signals.channelValues())) {
      emissions.markPending();
    }
  }

  /** Whether a tablet may link to this slot: an enabled input (answer 287). */
  public boolean accepts(NodeId nodeId, int index) {
    return nodes.isInput(nodeId, index);
  }

  /** How many tablets count now, for the Controller Screen; none when not online (364). */
  public int on() {
    return signals.remotesOn();
  }
}
