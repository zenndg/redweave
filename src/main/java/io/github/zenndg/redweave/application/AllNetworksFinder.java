package io.github.zenndg.redweave.application;

import io.github.zenndg.redweave.domain.network.Network;
import java.util.List;

/** Every network of the world, for the evaluation of each tick. */
@FunctionalInterface
public interface AllNetworksFinder {
  List<Network> all();
}
