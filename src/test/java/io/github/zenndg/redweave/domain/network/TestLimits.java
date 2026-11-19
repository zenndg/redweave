package io.github.zenndg.redweave.domain.network;

/** Fixed limits for the tests, with the starting values of _docs/limites.md. */
public record TestLimits(
    int networkNameLength,
    int channelNameLength,
    int nodeNameLength,
    int range,
    int networksPerPlayer)
    implements NetworkLimits {
  private static final int NETWORK_NAME = 32;
  private static final int CHANNEL_NAME = 16;
  private static final int NODE_NAME = 16;
  private static final int RANGE = 128;
  private static final int NETWORKS = 10;

  public static final TestLimits STARTING =
      new TestLimits(NETWORK_NAME, CHANNEL_NAME, NODE_NAME, RANGE, NETWORKS);

  /** The same limits with another number of networks per player (answer 145). */
  public TestLimits withNetworksPerPlayer(int networks) {
    return new TestLimits(networkNameLength, channelNameLength, nodeNameLength, range, networks);
  }
}
