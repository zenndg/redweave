package io.github.marcelofabianov.redweave.block.part;

import io.github.marcelofabianov.redweave.domain.module.DelayedOutput;
import io.github.marcelofabianov.redweave.domain.module.OutputDelay;

/**
 * A module part whose output waits for a delay its owner sets on its screen (answers 830 and 850),
 * for the `Delay` row every such screen shares.
 */
public interface DelayedPart {
  DelayedOutput delayed();

  /** The owner changed the delay; what waits keeps its tick (answer 839). */
  void setDelay(OutputDelay next);
}
