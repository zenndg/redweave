package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import java.util.Optional;

/**
 * What the floating label over a node shows with the Configurator in hand, in two lines: the
 * network, with its color, and the node, after a square in the color of its first enabled slot
 * (answers 268, 274 to 276 and 772). Without a network, the network is empty, and without an
 * enabled slot, so is the slot.
 */
public record NodeLabel(String node, Optional<NetworkPart> network, Optional<ChannelColor> slot) {
  /** The network of the node: its name and color, for the square before it (answer 275). */
  public record NetworkPart(String name, ChannelColor color) {}
}
