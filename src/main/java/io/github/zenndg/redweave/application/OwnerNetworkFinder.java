package io.github.zenndg.redweave.application;

import io.github.zenndg.redweave.domain.network.Network;
import io.github.zenndg.redweave.domain.network.PlayerId;
import java.util.List;

/** Finds the networks of one owner. */
@FunctionalInterface
public interface OwnerNetworkFinder {
  List<Network> ownedBy(PlayerId owner);
}
