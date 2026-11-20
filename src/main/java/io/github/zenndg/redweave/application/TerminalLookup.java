package io.github.zenndg.redweave.application;

import io.github.zenndg.redweave.domain.network.BlockLocation;

/** The world tells what is at a registered Network Terminal, without loading its chunk. */
@FunctionalInterface
public interface TerminalLookup {
  TerminalCheck at(BlockLocation terminal);
}
