package io.github.marcelofabianov.redweave.network;

import io.github.marcelofabianov.redweave.block.part.PartSpot;
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
  private static final Map<UUID, NodeLabelsUpdate.ModuleLine> MODULES = new LinkedHashMap<>();
  private static final Map<PartSpot, NodeLabelsUpdate.CableLine> CABLES = new LinkedHashMap<>();

  private ReceivedLabels() {}

  /** Applies an update from the server: a clear of all, or the changed labels and the gone ones. */
  public static synchronized void accept(NodeLabelsUpdate update) {
    if (update.clearAll()) {
      clear();
    }
    update.labels().forEach(label -> LABELS.put(label.node(), label));
    update.modules().forEach(module -> MODULES.put(module.module(), module));
    update.cables().forEach(cable -> CABLES.put(cable.place().spot(), cable));
    update.goneCables().forEach(CABLES::remove);
    update
        .gone()
        .forEach(id -> {
          LABELS.remove(id);
          MODULES.remove(id);
        });
  }

  public static synchronized List<NodeLabelsUpdate.Label> all() {
    return List.copyOf(LABELS.values());
  }

  public static synchronized List<NodeLabelsUpdate.ModuleLine> modules() {
    return List.copyOf(MODULES.values());
  }

  public static synchronized List<NodeLabelsUpdate.CableLine> cables() {
    return List.copyOf(CABLES.values());
  }

  /** Leaving the world takes every label away. */
  public static synchronized void clear() {
    LABELS.clear();
    MODULES.clear();
    CABLES.clear();
  }
}
