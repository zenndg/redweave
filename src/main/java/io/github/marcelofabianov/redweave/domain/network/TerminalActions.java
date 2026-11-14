package io.github.marcelofabianov.redweave.domain.network;

/**
 * What the Network Terminal does to a network, for its owner only: turn it off and on, take every
 * node out and check whether it may be deleted (answers 132, 133, 166 and 209). It left the
 * `Network` so the aggregate stays under 400 lines (answer 426).
 */
public final class TerminalActions {
  private final Network network;
  private final NetworkStatus status;
  private final Emissions emissions;

  TerminalActions(Network network, NetworkStatus status, Emissions emissions) {
    this.network = network;
    this.status = status;
    this.emissions = emissions;
  }

  /** The owner turns an online network off (answers 132 and 209). */
  public Outcome turnOff(PlayerId requester) {
    return turn(requester, false);
  }

  /** The owner turns an offline network back on; an unplaced one waits for its controller. */
  public Outcome turnOn(PlayerId requester) {
    return turn(requester, true);
  }

  private Outcome turn(PlayerId requester, boolean on) {
    if (!network.isOwner(requester)) {
      return Outcome.NOT_OWNER;
    }
    Outcome outcome = status.turn(on);
    if (outcome == Outcome.ACCEPTED) {
      emissions.markPending();
    }
    return outcome;
  }

  /**
   * Takes every node out, in any state: the nodes stay in the world with no network, and their
   * slots and readings go (answers 133 and 176).
   */
  public Outcome clearNodes(PlayerId requester) {
    if (!network.isOwner(requester)) {
      return Outcome.NOT_OWNER;
    }
    network.nodes().stream().map(Node::id).forEach(network::loseNode);
    return Outcome.ACCEPTED;
  }

  /**
   * Whether the owner may delete the network: only an unplaced one goes, so a network with its
   * controller in the world never does (answer 166). The saves remove it.
   */
  public Outcome deletion(PlayerId requester) {
    if (!network.isOwner(requester)) {
      return Outcome.NOT_OWNER;
    }
    return network.state() == NetworkState.UNPLACED
        ? Outcome.ACCEPTED
        : Outcome.NETWORK_NOT_UNPLACED;
  }
}
