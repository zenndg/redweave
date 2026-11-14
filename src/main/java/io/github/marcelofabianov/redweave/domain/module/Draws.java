package io.github.marcelofabianov.redweave.domain.module;

/**
 * Where the Signal Randomizer gets its draws: the domain reads no randomness, so the caller gives
 * it, as the random source of the world on the server, and a fixed list in the tests (answer 483).
 */
public interface Draws {
  /** A whole number from 0 to `bound` less 1, each as likely. */
  int next(int bound);
}
