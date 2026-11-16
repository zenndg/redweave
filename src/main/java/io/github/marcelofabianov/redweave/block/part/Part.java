package io.github.marcelofabianov.redweave.block.part;

import io.github.marcelofabianov.redweave.domain.part.PartKind;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
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

  /**
   * What the part gives to the side when the game asks the space, which knows the asker, as the
   * Signal Cable gives one less to the game wire (answer 565).
   */
  default int signal(BlockGetter level, BlockPos pos, Direction side) {
    return signal(side);
  }

  /**
   * Whether the game wire, coming from the direction, turns to the part, as it turns to a signal
   * source; a node turns it only when they exchange signal (answer 574).
   */
  default boolean turnsWire(Direction toward) {
    return true;
  }

  /** The light the part gives to the world, from 0 to 15, as the Signal Lamp (answer 650). */
  default int light() {
    return 0;
  }

  /** What the client draws for the part, which the model of the space joins (answer 536). */
  List<PartLook> looks();

  /** The item the part drops or the middle click picks, as its item (answer 539). */
  default ItemStack drop() {
    return new ItemStack(item());
  }

  /** The NBT of the part, kept under `data` (answer 543). */
  CompoundTag save();
}
