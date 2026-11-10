package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.Network;
import java.util.List;

/** Every network of the world, for the evaluation of each tick. */
@FunctionalInterface
public interface AllNetworksFinder {
  List<Network> all();
}
