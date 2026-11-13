package io.github.marcelofabianov.redweave.domain.network;

import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Inside the network: the Signal Remotes that count this tick. The server gives the whole set each
 * tick, so nothing goes to the saves (answers 356 and 360). A tablet on a slot that is not an
 * enabled input is ignored, so its item shows `Link lost` (answer 287).
 */
final class RemoteInputs {
  private Set<RemoteInput> active = Set.of();

  /** Takes the tablets of this tick and tells whether the set changed. */
  boolean replace(Set<RemoteInput> inputs) {
    Set<RemoteInput> next = Set.copyOf(inputs);
    boolean changed = !next.equals(active);
    active = next;
    return changed;
  }

  /** Puts 15 on the channel of each linked input that counts, with the strongest rule (352). */
  void addTo(Nodes nodes, Map<ChannelColor, Signal> channels, Predicate<RemoteInput> counts) {
    for (RemoteInput input : active) {
      if (counts.test(input) && nodes.isInput(input.node(), input.index())) {
        ChannelColor channel = nodes.at(input.node()).slotAt(input.index()).orElseThrow().channel();
        channels.merge(channel, Signal.MAX, Signal::strongest);
      }
    }
  }

  /** How many tablets count, with the copies of one tablet as one (answers 358 and 364). */
  int count(Nodes nodes) {
    return (int)
        active.stream()
            .filter(input -> nodes.isInput(input.node(), input.index()))
            .map(RemoteInput::remote)
            .distinct()
            .count();
  }
}
