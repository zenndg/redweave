package io.github.zenndg.redweave.network;

import io.github.zenndg.redweave.RedweaveMod;
import io.github.zenndg.redweave.application.ControllerLabel;
import io.github.zenndg.redweave.application.ModuleLabel;
import io.github.zenndg.redweave.application.NodeLabel;
import io.github.zenndg.redweave.block.part.PartSpot;
import io.github.zenndg.redweave.domain.signal.ChannelColor;
import io.netty.buffer.ByteBuf;
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
 * The labels over the nodes, the modules, the cables and the Network Controllers for the player who
 * holds the Configurator: the ones that changed or came into reach and the ones that left it, or a
 * clear of all when the player lets go (answers 268, 269, 274, 525 and 773, and rule 18). The IDs
 * of the nodes, the modules and the networks of the controllers that left go in one list.
 */
public record NodeLabelsUpdate(
    boolean clearAll,
    List<Label> labels,
    List<ModuleLine> modules,
    List<CableLine> cables,
    List<ControllerLine> controllers,
    List<UUID> gone,
    List<PartSpot> goneCables)
    implements CustomPacketPayload {
  private static final String ID = "node_labels";
  private static final int NO_COLOR = -1;
  private static final String NO_NAME = "";

  public static final Type<NodeLabelsUpdate> TYPE =
      new Type<>(ResourceLocation.fromNamespaceAndPath(RedweaveMod.MOD_ID, ID));

  /**
   * Where a label sits: the spot of its part, and whether its space has other labeled parts, so
   * each label moves toward the side of its own part instead of over the others (answer 547).
   */
  public record Place(PartSpot spot, boolean shared) {
    static final StreamCodec<ByteBuf, Place> STREAM_CODEC =
        StreamCodec.composite(
            PartSpot.STREAM_CODEC, Place::spot, ByteBufCodecs.BOOL, Place::shared, Place::new);
  }

  /**
   * A label: the node and its place, its name, the network with its color, and the color of its
   * first enabled slot (answer 772); each color is -1 for none.
   */
  public record Label(
      UUID node, Place place, String nodeName, String network, int color, int slot) {
    static final StreamCodec<RegistryFriendlyByteBuf, Label> STREAM_CODEC =
        StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            Label::node,
            Place.STREAM_CODEC,
            Label::place,
            ByteBufCodecs.STRING_UTF8,
            Label::nodeName,
            ByteBufCodecs.STRING_UTF8,
            Label::network,
            ByteBufCodecs.VAR_INT,
            Label::color,
            ByteBufCodecs.VAR_INT,
            Label::slot,
            Label::new);

    public static Label of(UUID node, Place place, NodeLabel label) {
      return new Label(
          node,
          place,
          label.node(),
          label.network().map(NodeLabel.NetworkPart::name).orElse(NO_NAME),
          label.network().map(part -> part.color().ordinal()).orElse(NO_COLOR),
          label.slot().map(ChannelColor::ordinal).orElse(NO_COLOR));
    }

    public boolean hasNetwork() {
      return color != NO_COLOR;
    }
  }

  /**
   * The label of a module: its place, its name, the key of the text of its state, and the channel
   * of its output, or -1 when the output is on its edge (answer 417).
   */
  public record ModuleLine(UUID module, Place place, String name, String state, int channel) {
    static final StreamCodec<RegistryFriendlyByteBuf, ModuleLine> STREAM_CODEC =
        StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            ModuleLine::module,
            Place.STREAM_CODEC,
            ModuleLine::place,
            ByteBufCodecs.STRING_UTF8,
            ModuleLine::name,
            ByteBufCodecs.STRING_UTF8,
            ModuleLine::state,
            ByteBufCodecs.VAR_INT,
            ModuleLine::channel,
            ModuleLine::new);

    public static ModuleLine of(UUID module, Place place, ModuleLabel label) {
      return new ModuleLine(
          module,
          place,
          label.name(),
          label.state(),
          label.output().map(ChannelColor::ordinal).orElse(NO_COLOR));
    }
  }

  /**
   * The label of a Signal Cable, in one line: its place, the number of its color, as the channels
   * show it, and its strength, as `15 · 12` (answers 525 and 770).
   */
  public record CableLine(Place place, int color, int strength) {
    static final StreamCodec<ByteBuf, CableLine> STREAM_CODEC =
        StreamCodec.composite(
            Place.STREAM_CODEC,
            CableLine::place,
            ByteBufCodecs.VAR_INT,
            CableLine::color,
            ByteBufCodecs.VAR_INT,
            CableLine::strength,
            CableLine::new);

    public static CableLine of(Place place, ChannelColor color, int strength) {
      return new CableLine(place, color.ordinal(), strength);
    }
  }

  /**
   * The label of a Network Controller: the ID of its network, its block, the name and color of the
   * network, its state as the number of a `NetworkState`, and how many components it has (answers
   * 773 and 804).
   */
  public record ControllerLine(
      UUID network, BlockPos pos, String name, int color, int state, int components) {
    static final StreamCodec<ByteBuf, ControllerLine> STREAM_CODEC =
        StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            ControllerLine::network,
            BlockPos.STREAM_CODEC,
            ControllerLine::pos,
            ByteBufCodecs.STRING_UTF8,
            ControllerLine::name,
            ByteBufCodecs.VAR_INT,
            ControllerLine::color,
            ByteBufCodecs.VAR_INT,
            ControllerLine::state,
            ByteBufCodecs.VAR_INT,
            ControllerLine::components,
            ControllerLine::new);

    public static ControllerLine of(UUID network, BlockPos pos, ControllerLabel label) {
      return new ControllerLine(
          network,
          pos,
          label.network(),
          label.color().ordinal(),
          label.state().ordinal(),
          label.components());
    }
  }

  private static final StreamCodec<RegistryFriendlyByteBuf, List<Label>> LABELS =
      Label.STREAM_CODEC.apply(ByteBufCodecs.list());
  private static final StreamCodec<RegistryFriendlyByteBuf, List<ModuleLine>> MODULES =
      ModuleLine.STREAM_CODEC.apply(ByteBufCodecs.list());
  private static final StreamCodec<ByteBuf, List<CableLine>> CABLES =
      CableLine.STREAM_CODEC.apply(ByteBufCodecs.list());
  private static final StreamCodec<ByteBuf, List<ControllerLine>> CONTROLLERS =
      ControllerLine.STREAM_CODEC.apply(ByteBufCodecs.list());
  private static final StreamCodec<ByteBuf, List<UUID>> IDS =
      UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs.list());
  private static final StreamCodec<ByteBuf, List<PartSpot>> SPOTS =
      PartSpot.STREAM_CODEC.apply(ByteBufCodecs.list());

  /** Written field by field, because the game composes at most six fields. */
  public static final StreamCodec<RegistryFriendlyByteBuf, NodeLabelsUpdate> STREAM_CODEC =
      StreamCodec.of(NodeLabelsUpdate::write, NodeLabelsUpdate::read);

  private static void write(RegistryFriendlyByteBuf buffer, NodeLabelsUpdate update) {
    ByteBufCodecs.BOOL.encode(buffer, update.clearAll);
    LABELS.encode(buffer, update.labels);
    MODULES.encode(buffer, update.modules);
    CABLES.encode(buffer, update.cables);
    CONTROLLERS.encode(buffer, update.controllers);
    IDS.encode(buffer, update.gone);
    SPOTS.encode(buffer, update.goneCables);
  }

  private static NodeLabelsUpdate read(RegistryFriendlyByteBuf buffer) {
    return new NodeLabelsUpdate(
        ByteBufCodecs.BOOL.decode(buffer),
        LABELS.decode(buffer),
        MODULES.decode(buffer),
        CABLES.decode(buffer),
        CONTROLLERS.decode(buffer),
        IDS.decode(buffer),
        SPOTS.decode(buffer));
  }

  public NodeLabelsUpdate {
    labels = List.copyOf(labels);
    modules = List.copyOf(modules);
    cables = List.copyOf(cables);
    controllers = List.copyOf(controllers);
    gone = List.copyOf(gone);
    goneCables = List.copyOf(goneCables);
  }

  /** The player let the Configurator go, so every label goes. */
  public static NodeLabelsUpdate clear() {
    return new NodeLabelsUpdate(
        true, List.of(), List.of(), List.of(), List.of(), List.of(), List.of());
  }

  @Override
  public Type<NodeLabelsUpdate> type() {
    return TYPE;
  }
}
