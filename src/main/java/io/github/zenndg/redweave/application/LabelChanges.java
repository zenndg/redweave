package io.github.zenndg.redweave.application;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * What the player who holds the Configurator already has, of the nodes or of the modules, to send
 * only what changed, at most once a tick (answer 269 and rule 18). One per player, only in memory.
 */
public final class LabelChanges<K, L> {
  private Map<K, L> sent = new HashMap<>();

  /** The labels that changed or came into reach, and the nodes that left it. */
  public record Delta<K, L>(Map<K, L> changed, List<K> gone) {
    public Delta {
      changed = Map.copyOf(changed);
      gone = List.copyOf(gone);
    }

    public boolean isEmpty() {
      return changed.isEmpty() && gone.isEmpty();
    }
  }

  public Delta<K, L> next(Map<K, L> now) {
    Map<K, L> changed = new LinkedHashMap<>();
    now.forEach((id, label) -> {
      if (!label.equals(sent.get(id))) {
        changed.put(id, label);
      }
    });
    List<K> gone = sent.keySet().stream().filter(id -> !now.containsKey(id)).toList();
    sent = new HashMap<>(now);
    return new Delta<>(changed, gone);
  }
}
