package io.github.zenndg.redweave.domain.network;

/** What the screens show for a node (answer 29). */
public enum NodeState {
  /** In the network, loaded and in range: it counts as it is. */
  ONLINE,
  /** Its chunk is unloaded: its inputs keep the last reading. */
  UNLOADED,
  /** Out of the radius or in another dimension: it does not count, and its outputs emit off. */
  OUT_OF_RANGE,
  /** Not in the network, or the network is unplaced (answer 146). */
  NO_NETWORK,
  /** Its network is turned off: it does not count, and its outputs emit off (answer 177). */
  OFFLINE
}
