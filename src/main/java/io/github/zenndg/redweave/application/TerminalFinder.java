package io.github.zenndg.redweave.application;

import io.github.zenndg.redweave.domain.network.BlockLocation;
import io.github.zenndg.redweave.domain.network.PlayerId;
import java.util.Optional;

/** Where the Network Terminal of a player is, from the saves (answer 175). */
@FunctionalInterface
public interface TerminalFinder {
  Optional<BlockLocation> terminalOf(PlayerId player);
}
