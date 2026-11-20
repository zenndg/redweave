package io.github.zenndg.redweave.domain.simulation;

import io.github.zenndg.redweave.domain.network.ChannelSlot;
import io.github.zenndg.redweave.domain.network.Network;
import io.github.zenndg.redweave.domain.network.Node;
import io.github.zenndg.redweave.domain.network.OutputChange;
import io.github.zenndg.redweave.domain.network.OutputFace;
import io.github.zenndg.redweave.domain.signal.ChannelColor;
import io.github.zenndg.redweave.domain.signal.Signal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Evaluates a network at most once a tick, and only when a signal of it changed (AGENTS.md, rule
 * 18). It keeps no state: the network knows whether it waits and what it last emitted (answer 38).
 */
public final class Simulator {
  /** Gives back only the faces whose signal changed, so the game makes no cascading updates. */
  public List<OutputChange> evaluate(Network network) {
    if (!network.isPending()) {
      return List.of();
    }
    Map<ChannelColor, Signal> channels = network.channelValues();
    Map<OutputFace, Signal> wanted = new LinkedHashMap<>();
    for (Node node : network.nodes()) {
      boolean counts = network.counts(node.id());
      for (Optional<ChannelSlot> slot : node.slots()) {
        slot.filter(ChannelSlot::emits)
            .ifPresent(output ->
                wanted.merge(
                    new OutputFace(node.id(), node.position().block(), output.face()),
                    counts ? channels.get(output.channel()) : Signal.OFF,
                    Signal::strongest));
      }
    }
    return network.settle(wanted);
  }
}
