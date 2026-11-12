package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.BlockLocation;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** The Network Terminal of each player, in memory, as the saves keep it (answer 175). */
final class InMemoryTerminals implements TerminalFinder, TerminalRecorder, TerminalForgetter {
  private final Map<PlayerId, BlockLocation> terminals = new HashMap<>();

  @Override
  public Optional<BlockLocation> terminalOf(PlayerId player) {
    return Optional.ofNullable(terminals.get(player));
  }

  @Override
  public void record(PlayerId player, BlockLocation terminal) {
    terminals.put(player, terminal);
  }

  @Override
  public void forget(PlayerId player) {
    terminals.remove(player);
  }
}
