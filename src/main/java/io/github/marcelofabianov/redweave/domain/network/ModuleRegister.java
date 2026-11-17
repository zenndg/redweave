package io.github.marcelofabianov.redweave.domain.network;

import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.List;
import java.util.Optional;

/**
 * The register of the Signal Modules of a network, by {@code register()}: the owner registers each
 * module with what the list shows, it leaves when it goes, and each one shows its state as a node
 * and what it writes this tick (answers 803, 806, 809, 810 and 814).
 */
public final class ModuleRegister {
  private final Network network;
  private final ModuleEntries entries;
  private final NetworkSignals signals;

  ModuleRegister(Network network, ModuleEntries entries, NetworkSignals signals) {
    this.network = network;
    this.entries = entries;
    this.signals = signals;
  }

  /** Registers a module of the owner, or brings its entry up to date; it keeps its place. */
  public Outcome register(PlayerId requester, ModuleEntry entry) {
    if (!network.isOwner(requester)) {
      return Outcome.NOT_OWNER;
    }
    entries.put(entry);
    return Outcome.ACCEPTED;
  }

  /** A module that went: broken, picked up or moved to another network (answer 806). */
  public boolean forget(ModuleId module) {
    return entries.remove(module);
  }

  /** Takes every module out, as `Clear components` asks (answer 807). */
  void clear() {
    entries.clear();
  }

  public Optional<ModuleEntry> entry(ModuleId module) {
    return entries.at(module);
  }

  /** The registered modules, in the order they joined. */
  public List<ModuleEntry> entries() {
    return entries.all();
  }

  /** The state of a registered module, as the one of a node (answer 809). */
  public NodeState state(ModuleId module, boolean loaded) {
    return signals.placeState(entries.at(module).map(entry -> entry.position().block()), loaded);
  }

  /** What a module writes on its channel this tick, or none (answer 814). */
  public Optional<Signal> signal(ModuleId module) {
    return signals.moduleSignal(module);
  }
}
