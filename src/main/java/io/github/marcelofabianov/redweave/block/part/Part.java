package io.github.marcelofabianov.redweave.block.part;

import io.github.marcelofabianov.redweave.domain.part.PartKind;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A piece of the multipart on one side of a block space: a Signal Node, a Signal Module or a Signal
 * Cable. The container asks each part for its shape, its signal and its save, and the rules of the
 * signal stay in the domain (answers 512, 538 and 542).
 */
public interface Part {
  /** The side of the space the part rests on. */
  Direction face();

  PartKind kind();

  /** The item of the part, whose ID is the type in the save and what it drops (544 and 539). */
  Item item();

  /** The shape in the space, for the aim and the outline; parts never collide (answer 517). */
  VoxelShape shape();

  /** What the part gives to the side, as the game asks a block, seen from the asker. */
  int signal(Direction side);

  PartLook look();

  /** The NBT of the part, kept under `data` (answer 543). */
  CompoundTag save();
}
