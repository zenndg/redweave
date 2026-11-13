package io.github.marcelofabianov.redweave.item;

import io.github.marcelofabianov.redweave.menu.RemoteLinkMenu;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/**
 * The Signal Remote, a tablet linked to an input slot: Shift + right click opens the screen of the
 * link, and the click fires it, on the server, for whoever holds it (answers 279, 291 and 357).
 */
public final class SignalRemoteItem extends Item {
  public SignalRemoteItem(Properties properties) {
    super(properties);
  }

  @Override
  public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
    ItemStack stack = player.getItemInHand(hand);
    if (player instanceof ServerPlayer holder && player.isShiftKeyDown()) {
      RemoteLinkMenu.open(holder, hand, getDescription());
    } else if (player instanceof ServerPlayer holder) {
      RemoteFiring.fire(holder, stack);
    }
    return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
  }

  /** The link, the mode, the state and the strongest rule (answer 366). */
  @Override
  public void appendHoverText(
      ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
    RemoteState state = RemoteFiring.stateOf(stack);
    tooltip.add(RemoteLinking.describe(stack));
    tooltip.add(
        Component.translatable(
            RemoteMessages.MODE, Component.translatable(RemoteMessages.nameOf(state.mode()))));
    tooltip.add(
        Component.translatable(state.on() ? RemoteMessages.STATE_ON : RemoteMessages.STATE_OFF));
    tooltip.add(Component.translatable(RemoteMessages.RULE).withStyle(ChatFormatting.GRAY));
    tooltip.add(Component.translatable(RemoteMessages.HOW_TO_LINK).withStyle(ChatFormatting.GRAY));
  }
}
