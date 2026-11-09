package io.github.marcelofabianov.redweave.domain.network;

/** Fixed limits for the tests, with the starting values of _docs/limites.md. */
public record TestLimits(int slotsPerNode, int networkNameLength, int channelNameLength, int range)
    implements NetworkLimits {
  private static final int SLOTS = 4;
  private static final int NETWORK_NAME = 32;
  private static final int CHANNEL_NAME = 16;
  private static final int RANGE = 128;

  public static final TestLimits STARTING =
      new TestLimits(SLOTS, NETWORK_NAME, CHANNEL_NAME, RANGE);
}
