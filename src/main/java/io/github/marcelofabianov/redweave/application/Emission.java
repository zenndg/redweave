package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.signal.DelayedOutput;
import io.github.marcelofabianov.redweave.domain.signal.OutputDelay;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.Objects;
import java.util.Optional;

/**
 * What a node should emit now, before its output delay: the signal of its network, or none when it
 * does not count, and the delay its owner chose (answers 860 and 862).
 */
public record Emission(Optional<Signal> signal, OutputDelay delay) {
  /** A node with no network: nothing to emit and no delay (answer 862). */
  public static final Emission NONE = new Emission(Optional.empty(), OutputDelay.NONE);

  public Emission {
    Objects.requireNonNull(signal);
    Objects.requireNonNull(delay);
  }

  /**
   * The delayed output of the node once it knows this: with the delay of today and, when the node
   * does not count, off at once with nothing waiting (answers 839 and 862).
   */
  public DelayedOutput onto(DelayedOutput now) {
    DelayedOutput delayed = now.withDelay(delay);
    return signal.isPresent() ? delayed : delayed.cut();
  }

  /** What the lens and the screens show at once, while only the output waits (answer 863). */
  public Signal wanted() {
    return signal.orElse(Signal.OFF);
  }
}
