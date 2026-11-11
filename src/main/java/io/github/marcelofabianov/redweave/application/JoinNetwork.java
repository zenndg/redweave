package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.Direction;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.NodePosition;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import java.util.Optional;

/**
 * How a node joins a network, picked or switched: one input on the face it sticks to, on the first
 * free channel, so it works at once. No free channel leaves every slot free (answers 25 and 171).
 */
final class JoinNetwork {
  private static final int FIRST_SLOT = 0;

  private JoinNetwork() {}

  static Outcome join(
      Network network, PlayerId player, NodeId node, NodePosition position, Optional<String> name) {
    Outcome outcome = network.attachNode(player, node, position, name);
    if (outcome != Outcome.ACCEPTED) {
      return outcome;
    }
    network
        .firstFreeChannel()
        .ifPresent(channel ->
            network.configureSlot(
                player,
                node,
                FIRST_SLOT,
                new ChannelSlot(Direction.INPUT, position.face(), channel)));
    return Outcome.ACCEPTED;
  }
}
