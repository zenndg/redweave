package io.github.zenndg.redweave.network;

import io.github.zenndg.redweave.RedweaveMod;
import io.github.zenndg.redweave.block.NodeSignals;
import io.github.zenndg.redweave.block.part.PartSpot;
import io.github.zenndg.redweave.config.RedweaveConfig;
import io.github.zenndg.redweave.domain.network.NodeId;
import io.github.zenndg.redweave.domain.network.PlayerId;
import io.github.zenndg.redweave.menu.NodeMenu;
import io.github.zenndg.redweave.server.RedweaveServer;
import java.util.Optional;
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
public record NodeRenamed(PartSpot spot, String name) implements CustomPacketPayload {
  private static final String ID = "node_renamed";

  public static final Type<NodeRenamed> TYPE =
      new Type<>(ResourceLocation.fromNamespaceAndPath(RedweaveMod.MOD_ID, ID));

  public static final StreamCodec<RegistryFriendlyByteBuf, NodeRenamed> STREAM_CODEC =
      StreamCodec.composite(
          PartSpot.STREAM_CODEC,
          NodeRenamed::spot,
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
            && menu.spot().equals(renamed.spot())
            && menu.stillValid(player);
    Optional<RedweaveServer> server = RedweaveServer.running();
    Optional<NodeId> node = NodeSignals.nodeAt(player.level(), renamed.spot());
    if (!screenOfTheNode || server.isEmpty() || node.isEmpty()) {
      return;
    }
    server.get().renameNode().rename(new PlayerId(player.getUUID()), node.get(), renamed.name());
  }
}
