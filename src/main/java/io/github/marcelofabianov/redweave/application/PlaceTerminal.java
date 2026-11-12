package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.BlockLocation;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import java.util.Optional;

/**
 * A player places a Network Terminal: one per player on the server (answer 137). A registered one
 * gone from a loaded chunk is replaced, and one in an unloaded chunk holds (answer 253).
 */
public final class PlaceTerminal {
  private final TerminalFinder finder;
  private final TerminalRecorder recorder;

  public PlaceTerminal(TerminalFinder finder, TerminalRecorder recorder) {
    this.finder = finder;
    this.recorder = recorder;
  }

  public TerminalPlacement place(PlayerId player, BlockLocation location, TerminalLookup world) {
    Optional<BlockLocation> existing = finder.terminalOf(player);
    boolean free =
        existing.isEmpty()
            || existing.get().equals(location)
            || world.at(existing.get()) == TerminalCheck.GONE;
    if (!free) {
      return new TerminalPlacement(Outcome.TERMINAL_LIMIT, existing);
    }
    recorder.record(player, location);
    return new TerminalPlacement(Outcome.ACCEPTED, Optional.empty());
  }
}
