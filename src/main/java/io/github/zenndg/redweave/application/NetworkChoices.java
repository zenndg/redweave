package io.github.zenndg.redweave.application;

import io.github.zenndg.redweave.domain.network.Network;
import io.github.zenndg.redweave.domain.network.NetworkId;
import io.github.zenndg.redweave.domain.network.PlayerId;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * The networks a player can pick for a node: only their online ones (answer 157), the last used on
 * top, so the common case is one click, and the others in alphabetical order (answer 203).
 */
public final class NetworkChoices {
  private final OwnerNetworkFinder owned;
  private final LastUsedNetworkFinder lastUsed;

  public NetworkChoices(OwnerNetworkFinder owned, LastUsedNetworkFinder lastUsed) {
    this.owned = owned;
    this.lastUsed = lastUsed;
  }

  public List<NetworkChoice> of(PlayerId player) {
    Optional<NetworkId> last = lastUsed.lastUsedOf(player);
    Comparator<NetworkChoice> lastFirst = Comparator.comparing(choice -> !choice.lastUsed());
    return owned.ownedBy(player).stream()
        .filter(Network::isOnline)
        .map(network -> choiceOf(network, last))
        .sorted(lastFirst.thenComparing(NetworkChoice::name, String.CASE_INSENSITIVE_ORDER))
        .toList();
  }

  private static NetworkChoice choiceOf(Network network, Optional<NetworkId> last) {
    boolean isLast = last.filter(network.id()::equals).isPresent();
    return new NetworkChoice(
        network.id(), network.name(), network.color(), network.components(), isLast);
  }
}
