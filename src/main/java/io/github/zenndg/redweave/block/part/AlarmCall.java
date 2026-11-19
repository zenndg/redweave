package io.github.zenndg.redweave.block.part;

import io.github.zenndg.redweave.block.ModuleMessages;
import io.github.zenndg.redweave.config.NeoForgeAlarmLimits;
import io.github.zenndg.redweave.domain.module.AlarmListeners;
import io.github.zenndg.redweave.domain.network.NetworkId;
import io.github.zenndg.redweave.item.RemoteLink;
import io.github.zenndg.redweave.item.RemoteState;
import io.github.zenndg.redweave.registry.RedweaveDataComponents;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/**
 * One ring of a Signal Alarm, on the server only: the `pling` of the note block and `Alarm <name>:
 * Ringing...` in red on the action bar, each sent only to its listeners within its reach, never to
 * the whole world (answers 644 to 647). A spectator never hears it.
 */
public final class AlarmCall {
  private static final float VOLUME = 1.0F;
  private static final float PITCH = 1.0F;
  private static final float OCTAVE_UP = 2.0F;

  private AlarmCall() {}

  static void ring(ServerLevel level, BlockPos pos, AlarmPart part) {
    Vec3 center = Vec3.atCenterOf(pos);
    Component text =
        Component.translatable(ModuleMessages.ALARM_RING, part.displayName())
            .withStyle(ChatFormatting.RED);
    for (ServerPlayer player : listeners(level, pos, part)) {
      pling(level, player, center, PITCH);
      pling(level, player, center, OCTAVE_UP);
      player.displayClientMessage(text, true);
    }
  }

  /** One `pling`, sent only to this listener; two at once, an octave apart, ring fuller. */
  private static void pling(ServerLevel level, ServerPlayer player, Vec3 center, float pitch) {
    player.connection.send(
        new ClientboundSoundPacket(
            SoundEvents.NOTE_BLOCK_PLING,
            SoundSource.BLOCKS,
            center.x,
            center.y,
            center.z,
            VOLUME,
            pitch,
            level.getRandom().nextLong()));
  }

  /** The players who hear the alarm: within its reach, and of the kind its owner chose (645). */
  public static List<ServerPlayer> listeners(ServerLevel level, BlockPos pos, AlarmPart part) {
    double reach = NeoForgeAlarmLimits.range();
    Vec3 center = Vec3.atCenterOf(pos);
    AlarmListeners kind = part.alarm().settings().listeners();
    return level.players().stream()
        .filter(player -> !player.isSpectator())
        .filter(player -> player.position().closerThan(center, reach))
        .filter(player -> hears(player, part, kind))
        .toList();
  }

  private static boolean hears(ServerPlayer player, AlarmPart part, AlarmListeners kind) {
    return switch (kind) {
      case OWNER -> part.isOwner(player);
      case NETWORK -> part.isOwner(player) || usesNetwork(player, part.network());
      case RANGE -> true;
    };
  }

  /** Whether the player carries a Signal Remote linked to the network of the alarm. */
  private static boolean usesNetwork(ServerPlayer player, Optional<NetworkId> network) {
    if (network.isEmpty()) {
      return false;
    }
    return player.getInventory().items.stream()
        .map(AlarmCall::linkOf)
        .flatMap(Optional::stream)
        .anyMatch(link -> link.network().equals(network.get().value()));
  }

  private static Optional<RemoteLink> linkOf(ItemStack stack) {
    RemoteState state = stack.get(RedweaveDataComponents.SIGNAL_REMOTE.get());
    return state == null ? Optional.empty() : state.link();
  }
}
