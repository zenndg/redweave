package io.github.marcelofabianov.redweave.item;

import io.github.marcelofabianov.redweave.block.PartSpaceBlockEntity;
import io.github.marcelofabianov.redweave.block.part.CablePart;
import io.github.marcelofabianov.redweave.block.part.ModulePart;
import io.github.marcelofabianov.redweave.block.part.NodePart;
import io.github.marcelofabianov.redweave.block.part.Part;
import io.github.marcelofabianov.redweave.block.part.PartTargets;
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
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * The Configurator: opens, copies, pastes and picks up Signal Nodes, turns, toggles and picks up
 * Signal Modules, and picks up Signal Cables, in the mode chosen with Shift + scroll
 * (configurador.md). The server decides every action (rule 16).
 */
public final class ConfiguratorItem extends Item {
  public ConfiguratorItem(Properties properties) {
    super(properties);
  }

  public static ConfiguratorMode modeOf(ItemStack stack) {
    return stack.getOrDefault(
        RedweaveDataComponents.CONFIGURATOR_MODE.get(), ConfiguratorMode.FIRST);
  }

  /**
   * Whether a stack is a Configurator in the Open mode, which uses a block as an empty hand does:
   * the controller and the terminal (answers 232 and 343).
   */
  public static boolean opensBlocks(ItemStack stack) {
    return stack.getItem() instanceof ConfiguratorItem && modeOf(stack) == ConfiguratorMode.OPEN;
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

  /**
   * Only a Signal Node, a Signal Module or, with the Wrench, a Signal Cable takes the click; any
   * other block, as the controller, does nothing (answer 525).
   */
  @Override
  public InteractionResult useOn(UseOnContext context) {
    Level level = context.getLevel();
    BlockEntity clicked = level.getBlockEntity(context.getClickedPos());
    Optional<Part> part = partOf(clicked, context);
    if (!(clicked instanceof PartSpaceBlockEntity space)
        || part.isEmpty()
        || !takes(part.get(), modeOf(context.getItemInHand()))) {
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
    ConfiguratorMode mode = modeOf(context.getItemInHand());
    Optional<Component> message =
        switch (part.get()) {
          case NodePart node ->
              ConfiguratorActions.onNode(
                  server.get(), player, context, space.spotOf(node), node, mode);
          case CablePart cable -> Optional.of(CableClicks.pickUp(player, space, cable));
          default ->
              ModuleClicks.onModule(server.get(), player, space, (ModulePart) part.get(), mode);
        };
    message.ifPresent(found -> player.displayClientMessage(found, true));
    return InteractionResult.CONSUME;
  }

  private static boolean takes(Part part, ConfiguratorMode mode) {
    return part instanceof NodePart
        || part instanceof ModulePart
        || part instanceof CablePart && CableClicks.takes(mode);
  }

  /** The part where the click landed, as only the aimed part answers (answer 515). */
  private static Optional<Part> partOf(BlockEntity clicked, UseOnContext context) {
    if (!(clicked instanceof PartSpaceBlockEntity space)) {
      return Optional.empty();
    }
    return PartTargets.nearest(context.getClickedPos(), space.parts(), context.getClickLocation());
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
    ConfiguratorCopy copy = stack.get(RedweaveDataComponents.NODE_SLOTS.get());
    if (copy != null) {
      tooltip.add(
          Component.translatable(ConfiguratorMessages.COPY, copy.slots().configured())
              .withStyle(ChatFormatting.GRAY));
    }
    tooltip.add(hint(ConfiguratorMessages.hintOf(mode)));
    if (mode == ConfiguratorMode.TOGGLE) {
      tooltip.add(hint(ConfiguratorMessages.HINT_TOGGLE_MODULE));
    }
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
