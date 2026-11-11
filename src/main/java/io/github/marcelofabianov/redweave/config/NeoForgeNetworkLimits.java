package io.github.marcelofabianov.redweave.config;

import io.github.marcelofabianov.redweave.domain.network.NetworkLimits;

/** The limits of a network, read from the server config every time, so an edit holds at once. */
public final class NeoForgeNetworkLimits implements NetworkLimits {
  @Override
  public int slotNameLength() {
    return RedweaveConfig.SLOT_NAME_LENGTH.get();
  }

  @Override
  public int networkNameLength() {
    return RedweaveConfig.NETWORK_NAME_LENGTH.get();
  }

  @Override
  public int channelNameLength() {
    return RedweaveConfig.CHANNEL_NAME_LENGTH.get();
  }

  @Override
  public int nodeNameLength() {
    return RedweaveConfig.NODE_NAME_LENGTH.get();
  }

  @Override
  public int range() {
    return RedweaveConfig.RANGE_VALUE.get();
  }

  @Override
  public int networksPerPlayer() {
    return RedweaveConfig.MAX_PER_PLAYER.get();
  }
}
