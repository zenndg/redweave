package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.NodeSignals;
import io.github.marcelofabianov.redweave.block.PlayerNames;
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
 * The names the Node Screen shows, which do not fit the numbers of the data slots: the network and
 * its owner. Sent when the screen opens and again when they change (answer 82).
 */
public record NodeScreenNames(int menu, String network, String owner)
    implements CustomPacketPayload {
  private static final String ID = "node_screen_names";
  private static final String NONE = "";

  public static final Type<NodeScreenNames> TYPE =
      new Type<>(ResourceLocation.fromNamespaceAndPath(RedweaveMod.MOD_ID, ID));

  public static final StreamCodec<RegistryFriendlyByteBuf, NodeScreenNames> STREAM_CODEC =
      StreamCodec.composite(
          ByteBufCodecs.VAR_INT,
          NodeScreenNames::menu,
          ByteBufCodecs.STRING_UTF8,
          NodeScreenNames::network,
          ByteBufCodecs.STRING_UTF8,
          NodeScreenNames::owner,
          NodeScreenNames::new);

  @Override
  public Type<NodeScreenNames> type() {
    return TYPE;
  }

  static NodeScreenNames read(ServerPlayer player, BlockPos pos, int menu) {
    Optional<Network> network =
        NodeSignals.nodeAt(player.level(), pos)
            .flatMap(id -> RedweaveServer.running().flatMap(server -> server.networkOfNode(id)));
    return network
        .map(found ->
            new NodeScreenNames(
                menu, found.name(), PlayerNames.of(player.getServer(), found.owner().value())))
        .orElse(new NodeScreenNames(menu, NONE, NONE));
  }

  static void send(ServerPlayer player, NodeScreenNames names) {
    PacketDistributor.sendToPlayer(player, names);
  }

  /** On the client: only the open Node Screen of the same menu takes the names. */
  public static void receive(NodeScreenNames names, Player player) {
    if (player.containerMenu instanceof NodeMenu menu && menu.containerId == names.menu()) {
      menu.acceptNames(names);
    }
  }
}
