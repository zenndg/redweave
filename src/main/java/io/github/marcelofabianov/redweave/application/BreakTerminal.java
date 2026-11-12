package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.BlockLocation;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;

/** The Network Terminal of a player was broken, so the player may place another (137). */
public final class BreakTerminal {
  private final TerminalFinder finder;
  private final TerminalForgetter forgetter;

  public BreakTerminal(TerminalFinder finder, TerminalForgetter forgetter) {
    this.finder = finder;
    this.forgetter = forgetter;
  }

  /** Only the registered block frees the player, so a stale block elsewhere changes nothing. */
  public void broke(PlayerId owner, BlockLocation location) {
    if (finder.terminalOf(owner).filter(location::equals).isPresent()) {
      forgetter.forget(owner);
    }
  }
}
