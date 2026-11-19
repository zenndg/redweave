package io.github.zenndg.redweave.domain.network;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The Signal Modules registered in a network, in the order they joined; a module registered again
 * keeps its place (answers 803 and 806).
 */
final class ModuleEntries {
  private final Map<ModuleId, ModuleEntry> byId = new LinkedHashMap<>();

  void put(ModuleEntry entry) {
    byId.put(entry.id(), entry);
  }

  boolean remove(ModuleId id) {
    return byId.remove(id) != null;
  }

  void clear() {
    byId.clear();
  }

  Optional<ModuleEntry> at(ModuleId id) {
    return Optional.ofNullable(byId.get(id));
  }

  List<ModuleEntry> all() {
    return List.copyOf(byId.values());
  }

  int size() {
    return byId.size();
  }
}
