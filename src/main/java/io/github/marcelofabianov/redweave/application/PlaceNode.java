package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.NodeId;

/**
 * A player places a Signal Node. It is born with no network and cannot be configured until whoever
 * placed it picks one on the Node Screen (answers 139 and 172).
 */
public final class PlaceNode {
  private final IdGenerator ids;

  public PlaceNode(IdGenerator ids) {
    this.ids = ids;
  }

  public NodeId place() {
    return new NodeId(ids.newId());
  }
}
