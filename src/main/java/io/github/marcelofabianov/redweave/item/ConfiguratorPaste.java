package io.github.marcelofabianov.redweave.item;

import io.github.marcelofabianov.redweave.application.PasteResult;
import io.github.marcelofabianov.redweave.block.ControllerMessages;
import io.github.marcelofabianov.redweave.block.SignalNodeBlock;
import io.github.marcelofabianov.redweave.block.SignalNodeBlockEntity;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.menu.NodeScreenTexts;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Shift + click of the Copy mode: on a node with a network, the slots are pasted and the network
 * stays (answer 239); on a node with no network, whoever placed it joins it to the network of the
 * copy first, as picking it on the Node Screen would (answers 172 and 238).
 */
final class ConfiguratorPaste {
  private static final String NO_NAME = "";

  private ConfiguratorPaste() {}

  static Component paste(
      RedweaveServer server, Player player, Level level, BlockPos pos, ConfiguratorCopy copy) {
    if (!(level.getBlockEntity(pos) instanceof SignalNodeBlockEntity block)
        || block.node().isEmpty()) {
      return Component.translatable(ControllerMessages.NO_NETWORK);
    }
    NodeId node = block.node().get();
    if (server.networkOfNode(node).isEmpty()) {
      Optional<Component> refused = join(server, player, level, pos, block, copy.network());
      if (refused.isPresent()) {
        return refused.get();
      }
    }
    return pasted(server, player, node, copy);
  }

  /** Joins the node to the network of the copy, or gives back why it did not. */
  private static Optional<Component> join(
      RedweaveServer server,
      Player player,
      Level level,
      BlockPos pos,
      SignalNodeBlockEntity block,
      NetworkId network) {
    if (!block.wasPlacedBy(player)) {
      return Optional.of(Component.translatable(ConfiguratorMessages.PASTE_NOT_PLACER));
    }
    Outcome joined =
        server
            .linkNode()
            .link(
                new PlayerId(player.getUUID()),
                block.node().get(),
                SignalNodeBlock.positionOf(level, pos, level.getBlockState(pos)),
                network);
    String name = server.network(network).map(Network::name).orElse(NO_NAME);
    return switch (joined) {
      case ACCEPTED -> Optional.empty();
      case NOT_OWNER -> Optional.of(Component.translatable(NodeScreenTexts.READ_ONLY, name));
      case NETWORK_NOT_ONLINE -> Optional.of(ControllerMessages.offline(name));
      default -> Optional.of(Component.translatable(ControllerMessages.NO_NETWORK));
    };
  }

  private static Component pasted(
      RedweaveServer server, Player player, NodeId node, ConfiguratorCopy copy) {
    PasteResult result =
        server.pasteNode().paste(new PlayerId(player.getUUID()), node, copy.slots());
    return switch (result.outcome()) {
      case ACCEPTED -> Component.translatable(ConfiguratorMessages.PASTED, result.pasted());
      case NOT_OWNER ->
          Component.translatable(
              NodeScreenTexts.READ_ONLY,
              server.networkOfNode(node).map(Network::name).orElse(NO_NAME));
      default -> Component.translatable(ControllerMessages.NO_NETWORK);
    };
  }
}
