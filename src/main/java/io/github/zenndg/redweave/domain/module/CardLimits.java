package io.github.zenndg.redweave.domain.module;

/**
 * The number of Signal Cards an item accepts, set by the pack in the config (answers 669 and 915).
 * The domain defines it, and the config implements it.
 */
public interface CardLimits {
  int maxLinksPerItem();
}
