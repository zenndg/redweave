package io.github.marcelofabianov.redweave.item;

import io.github.marcelofabianov.redweave.application.ModuleNetworkView;
import io.github.marcelofabianov.redweave.block.BlockLocations;
import io.github.marcelofabianov.redweave.block.ControllerMessages;
import io.github.marcelofabianov.redweave.block.Faces;
import io.github.marcelofabianov.redweave.block.ModuleBlockEntity;
import io.github.marcelofabianov.redweave.block.ModuleMessages;
import io.github.marcelofabianov.redweave.block.ModulePlateBlock;
import io.github.marcelofabianov.redweave.domain.module.SignalModule;
import io.github.marcelofabianov.redweave.domain.network.NodeState;
import io.github.marcelofabianov.redweave.menu.NodeScreenTexts;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

/**
 * What the Configurator does on a Signal Module on the server, each action giving its message, if
 * any (modulos-de-sinal.md). Only the owner turns, toggles and picks it up (answers 378 and 421).
 */
final class ModuleClicks {
  private static final boolean KEEP_DROPS = false;
  private static final boolean NOT_THROWN = false;
  private static final String NO_NAME = "";

  private ModuleClicks() {}

  static Optional<Component> onModule(
      RedweaveServer server,
      Player player,
      UseOnContext context,
      ModuleBlockEntity block,
      ConfiguratorMode mode) {
    Level level = context.getLevel();
    BlockPos pos = context.getClickedPos();
    return switch (mode) {
      case OPEN ->
          player.isSecondaryUseActive()
              ? owned(player, block, () -> turn(level, pos))
              : unlessOffline(server, context, block, () -> open(player, block));
      case COPY -> message(ModuleMessages.NOT_COPIED);
      case TOGGLE ->
          unlessOffline(server, context, block, () -> owned(player, block, block::toggle));
      case WRENCH -> owned(player, block, () -> pickUp(player, level, pos));
    };
  }

  /** Only the owner changes the module; anyone else is told so (answers 378 and 421). */
  private static Optional<Component> owned(
      Player player, ModuleBlockEntity block, Supplier<Optional<Component>> action) {
    return block.isOwner(player) ? action.get() : message(ModuleMessages.NOT_OWNER);
  }

  /** The action, unless the network of the module is turned off (answers 402 and 431). */
  private static Optional<Component> unlessOffline(
      RedweaveServer server,
      UseOnContext context,
      ModuleBlockEntity block,
      Supplier<Optional<Component>> action) {
    Optional<Component> offline = offline(server, context, block);
    return offline.isPresent() ? offline : action.get();
  }

  /**
   * A network turned off takes no Open and no Toggle; the turn and the Wrench still work, as they
   * do not touch it (answers 402 and 431).
   */
  private static Optional<Component> offline(
      RedweaveServer server, UseOnContext context, ModuleBlockEntity block) {
    Optional<SignalModule> module = block.module();
    if (module.isEmpty()) {
      return Optional.empty();
    }
    ModuleNetworkView view =
        server
            .modules()
            .show()
            .show(module.get(), BlockLocations.of(context.getLevel(), context.getClickedPos()));
    if (view.state() != NodeState.OFFLINE) {
      return Optional.empty();
    }
    return Optional.of(ControllerMessages.offline(view.name().orElse(NO_NAME)));
  }

  /** Anyone opens the screen; who is not the owner sees it read only (answers 378 and 393). */
  private static Optional<Component> open(Player player, ModuleBlockEntity block) {
    if (player instanceof ServerPlayer viewer) {
      block.openScreen(viewer);
    }
    return Optional.empty();
  }

  private static Optional<Component> turn(Level level, BlockPos pos) {
    Direction front = ModulePlateBlock.turn(level, pos, level.getBlockState(pos));
    Component edge = Component.translatable(NodeScreenTexts.face(Faces.of(front)));
    return Optional.of(Component.translatable(ModuleMessages.OUTPUT, edge));
  }

  /**
   * As a plain item of its own block, to the inventory or to the ground when it is full (answers
   * 161 and 394).
   */
  private static Optional<Component> pickUp(Player player, Level level, BlockPos pos) {
    ItemStack item = new ItemStack(level.getBlockState(pos).getBlock());
    level.removeBlock(pos, KEEP_DROPS);
    if (!player.getInventory().add(item)) {
      player.drop(item, NOT_THROWN);
    }
    return message(ModuleMessages.PICKED_UP);
  }

  private static Optional<Component> message(String key) {
    return Optional.of(Component.translatable(key));
  }
}
