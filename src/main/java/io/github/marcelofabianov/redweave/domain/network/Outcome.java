package io.github.marcelofabianov.redweave.domain.network;

/**
 * What an operation on a network did. A refusal is an expected case, such as a packet from another
 * player, so it is a result and not an exception (answer 32).
 */
public enum Outcome {
  ACCEPTED,
  NOT_OWNER,
  INVALID_NAME,
  ALREADY_ATTACHED,
  UNKNOWN_NODE,
  INVALID_SLOT,
  /** The network already has its controller in the world, as with a copied item (answer 56). */
  ALREADY_PLACED,
  /** The network is not in the world, as one asked for by an old packet (answer 41). */
  UNKNOWN_NETWORK,
  /** The player has no current network, so a node they placed stays without one (25). */
  NO_CURRENT_NETWORK
}
