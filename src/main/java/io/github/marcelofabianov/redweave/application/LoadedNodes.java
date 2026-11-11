package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.NodePosition;

/**
 * Tells whether the chunk of a node is loaded, which only the world knows. A node in an unloaded
 * chunk is shown as Unloaded (answer 29), and the world is never loaded to find out (rule 6).
 */
@FunctionalInterface
public interface LoadedNodes {
  boolean isLoaded(NodePosition node);
}
