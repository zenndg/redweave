package io.github.zenndg.redweave.application;

import java.util.UUID;

/** Gives a new ID, so the domain has no randomness and the tests use fixed IDs (answer 31). */
@FunctionalInterface
public interface IdGenerator {
  UUID newId();
}
