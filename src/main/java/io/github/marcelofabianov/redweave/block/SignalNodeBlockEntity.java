package io.github.marcelofabianov.redweave.block;

import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.registry.RedweaveBlockEntities;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * What a node block keeps: the ID of its node, saved under the key "node" (answer 76), and what it
 * emits now, which is not saved because the first tick after loading applies it again (38).
 */
public final class SignalNodeBlockEntity extends BlockEntity {
  private static final String NODE_TAG = "node";

  /** Who placed the node, saved: the owner of a node without a network (answers 165 and 189). */
  private static final String PLACER_TAG = "owner";

  /** The owner of the network, only sent to the client, never saved. */
  private static final String NETWORK_OWNER_TAG = "network_owner";

  private static final String CORNERS_TAG = "corners";

  private Optional<NodeId> node = Optional.empty();
  private Optional<UUID> ownerOnClient = Optional.empty();
  private Optional<UUID> placer = Optional.empty();
  private int emitted;
  private int[] corners = {NodeLook.FREE, NodeLook.FREE, NodeLook.FREE, NodeLook.FREE};

  public SignalNodeBlockEntity(BlockPos pos, BlockState state) {
    super(RedweaveBlockEntities.SIGNAL_NODE.get(), pos, state);
  }

  public Optional<NodeId> node() {
    return node;
  }

  void setNode(NodeId id) {
    node = Optional.of(id);
    setChanged();
  }

  void setPlacer(UUID player) {
    placer = Optional.of(player);
    setChanged();
  }

  /** A node saved before the placer was kept has none, and nobody counts as its placer (193). */
  public boolean wasPlacedBy(Player player) {
    return placer.map(player.getUUID()::equals).orElse(false);
  }

  int emitted() {
    return emitted;
  }

  void setEmitted(int strength) {
    emitted = strength;
  }

  /** The channel of each corner, or NodeLook.FREE, as the client draws it (answer 87). */
  public int corner(int index) {
    return index < corners.length ? corners[index] : NodeLook.FREE;
  }

  /** Keeps the corners and tells whether they changed, so only a change goes to the clients. */
  boolean setCorners(int[] next) {
    if (Arrays.equals(corners, next)) {
      return false;
    }
    corners = next.clone();
    return true;
  }

  /** The owner of the network of the node, known on the server and sent to the client. */
  boolean isOwner(Player player) {
    return owner().map(player.getUUID()::equals).orElse(true);
  }

  private Optional<UUID> owner() {
    if (level == null || level.isClientSide) {
      return ownerOnClient;
    }
    return node.flatMap(id ->
        RedweaveServer.running()
            .flatMap(server -> server.networkOfNode(id))
            .map(network -> network.owner().value()));
  }

  /** A node that loads again asks its network for its signal on the next tick (no-de-sinal.md). */
  @Override
  public void onLoad() {
    super.onLoad();
    if (level != null && !level.isClientSide) {
      level.scheduleTick(worldPosition, getBlockState().getBlock(), 1);
    }
  }

  @Override
  protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.saveAdditional(tag, registries);
    node.ifPresent(id -> tag.putUUID(NODE_TAG, id.value()));
    placer.ifPresent(id -> tag.putUUID(PLACER_TAG, id));
  }

  @Override
  protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.loadAdditional(tag, registries);
    node = tag.hasUUID(NODE_TAG) ? Optional.of(new NodeId(tag.getUUID(NODE_TAG))) : node;
    placer = tag.hasUUID(PLACER_TAG) ? Optional.of(tag.getUUID(PLACER_TAG)) : placer;
    ownerOnClient =
        tag.hasUUID(NETWORK_OWNER_TAG)
            ? Optional.of(tag.getUUID(NETWORK_OWNER_TAG))
            : ownerOnClient;
    if (tag.contains(CORNERS_TAG) && tag.getIntArray(CORNERS_TAG).length == NodeLook.CORNERS) {
      corners = tag.getIntArray(CORNERS_TAG);
      redrawOnClient();
    }
  }

  /** The corners tint the model, so the client draws the block again when they change. */
  private void redrawOnClient() {
    if (level != null && level.isClientSide) {
      level.sendBlockUpdated(
          worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL_IMMEDIATE);
    }
  }

  /** Only the client gets the owner, so it shows the breaking as the server decides it (74). */
  @Override
  public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
    CompoundTag tag = saveCustomOnly(registries);
    owner().ifPresent(id -> tag.putUUID(NETWORK_OWNER_TAG, id));
    tag.putIntArray(CORNERS_TAG, corners);
    return tag;
  }

  @Override
  public Packet<ClientGamePacketListener> getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this);
  }
}
