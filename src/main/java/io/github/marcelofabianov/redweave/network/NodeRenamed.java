package io.github.marcelofabianov.redweave.network;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.NodeSignals;
import io.github.marcelofabianov.redweave.config.RedweaveConfig;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.menu.NodeMenu;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

/**
 * The owner renamed the node on the Node Screen (answer 204). The server takes it only from the
 * open screen of that node, still within reach, and the domain checks the owner and the length of
 * node.name_length (rules 16 and 17). A text longer than the ceiling of the config never decodes.
 */
public record NodeRenamed(BlockPos pos, String name) implements CustomPacketPayload {
  private static final String ID = "node_renamed";

  public static final Type<NodeRenamed> TYPE =
      new Type<>(ResourceLocation.fromNamespaceAndPath(RedweaveMod.MOD_ID, ID));

  public static final StreamCodec<RegistryFriendlyByteBuf, NodeRenamed> STREAM_CODEC =
      StreamCodec.composite(
          BlockPos.STREAM_CODEC,
          NodeRenamed::pos,
          ByteBufCodecs.stringUtf8(RedweaveConfig.MAX_NAME),
          NodeRenamed::name,
          NodeRenamed::new);

  @Override
  public Type<NodeRenamed> type() {
    return TYPE;
  }

  /** On the server thread (the default of the registrar). */
  static void receive(NodeRenamed renamed, Player player) {
    boolean screenOfTheNode =
        player.containerMenu instanceof NodeMenu menu
            && menu.pos().equals(renamed.pos())
            && menu.stillValid(player);
    Optional<RedweaveServer> server = RedweaveServer.running();
    Optional<NodeId> node = NodeSignals.nodeAt(player.level(), renamed.pos());
    if (!screenOfTheNode || server.isEmpty() || node.isEmpty()) {
      return;
    }
    server.get().renameNode().rename(new PlayerId(player.getUUID()), node.get(), renamed.name());
  }
}
