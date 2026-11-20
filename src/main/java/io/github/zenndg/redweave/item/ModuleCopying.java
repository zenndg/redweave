package io.github.zenndg.redweave.item;

import io.github.zenndg.redweave.block.part.ModulePart;
import io.github.zenndg.redweave.block.part.PartSettings;
import io.github.zenndg.redweave.domain.module.SignalModule;
import io.github.zenndg.redweave.domain.network.NetworkId;
import io.github.zenndg.redweave.domain.network.Outcome;
import io.github.zenndg.redweave.domain.network.PlayerId;
import io.github.zenndg.redweave.registry.RedweaveDataComponents;
import io.github.zenndg.redweave.registry.RedweaveItems;
import io.github.zenndg.redweave.server.RedweaveServer;
import java.util.Optional;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * The Copy mode on a Signal Module: a click copies its settings, Shift + click pastes them on one
 * of the same type, as on the node (answers 722 to 728). The caller checks the owner first.
 */
final class ModuleCopying {
  private ModuleCopying() {}

  static Optional<Component> copyOrPaste(
      RedweaveServer server, Player player, ItemStack stack, ModulePart part) {
    return player.isSecondaryUseActive()
        ? paste(server, player, stack, part)
        : Optional.of(copy(stack, part));
  }

  /** The copy replaces any other in the Configurator, of a node too (answer 729). */
  private static Component copy(ItemStack stack, ModulePart part) {
    stack.set(
        RedweaveDataComponents.MODULE_COPY.get(),
        new ModuleCopy(typeOf(part), networkOf(part), PartSettings.copy(part)));
    stack.remove(RedweaveDataComponents.NODE_SLOTS.get());
    return Component.translatable(ConfiguratorMessages.COPIED_ITEM, part.item().getDescription());
  }

  /**
   * Only on a module of the same type. The copy carries its network: the module joins it, as its
   * owner picking it on the screen, and keeps the channels; when the network is gone or the player
   * cannot pick it, the channels are taken off and the player is told (answers 728 and 732). With
   * no copy at all, nothing happens (answer 195).
   */
  private static Optional<Component> paste(
      RedweaveServer server, Player player, ItemStack stack, ModulePart part) {
    ModuleCopy copy = stack.get(RedweaveDataComponents.MODULE_COPY.get());
    if (copy == null) {
      return stack.has(RedweaveDataComponents.NODE_SLOTS.get())
          ? Optional.of(wrongType(RedweaveItems.SIGNAL_NODE.get().getDescription()))
          : Optional.empty();
    }
    if (!copy.type().equals(typeOf(part))) {
      return Optional.of(wrongType(nameOf(copy)));
    }
    boolean joined = copy.network().filter(id -> joins(server, player, part, id)).isPresent();
    boolean cleared = PartSettings.paste(part, copy.settings(), joined);
    return Optional.of(
        Component.translatable(
            cleared ? ConfiguratorMessages.CHANNELS_CLEARED : ConfiguratorMessages.PASTED_ITEM));
  }

  /** `This copy is from a <type>.`, for a paste on another type (answer 728). */
  static Component wrongType(Component type) {
    return Component.translatable(ConfiguratorMessages.WRONG_TYPE, type);
  }

  /** The name of the item the copy came from, in the language of the player. */
  static Component nameOf(ModuleCopy copy) {
    return Optional.ofNullable(ResourceLocation.tryParse(copy.type()))
        .flatMap(BuiltInRegistries.ITEM::getOptional)
        .map(Item::getDescription)
        .orElseGet(() -> Component.literal(copy.type()));
  }

  /**
   * Whether the module is in the network of the copy: already there, or now joined to it by the
   * same rule as the screen, only a network of the player that is online (answers 397 and 732).
   */
  private static boolean joins(
      RedweaveServer server, Player player, ModulePart part, NetworkId id) {
    if (networkOf(part).filter(id::equals).isPresent()) {
      return true;
    }
    return part.module()
        .map(module ->
            server.modules().chooseNetwork().choose(new PlayerId(player.getUUID()), module, id))
        .filter(Outcome.ACCEPTED::equals)
        .isPresent();
  }

  private static String typeOf(ModulePart part) {
    return BuiltInRegistries.ITEM.getKey(part.item()).toString();
  }

  private static Optional<NetworkId> networkOf(ModulePart part) {
    return part.module().flatMap(SignalModule::network);
  }
}
