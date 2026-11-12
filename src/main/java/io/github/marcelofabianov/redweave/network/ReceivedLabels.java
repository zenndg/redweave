package io.github.marcelofabianov.redweave.network;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The labels the client holds, as the server sent them (answer 269). Common code, with no client
 * class, so the dedicated server opens without them (rule 19); only the client fills it.
 */
public final class ReceivedLabels {
  private static final Map<UUID, NodeLabelsUpdate.Label> LABELS = new LinkedHashMap<>();

  private ReceivedLabels() {}

  /** Applies an update from the server: a clear of all, or the changed labels and the gone ones. */
  public static synchronized void accept(NodeLabelsUpdate update) {
    if (update.clearAll()) {
      LABELS.clear();
    }
    update.labels().forEach(label -> LABELS.put(label.node(), label));
    update.gone().forEach(LABELS::remove);
  }

  public static synchronized List<NodeLabelsUpdate.Label> all() {
    return List.copyOf(LABELS.values());
  }

  /** Leaving the world takes every label away. */
  public static synchronized void clear() {
    LABELS.clear();
  }
}
