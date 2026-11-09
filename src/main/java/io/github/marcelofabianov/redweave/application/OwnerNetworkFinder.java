package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import java.util.List;

/** Finds the networks of one owner. */
@FunctionalInterface
public interface OwnerNetworkFinder {
  List<Network> ownedBy(PlayerId owner);
}
