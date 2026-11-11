package io.github.marcelofabianov.redweave.domain.network;

/** The state of a network (answers 135 and 146). Off and Unplaced, every channel is off. */
public enum NetworkState {
  /** The controller is in the world, and the network is on. */
  ONLINE,
  /** Its owner turned it off at a Network Terminal (answer 132). */
  OFFLINE,
  /** Its controller is not in the world: broken, or gone some other way. */
  UNPLACED
}
