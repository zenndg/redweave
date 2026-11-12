package io.github.marcelofabianov.redweave.network;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.application.NodeLabel;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * The labels over the nodes for the player who holds the Configurator: the ones that changed or
 * came into reach and the nodes that left it, or a clear of all when the player lets go (answers
 * 268, 269 and 274, and rule 18).
 */
public record NodeLabelsUpdate(boolean clearAll, List<Label> labels, List<UUID> gone)
    implements CustomPacketPayload {
  private static final String ID = "node_labels";
  private static final int NO_COLOR = -1;
  private static final String NO_NAME = "";

  public static final Type<NodeLabelsUpdate> TYPE =
      new Type<>(ResourceLocation.fromNamespaceAndPath(RedweaveMod.MOD_ID, ID));

  /** A label: the node and its block, its name, and the network with its color, or -1 for none. */
  public record Label(UUID node, BlockPos pos, String nodeName, String network, int color) {
    static final StreamCodec<RegistryFriendlyByteBuf, Label> STREAM_CODEC =
        StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            Label::node,
            BlockPos.STREAM_CODEC,
            Label::pos,
            ByteBufCodecs.STRING_UTF8,
            Label::nodeName,
            ByteBufCodecs.STRING_UTF8,
            Label::network,
            ByteBufCodecs.VAR_INT,
            Label::color,
            Label::new);

    public static Label of(UUID node, BlockPos pos, NodeLabel label) {
      return new Label(
          node,
          pos,
          label.node(),
          label.network().map(NodeLabel.NetworkPart::name).orElse(NO_NAME),
          label.network().map(part -> part.color().ordinal()).orElse(NO_COLOR));
    }

    public boolean hasNetwork() {
      return color != NO_COLOR;
    }
  }

  public static final StreamCodec<RegistryFriendlyByteBuf, NodeLabelsUpdate> STREAM_CODEC =
      StreamCodec.composite(
          ByteBufCodecs.BOOL,
          NodeLabelsUpdate::clearAll,
          Label.STREAM_CODEC.apply(ByteBufCodecs.list()),
          NodeLabelsUpdate::labels,
          UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs.list()),
          NodeLabelsUpdate::gone,
          NodeLabelsUpdate::new);

  public NodeLabelsUpdate {
    labels = List.copyOf(labels);
    gone = List.copyOf(gone);
  }

  /** The player let the Configurator go, so every label goes. */
  public static NodeLabelsUpdate clear() {
    return new NodeLabelsUpdate(true, List.of(), List.of());
  }

  @Override
  public Type<NodeLabelsUpdate> type() {
    return TYPE;
  }
}
