package io.github.zenndg.redweave.item;

import io.github.zenndg.redweave.application.RemoteSighting;
import io.github.zenndg.redweave.application.RemoteView;
import io.github.zenndg.redweave.block.BlockLocations;
import io.github.zenndg.redweave.domain.network.BlockLocation;
import io.github.zenndg.redweave.domain.network.NetworkId;
import io.github.zenndg.redweave.domain.network.RemoteInput;
import io.github.zenndg.redweave.registry.RedweaveDataComponents;
import io.github.zenndg.redweave.server.RedweaveServer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

/**
 * Each tick, the Signal Remotes in the inventories of the players in the worlds: each one learns
 * whether its link is lost and the names of its link, and those that are on are sighted where their
 * player stands. A tablet anywhere else, as in a chest, is simply not seen (answers 287, 355 and
 * 356).
 */
public final class RemoteTicks {
  private RemoteTicks() {}

  public static List<RemoteSighting> sightings(MinecraftServer minecraft, RedweaveServer server) {
    List<RemoteSighting> sightings = new ArrayList<>();
    for (ServerLevel level : minecraft.getAllLevels()) {
      for (ServerPlayer player : level.players()) {
        sightings.addAll(sightingsOf(server, player));
      }
    }
    return sightings;
  }

  private static List<RemoteSighting> sightingsOf(RedweaveServer server, ServerPlayer player) {
    BlockLocation holder = BlockLocations.of(player.level(), player.blockPosition());
    long now = player.serverLevel().getGameTime();
    List<RemoteSighting> sightings = new ArrayList<>();
    for (ItemStack stack : stacksOf(player.getInventory())) {
      if (stack.getItem() instanceof SignalRemoteItem) {
        refresh(server, stack, now, holder).ifPresent(sightings::add);
      }
    }
    return sightings;
  }

  /** The main inventory, the armor and the off hand, all of what a player carries. */
  private static List<ItemStack> stacksOf(Inventory inventory) {
    List<ItemStack> stacks = new ArrayList<>(inventory.items);
    stacks.addAll(inventory.armor);
    stacks.addAll(inventory.offhand);
    return stacks;
  }

  private static Optional<RemoteSighting> refresh(
      RedweaveServer server, ItemStack stack, long now, BlockLocation holder) {
    RemoteState state = RemoteFiring.stateOf(stack);
    Optional<RemoteInput> tablet = RemoteFiring.inputOf(state);
    if (tablet.isEmpty()) {
      return Optional.empty();
    }
    NetworkId network = new NetworkId(state.link().orElseThrow().network());
    Optional<RemoteView> view = server.remotes().show().show(network, tablet.get());
    RemoteState seen = state.lost(view.isEmpty());
    if (!seen.equals(state)) {
      stack.set(RedweaveDataComponents.SIGNAL_REMOTE.get(), seen);
    }
    view.map(RemoteLabel::of).ifPresent(label -> label(stack, label));
    return seen.counts(now)
        ? Optional.of(new RemoteSighting(network, tablet.get(), holder))
        : Optional.empty();
  }

  /** Writes the names only when they change, so the item is not synchronized every tick. */
  private static void label(ItemStack stack, RemoteLabel label) {
    if (!Objects.equals(stack.get(RedweaveDataComponents.SIGNAL_REMOTE_LABEL.get()), label)) {
      stack.set(RedweaveDataComponents.SIGNAL_REMOTE_LABEL.get(), label);
    }
  }
}
