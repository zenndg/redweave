package io.github.marcelofabianov.redweave.block.part;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.BlockLocations;
import io.github.marcelofabianov.redweave.block.Faces;
import io.github.marcelofabianov.redweave.block.NodeLook;
import io.github.marcelofabianov.redweave.block.NodeShapes;
import io.github.marcelofabianov.redweave.block.NodeSignals;
import io.github.marcelofabianov.redweave.block.PartSpaceBlockEntity;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.NodePosition;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.domain.part.PartKind;
import io.github.marcelofabianov.redweave.item.ConfiguratorMessages;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * The Signal Node as a part: _docs/funcionalidades/no-de-sinal.md. A thin plate on a side of its
 * space that reads and emits on the block it rests on. It adapts the world to the use cases, and
 * the rules stay in the domain (rule 10). Its looks of the old block states, `powered` and
 * `out_of_range`, are fields kept in its NBT (answer 534).
 */
public final class NodePart implements Part, LivePart, OwnedPart, ClientPart, HandPart {
  private static final String NODE_TAG = "node";

  /** Who placed the node, saved: the owner of a node without a network (answers 165 and 189). */
  private static final String PLACER_TAG = "owner";

  private static final String POWERED_TAG = "powered";
  private static final String OUT_OF_RANGE_TAG = "out_of_range";

  /** The owner of the network, only sent to the client, never saved. */
  private static final String NETWORK_OWNER_TAG = "network_owner";

  private static final String CORNERS_TAG = "corners";
  private static final ResourceLocation OFF = model("block/signal_node");
  private static final ResourceLocation ON = model("block/signal_node_on");
  private static final ResourceLocation OUT_OF_RANGE = model("block/signal_node_out_of_range");

  /** The looks of the node, which the client loads for the part space (answer 536). */
  public static final List<ResourceLocation> MODELS = List.of(OFF, ON, OUT_OF_RANGE);

  private final Direction face;
  private Optional<NodeId> node = Optional.empty();
  private Optional<UUID> placer = Optional.empty();
  private Optional<UUID> ownerOnClient = Optional.empty();
  private int emitted;
  private boolean powered;
  private boolean outOfRange;
  private int[] corners = {NodeLook.FREE, NodeLook.FREE, NodeLook.FREE, NodeLook.FREE};

  private NodePart(Direction face) {
    this.face = face;
  }

  /** A node on the side of its space, from its NBT, or a new one from an empty tag. */
  public static NodePart load(Direction face, CompoundTag tag) {
    NodePart part = new NodePart(face);
    part.node = tag.hasUUID(NODE_TAG) ? Optional.of(new NodeId(tag.getUUID(NODE_TAG))) : part.node;
    part.placer = tag.hasUUID(PLACER_TAG) ? Optional.of(tag.getUUID(PLACER_TAG)) : part.placer;
    part.powered = tag.getBoolean(POWERED_TAG);
    part.outOfRange = tag.getBoolean(OUT_OF_RANGE_TAG);
    return part;
  }

  /** The node part at the spot, if the space is loaded and has one there. */
  public static Optional<NodePart> at(Level level, PartSpot spot) {
    return PartSpaceBlockEntity.at(level, spot.pos())
        .flatMap(space -> space.at(spot.face()))
        .filter(NodePart.class::isInstance)
        .map(NodePart.class::cast);
  }

  /** The domain keeps the block the node sticks to and the face of it (answer 72). */
  public static NodePosition positionOf(Level level, PartSpot spot) {
    return new NodePosition(
        BlockLocations.of(level, spot.support()), Faces.of(spot.face().getOpposite()));
  }

  private static ResourceLocation model(String path) {
    return ResourceLocation.fromNamespaceAndPath(RedweaveMod.MOD_ID, path);
  }

  @Override
  public Direction face() {
    return face;
  }

  /** The face of the block of support the node sticks on, pointing away from it (answer 72). */
  public Direction facing() {
    return face.getOpposite();
  }

  @Override
  public PartKind kind() {
    return PartKind.PLATE;
  }

  @Override
  public Item item() {
    return RedweaveItems.SIGNAL_NODE.get();
  }

  @Override
  public VoxelShape shape() {
    return NodeShapes.of(facing());
  }

  /**
   * Only the block the node sticks to gets its signal, unlike a lever, so the neighbors of the
   * space stay off (answer 262), with strength, so it powers what touches it (answer 71).
   */
  @Override
  public int signal(Direction side) {
    return side == facing() ? emitted : 0;
  }

  /** The ring out of range wins over the lit lens (answers 72 and 86). */
  @Override
  public List<PartLook> looks() {
    return List.of(look());
  }

  /** The look of the plate, which the client draws (answer 534). */
  public PartLook look() {
    ResourceLocation model = outOfRange ? OUT_OF_RANGE : powered ? ON : OFF;
    return new PartLook(model, face, PlateTurn.facing(facing()));
  }

  @Override
  public CompoundTag save() {
    CompoundTag tag = new CompoundTag();
    node.ifPresent(id -> tag.putUUID(NODE_TAG, id.value()));
    placer.ifPresent(id -> tag.putUUID(PLACER_TAG, id));
    tag.putBoolean(POWERED_TAG, powered);
    tag.putBoolean(OUT_OF_RANGE_TAG, outOfRange);
    return tag;
  }

  public Optional<NodeId> node() {
    return node;
  }

  /** A node saved before the placer was kept has none, and nobody counts as its placer (193). */
  public boolean wasPlacedBy(Player player) {
    return placer.map(player.getUUID()::equals).orElse(false);
  }

  /** What it emits now, not saved, as the first tick after loading applies it again (38). */
  public int emitted() {
    return emitted;
  }

  public void setEmitted(int strength) {
    emitted = strength;
  }

  public boolean powered() {
    return powered;
  }

  /** Keeps the lit lens and tells whether it changed, so only a change reaches the clients. */
  public boolean setPowered(boolean on) {
    boolean changed = powered != on;
    powered = on;
    return changed;
  }

  public boolean outOfRange() {
    return outOfRange;
  }

  /** Keeps the dashed ring and tells whether it changed (answer 86). */
  public boolean setOutOfRange(boolean out) {
    boolean changed = outOfRange != out;
    outOfRange = out;
    return changed;
  }

  /** The channel of each corner, or NodeLook.FREE, as the client draws it (answer 87). */
  public int corner(int index) {
    return index < corners.length ? corners[index] : NodeLook.FREE;
  }

  /** Keeps the corners and tells whether they changed, so only a change goes to the clients. */
  public boolean setCorners(int[] next) {
    if (Arrays.equals(corners, next)) {
      return false;
    }
    corners = next.clone();
    return true;
  }

  /**
   * The node joins no network yet and keeps who placed it, the owner of a node without a network
   * for the Wrench (answers 139, 165 and 189); then it reads and shows its look.
   */
  @Override
  public void placed(PartSpaceBlockEntity space, Player placer) {
    Optional<RedweaveServer> server = RedweaveServer.running();
    if (server.isEmpty()) {
      return;
    }
    this.placer = Optional.of(placer.getUUID());
    node = Optional.of(server.get().placeNode().place());
    PartSpot spot = space.spotOf(this);
    NodeSignals.read(server.get(), space.getLevel(), spot);
    NodeLook.refresh(server.get(), space.getLevel(), spot);
    space.changed();
  }

  @Override
  public void neighborChanged(PartSpaceBlockEntity space) {
    RedweaveServer.running()
        .ifPresent(server -> NodeSignals.read(server, space.getLevel(), space.spotOf(this)));
  }

  /** When the node loads, it reads again and takes the signal of its network. */
  @Override
  public void tick(PartSpaceBlockEntity space) {
    PartSpot spot = space.spotOf(this);
    RedweaveServer.running()
        .ifPresent(server -> {
          NodeSignals.read(server, space.getLevel(), spot);
          NodeSignals.refreshEmission(server, space.getLevel(), spot);
          NodeLook.refresh(server, space.getLevel(), spot);
        });
  }

  /** Gone by any means, the node leaves its network (answer 74). */
  @Override
  public void removed(PartSpaceBlockEntity space) {
    RedweaveServer.running().ifPresent(server -> node.ifPresent(id -> server.loseNode().lose(id)));
  }

  /** In survival, only the owner of its network breaks the node (answer 74). */
  @Override
  public boolean mayBreak(Player player) {
    return owner(player.level()).map(player.getUUID()::equals).orElse(true);
  }

  @Override
  public boolean breakBy(PartSpaceBlockEntity space, Player player) {
    Optional<RedweaveServer> server = RedweaveServer.running();
    if (server.isEmpty() || node.isEmpty()) {
      return true;
    }
    PlayerId breaker = new PlayerId(player.getUUID());
    return server.get().breakNode().breakBy(breaker, node.get()) == Outcome.ACCEPTED;
  }

  private Optional<UUID> owner(Level level) {
    return level.isClientSide ? ownerOnClient : networkOwner();
  }

  private Optional<UUID> networkOwner() {
    return node.flatMap(id ->
        RedweaveServer.running()
            .flatMap(server -> server.networkOfNode(id))
            .map(network -> network.owner().value()));
  }

  /** Only the client gets the owner, so it shows the breaking as the server decides it (74). */
  @Override
  public void writeClient(CompoundTag tag) {
    networkOwner().ifPresent(id -> tag.putUUID(NETWORK_OWNER_TAG, id));
    tag.putIntArray(CORNERS_TAG, corners);
  }

  @Override
  public void readClient(CompoundTag tag) {
    ownerOnClient =
        tag.hasUUID(NETWORK_OWNER_TAG)
            ? Optional.of(tag.getUUID(NETWORK_OWNER_TAG))
            : ownerOnClient;
    if (tag.contains(CORNERS_TAG) && tag.getIntArray(CORNERS_TAG).length == NodeLook.CORNERS) {
      corners = tag.getIntArray(CORNERS_TAG);
    }
  }

  /**
   * An empty hand does not open the node: only the Configurator in the Open mode does, and the
   * action bar teaches it (answers 154 and 188).
   */
  @Override
  public void emptyHand(Player player) {
    player.displayClientMessage(Component.translatable(ConfiguratorMessages.USE_TO_OPEN), true);
  }
}
