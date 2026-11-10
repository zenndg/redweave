package io.github.marcelofabianov.redweave.item;

import io.github.marcelofabianov.redweave.application.PasteResult;
import io.github.marcelofabianov.redweave.block.ControllerMessages;
import io.github.marcelofabianov.redweave.block.NetworkControllerBlockEntity;
import io.github.marcelofabianov.redweave.block.NodeLook;
import io.github.marcelofabianov.redweave.block.NodeSignals;
import io.github.marcelofabianov.redweave.block.SignalNodeBlock;
import io.github.marcelofabianov.redweave.block.SignalNodeBlockEntity;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.NodeCopy;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.menu.NodeScreenTexts;
import io.github.marcelofabianov.redweave.registry.RedweaveDataComponents;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/** What the Configurator does on the server, each action giving the message for the player. */
final class ConfiguratorActions {
  private static final String NO_NAME = "";

  private ConfiguratorActions() {}

  /** Only the owner keeps the network, with its name for the tooltip (answers 104 and 112). */
  static Component store(
      RedweaveServer server, Player player, ItemStack stack, NetworkControllerBlockEntity block) {
    Optional<Network> network = block.network().flatMap(server::network);
    if (network.isEmpty()) {
      return Component.translatable(ControllerMessages.NO_NETWORK);
    }
    if (server.storeNetwork().store(playerOf(player), network.get().id()) != Outcome.ACCEPTED) {
      return Component.translatable(ConfiguratorMessages.NOT_YOURS);
    }
    stack.set(RedweaveDataComponents.NETWORK.get(), network.get().id());
    stack.set(RedweaveDataComponents.NETWORK_NAME.get(), network.get().name());
    return Component.translatable(ConfiguratorMessages.STORED, network.get().name());
  }

  /** Shift + click copies; a click pastes the copy, or links the node with no copy (105, 106). */
  static Component onNode(
      RedweaveServer server, UseOnContext context, SignalNodeBlockEntity block) {
    Optional<NodeId> node = block.node();
    if (node.isEmpty()) {
      return Component.translatable(ControllerMessages.NO_NETWORK);
    }
    Player player = context.getPlayer();
    ItemStack stack = context.getItemInHand();
    if (player.isSecondaryUseActive()) {
      return copy(server, player, stack, node.get());
    }
    NodeCopy copy = stack.get(RedweaveDataComponents.NODE_SLOTS.get());
    if (copy != null) {
      return apply(server, context, block, copy);
    }
    Component message = link(server, context, node.get());
    refresh(server, context.getLevel(), context.getClickedPos());
    return message;
  }

  /** Pastes a copy on the node, from the Configurator or from a Label (answers 106 and 118). */
  static Component apply(
      RedweaveServer server, UseOnContext context, SignalNodeBlockEntity block, NodeCopy copy) {
    Optional<NodeId> node = block.node();
    if (node.isEmpty()) {
      return Component.translatable(ControllerMessages.NO_NETWORK);
    }
    Component message = paste(server, context.getPlayer(), node.get(), copy);
    refresh(server, context.getLevel(), context.getClickedPos());
    return message;
  }

  /** The Configurator keeps the copy, and an empty Label in the other hand gets it too (122). */
  private static Component copy(
      RedweaveServer server, Player player, ItemStack stack, NodeId node) {
    Optional<NodeCopy> copy = server.copyNode().copy(node);
    if (copy.isEmpty()) {
      return Component.translatable(ControllerMessages.NO_NETWORK);
    }
    stack.set(RedweaveDataComponents.NODE_SLOTS.get(), copy.get());
    int slots = copy.get().slots().size();
    if (LabelWriting.write(player, copy.get())) {
      return Component.translatable(LabelMessages.WRITTEN, slots);
    }
    return Component.translatable(ConfiguratorMessages.COPIED, slots);
  }

  private static Component paste(RedweaveServer server, Player player, NodeId node, NodeCopy copy) {
    PasteResult result = server.pasteNode().paste(playerOf(player), node, copy);
    return switch (result.outcome()) {
      case ACCEPTED -> pasted(result);
      case NOT_OWNER -> Component.translatable(NodeScreenTexts.READ_ONLY, nameOfNode(server, node));
      default -> Component.translatable(ControllerMessages.NO_NETWORK);
    };
  }

  private static Component pasted(PasteResult result) {
    if (result.leftOut() == 0) {
      return Component.translatable(ConfiguratorMessages.PASTED, result.pasted());
    }
    return Component.translatable(
        ConfiguratorMessages.PASTED_LEFT_OUT, result.pasted(), result.leftOut());
  }

  /** Links the node to the stored network, as a node just placed (answers 25 and 42). */
  private static Component link(RedweaveServer server, UseOnContext context, NodeId node) {
    NetworkId stored = context.getItemInHand().get(RedweaveDataComponents.NETWORK.get());
    if (stored == null) {
      return Component.translatable(ConfiguratorMessages.NOTHING_STORED);
    }
    Level level = context.getLevel();
    BlockPos pos = context.getClickedPos();
    Outcome outcome =
        server
            .linkNode()
            .link(
                playerOf(context.getPlayer()),
                node,
                SignalNodeBlock.positionOf(level, pos, level.getBlockState(pos)),
                stored);
    String name = server.network(stored).map(Network::name).orElse(NO_NAME);
    return switch (outcome) {
      case ACCEPTED -> Component.translatable(ConfiguratorMessages.LINKED, name);
      case ALREADY_ATTACHED -> Component.translatable(ConfiguratorMessages.ALREADY_IN, name);
      case NOT_OWNER -> Component.translatable(ConfiguratorMessages.OWN_BOTH);
      default -> Component.translatable(ConfiguratorMessages.GONE);
    };
  }

  /** The new inputs are read at once, as when the node is placed, and the clients see the node. */
  private static void refresh(RedweaveServer server, Level level, BlockPos pos) {
    BlockState state = level.getBlockState(pos);
    NodeSignals.read(server, level, pos, state);
    NodeLook.refresh(server, level, pos);
    level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
  }

  private static String nameOfNode(RedweaveServer server, NodeId node) {
    return server.networkOfNode(node).map(Network::name).orElse(NO_NAME);
  }

  private static PlayerId playerOf(Player player) {
    return new PlayerId(player.getUUID());
  }
}
