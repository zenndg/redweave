package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.NodeSignals;
import io.github.marcelofabianov.redweave.block.PlayerNames;
import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.DefaultNames;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.Node;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.List;
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
 * The names the Node Screen shows, which do not fit the numbers of the data slots: the node, the
 * network, its color and its owner. Sent when the screen opens and again when they change (82). A
 * node with no network has the default name and no color (answers 142 and 208). The names the owner
 * gave the channels and the slots come along, so a slot shows `15 Main door` and its own name
 * (answers 221 and 227).
 */
public record NodeScreenNames(
    int menu, String node, String network, int color, String owner, Labels labels)
    implements CustomPacketPayload {
  private static final String ID = "node_screen_names";
  private static final String NONE = "";

  /** The color of a node with no network. */
  public static final int NO_COLOR = -1;

  public static final Type<NodeScreenNames> TYPE =
      new Type<>(ResourceLocation.fromNamespaceAndPath(RedweaveMod.MOD_ID, ID));

  public static final StreamCodec<RegistryFriendlyByteBuf, NodeScreenNames> STREAM_CODEC =
      StreamCodec.composite(
          ByteBufCodecs.VAR_INT,
          NodeScreenNames::menu,
          ByteBufCodecs.STRING_UTF8,
          NodeScreenNames::node,
          ByteBufCodecs.STRING_UTF8,
          NodeScreenNames::network,
          ByteBufCodecs.VAR_INT,
          NodeScreenNames::color,
          ByteBufCodecs.STRING_UTF8,
          NodeScreenNames::owner,
          Labels.STREAM_CODEC,
          NodeScreenNames::labels,
          NodeScreenNames::new);

  /**
   * The names of the 16 channels of the network and of the 16 slots of the node, in color order,
   * empty where there is none.
   */
  public record Labels(List<String> channels, List<String> slots) {
    static final Labels NONE = new Labels(List.of(), List.of());

    static final StreamCodec<RegistryFriendlyByteBuf, Labels> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()),
            Labels::channels,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()),
            Labels::slots,
            Labels::new);

    public Labels {
      channels = List.copyOf(channels);
      slots = List.copyOf(slots);
    }
  }

  @Override
  public Type<NodeScreenNames> type() {
    return TYPE;
  }

  static NodeScreenNames read(ServerPlayer player, BlockPos pos, int menu) {
    Optional<NodeId> id = NodeSignals.nodeAt(player.level(), pos);
    Optional<Network> network =
        id.flatMap(node -> RedweaveServer.running().flatMap(server -> server.networkOfNode(node)));
    String node =
        network
            .flatMap(found -> found.node(id.get()))
            .map(Node::displayName)
            .or(() -> id.map(found -> DefaultNames.of(found.value())))
            .orElse(NONE);
    return network
        .map(found ->
            new NodeScreenNames(
                menu,
                node,
                found.name(),
                found.color().ordinal(),
                PlayerNames.of(player.getServer(), found.owner().value()),
                labelsOf(found, id.get())))
        .orElse(new NodeScreenNames(menu, node, NONE, NO_COLOR, NONE, Labels.NONE));
  }

  /** The name of each channel and each slot, empty where the owner gave none (221 and 227). */
  private static Labels labelsOf(Network network, NodeId id) {
    List<String> channels =
        network.channels().stream().map(channel -> channel.name().orElse(NONE)).toList();
    List<String> slots =
        network.node(id).map(Node::slots).orElse(List.of()).stream()
            .map(slot -> slot.flatMap(ChannelSlot::name).orElse(NONE))
            .toList();
    return new Labels(channels, slots);
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
