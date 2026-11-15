package io.github.marcelofabianov.redweave.item;

import io.github.marcelofabianov.redweave.application.ModuleNetworkView;
import io.github.marcelofabianov.redweave.block.BlockLocations;
import io.github.marcelofabianov.redweave.block.ControllerMessages;
import io.github.marcelofabianov.redweave.block.Faces;
import io.github.marcelofabianov.redweave.block.ModuleMessages;
import io.github.marcelofabianov.redweave.block.PartSpaceBlockEntity;
import io.github.marcelofabianov.redweave.block.PartSpaces;
import io.github.marcelofabianov.redweave.block.part.ModulePart;
import io.github.marcelofabianov.redweave.domain.module.SignalModule;
import io.github.marcelofabianov.redweave.domain.network.NodeState;
import io.github.marcelofabianov.redweave.menu.NodeScreenTexts;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * What the Configurator does on a Signal Module on the server, each action giving its message, if
 * any (modulos-de-sinal.md). Only the owner turns, toggles and picks it up (answers 378 and 421).
 */
final class ModuleClicks {
  private static final boolean NOT_THROWN = false;
  private static final String NO_NAME = "";

  private ModuleClicks() {}

  static Optional<Component> onModule(
      RedweaveServer server,
      Player player,
      PartSpaceBlockEntity space,
      ModulePart part,
      ConfiguratorMode mode) {
    return switch (mode) {
      case OPEN ->
          player.isSecondaryUseActive()
              ? owned(player, part, () -> turn(part))
              : unlessOffline(server, space, part, () -> open(player, part));
      case COPY -> message(ModuleMessages.NOT_COPIED);
      case TOGGLE ->
          part.togglesOffline()
              ? owned(player, part, part::toggle)
              : unlessOffline(server, space, part, () -> owned(player, part, part::toggle));
      case WRENCH -> owned(player, part, () -> pickUp(player, space, part));
    };
  }

  /** Only the owner changes the module; anyone else is told so (answers 378 and 421). */
  private static Optional<Component> owned(
      Player player, ModulePart part, Supplier<Optional<Component>> action) {
    return part.isOwner(player) ? action.get() : message(ModuleMessages.NOT_OWNER);
  }

  /** The action, unless the network of the module is turned off (answers 402 and 431). */
  private static Optional<Component> unlessOffline(
      RedweaveServer server,
      PartSpaceBlockEntity space,
      ModulePart part,
      Supplier<Optional<Component>> action) {
    Optional<Component> offline = offline(server, space, part);
    return offline.isPresent() ? offline : action.get();
  }

  /**
   * A network turned off takes no Open and no Toggle; the turn and the Wrench still work, as they
   * do not touch it (answers 402 and 431).
   */
  private static Optional<Component> offline(
      RedweaveServer server, PartSpaceBlockEntity space, ModulePart part) {
    Optional<SignalModule> module = part.module();
    if (module.isEmpty()) {
      return Optional.empty();
    }
    ModuleNetworkView view =
        server
            .modules()
            .show()
            .show(module.get(), BlockLocations.of(space.getLevel(), space.getBlockPos()));
    if (view.state() != NodeState.OFFLINE) {
      return Optional.empty();
    }
    return Optional.of(ControllerMessages.offline(view.name().orElse(NO_NAME)));
  }

  /** Anyone opens the screen; who is not the owner sees it read only (answers 378 and 393). */
  private static Optional<Component> open(Player player, ModulePart part) {
    if (player instanceof ServerPlayer viewer) {
      part.openScreen(viewer);
    }
    return Optional.empty();
  }

  private static Optional<Component> turn(ModulePart part) {
    Direction front = part.turn();
    Component edge = Component.translatable(NodeScreenTexts.face(Faces.of(front)));
    return Optional.of(Component.translatable(ModuleMessages.OUTPUT, edge));
  }

  /**
   * As a plain item of its own, to the inventory or to the ground when it is full; the other parts
   * of the space stay (answers 161, 394 and 515).
   */
  private static Optional<Component> pickUp(
      Player player, PartSpaceBlockEntity space, ModulePart part) {
    ItemStack item = new ItemStack(part.item());
    PartSpaces.pickUp(space, part);
    if (!player.getInventory().add(item)) {
      player.drop(item, NOT_THROWN);
    }
    return message(ModuleMessages.PICKED_UP);
  }

  private static Optional<Component> message(String key) {
    return Optional.of(Component.translatable(key));
  }
}
