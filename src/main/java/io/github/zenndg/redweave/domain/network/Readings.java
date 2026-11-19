package io.github.zenndg.redweave.domain.network;

import io.github.zenndg.redweave.domain.signal.ChannelColor;
import io.github.zenndg.redweave.domain.signal.Signal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The last reading of each input slot of a network, which holds while the chunk of the node is
 * unloaded (answers 13 and 37). A slot that was never read is off. The order is the order of the
 * readings, so a snapshot comes out the same every time.
 */
final class Readings {
  private final Map<SlotRef, Signal> values = new LinkedHashMap<>();

  /** Keeps a reading and tells whether it changed. */
  boolean record(SlotRef slot, Signal signal) {
    Signal previous = values.put(slot, signal);
    return !signal.equals(previous == null ? Signal.OFF : previous);
  }

  /**
   * Every reading that is on, as it goes to the saves. Off is the same as never read (answer 89).
   */
  List<SlotReading> all() {
    return values.entrySet().stream()
        .filter(entry -> entry.getValue().isOn())
        .map(entry ->
            new SlotReading(entry.getKey().node(), entry.getKey().index(), entry.getValue()))
        .toList();
  }

  /** Puts back a reading from the saves, already checked by the restore. */
  void restore(SlotReading reading) {
    values.put(new SlotRef(reading.node(), reading.index()), reading.signal());
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
      if (slot.filter(ChannelSlot::reads).isPresent()) {
        channels.merge(slot.get().channel(), of(new SlotRef(node.id(), index)), Signal::strongest);
      }
    }
  }
}
