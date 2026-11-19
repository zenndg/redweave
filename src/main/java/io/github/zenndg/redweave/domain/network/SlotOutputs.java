package io.github.zenndg.redweave.domain.network;

import io.github.zenndg.redweave.domain.signal.ChannelColor;
import io.github.zenndg.redweave.domain.signal.Signal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * What each output slot of a network emitted the last time the log looked, to tell which changed.
 * The simulator works by face, and the log by slot (answer 243). A slot that does not emit is worth
 * 0, so disabling and enabling an output are changes (answer 246). The first look only learns the
 * values, because applying the faces again after loading is no change (answer 244).
 */
public final class SlotOutputs {
  private static final ChannelColor[] COLORS = ChannelColor.values();

  private Map<SlotRef, Signal> last = new HashMap<>();
  private boolean looked;

  /** The output slots whose value changed since the last look, as lines of the given time. */
  public List<SignalChange> observe(Network network, LocalDateTime time) {
    Map<SlotRef, Signal> now = new HashMap<>();
    List<SignalChange> changes = new ArrayList<>();
    for (Node node : network.nodes()) {
      for (int index = 0; index < Node.SLOT_COUNT; index++) {
        Signal value = network.output(node.id(), index);
        SlotRef slot = new SlotRef(node.id(), index);
        if (value.isOn()) {
          now.put(slot, value);
        }
        if (looked && !value.equals(last.getOrDefault(slot, Signal.OFF))) {
          changes.add(lineOf(network, node, COLORS[index], value, time));
        }
      }
    }
    last = now;
    looked = true;
    return changes;
  }

  private static SignalChange lineOf(
      Network network, Node node, ChannelColor color, Signal value, LocalDateTime time) {
    Optional<String> channelName = network.channel(color).name();
    return new SignalChange(time, node.displayName(), color, channelName, Direction.OUTPUT, value);
  }
}
