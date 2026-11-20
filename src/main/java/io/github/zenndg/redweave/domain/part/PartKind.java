package io.github.zenndg.redweave.domain.part;

/**
 * What takes a face of a block space: a Signal Cable, or a plate, which is a Signal Node or a
 * Signal Module. Only cables have a limit of their own in a space (answers 498 and 513).
 */
public enum PartKind {
  CABLE,
  PLATE
}
