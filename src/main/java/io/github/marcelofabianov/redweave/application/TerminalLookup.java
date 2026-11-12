package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.BlockLocation;

/** The world tells what is at a registered Network Terminal, without loading its chunk. */
@FunctionalInterface
public interface TerminalLookup {
  TerminalCheck at(BlockLocation terminal);
}
