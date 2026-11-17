package io.github.marcelofabianov.redweave.domain.module;

/**
 * The longest output delay a pack sets in the config, in ticks, shared by every module with a delay
 * (answer 837). The domain defines it, and the config implements it.
 */
public interface OutputDelayLimits {
  int maxOutputDelayTicks();
}
