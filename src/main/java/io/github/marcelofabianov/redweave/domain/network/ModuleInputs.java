package io.github.marcelofabianov.redweave.domain.network;

import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Inside the network: the Signal Modules that write into a channel this tick. The server gives the
 * whole set each tick, only with loaded modules, so an unloaded one is worth 0 and nothing goes to
 * the saves (answers 386 and 387).
 */
final class ModuleInputs {
  private Set<ModuleInput> active = Set.of();

  /** Takes the modules of this tick and tells whether the set changed. */
  boolean replace(Set<ModuleInput> inputs) {
    Set<ModuleInput> next = Set.copyOf(inputs);
    boolean changed = !next.equals(active);
    active = next;
    return changed;
  }

  /** Puts the signal of each module in range on its channel, with the strongest rule (375). */
  void addTo(Map<ChannelColor, Signal> channels, Predicate<BlockLocation> inRange) {
    for (ModuleInput input : active) {
      if (inRange.test(input.location())) {
        channels.merge(input.channel(), input.signal(), Signal::strongest);
      }
    }
  }

  /** What a module writes this tick, the strongest of its channels, or none (answer 814). */
  Optional<Signal> of(ModuleId module) {
    return active.stream()
        .filter(input -> input.module().equals(module))
        .map(ModuleInput::signal)
        .reduce(Signal::strongest);
  }

  /** How many modules count, on or off (answer 424). */
  int count(Predicate<BlockLocation> inRange) {
    return (int)
        active.stream()
            .filter(input -> inRange.test(input.location()))
            .map(ModuleInput::module)
            .distinct()
            .count();
  }
}
