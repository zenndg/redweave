package io.github.zenndg.redweave.item;

import io.github.zenndg.redweave.block.BlockLocations;
import io.github.zenndg.redweave.block.Faces;
import io.github.zenndg.redweave.block.ModuleMessages;
import io.github.zenndg.redweave.block.PartSpaceBlockEntity;
import io.github.zenndg.redweave.block.part.EmitterPart;
import io.github.zenndg.redweave.config.NeoForgeRemoteEmitterRange;
import io.github.zenndg.redweave.config.NeoForgeRemotePulse;
import io.github.zenndg.redweave.domain.module.SignalModule;
import io.github.zenndg.redweave.domain.network.BlockLocation;
import io.github.zenndg.redweave.domain.network.Network;
import io.github.zenndg.redweave.domain.network.Range;
import io.github.zenndg.redweave.registry.RedweaveDataComponents;
import io.github.zenndg.redweave.server.RedweaveServer;
import java.util.Optional;
import java.util.function.IntSupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/**
 * A Signal Remote linked to a Signal Emitter, on the server: the owner of the emitter links it with
 * Shift + right click on it, and whoever holds it fires the emitter, as the click of its owner,
 * when its chunk is loaded and it is within `remote.emitter_range` (answers 778 to 791).
 */
public final class EmitterRemotes {
  private static final IntSupplier PULSE = new NeoForgeRemotePulse();
  private static final IntSupplier RANGE = new NeoForgeRemoteEmitterRange();
  private static final String NO_NAME = "";

  private EmitterRemotes() {}

  /** Only the owner of the emitter links, and the click never fires it (answers 782 and 790). */
  public static void link(ServerPlayer player, ItemStack stack, BlockPos pos, EmitterPart emitter) {
    if (!emitter.isOwner(player) || emitter.module().isEmpty()) {
      say(player, Component.translatable(ModuleMessages.NOT_OWNER));
      return;
    }
    EmitterLink link =
        new EmitterLink(
            BlockLocations.of(player.level(), pos),
            Faces.of(emitter.face()),
            emitter.module().get().id().value());
    stack.set(
        RedweaveDataComponents.SIGNAL_REMOTE.get(), RemoteFiring.stateOf(stack).linkedTo(link));
    stack.remove(RedweaveDataComponents.SIGNAL_REMOTE_LABEL.get());
    EmitterLabel label = labelOf(emitter);
    stack.set(RedweaveDataComponents.SIGNAL_REMOTE_EMITTER.get(), label);
    say(player, Component.translatable(RemoteMessages.LINKED_EMITTER, label.name()));
  }

  /**
   * Fires the linked emitter: out of reach keeps the link, a missing emitter loses it, and an
   * offline network refuses (answers 784, 785 and 787).
   */
  static void fire(ServerPlayer player, ItemStack stack, EmitterLink link) {
    Optional<ServerLevel> level = levelOf(player, link.place());
    if (level.isEmpty() || !reaches(player, link.place(), level.get())) {
      say(player, Component.translatable(RemoteMessages.OUT_OF_REACH, nameOf(stack)));
      return;
    }
    Optional<EmitterPart> emitter = emitterAt(level.get(), link);
    if (emitter.isEmpty()) {
      stack.set(RedweaveDataComponents.SIGNAL_REMOTE.get(), RemoteFiring.stateOf(stack).lost(true));
      say(player, Component.translatable(RemoteMessages.LINK_LOST));
      return;
    }
    Optional<Network> offline = offlineNetworkOf(emitter.get());
    if (offline.isPresent()) {
      say(player, Component.translatable(RemoteMessages.OFFLINE, offline.get().name()));
      return;
    }
    stack.set(RedweaveDataComponents.SIGNAL_REMOTE_EMITTER.get(), labelOf(emitter.get()));
    emitter.get().fireRemotely(PULSE.getAsInt()).ifPresent(message -> say(player, message));
  }

  /** The level of the emitter, if the server has it; never loads one (rule 6). */
  private static Optional<ServerLevel> levelOf(ServerPlayer player, BlockLocation place) {
    ResourceLocation dimension = ResourceLocation.tryParse(place.dimension());
    if (dimension == null) {
      return Optional.empty();
    }
    return Optional.ofNullable(
        player.server.getLevel(ResourceKey.create(Registries.DIMENSION, dimension)));
  }

  /** The same dimension, within the range, and a loaded chunk, which is never forced (rule 6). */
  private static boolean reaches(ServerPlayer player, BlockLocation place, ServerLevel level) {
    BlockLocation holder = BlockLocations.of(player.level(), player.blockPosition());
    return new Range(RANGE.getAsInt()).reaches(holder, place) && level.isLoaded(posOf(place));
  }

  /** The emitter on the face of the link, only if it is the same module (answer 785). */
  private static Optional<EmitterPart> emitterAt(ServerLevel level, EmitterLink link) {
    if (!(level.getBlockEntity(posOf(link.place())) instanceof PartSpaceBlockEntity space)) {
      return Optional.empty();
    }
    return space.parts().stream()
        .filter(EmitterPart.class::isInstance)
        .map(EmitterPart.class::cast)
        .filter(part -> Faces.of(part.face()) == link.face())
        .filter(part ->
            part.module().map(found -> found.id().value().equals(link.module())).orElse(false))
        .findFirst();
  }

  /** The network of the emitter when it is offline; an emitter with no network always fires. */
  private static Optional<Network> offlineNetworkOf(EmitterPart emitter) {
    return emitter
        .module()
        .flatMap(SignalModule::network)
        .flatMap(id -> RedweaveServer.running().flatMap(server -> server.network(id)))
        .filter(network -> !network.isOnline());
  }

  private static EmitterLabel labelOf(EmitterPart emitter) {
    String name = emitter.module().map(SignalModule::displayName).orElseThrow();
    return new EmitterLabel(name, emitter.remotePresses());
  }

  private static String nameOf(ItemStack stack) {
    EmitterLabel label = stack.get(RedweaveDataComponents.SIGNAL_REMOTE_EMITTER.get());
    return label == null ? NO_NAME : label.name();
  }

  private static BlockPos posOf(BlockLocation place) {
    return new BlockPos(place.x(), place.y(), place.z());
  }

  private static void say(ServerPlayer player, Component message) {
    player.displayClientMessage(message, true);
  }
}
