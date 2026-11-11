package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.application.ChannelRow;
import io.github.marcelofabianov.redweave.application.NetworkView;
import io.github.marcelofabianov.redweave.application.NodeRow;
import io.github.marcelofabianov.redweave.application.SlotRow;
import io.github.marcelofabianov.redweave.block.NodeBlocks;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

/**
 * What the Controller Screen draws and changes only now and then: the header, the node list and the
 * channel names and counts. The live values go apart, in ControllerScreenSignals, so a busy circuit
 * does not resend the whole list (rule 18). The position only comes for the owner (answer 149).
 */
public record ControllerScreenRows(
    int menu, Header header, List<NodeLine> nodes, List<ChannelLine> channels)
    implements CustomPacketPayload {
  private static final String ID = "controller_screen_rows";
  private static final String DEFAULT_NAME = "";

  public static final Type<ControllerScreenRows> TYPE =
      new Type<>(ResourceLocation.fromNamespaceAndPath(RedweaveMod.MOD_ID, ID));

  /** The name and color of the network, the name of its owner and whether the viewer is it. */
  public record Header(String name, int color, String owner, boolean viewerIsOwner) {
    static final StreamCodec<RegistryFriendlyByteBuf, Header> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            Header::name,
            ByteBufCodecs.VAR_INT,
            Header::color,
            ByteBufCodecs.STRING_UTF8,
            Header::owner,
            ByteBufCodecs.BOOL,
            Header::viewerIsOwner,
            Header::new);
  }

  /** An enabled slot of a node: its index, channel and direction, as ordinals, and its name. */
  public record SlotLine(int index, int channel, int direction, String name) {
    static final StreamCodec<RegistryFriendlyByteBuf, SlotLine> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            SlotLine::index,
            ByteBufCodecs.VAR_INT,
            SlotLine::channel,
            ByteBufCodecs.VAR_INT,
            SlotLine::direction,
            ByteBufCodecs.STRING_UTF8,
            SlotLine::name,
            SlotLine::new);

    static SlotLine of(SlotRow row) {
      return new SlotLine(
          row.index(),
          row.channel().ordinal(),
          row.direction().ordinal(),
          row.name().orElse(DEFAULT_NAME));
    }
  }

  /** A row of the node list: the name, the block of the node for the owner, and its slots. */
  public record NodeLine(String name, Optional<BlockPos> position, List<SlotLine> slots) {
    static final StreamCodec<RegistryFriendlyByteBuf, NodeLine> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            NodeLine::name,
            ByteBufCodecs.optional(BlockPos.STREAM_CODEC),
            NodeLine::position,
            SlotLine.STREAM_CODEC.apply(ByteBufCodecs.list()),
            NodeLine::slots,
            NodeLine::new);

    public NodeLine {
      slots = List.copyOf(slots);
    }

    static NodeLine of(NodeRow row) {
      return new NodeLine(
          row.name(),
          row.position().map(NodeBlocks::posOf),
          row.slots().stream().map(SlotLine::of).toList());
    }
  }

  /** A channel of the Signals tab: its own name, or empty for the name of the color (answer 26). */
  public record ChannelLine(String name, int inputs, int outputs) {
    static final StreamCodec<RegistryFriendlyByteBuf, ChannelLine> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            ChannelLine::name,
            ByteBufCodecs.VAR_INT,
            ChannelLine::inputs,
            ByteBufCodecs.VAR_INT,
            ChannelLine::outputs,
            ChannelLine::new);

    static ChannelLine of(ChannelRow row) {
      return new ChannelLine(row.name().orElse(DEFAULT_NAME), row.inputs(), row.outputs());
    }

    /** A channel with a slot of some node; the others are dimmer (rede-e-canais.md). */
    public boolean inUse() {
      return inputs + outputs > 0;
    }
  }

  public static final StreamCodec<RegistryFriendlyByteBuf, ControllerScreenRows> STREAM_CODEC =
      StreamCodec.composite(
          ByteBufCodecs.VAR_INT,
          ControllerScreenRows::menu,
          Header.STREAM_CODEC,
          ControllerScreenRows::header,
          NodeLine.STREAM_CODEC.apply(ByteBufCodecs.list()),
          ControllerScreenRows::nodes,
          ChannelLine.STREAM_CODEC.apply(ByteBufCodecs.list()),
          ControllerScreenRows::channels,
          ControllerScreenRows::new);

  public ControllerScreenRows {
    nodes = List.copyOf(nodes);
    channels = List.copyOf(channels);
  }

  /** The rows of a view, with the name the server knows for the owner. */
  static ControllerScreenRows of(int menu, NetworkView view, String owner) {
    Header header = new Header(view.name(), view.color().ordinal(), owner, view.viewerIsOwner());
    return new ControllerScreenRows(
        menu,
        header,
        view.nodes().stream().map(NodeLine::of).toList(),
        view.channels().stream().map(ChannelLine::of).toList());
  }

  /** No network to show: an empty header and no rows. */
  static ControllerScreenRows empty(int menu) {
    return new ControllerScreenRows(
        menu, new Header(DEFAULT_NAME, 0, DEFAULT_NAME, false), List.of(), List.of());
  }

  /** The same rows for another menu, as when the client puts its own ID. */
  ControllerScreenRows forMenu(int other) {
    return new ControllerScreenRows(other, header, nodes, channels);
  }

  /** On the client: only the open Controller Screen of the same menu takes the rows. */
  public static void receive(ControllerScreenRows rows, Player player) {
    if (player.containerMenu instanceof ControllerMenu menu && menu.containerId == rows.menu()) {
      menu.acceptRows(rows);
    }
  }

  @Override
  public Type<ControllerScreenRows> type() {
    return TYPE;
  }
}
