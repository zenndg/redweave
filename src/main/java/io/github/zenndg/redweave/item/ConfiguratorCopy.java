package io.github.zenndg.redweave.item;

import io.github.zenndg.redweave.domain.network.NetworkId;
import io.github.zenndg.redweave.domain.network.NodeCopy;
import java.util.Objects;

/**
 * What the Configurator keeps when it copies a node: the slots and the network the node was in, so
 * a paste on a node with no network joins it there (answers 105, 230 and 238).
 */
public record ConfiguratorCopy(NetworkId network, NodeCopy slots) {
  private static final String MISSING = "A copy needs its network and its slots";

  public ConfiguratorCopy {
    Objects.requireNonNull(network, MISSING);
    Objects.requireNonNull(slots, MISSING);
  }
}
