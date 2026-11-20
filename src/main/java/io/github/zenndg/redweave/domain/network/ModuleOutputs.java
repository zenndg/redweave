package io.github.zenndg.redweave.domain.network;

import io.github.zenndg.redweave.domain.signal.ChannelColor;
import io.github.zenndg.redweave.domain.signal.Signal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * What each Signal Module wrote into a channel the last time the log looked, to tell which changed.
 * Only a module that counts writes: in range and in an online network. A module that leaves while
 * on goes back to 0, and the first look only learns, as for the outputs of the nodes (answers 244,
 * 392, 396 and 424). Only the modules that wrote a signal are kept, so the map stays small.
 */
public final class ModuleOutputs {
  private Map<Writer, Written> last = new HashMap<>();
  private boolean looked;

  /** The modules whose written value changed since the last look, as lines of the given time. */
  public List<SignalChange> observe(
      Network network, Map<ModuleInput, String> modules, LocalDateTime time) {
    Map<Writer, Written> now = new HashMap<>();
    Map<Writer, String> names = new HashMap<>();
    modules.forEach((input, name) -> {
      Writer writer = new Writer(input.module(), input.channel());
      names.put(writer, name);
      if (counts(network, input) && input.signal().isOn()) {
        now.put(writer, new Written(name, input.signal()));
      }
    });
    List<SignalChange> changes = looked ? changes(network, now, names, time) : List.of();
    last = now;
    looked = true;
    return changes;
  }

  private List<SignalChange> changes(
      Network network, Map<Writer, Written> now, Map<Writer, String> names, LocalDateTime time) {
    Set<Writer> writers = new HashSet<>(now.keySet());
    writers.addAll(last.keySet());
    List<SignalChange> changes = new ArrayList<>();
    for (Writer writer : writers) {
      Signal value = valueOf(now.get(writer));
      if (!value.equals(valueOf(last.get(writer)))) {
        String name = names.containsKey(writer) ? names.get(writer) : last.get(writer).name();
        changes.add(lineOf(network, name, writer.channel(), value, time));
      }
    }
    return changes;
  }

  private static Signal valueOf(Written written) {
    return written == null ? Signal.OFF : written.signal();
  }

  private static boolean counts(Network network, ModuleInput input) {
    return network.isOnline() && network.modules().reaches(input.location());
  }

  private static SignalChange lineOf(
      Network network, String name, ChannelColor channel, Signal value, LocalDateTime time) {
    return new SignalChange(
        time, name, channel, network.channel(channel).name(), Direction.OUTPUT, value);
  }

  /** A module on one channel: changing channel turns the old one off and the new one on. */
  private record Writer(ModuleId module, ChannelColor channel) {}

  /** What a module wrote, with its name then, for the line when it leaves. */
  private record Written(String name, Signal signal) {}
}
