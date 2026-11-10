package io.github.marcelofabianov.redweave.item;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.SignalNodeBlockEntity;
import io.github.marcelofabianov.redweave.domain.network.CopiedSlot;
import io.github.marcelofabianov.redweave.domain.network.Direction;
import io.github.marcelofabianov.redweave.domain.network.NodeCopy;
import io.github.marcelofabianov.redweave.menu.NodeScreenTexts;
import io.github.marcelofabianov.redweave.registry.RedweaveDataComponents;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

/**
 * The Label keeps the configuration of a node to use again: a click on a node applies it, with the
 * rules of pasting, and the Label is not spent (etiqueta.md, answers 110 and 118).
 */
public final class LabelItem extends Item {
  private static final String WRITTEN_ID = "written";

  /** 1 when the Label is written, for the model to show the written texture (answer 120). */
  public static final ResourceLocation WRITTEN =
      ResourceLocation.fromNamespaceAndPath(RedweaveMod.MOD_ID, WRITTEN_ID);

  public LabelItem(Properties properties) {
    super(properties);
  }

  /** Only a written Label on a node does something; an empty one does nothing. */
  @Override
  public InteractionResult useOn(UseOnContext context) {
    Level level = context.getLevel();
    NodeCopy copy = context.getItemInHand().get(RedweaveDataComponents.NODE_SLOTS.get());
    if (copy == null
        || !(level.getBlockEntity(context.getClickedPos()) instanceof SignalNodeBlockEntity node)) {
      return InteractionResult.PASS;
    }
    if (level.isClientSide) {
      return InteractionResult.SUCCESS;
    }
    Player player = context.getPlayer();
    Optional<RedweaveServer> server = RedweaveServer.running();
    if (player == null || server.isEmpty()) {
      return InteractionResult.FAIL;
    }
    player.displayClientMessage(ConfiguratorActions.apply(server.get(), context, node, copy), true);
    return InteractionResult.CONSUME;
  }

  /** One line per slot, in the color of its channel (answer 119). */
  @Override
  public void appendHoverText(
      ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
    NodeCopy copy = stack.get(RedweaveDataComponents.NODE_SLOTS.get());
    if (copy == null) {
      tooltip.add(Component.translatable(LabelMessages.EMPTY).withStyle(ChatFormatting.GRAY));
      tooltip.add(hint(LabelMessages.HINT_WRITE));
      return;
    }
    copy.slots().forEach(slot -> tooltip.add(line(slot)));
    tooltip.add(hint(LabelMessages.HINT_APPLY));
  }

  private static Component line(CopiedSlot copied) {
    String color = copied.slot().channel().key();
    String direction =
        copied.slot().direction() == Direction.INPUT
            ? NodeScreenTexts.INPUT
            : NodeScreenTexts.OUTPUT;
    int rgb = DyeColor.byName(color, DyeColor.WHITE).getTextColor();
    return Component.translatable(
            LabelMessages.SLOT,
            Component.translatable(NodeScreenTexts.color(color)),
            Component.translatable(direction),
            Component.translatable(NodeScreenTexts.face(copied.slot().face())))
        .withStyle(style -> style.withColor(TextColor.fromRgb(rgb)));
  }

  private static Component hint(String key) {
    return Component.translatable(key).withStyle(ChatFormatting.DARK_GRAY);
  }
}
