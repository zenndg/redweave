package io.github.marcelofabianov.redweave.block;

import io.github.marcelofabianov.redweave.registry.RedweaveBlockEntities;
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
 * The Network Terminal in the world keeps who placed it, with the NBT key {@code owner}, fixed in
 * issue #26 (answers 134 and 175). Only the owner uses and breaks it.
 */
public final class NetworkTerminalBlockEntity extends BlockEntity {
  private static final String OWNER_TAG = "owner";

  private Optional<UUID> owner = Optional.empty();

  public NetworkTerminalBlockEntity(BlockPos pos, BlockState state) {
    super(RedweaveBlockEntities.NETWORK_TERMINAL.get(), pos, state);
  }

  public Optional<UUID> owner() {
    return owner;
  }

  public boolean isOwner(Player player) {
    return owner.map(player.getUUID()::equals).orElse(false);
  }

  void own(UUID player) {
    owner = Optional.of(player);
    setChanged();
  }

  @Override
  protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.saveAdditional(tag, registries);
    owner.ifPresent(id -> tag.putUUID(OWNER_TAG, id));
  }

  @Override
  protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.loadAdditional(tag, registries);
    owner = tag.hasUUID(OWNER_TAG) ? Optional.of(tag.getUUID(OWNER_TAG)) : Optional.empty();
  }

  /** The client knows the owner too, so it shows the same breaking as the server (answer 137). */
  @Override
  public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
    return saveCustomOnly(registries);
  }

  @Override
  public Packet<ClientGamePacketListener> getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this);
  }
}
