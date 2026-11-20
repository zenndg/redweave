package io.github.zenndg.redweave.domain.signal;

/**
 * The most changes a delay line holds, set in the config and shared by every module with a delay
 * (answer 847). The domain defines it, and the config implements it.
 */
public interface DelayLineLimits {
  int maxDelayChanges();
}
