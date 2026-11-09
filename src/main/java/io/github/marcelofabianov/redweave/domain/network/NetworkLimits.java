package io.github.marcelofabianov.redweave.domain.network;

/**
 * The numbers of a network. The domain only reads them, and the config implements them, so they are
 * never fixed in the logic (AGENTS.md, rule 3).
 */
public interface NetworkLimits {
  int slotsPerNode();

  int networkNameLength();

  int channelNameLength();
}
