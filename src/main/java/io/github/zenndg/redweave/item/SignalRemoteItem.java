package io.github.zenndg.redweave.item;

import io.github.zenndg.redweave.block.PartSpaceBlockEntity;
import io.github.zenndg.redweave.block.part.EmitterPart;
import io.github.zenndg.redweave.block.part.PartTargets;
import io.github.zenndg.redweave.menu.RemoteLinkMenu;
import io.github.zenndg.redweave.registry.RedweaveDataComponents;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
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
 * The Signal Remote, a tablet linked to an input slot or to a Signal Emitter: Shift + right click
 * on an emitter links it, anywhere else opens the screen of the link, and the click fires it, on
 * the server, for whoever holds it (answers 279, 291, 357, 779 and 790).
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

  /**
   * Shift + right click on a Signal Emitter links the tablet to it, without firing it; the block
   * does not take the click while Shift is held, so the emitter never turns (answer 790). Anywhere
   * else, the click goes on to {@link #use}.
   */
  @Override
  public InteractionResult useOn(UseOnContext context) {
    Player player = context.getPlayer();
    Optional<EmitterPart> emitter = emitterOf(context);
    if (player == null || !player.isSecondaryUseActive() || emitter.isEmpty()) {
      return InteractionResult.PASS;
    }
    if (player instanceof ServerPlayer holder) {
      EmitterRemotes.link(holder, context.getItemInHand(), context.getClickedPos(), emitter.get());
    }
    return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
  }

  /** The emitter where the click landed, as only the aimed part answers (answer 515). */
  private static Optional<EmitterPart> emitterOf(UseOnContext context) {
    if (!(context.getLevel().getBlockEntity(context.getClickedPos())
        instanceof PartSpaceBlockEntity space)) {
      return Optional.empty();
    }
    return PartTargets.nearest(context.getClickedPos(), space.parts(), context.getClickLocation())
        .filter(EmitterPart.class::isInstance)
        .map(EmitterPart.class::cast);
  }

  /**
   * The link, the mode, the state and the strongest rule (answer 366); linked to an emitter, what
   * the click does takes the place of the mode and the state (answer 789).
   */
  @Override
  public void appendHoverText(
      ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
    RemoteState state = RemoteFiring.stateOf(stack);
    tooltip.add(RemoteLinking.describe(stack));
    if (state.emitter().isPresent()) {
      EmitterLabel label = stack.get(RedweaveDataComponents.SIGNAL_REMOTE_EMITTER.get());
      boolean presses = label == null || label.presses();
      tooltip.add(Component.translatable(presses ? RemoteMessages.PRESSES : RemoteMessages.TURNS));
      tooltip.add(
          Component.translatable(RemoteMessages.HOW_TO_LINK).withStyle(ChatFormatting.GRAY));
      return;
    }
    tooltip.add(
        Component.translatable(
            RemoteMessages.MODE, Component.translatable(RemoteMessages.nameOf(state.mode()))));
    tooltip.add(
        RemoteMessages.state(state.on(), RemoteMessages.STATE_ON, RemoteMessages.STATE_OFF));
    tooltip.add(Component.translatable(RemoteMessages.RULE).withStyle(ChatFormatting.GRAY));
    tooltip.add(Component.translatable(RemoteMessages.HOW_TO_LINK).withStyle(ChatFormatting.GRAY));
  }
}
