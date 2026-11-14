package io.github.marcelofabianov.redweave.block;

import io.github.marcelofabianov.redweave.domain.module.Connection;
import io.github.marcelofabianov.redweave.domain.module.SignalModule;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * What every Signal Module block keeps: its module, with the owner, the name and the network, in
 * the NBT of answer 411, and what its front edge gives now. Each module adds its own rule, its
 * connections, its `Toggle`, its state for the label and its screen (answer 415).
 */
public abstract class ModuleBlockEntity extends BlockEntity {
  private Optional<SignalModule> module = Optional.empty();
  private int emitted;

  protected ModuleBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  /** The module just placed, owned by whoever placed it (answer 416). */
  abstract void place(SignalModule placed);

  /** The `Toggle` of the Configurator, with the message of the action bar, if any (395). */
  public abstract Optional<Component> toggle();

  /** The output, whose channel the label shows and the network counts (answers 417 and 424). */
  public abstract Connection output();

  /** The key of the text of the state, which the client writes in its language (417). */
  public abstract Optional<String> stateKey();

  /** Opens the screen of the module for the player (answer 393). */
  public abstract void openScreen(ServerPlayer viewer);

  public Optional<SignalModule> module() {
    return module;
  }

  protected void setModule(SignalModule placed) {
    module = Optional.of(placed);
    setChanged();
  }

  /** A plate without a valid module, as from a damaged save, has no owner to protect it. */
  public boolean isOwner(Player player) {
    return module.map(found -> found.isOwner(new PlayerId(player.getUUID()))).orElse(true);
  }

  /** Saves the block and sends it to the clients, after its module changed from the screen. */
  public void changed() {
    setChanged();
    if (level != null) {
      level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
    }
  }

  /** The network of the module was deleted: it has none until its owner picks another (385). */
  void loseNetwork() {
    module.ifPresent(SignalModule::loseNetwork);
    setChanged();
  }

  /** What the front edge gives now, not saved, as the next tick works it out again. */
  int emitted() {
    return emitted;
  }

  void setEmitted(int strength) {
    emitted = strength;
  }

  @Override
  protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.saveAdditional(tag, registries);
    module.ifPresent(found -> ModuleSave.writeModule(tag, found));
  }

  @Override
  protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.loadAdditional(tag, registries);
    module = ModuleSave.readModule(tag);
  }

  /**
   * The client gets the module too, for its owner, so it shows the breaking as the server (394).
   */
  @Override
  public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
    return saveCustomOnly(registries);
  }

  @Override
  public Packet<ClientGamePacketListener> getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this);
  }
}
