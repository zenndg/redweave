package io.github.marcelofabianov.redweave.application;

/** What the world says of a registered Network Terminal, without loading a chunk (rule 6). */
public enum TerminalCheck {
  /** The block is there. */
  STANDING,
  /** The chunk is loaded and the block is no longer a Network Terminal (answer 253). */
  GONE,
  /** The chunk is not loaded, so nobody knows, and the terminal holds. */
  UNLOADED
}
