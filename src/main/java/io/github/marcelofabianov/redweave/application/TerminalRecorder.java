package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.BlockLocation;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;

/** Keeps the Network Terminal of a player in the saves, in place of any other (answer 175). */
@FunctionalInterface
public interface TerminalRecorder {
  void record(PlayerId player, BlockLocation terminal);
}
