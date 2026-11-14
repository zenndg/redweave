package io.github.marcelofabianov.redweave.domain.module;

/**
 * The numbers of every Signal Module that the pack sets in the config: the length of a name (answer
 * 391). The domain defines it, and the config implements it.
 */
@FunctionalInterface
public interface ModuleLimits {
  int moduleNameLength();
}
