package io.github.marcelofabianov.redweave.domain.module;

import io.github.marcelofabianov.redweave.domain.network.DefaultNames;
import io.github.marcelofabianov.redweave.domain.network.ModuleId;
import io.github.marcelofabianov.redweave.domain.network.Names;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import java.util.Objects;
import java.util.Optional;

/**
 * The shared base of every Signal Module: its ID, its owner, whoever placed it, an optional name
 * and one network (answers 378, 389, 397 and 415). Each change takes who asks, as in the network,
 * and only the owner changes it. Whether the network is an online one of the owner is checked by
 * the application, which sees the networks.
 */
public final class SignalModule {
  private static final String MISSING = "A module needs its ID, owner, name and network";

  private final ModuleId id;
  private final PlayerId owner;
  private Optional<String> name;
  private Optional<NetworkId> network;

  private SignalModule(
      ModuleId id, PlayerId owner, Optional<String> name, Optional<NetworkId> network) {
    this.id = Objects.requireNonNull(id, MISSING);
    this.owner = Objects.requireNonNull(owner, MISSING);
    this.name = Objects.requireNonNull(name, MISSING);
    this.network = Objects.requireNonNull(network, MISSING);
  }

  /** A module just placed: it belongs to whoever placed it, with no name and no network (416). */
  public static SignalModule placed(ModuleId id, PlayerId owner) {
    return new SignalModule(id, owner, Optional.empty(), Optional.empty());
  }

  /** A module from the saves; the boundary already dropped what was invalid (answer 411). */
  public static SignalModule restore(
      ModuleId id, PlayerId owner, Optional<String> name, Optional<NetworkId> network) {
    return new SignalModule(id, owner, name, network);
  }

  public boolean isOwner(PlayerId player) {
    return owner.equals(player);
  }

  /** Names the module; blank goes back to the default, as for a node (answers 144 and 391). */
  public Outcome rename(PlayerId requester, String newName, ModuleLimits limits) {
    if (!isOwner(requester)) {
      return Outcome.NOT_OWNER;
    }
    if (newName.isBlank()) {
      name = Optional.empty();
      return Outcome.ACCEPTED;
    }
    Optional<String> valid = Names.valid(newName, limits.moduleNameLength());
    if (valid.isEmpty()) {
      return Outcome.INVALID_NAME;
    }
    name = valid;
    return Outcome.ACCEPTED;
  }

  /** The owner chooses the network of the module, one for all its connections (answer 397). */
  public Outcome choose(PlayerId requester, NetworkId chosen) {
    if (!isOwner(requester)) {
      return Outcome.NOT_OWNER;
    }
    network = Optional.of(chosen);
    return Outcome.ACCEPTED;
  }

  /** Whether the owner may use this connection: a channel needs a network (answers 375, 414). */
  public Outcome connects(PlayerId requester, Connection connection) {
    if (!isOwner(requester)) {
      return Outcome.NOT_OWNER;
    }
    return connection.isEdge() || network.isPresent() ? Outcome.ACCEPTED : Outcome.UNKNOWN_NETWORK;
  }

  /** The network was deleted: the module has none until its owner chooses another (385). */
  public void loseNetwork() {
    network = Optional.empty();
  }

  /** The name, or the first 8 characters of the ID, as a node, in any language (answer 427). */
  public String displayName() {
    return name.orElseGet(() -> DefaultNames.of(id.value()));
  }

  public ModuleId id() {
    return id;
  }

  public PlayerId owner() {
    return owner;
  }

  public Optional<String> name() {
    return name;
  }

  public Optional<NetworkId> network() {
    return network;
  }
}
