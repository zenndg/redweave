package io.github.marcelofabianov.redweave.item;

import io.github.marcelofabianov.redweave.block.ControllerMessages;
import io.github.marcelofabianov.redweave.block.NodeLook;
import io.github.marcelofabianov.redweave.block.NodeSignals;
import io.github.marcelofabianov.redweave.block.SignalNodeBlock;
import io.github.marcelofabianov.redweave.block.SignalNodeBlockEntity;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.NetworkState;
import io.github.marcelofabianov.redweave.domain.network.NodeCopy;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.menu.NodeScreenTexts;
import io.github.marcelofabianov.redweave.registry.RedweaveDataComponents;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/** What the Configurator does on a node on the server, each action giving its message, if any. */
final class ConfiguratorActions {
  private static final String NO_NAME = "";
  private static final boolean KEEP_DROPS = false;
  private static final boolean NOT_THROWN = false;

  private ConfiguratorActions() {}

  static Optional<Component> onNode(
      RedweaveServer server,
      Player player,
      UseOnContext context,
      SignalNodeBlockEntity block,
      ConfiguratorMode mode) {
    Optional<Network> offline =
        block.node().flatMap(server::networkOfNode).filter(ConfiguratorActions::isOff);
    if (offline.isPresent() && mode != ConfiguratorMode.WRENCH) {
      return Optional.of(ControllerMessages.offline(offline.get().name()));
    }
    return switch (mode) {
      case OPEN -> open(player, context.getClickedPos());
      case COPY -> copyOrPaste(server, player, context, block);
      case WRENCH -> pickUp(server, player, context.getLevel(), context.getClickedPos(), block);
    };
  }

  /** The only way to open the Node Screen (answers 140 and 188). */
  private static Optional<Component> open(Player player, BlockPos pos) {
    if (player instanceof ServerPlayer viewer) {
      SignalNodeBlock.openScreen(viewer, pos);
    }
    return Optional.empty();
  }

  /** A click copies, and Shift + click pastes; with no copy, nothing happens (127, 143, 195). */
  private static Optional<Component> copyOrPaste(
      RedweaveServer server, Player player, UseOnContext context, SignalNodeBlockEntity block) {
    Optional<NodeId> node = block.node();
    if (node.isEmpty()) {
      return Optional.of(Component.translatable(ControllerMessages.NO_NETWORK));
    }
    ItemStack stack = context.getItemInHand();
    if (!player.isSecondaryUseActive()) {
      return Optional.of(copy(server, stack, node.get()));
    }
    ConfiguratorCopy copy = stack.get(RedweaveDataComponents.NODE_SLOTS.get());
    if (copy == null) {
      return Optional.empty();
    }
    Component message =
        ConfiguratorPaste.paste(server, player, context.getLevel(), context.getClickedPos(), copy);
    refresh(server, context.getLevel(), context.getClickedPos());
    return Optional.of(message);
  }

  private static Component copy(RedweaveServer server, ItemStack stack, NodeId node) {
    Optional<NodeCopy> copy = server.copyNode().copy(node);
    Optional<Network> network = server.networkOfNode(node);
    if (copy.isEmpty() || network.isEmpty()) {
      return Component.translatable(ControllerMessages.NO_NETWORK);
    }
    stack.set(
        RedweaveDataComponents.NODE_SLOTS.get(),
        new ConfiguratorCopy(network.get().id(), copy.get()));
    return Component.translatable(ConfiguratorMessages.COPIED, copy.get().enabled());
  }

  /**
   * Picks the node up as a plain item, as breaking it (answers 161 to 165): only the owner of its
   * network or, without a network, who placed it. A node with no placer saved is refused (193).
   */
  private static Optional<Component> pickUp(
      RedweaveServer server,
      Player player,
      Level level,
      BlockPos pos,
      SignalNodeBlockEntity block) {
    Optional<NodeId> node = block.node();
    Optional<Network> network = node.flatMap(server::networkOfNode);
    if (network.isEmpty() && !block.wasPlacedBy(player)) {
      return Optional.of(Component.translatable(ConfiguratorMessages.NOT_PLACER));
    }
    if (network.isPresent()
        && server.breakNode().breakBy(playerOf(player), node.orElseThrow()) != Outcome.ACCEPTED) {
      return Optional.of(notOwner(server, node.orElseThrow()));
    }
    level.removeBlock(pos, KEEP_DROPS);
    give(player, new ItemStack(RedweaveItems.SIGNAL_NODE.get()));
    return Optional.of(Component.translatable(ConfiguratorMessages.PICKED_UP));
  }

  /** To the inventory, or to the ground at the feet of the player when it is full (answer 161). */
  private static void give(Player player, ItemStack item) {
    if (!player.getInventory().add(item)) {
      player.drop(item, NOT_THROWN);
    }
  }

  /** The new inputs are read at once, as when the node is placed, and the clients see the node. */
  private static void refresh(RedweaveServer server, Level level, BlockPos pos) {
    BlockState state = level.getBlockState(pos);
    NodeSignals.read(server, level, pos, state);
    NodeLook.refresh(server, level, pos);
    level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
  }

  private static Component notOwner(RedweaveServer server, NodeId node) {
    return Component.translatable(
        NodeScreenTexts.READ_ONLY, server.networkOfNode(node).map(Network::name).orElse(NO_NAME));
  }

  /** A network turned off takes no Open, Copy nor Paste; the Wrench still breaks (132, 163). */
  private static boolean isOff(Network network) {
    return network.state() == NetworkState.OFFLINE;
  }

  private static PlayerId playerOf(Player player) {
    return new PlayerId(player.getUUID());
  }
}
