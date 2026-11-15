package io.github.marcelofabianov.redweave.block;

import io.github.marcelofabianov.redweave.block.part.ClientPart;
import io.github.marcelofabianov.redweave.block.part.HostedPart;
import io.github.marcelofabianov.redweave.block.part.LivePart;
import io.github.marcelofabianov.redweave.block.part.Part;
import io.github.marcelofabianov.redweave.block.part.PartLook;
import io.github.marcelofabianov.redweave.block.part.PartSave;
import io.github.marcelofabianov.redweave.block.part.PartSpot;
import io.github.marcelofabianov.redweave.block.part.TickingPart;
import io.github.marcelofabianov.redweave.domain.part.Fit;
import io.github.marcelofabianov.redweave.domain.part.PartKind;
import io.github.marcelofabianov.redweave.domain.part.PartSpace;
import io.github.marcelofabianov.redweave.registry.RedweaveBlockEntities;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;

/**
 * What a part space keeps: its parts by the side they rest on, at most one per side and 2 cables,
 * as the domain says (answers 512, 513 and 543). Each part keeps its own NBT and look, and the
 * client gets them to draw the space (answers 534 and 536).
 */
public final class PartSpaceBlockEntity extends BlockEntity {
  /** The looks of the parts, which the model of the client joins (answer 536). */
  public static final ModelProperty<List<PartLook>> LOOKS = new ModelProperty<>();

  private static final String CLIENT = "client";

  private Map<Direction, Part> parts = new EnumMap<>(Direction.class);

  public PartSpaceBlockEntity(BlockPos pos, BlockState state) {
    super(RedweaveBlockEntities.PART_SPACE.get(), pos, state);
  }

  /** The part space at the position, if it is loaded and is one (rule 6). */
  public static Optional<PartSpaceBlockEntity> at(Level level, BlockPos pos) {
    if (!level.isLoaded(pos)) {
      return Optional.empty();
    }
    return level.getBlockEntity(pos) instanceof PartSpaceBlockEntity space
        ? Optional.of(space)
        : Optional.empty();
  }

  public Optional<Part> at(Direction face) {
    return Optional.ofNullable(parts.get(face));
  }

  public Collection<Part> parts() {
    return List.copyOf(parts.values());
  }

  public boolean isEmpty() {
    return parts.isEmpty();
  }

  public PartSpot spotOf(Part part) {
    return new PartSpot(worldPosition, part.face());
  }

  /** Whether a part of the kind fits on the side, or why not (answers 513 and 533). */
  public Fit fit(Direction face, PartKind kind) {
    return space().fit(Faces.of(face), kind);
  }

  private PartSpace space() {
    PartSpace space = PartSpace.EMPTY;
    for (Part part : parts.values()) {
      space = space.with(Faces.of(part.face()), part.kind());
    }
    return space;
  }

  /** Adds a part that fits, which the caller checked with `fit`. */
  public void add(Part part) {
    space().with(Faces.of(part.face()), part.kind());
    parts.put(part.face(), part);
    attach(part);
    changed();
  }

  public Optional<Part> remove(Direction face) {
    Optional<Part> removed = Optional.ofNullable(parts.remove(face));
    removed.ifPresent(found -> changed());
    return removed;
  }

  /** Saves the space and sends it to the clients, after a part changed. */
  public void changed() {
    setChanged();
    if (level != null && !level.isClientSide) {
      level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
    }
  }

  /**
   * The strongest signal the parts other than the given one give to the side, as a node reads its
   * block of support without its own output (answer 514).
   */
  public int signalWithout(Part left, Direction side) {
    return parts.values().stream()
        .filter(part -> part != left)
        .mapToInt(part -> part.signal(side))
        .max()
        .orElse(0);
  }

  /**
   * A space that loads again lets its parts settle, as a block entity loads, and read and take
   * their signal on the next tick.
   */
  @Override
  public void onLoad() {
    super.onLoad();
    if (level != null && !level.isClientSide) {
      parts.values().stream()
          .filter(LivePart.class::isInstance)
          .forEach(part -> ((LivePart) part).loaded(this));
      level.scheduleTick(worldPosition, getBlockState().getBlock(), 1);
    }
  }

  /**
   * The other parts of the space hear the change of a part, as neighbors do, which the updates of
   * the game skip, as they never wake the block that changed (answer 514).
   */
  public void wakeOthers(Part changed) {
    for (Part part : parts()) {
      if (part != changed && part instanceof LivePart live) {
        live.neighborChanged(this);
      }
    }
  }

  /** Every part that ticks, on the server, once a tick (answer 381). */
  public void tickParts() {
    for (Part part : parts()) {
      if (part instanceof TickingPart ticking && parts.get(part.face()) == part) {
        ticking.serverTick(this);
      }
    }
  }

  private void attach(Part part) {
    if (part instanceof HostedPart hosted) {
      hosted.attach(this);
    }
  }

  @Override
  protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.saveAdditional(tag, registries);
    PartSave.write(tag, parts.values(), (part, entry) -> {});
  }

  @Override
  protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.loadAdditional(tag, registries);
    parts = PartSave.read(tag, PartSpaceBlockEntity::readClient);
    parts.values().forEach(this::attach);
    if (level != null && level.isClientSide) {
      requestModelDataUpdate();
      level.sendBlockUpdated(
          worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL_IMMEDIATE);
    }
  }

  @Override
  public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
    CompoundTag tag = new CompoundTag();
    PartSave.write(tag, parts.values(), PartSpaceBlockEntity::writeClient);
    return tag;
  }

  @Override
  public Packet<ClientGamePacketListener> getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this);
  }

  @Override
  public ModelData getModelData() {
    return ModelData.builder()
        .with(LOOKS, parts.values().stream().flatMap(part -> part.looks().stream()).toList())
        .build();
  }

  private static void writeClient(Part part, CompoundTag entry) {
    if (part instanceof ClientPart client) {
      CompoundTag extra = new CompoundTag();
      client.writeClient(extra);
      entry.put(CLIENT, extra);
    }
  }

  private static void readClient(Part part, CompoundTag entry) {
    if (part instanceof ClientPart client && entry.contains(CLIENT)) {
      client.readClient(entry.getCompound(CLIENT));
    }
  }
}
