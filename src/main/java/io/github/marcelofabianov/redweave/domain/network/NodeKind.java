package io.github.marcelofabianov.redweave.domain.network;

/** What a node does, from its enabled slots, for the Type column of the lists (283 and 337). */
public enum NodeKind {
  /** Only enabled inputs: it reads, like a node on a lever. */
  INPUT,
  /** Only enabled outputs: it emits, like a node on a door. */
  OUTPUT,
  /** Enabled slots in both directions. */
  BOTH,
  /** No enabled slot: it neither reads nor emits. */
  NONE;

  static NodeKind of(boolean reads, boolean emits) {
    if (reads && emits) {
      return BOTH;
    }
    if (reads) {
      return INPUT;
    }
    return emits ? OUTPUT : NONE;
  }
}
