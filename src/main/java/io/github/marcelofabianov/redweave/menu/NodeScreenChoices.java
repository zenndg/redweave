package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.application.NetworkChoice;
import io.github.marcelofabianov.redweave.block.NodeSignals;
import io.github.marcelofabianov.redweave.block.part.NodePart;
import io.github.marcelofabianov.redweave.block.part.PartSpot;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * The network choice of the Node Screen and of the module screens: the rows the client draws, in
 * the order the server keeps. The client only sends back a position, so the server never trusts a
 * network ID from it (rules 16 and 17, answers 157, 172 and 202).
 */
public record NodeScreenChoices(int menu, List<Row> rows) implements CustomPacketPayload {
  private static final String ID = "node_screen_choices";

  /** The most networks a player may have, the ceiling of network.max_per_player (answer 145). */
  private static final int MAX_ROWS = 100;

  public static final Type<NodeScreenChoices> TYPE =
      new Type<>(ResourceLocation.fromNamespaceAndPath(RedweaveMod.MOD_ID, ID));

  /** One row: the color, the name and the node count, and whether it was used last (202). */
  public record Row(String name, int color, int nodes, boolean lastUsed) {
    static final StreamCodec<RegistryFriendlyByteBuf, Row> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            Row::name,
            ByteBufCodecs.VAR_INT,
            Row::color,
            ByteBufCodecs.VAR_INT,
            Row::nodes,
            ByteBufCodecs.BOOL,
            Row::lastUsed,
            Row::new);

    static Row of(NetworkChoice choice) {
      return new Row(choice.name(), choice.color().ordinal(), choice.nodes(), choice.lastUsed());
    }
  }

  public static final StreamCodec<RegistryFriendlyByteBuf, NodeScreenChoices> STREAM_CODEC =
      StreamCodec.composite(
          ByteBufCodecs.VAR_INT,
          NodeScreenChoices::menu,
          Row.STREAM_CODEC.apply(ByteBufCodecs.list(MAX_ROWS)),
          NodeScreenChoices::rows,
          NodeScreenChoices::new);

  public NodeScreenChoices {
    rows = List.copyOf(rows);
  }

  @Override
  public Type<NodeScreenChoices> type() {
    return TYPE;
  }

  /**
   * Who may pick the network of the node: with no network, only whoever placed it (answer 172);
   * with one, only its owner, and the move checks the new one too (answer 141).
   */
  static boolean mayChoose(Player player, PartSpot spot) {
    Optional<NodeId> node = NodeSignals.nodeAt(player.level(), spot);
    Optional<RedweaveServer> server = RedweaveServer.running();
    if (node.isEmpty() || server.isEmpty()) {
      return false;
    }
    Optional<Network> network = server.get().networkOfNode(node.get());
    if (network.isPresent()) {
      return network.get().isOwner(new PlayerId(player.getUUID()));
    }
    return NodePart.at(player.level(), spot).map(part -> part.wasPlacedBy(player)).orElse(false);
  }

  /** The networks the player may pick now, or none when they may not pick. */
  static List<NetworkChoice> read(ServerPlayer player, PartSpot spot) {
    if (!mayChoose(player, spot)) {
      return List.of();
    }
    return RedweaveServer.running()
        .map(server -> server.networkChoices().of(new PlayerId(player.getUUID())))
        .orElse(List.of());
  }

  static void send(ServerPlayer player, int menu, List<NetworkChoice> choices) {
    PacketDistributor.sendToPlayer(
        player, new NodeScreenChoices(menu, choices.stream().map(Row::of).toList()));
  }

  /** On the client: only the open screen of the same menu takes the rows. */
  public static void receive(NodeScreenChoices choices, Player player) {
    if (player.containerMenu instanceof NetworkChoiceMenu menu
        && player.containerMenu.containerId == choices.menu()) {
      menu.acceptChoices(choices.rows());
    }
  }
}
