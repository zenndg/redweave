package io.github.marcelofabianov.redweave.network;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.application.NodeLabel;
import java.util.List;
import java.util.Optional;
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
 * 268 to 270, and rule 18). Numbers stand for the color, the direction and the state.
 */
public record NodeLabelsUpdate(boolean clearAll, List<Label> labels, List<UUID> gone)
    implements CustomPacketPayload {
  private static final String ID = "node_labels";
  private static final int NO_STATE = -1;
  private static final String NO_NAME = "";

  public static final Type<NodeLabelsUpdate> TYPE =
      new Type<>(ResourceLocation.fromNamespaceAndPath(RedweaveMod.MOD_ID, ID));

  /** An enabled channel of a label; an empty name shows the color. */
  public record ChannelLine(int color, String name, int direction, int signal) {
    static final StreamCodec<RegistryFriendlyByteBuf, ChannelLine> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            ChannelLine::color,
            ByteBufCodecs.STRING_UTF8,
            ChannelLine::name,
            ByteBufCodecs.VAR_INT,
            ChannelLine::direction,
            ByteBufCodecs.VAR_INT,
            ChannelLine::signal,
            ChannelLine::new);

    static ChannelLine of(NodeLabel.Channel channel) {
      return new ChannelLine(
          channel.color().ordinal(),
          channel.name().orElse(NO_NAME),
          channel.direction().ordinal(),
          channel.signal().strength());
    }
  }

  /** A label: the node and its block, the names, the state of the network or -1, and channels. */
  public record Label(
      UUID node,
      BlockPos pos,
      String nodeName,
      String network,
      int state,
      List<ChannelLine> channels,
      int more) {
    /** Seven fields, one more than a composite codec takes, so it reads and writes by hand. */
    static final StreamCodec<RegistryFriendlyByteBuf, Label> STREAM_CODEC =
        StreamCodec.of(Label::write, Label::read);

    private static final StreamCodec<RegistryFriendlyByteBuf, List<ChannelLine>> CHANNELS =
        ChannelLine.STREAM_CODEC.apply(ByteBufCodecs.list());

    private static void write(RegistryFriendlyByteBuf buf, Label label) {
      UUIDUtil.STREAM_CODEC.encode(buf, label.node());
      BlockPos.STREAM_CODEC.encode(buf, label.pos());
      ByteBufCodecs.STRING_UTF8.encode(buf, label.nodeName());
      ByteBufCodecs.STRING_UTF8.encode(buf, label.network());
      ByteBufCodecs.VAR_INT.encode(buf, label.state());
      CHANNELS.encode(buf, label.channels());
      ByteBufCodecs.VAR_INT.encode(buf, label.more());
    }

    private static Label read(RegistryFriendlyByteBuf buf) {
      return new Label(
          UUIDUtil.STREAM_CODEC.decode(buf),
          BlockPos.STREAM_CODEC.decode(buf),
          ByteBufCodecs.STRING_UTF8.decode(buf),
          ByteBufCodecs.STRING_UTF8.decode(buf),
          ByteBufCodecs.VAR_INT.decode(buf),
          CHANNELS.decode(buf),
          ByteBufCodecs.VAR_INT.decode(buf));
    }

    public static Label of(UUID node, BlockPos pos, NodeLabel label) {
      return new Label(
          node,
          pos,
          label.node(),
          label.network().map(NodeLabel.NetworkPart::name).orElse(NO_NAME),
          label.network().map(part -> part.state().ordinal()).orElse(NO_STATE),
          label.channels().stream().map(ChannelLine::of).toList(),
          label.more());
    }

    public boolean hasNetwork() {
      return state != NO_STATE;
    }

    public Optional<String> networkName() {
      return hasNetwork() ? Optional.of(network) : Optional.empty();
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
