package io.github.marcelofabianov.redweave.item;

import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.menu.ChannelNames;
import io.github.marcelofabianov.redweave.registry.RedweaveDataComponents;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/**
 * The Signal Remote, a tablet linked to an input slot: the click fires it, on the server, for
 * whoever holds it (signal-remote.md and answers 279 and 357).
 */
public final class SignalRemoteItem extends Item {
  private static final String SQUARE = "■ ";

  public SignalRemoteItem(Properties properties) {
    super(properties);
  }

  @Override
  public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
    ItemStack stack = player.getItemInHand(hand);
    if (player instanceof ServerPlayer holder && !player.isShiftKeyDown()) {
      RemoteFiring.fire(holder, stack);
    } else if (player instanceof ServerPlayer holder) {
      holder.displayClientMessage(Component.translatable(RemoteMessages.HOW_TO_LINK), true);
    }
    return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
  }

  /** The link, the mode, the state and the strongest rule (answer 366). */
  @Override
  public void appendHoverText(
      ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
    RemoteState state = RemoteFiring.stateOf(stack);
    tooltip.add(linkOf(stack, state));
    tooltip.add(
        Component.translatable(
            RemoteMessages.MODE, Component.translatable(RemoteMessages.nameOf(state.mode()))));
    tooltip.add(
        Component.translatable(state.on() ? RemoteMessages.STATE_ON : RemoteMessages.STATE_OFF));
    tooltip.add(Component.translatable(RemoteMessages.RULE).withStyle(ChatFormatting.GRAY));
    tooltip.add(Component.translatable(RemoteMessages.HOW_TO_LINK).withStyle(ChatFormatting.GRAY));
  }

  private static Component linkOf(ItemStack stack, RemoteState state) {
    if (state.link().isEmpty()) {
      return Component.translatable(RemoteMessages.NO_LINK);
    }
    if (state.isLost()) {
      return Component.translatable(RemoteMessages.LINK_LOST);
    }
    RemoteLabel label = stack.get(RedweaveDataComponents.SIGNAL_REMOTE_LABEL.get());
    if (label == null) {
      return Component.translatable(RemoteMessages.NO_LINK);
    }
    return Component.translatable(
        RemoteMessages.LINK,
        squareOf(label.color()).append(label.network()),
        label.node(),
        ChannelNames.of(label.channel(), label.channelName()));
  }

  /** The square in the color of the network; the colors follow the order of the wool (224). */
  private static MutableComponent squareOf(ChannelColor color) {
    int rgb = DyeColor.byId(color.ordinal()).getTextColor();
    return Component.literal(SQUARE).withStyle(style -> style.withColor(rgb));
  }
}
