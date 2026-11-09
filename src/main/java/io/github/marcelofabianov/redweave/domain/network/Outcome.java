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
  INVALID_SLOT
}
