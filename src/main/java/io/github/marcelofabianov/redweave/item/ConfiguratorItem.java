package io.github.marcelofabianov.redweave.item;

import io.github.marcelofabianov.redweave.block.SignalNodeBlockEntity;
import io.github.marcelofabianov.redweave.domain.network.NodeCopy;
import io.github.marcelofabianov.redweave.registry.RedweaveDataComponents;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
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

/**
 * The Configurator: opens, copies, pastes and picks up Signal Nodes, in the mode chosen with Shift
 * + scroll (configurador.md). The server decides every action (rule 16).
 */
public final class ConfiguratorItem extends Item {
  public ConfiguratorItem(Properties properties) {
    super(properties);
  }

  public static ConfiguratorMode modeOf(ItemStack stack) {
    return stack.getOrDefault(
        RedweaveDataComponents.CONFIGURATOR_MODE.get(), ConfiguratorMode.FIRST);
  }

  /** On the server, from the Shift + scroll of the client: only a Configurator in the main hand. */
  public static void switchMode(Player player, double delta) {
    ItemStack stack = player.getMainHandItem();
    if (!stack.is(RedweaveItems.CONFIGURATOR.get())) {
      return;
    }
    ConfiguratorMode mode = modeOf(stack).scrolled(delta);
    stack.set(RedweaveDataComponents.CONFIGURATOR_MODE.get(), mode);
    player.displayClientMessage(
        Component.translatable(ConfiguratorMessages.MODE, named(mode)), true);
  }

  /** Only a Signal Node takes the click; any other block, as the controller, does nothing. */
  @Override
  public InteractionResult useOn(UseOnContext context) {
    Level level = context.getLevel();
    if (!(level.getBlockEntity(context.getClickedPos()) instanceof SignalNodeBlockEntity block)) {
      return InteractionResult.FAIL;
    }
    if (level.isClientSide) {
      return InteractionResult.SUCCESS;
    }
    Optional<RedweaveServer> server = RedweaveServer.running();
    Player player = context.getPlayer();
    if (player == null || server.isEmpty()) {
      return InteractionResult.FAIL;
    }
    ConfiguratorActions.onNode(
            server.get(), player, context, block, modeOf(context.getItemInHand()))
        .ifPresent(message -> player.displayClientMessage(message, true));
    return InteractionResult.CONSUME;
  }

  /** Shift + click in the air clears the copy, in any mode (answers 106 and 128). */
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
    ConfiguratorMode mode = modeOf(stack);
    tooltip.add(Component.translatable(ConfiguratorMessages.MODE, named(mode)));
    NodeCopy copy = stack.get(RedweaveDataComponents.NODE_SLOTS.get());
    if (copy != null) {
      tooltip.add(
          Component.translatable(ConfiguratorMessages.COPY, copy.enabled())
              .withStyle(ChatFormatting.GRAY));
    }
    tooltip.add(hint(ConfiguratorMessages.hintOf(mode)));
    tooltip.add(hint(ConfiguratorMessages.HINT_SCROLL));
  }

  /** The name of the mode, always written, in the color of the mode (answer 168). */
  private static Component named(ConfiguratorMode mode) {
    return Component.translatable(ConfiguratorMessages.nameOf(mode)).withColor(mode.color());
  }

  private static Component hint(String key) {
    return Component.translatable(key).withStyle(ChatFormatting.DARK_GRAY);
  }
}
