package io.github.zenndg.redweave.server;

import io.github.zenndg.redweave.application.BreakTerminal;
import io.github.zenndg.redweave.application.ClearComponents;
import io.github.zenndg.redweave.application.DeleteNetwork;
import io.github.zenndg.redweave.application.PlaceTerminal;
import io.github.zenndg.redweave.application.SignalLogs;
import io.github.zenndg.redweave.application.TerminalNetworks;
import io.github.zenndg.redweave.application.TurnNetwork;
import io.github.zenndg.redweave.domain.network.BlockLocation;
import io.github.zenndg.redweave.domain.network.PlayerId;
import java.util.Optional;

/**
 * The use cases of the Network Terminal, wired to the saves of the server: the block and its screen
 * (terminal-de-rede.md). Kept apart from RedweaveServer, so each wiring stays small.
 */
public final class TerminalServices {
  private final SavedDataNetworks networks;
  private final PlaceTerminal placeTerminal;
  private final BreakTerminal breakTerminal;
  private final TerminalNetworks terminalNetworks;
  private final TurnNetwork turnNetwork;
  private final ClearComponents clearComponents;
  private final DeleteNetwork deleteNetwork;

  TerminalServices(SavedDataNetworks networks, SignalLogs logs) {
    this.networks = networks;
    placeTerminal = new PlaceTerminal(networks, networks);
    breakTerminal = new BreakTerminal(networks, networks);
    terminalNetworks = new TerminalNetworks(networks);
    turnNetwork = new TurnNetwork(networks, networks);
    clearComponents = new ClearComponents(networks, networks);
    deleteNetwork = new DeleteNetwork(networks, networks, logs);
  }

  public PlaceTerminal placeTerminal() {
    return placeTerminal;
  }

  public BreakTerminal breakTerminal() {
    return breakTerminal;
  }

  public TerminalNetworks terminalNetworks() {
    return terminalNetworks;
  }

  public TurnNetwork turnNetwork() {
    return turnNetwork;
  }

  public ClearComponents clearComponents() {
    return clearComponents;
  }

  public DeleteNetwork deleteNetwork() {
    return deleteNetwork;
  }

  /** Where the Network Terminal of a player is, from the saves (answer 175). */
  public Optional<BlockLocation> terminalOf(PlayerId player) {
    return networks.terminalOf(player);
  }
}
