package io.github.zenndg.redweave.application;

import io.github.zenndg.redweave.domain.network.Network;
import io.github.zenndg.redweave.domain.network.PlayerId;
import java.util.Comparator;
import java.util.List;

/**
 * The networks of an owner for the Network Terminal, in alphabetical order whatever the case, in
 * any state, from the saves, so no chunk is loaded (answers 131, 138 and 152, and rule 6).
 */
public final class TerminalNetworks {
  private static final Comparator<TerminalRow> ALPHABETICAL =
      Comparator.comparing(TerminalRow::name, String.CASE_INSENSITIVE_ORDER)
          .thenComparing(row -> row.id().value());

  private final OwnerNetworkFinder finder;

  public TerminalNetworks(OwnerNetworkFinder finder) {
    this.finder = finder;
  }

  public List<TerminalRow> of(PlayerId owner) {
    return finder.ownedBy(owner).stream()
        .map(TerminalNetworks::rowOf)
        .sorted(ALPHABETICAL)
        .toList();
  }

  private static TerminalRow rowOf(Network network) {
    return new TerminalRow(
        network.id(), network.name(), network.color(), network.state(), network.components());
  }
}
