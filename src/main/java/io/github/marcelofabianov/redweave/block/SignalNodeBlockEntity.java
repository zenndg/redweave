package io.github.marcelofabianov.redweave.block;

import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.registry.RedweaveBlockEntities;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * What a node block keeps: the ID of its node, saved under the key "node" (answer 76), and what it
 * emits now, which is not saved because the first tick after loading applies it again (38).
 */
public final class SignalNodeBlockEntity extends BlockEntity {
  private static final String NODE_TAG = "node";
  private static final String OWNER_TAG = "owner";

  private Optional<NodeId> node = Optional.empty();
  private Optional<UUID> ownerOnClient = Optional.empty();
  private int emitted;

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

  int emitted() {
    return emitted;
  }

  void setEmitted(int strength) {
    emitted = strength;
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
  }

  @Override
  protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.loadAdditional(tag, registries);
    node = tag.hasUUID(NODE_TAG) ? Optional.of(new NodeId(tag.getUUID(NODE_TAG))) : node;
    ownerOnClient = tag.hasUUID(OWNER_TAG) ? Optional.of(tag.getUUID(OWNER_TAG)) : ownerOnClient;
  }

  /** Only the client gets the owner, so it shows the breaking as the server decides it (74). */
  @Override
  public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
    CompoundTag tag = saveCustomOnly(registries);
    owner().ifPresent(id -> tag.putUUID(OWNER_TAG, id));
    return tag;
  }

  @Override
  public Packet<ClientGamePacketListener> getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this);
  }
}
