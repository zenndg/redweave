package io.github.zenndg.redweave.domain.network;

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
  /** An enabled slot is locked; the owner disables it to set it up (answer 237). */
  SLOT_ENABLED,
  /** The network already has its controller in the world, as with a copied item (answer 56). */
  ALREADY_PLACED,
  /** The network is not in the world, as one asked for by an old packet (answer 41). */
  UNKNOWN_NETWORK,
  /** A node only joins a network that is online, with its controller in the world (157 and 172). */
  NETWORK_NOT_ONLINE,
  /** Only an offline network turns back on; an unplaced one waits for its controller (135). */
  NETWORK_NOT_OFFLINE,
  /** Only an unplaced network is deleted, so one in the world never goes (answer 166). */
  NETWORK_NOT_UNPLACED,
  /** The player already has a Network Terminal, one per player (answer 137). */
  TERMINAL_LIMIT,
  /** The player already has network.max_per_player networks, so no new one is born (145). */
  NETWORK_LIMIT
}
