package io.github.marcelofabianov.redweave.item;

import io.github.marcelofabianov.redweave.block.NetworkControllerBlockEntity;
import io.github.marcelofabianov.redweave.block.SignalNodeBlockEntity;
import io.github.marcelofabianov.redweave.domain.network.NodeCopy;
import io.github.marcelofabianov.redweave.registry.RedweaveDataComponents;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * The Configurator: keeps a network from a controller, links nodes to it, and copies and pastes the
 * slots of a node (configurador.md). The server decides every action (rule 16).
 */
public final class ConfiguratorItem extends Item {
  public ConfiguratorItem(Properties properties) {
    super(properties);
  }

  /** Any other block refuses, so a Shift + click on it does not clear the copy as the air does. */
  @Override
  public InteractionResult useOn(UseOnContext context) {
    Level level = context.getLevel();
    BlockEntity block = level.getBlockEntity(context.getClickedPos());
    if (!(block instanceof NetworkControllerBlockEntity
        || block instanceof SignalNodeBlockEntity)) {
      return InteractionResult.FAIL;
    }
    if (level.isClientSide) {
      return InteractionResult.SUCCESS;
    }
    Player player = context.getPlayer();
    Optional<RedweaveServer> server = RedweaveServer.running();
    if (player == null || server.isEmpty()) {
      return InteractionResult.FAIL;
    }
    Component message =
        block instanceof NetworkControllerBlockEntity controller
            ? ConfiguratorActions.store(server.get(), player, context.getItemInHand(), controller)
            : ConfiguratorActions.onNode(server.get(), context, (SignalNodeBlockEntity) block);
    player.displayClientMessage(message, true);
    return InteractionResult.CONSUME;
  }

  /** Shift + click in the air clears the copy (answer 106). */
  @Override
  public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
    ItemStack stack = player.getItemInHand(hand);
    if (!player.isSecondaryUseActive() || !stack.has(RedweaveDataComponents.NODE_SLOTS.get())) {
      return InteractionResultHolder.pass(stack);
    }
    if (!level.isClientSide) {
      stack.remove(RedweaveDataComponents.NODE_SLOTS.get());
      player.displayClientMessage(Component.translatable(ConfiguratorMessages.CLEARED), true);
    }
    return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
  }

  @Override
  public void appendHoverText(
      ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
    String name = stack.get(RedweaveDataComponents.NETWORK_NAME.get());
    tooltip.add(
        (name == null
                ? Component.translatable(ConfiguratorMessages.NO_NETWORK_STORED)
                : Component.translatable(ConfiguratorMessages.NETWORK, name))
            .withStyle(ChatFormatting.GRAY));
    NodeCopy copy = stack.get(RedweaveDataComponents.NODE_SLOTS.get());
    if (copy != null) {
      tooltip.add(
          Component.translatable(ConfiguratorMessages.COPY, copy.slots().size())
              .withStyle(ChatFormatting.GRAY));
    }
    tooltip.add(hint(ConfiguratorMessages.HINT_STORE));
    tooltip.add(hint(ConfiguratorMessages.HINT_COPY));
  }

  private static Component hint(String key) {
    return Component.translatable(key).withStyle(ChatFormatting.DARK_GRAY);
  }
}
