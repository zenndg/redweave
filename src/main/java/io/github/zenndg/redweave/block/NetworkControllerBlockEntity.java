package io.github.zenndg.redweave.block;

import io.github.zenndg.redweave.domain.network.NetworkId;
import io.github.zenndg.redweave.registry.RedweaveBlockEntities;
import io.github.zenndg.redweave.registry.RedweaveDataComponents;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * What the controller block keeps: the ID of its network and its owner. The network itself lives in
 * the world, not here (answer 24). The owner also goes to the client, which shows the breaking.
 */
public final class NetworkControllerBlockEntity extends BlockEntity {
  private static final String NETWORK_TAG = "network";
  private static final String OWNER_TAG = "owner";

  private Optional<NetworkId> network = Optional.empty();
  private Optional<UUID> owner = Optional.empty();

  public NetworkControllerBlockEntity(BlockPos pos, BlockState state) {
    super(RedweaveBlockEntities.NETWORK_CONTROLLER.get(), pos, state);
  }

  public Optional<NetworkId> network() {
    return network;
  }

  public boolean isOwner(Player player) {
    return owner.map(player.getUUID()::equals).orElse(false);
  }

  void link(NetworkId id, UUID ownerId) {
    network = Optional.of(id);
    owner = Optional.of(ownerId);
    setChanged();
  }

  /** The core waits for the first tick, because the block cannot change while it loads (177). */
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
    network.ifPresent(id -> tag.putUUID(NETWORK_TAG, id.value()));
    owner.ifPresent(id -> tag.putUUID(OWNER_TAG, id));
  }

  @Override
  protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.loadAdditional(tag, registries);
    network = readUuid(tag, NETWORK_TAG).map(NetworkId::new);
    owner = readUuid(tag, OWNER_TAG);
  }

  private static Optional<UUID> readUuid(CompoundTag tag, String key) {
    return tag.hasUUID(key) ? Optional.of(tag.getUUID(key)) : Optional.empty();
  }

  @Override
  public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
    return saveCustomOnly(registries);
  }

  @Override
  public Packet<ClientGamePacketListener> getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this);
  }

  /** The broken block hands its network to the item it drops (answer 24). */
  @Override
  protected void collectImplicitComponents(DataComponentMap.Builder components) {
    super.collectImplicitComponents(components);
    network.ifPresent(id -> components.set(RedweaveDataComponents.NETWORK.get(), id));
  }

  /** The network already goes in the component, so the copied block data does not repeat it. */
  @Override
  public void removeComponentsFromTag(CompoundTag tag) {
    tag.remove(NETWORK_TAG);
  }
}
