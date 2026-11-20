package io.github.zenndg.redweave.item;

import io.github.zenndg.redweave.application.ToggleResult;
import io.github.zenndg.redweave.block.ControllerMessages;
import io.github.zenndg.redweave.block.NodeLook;
import io.github.zenndg.redweave.block.NodeSignals;
import io.github.zenndg.redweave.block.PartSpaceBlockEntity;
import io.github.zenndg.redweave.block.PartSpaces;
import io.github.zenndg.redweave.block.part.NodePart;
import io.github.zenndg.redweave.block.part.PartSpot;
import io.github.zenndg.redweave.domain.network.Network;
import io.github.zenndg.redweave.domain.network.NetworkState;
import io.github.zenndg.redweave.domain.network.NodeCopy;
import io.github.zenndg.redweave.domain.network.NodeId;
import io.github.zenndg.redweave.domain.network.Outcome;
import io.github.zenndg.redweave.domain.network.PlayerId;
import io.github.zenndg.redweave.menu.NodeMenu;
import io.github.zenndg.redweave.menu.NodeScreenTexts;
import io.github.zenndg.redweave.registry.RedweaveDataComponents;
import io.github.zenndg.redweave.registry.RedweaveItems;
import io.github.zenndg.redweave.server.RedweaveServer;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

/** What the Configurator does on a node on the server, each action giving its message, if any. */
final class ConfiguratorActions {
  private static final String NO_NAME = "";
  private static final boolean NOT_THROWN = false;

  private ConfiguratorActions() {}

  static Optional<Component> onNode(
      RedweaveServer server,
      Player player,
      UseOnContext context,
      PartSpot spot,
      NodePart block,
      ConfiguratorMode mode) {
    if (mode == ConfiguratorMode.ROTATE) {
      return Optional.of(Component.translatable(ConfiguratorMessages.ONLY_MODULES));
    }
    Optional<Network> offline =
        block.node().flatMap(server::networkOfNode).filter(ConfiguratorActions::isOff);
    if (offline.isPresent() && mode != ConfiguratorMode.WRENCH) {
      return Optional.of(ControllerMessages.offline(offline.get().name()));
    }
    return switch (mode) {
      case OPEN -> open(player, spot);
      case ROTATE -> Optional.empty();
      case COPY -> copyOrPaste(server, player, context, spot, block);
      case TOGGLE -> toggle(server, player, context, spot, block);
      case WRENCH -> pickUp(server, player, context.getLevel(), spot, block);
    };
  }

  /** The only way to open the Node Screen (answers 140 and 188). */
  private static Optional<Component> open(Player player, PartSpot spot) {
    if (player instanceof ServerPlayer viewer) {
      NodeMenu.open(viewer, spot);
    }
    return Optional.empty();
  }

  /**
   * A click enables every configured channel of the node, and Shift + click disables them, without
   * opening the Node Screen (answer 241).
   */
  private static Optional<Component> toggle(
      RedweaveServer server, Player player, UseOnContext context, PartSpot spot, NodePart block) {
    Optional<NodeId> node = block.node();
    if (node.isEmpty()) {
      return Optional.of(Component.translatable(ControllerMessages.NO_NETWORK));
    }
    boolean enable = !player.isSecondaryUseActive();
    ToggleResult result = server.toggleSlots().toggle(playerOf(player), node.get(), enable);
    refresh(server, context.getLevel(), spot);
    return Optional.of(
        switch (result.outcome()) {
          case ACCEPTED ->
              Component.translatable(
                  enable ? ConfiguratorMessages.ENABLED : ConfiguratorMessages.DISABLED,
                  result.channels());
          case NOT_OWNER -> notOwner(server, node.get());
          default -> Component.translatable(ControllerMessages.NO_NETWORK);
        });
  }

  /** A click copies, and Shift + click pastes; with no copy, nothing happens (127, 143, 195). */
  private static Optional<Component> copyOrPaste(
      RedweaveServer server, Player player, UseOnContext context, PartSpot spot, NodePart block) {
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
      return Optional.ofNullable(stack.get(RedweaveDataComponents.MODULE_COPY.get()))
          .map(module -> ModuleCopying.wrongType(ModuleCopying.nameOf(module)));
    }
    Component message = ConfiguratorPaste.paste(server, player, context.getLevel(), spot, copy);
    refresh(server, context.getLevel(), spot);
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
    stack.remove(RedweaveDataComponents.MODULE_COPY.get());
    return Component.translatable(ConfiguratorMessages.COPIED, copy.get().configured());
  }

  /**
   * Picks the node up as a plain item, as breaking it (answers 161 to 165): only the owner of its
   * network or, without a network, who placed it. A node with no placer saved is refused (193).
   */
  private static Optional<Component> pickUp(
      RedweaveServer server, Player player, Level level, PartSpot spot, NodePart block) {
    Optional<NodeId> node = block.node();
    Optional<Network> network = node.flatMap(server::networkOfNode);
    if (network.isEmpty() && !block.wasPlacedBy(player)) {
      return Optional.of(Component.translatable(ConfiguratorMessages.NOT_PLACER));
    }
    if (network.isPresent()
        && server.breakNode().breakBy(playerOf(player), node.orElseThrow()) != Outcome.ACCEPTED) {
      return Optional.of(notOwner(server, node.orElseThrow()));
    }
    PartSpaceBlockEntity.at(level, spot.pos()).ifPresent(space -> PartSpaces.pickUp(space, block));
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
  private static void refresh(RedweaveServer server, Level level, PartSpot spot) {
    NodeSignals.read(server, level, spot);
    NodeSignals.refreshEmission(server, level, spot);
    NodeLook.refresh(server, level, spot);
    PartSpaceBlockEntity.at(level, spot.pos()).ifPresent(PartSpaceBlockEntity::changed);
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
