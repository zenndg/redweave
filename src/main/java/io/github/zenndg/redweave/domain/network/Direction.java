package io.github.zenndg.redweave.domain.network;

/** Whether a slot reads the block or emits into it. */
public enum Direction {
  /** The node reads the redstone of the block into the channel. */
  INPUT,
  /** The node emits the channel into the block. */
  OUTPUT
}
