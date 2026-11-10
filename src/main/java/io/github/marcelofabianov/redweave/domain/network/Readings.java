package io.github.marcelofabianov.redweave.domain.network;

import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * The last reading of each input slot of a network, which holds while the chunk of the node is
 * unloaded (answers 13 and 37). A slot that was never read is off.
 */
final class Readings {
  private final Map<SlotRef, Signal> values = new HashMap<>();

  /** Keeps a reading and tells whether it changed. */
  boolean record(SlotRef slot, Signal signal) {
    Signal previous = values.put(slot, signal);
    return !signal.equals(previous == null ? Signal.OFF : previous);
  }

  Signal of(SlotRef slot) {
    return values.getOrDefault(slot, Signal.OFF);
  }

  void forget(SlotRef slot) {
    values.remove(slot);
  }

  void forgetNode(NodeId node) {
    values.keySet().removeIf(slot -> slot.node().equals(node));
  }

  /** Adds the inputs of a node to the strongest value of each channel (rede-e-canais.md). */
  void addInputsOf(Node node, Map<ChannelColor, Signal> channels) {
    for (int index = 0; index < node.slots().size(); index++) {
      Optional<ChannelSlot> slot = node.slots().get(index);
      if (slot.isPresent() && slot.get().direction() == Direction.INPUT) {
        channels.merge(slot.get().channel(), of(new SlotRef(node.id(), index)), Signal::strongest);
      }
    }
  }
}
