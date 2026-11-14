package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.SignalTimerBlockEntity;
import io.github.marcelofabianov.redweave.domain.module.SignalModule;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * The names the Signal Timer Screen shows, built on the server: the name of the module, or the
 * first 8 characters of its ID, and the name, color and channels of its network (answers 385, 404
 * and 427). Sent when the screen opens and again when they change.
 */
public record TimerScreenNames(
    int menu, String module, String network, int color, NodeScreenNames.Labels labels)
    implements CustomPacketPayload {
  private static final String ID = "timer_screen_names";
  private static final String NONE = "";

  public static final Type<TimerScreenNames> TYPE =
      new Type<>(ResourceLocation.fromNamespaceAndPath(RedweaveMod.MOD_ID, ID));

  public static final StreamCodec<RegistryFriendlyByteBuf, TimerScreenNames> STREAM_CODEC =
      StreamCodec.composite(
          ByteBufCodecs.VAR_INT,
          TimerScreenNames::menu,
          ByteBufCodecs.STRING_UTF8,
          TimerScreenNames::module,
          ByteBufCodecs.STRING_UTF8,
          TimerScreenNames::network,
          ByteBufCodecs.VAR_INT,
          TimerScreenNames::color,
          NodeScreenNames.Labels.STREAM_CODEC,
          TimerScreenNames::labels,
          TimerScreenNames::new);

  @Override
  public Type<TimerScreenNames> type() {
    return TYPE;
  }

  static TimerScreenNames read(ServerPlayer player, BlockPos pos, int menu) {
    Optional<SignalModule> module =
        player.level().getBlockEntity(pos) instanceof SignalTimerBlockEntity block
            ? block.module()
            : Optional.empty();
    String name = module.map(SignalModule::displayName).orElse(NONE);
    Optional<Network> network =
        module
            .flatMap(SignalModule::network)
            .flatMap(id -> RedweaveServer.running().flatMap(server -> server.network(id)));
    return network
        .map(found ->
            new TimerScreenNames(
                menu, name, found.name(), found.color().ordinal(), NodeScreenNames.labelsOf(found)))
        .orElse(
            new TimerScreenNames(
                menu, name, NONE, NodeScreenNames.NO_COLOR, NodeScreenNames.Labels.NONE));
  }

  static void send(ServerPlayer player, TimerScreenNames names) {
    PacketDistributor.sendToPlayer(player, names);
  }

  /** On the client: only the open Signal Timer Screen of the same menu takes the names. */
  public static void receive(TimerScreenNames names, Player player) {
    if (player.containerMenu instanceof TimerMenu menu && menu.containerId == names.menu()) {
      menu.acceptNames(names);
    }
  }
}
