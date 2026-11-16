package io.github.marcelofabianov.redweave.domain.module;

/**
 * The numbers of the Signal Lock that the pack sets in the config: the shortest and the longest
 * pulse of its `Toggle`, in ticks (answer 640). The domain defines it, and the config implements
 * it.
 */
public interface LockLimits extends IntervalLimits {}
