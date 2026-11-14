package io.github.marcelofabianov.redweave.item;

import io.github.marcelofabianov.redweave.application.ModuleNetworkView;
import io.github.marcelofabianov.redweave.block.BlockLocations;
import io.github.marcelofabianov.redweave.block.ControllerMessages;
import io.github.marcelofabianov.redweave.block.Faces;
import io.github.marcelofabianov.redweave.block.ModuleMessages;
import io.github.marcelofabianov.redweave.block.SignalTimerBlock;
import io.github.marcelofabianov.redweave.block.SignalTimerBlockEntity;
import io.github.marcelofabianov.redweave.domain.module.SignalModule;
import io.github.marcelofabianov.redweave.domain.module.SignalTimer;
import io.github.marcelofabianov.redweave.domain.network.NodeState;
import io.github.marcelofabianov.redweave.menu.NodeScreenTexts;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
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
      SignalTimerBlockEntity block,
      ConfiguratorMode mode) {
    Level level = context.getLevel();
    BlockPos pos = context.getClickedPos();
    return switch (mode) {
      case OPEN ->
          player.isSecondaryUseActive()
              ? owned(player, block, () -> turn(level, pos))
              : unlessOffline(server, context, block, Optional::empty);
      case COPY -> message(ModuleMessages.NOT_COPIED);
      case TOGGLE ->
          unlessOffline(server, context, block, () -> owned(player, block, () -> toggle(block)));
      case WRENCH -> owned(player, block, () -> pickUp(player, level, pos));
    };
  }

  /** Only the owner changes the module; anyone else is told so (answers 378 and 421). */
  private static Optional<Component> owned(
      Player player, SignalTimerBlockEntity block, Supplier<Optional<Component>> action) {
    return block.isOwner(player) ? action.get() : message(ModuleMessages.NOT_OWNER);
  }

  /** The action, unless the network of the module is turned off (answers 402 and 431). */
  private static Optional<Component> unlessOffline(
      RedweaveServer server,
      UseOnContext context,
      SignalTimerBlockEntity block,
      Supplier<Optional<Component>> action) {
    Optional<Component> offline = offline(server, context, block);
    return offline.isPresent() ? offline : action.get();
  }

  /**
   * A network turned off takes no Open and no Toggle; the turn and the Wrench still work, as they
   * do not touch it (answers 402 and 431).
   */
  private static Optional<Component> offline(
      RedweaveServer server, UseOnContext context, SignalTimerBlockEntity block) {
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

  private static Optional<Component> turn(Level level, BlockPos pos) {
    Direction front = SignalTimerBlock.turn(level, pos, level.getBlockState(pos));
    Component edge = Component.translatable(NodeScreenTexts.face(Faces.of(front)));
    return Optional.of(Component.translatable(ModuleMessages.OUTPUT, edge));
  }

  /** The Signal Timer starts or stops, and says so with its name (answers 395, 421 and 427). */
  private static Optional<Component> toggle(SignalTimerBlockEntity block) {
    Optional<SignalTimer> timer = block.toggle();
    if (timer.isEmpty() || block.module().isEmpty()) {
      return Optional.empty();
    }
    String key =
        timer.get().isRunning() ? ModuleMessages.TIMER_RUNNING : ModuleMessages.TIMER_STOPPED;
    return Optional.of(Component.translatable(key, block.module().get().displayName()));
  }

  /** As a plain item, to the inventory or to the ground when it is full (answers 161 and 394). */
  private static Optional<Component> pickUp(Player player, Level level, BlockPos pos) {
    level.removeBlock(pos, KEEP_DROPS);
    ItemStack item = new ItemStack(RedweaveItems.SIGNAL_TIMER.get());
    if (!player.getInventory().add(item)) {
      player.drop(item, NOT_THROWN);
    }
    return message(ModuleMessages.PICKED_UP);
  }

  private static Optional<Component> message(String key) {
    return Optional.of(Component.translatable(key));
  }
}
