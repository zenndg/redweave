package io.github.zenndg.redweave.block.part;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

/**
 * What a Signal Item Sensor reads from the container it sticks on: the items of a type, summing
 * every slot, through the item capability of NeoForge, so the containers of other mods count too
 * (answers 690, 692 and 694). It only reads: it never takes or puts an item (rule 2). A block that
 * is not loaded counts nothing, and is never loaded for it (rule 6).
 */
public final class ItemSensorReading {
  private ItemSensorReading() {}

  /** The container on the side of the space the plate rests on, if loaded and if it has one. */
  public static Optional<IItemHandler> containerOf(Level level, BlockPos space, Direction face) {
    BlockPos support = space.relative(face);
    if (!level.isLoaded(support)) {
      return Optional.empty();
    }
    return Optional.ofNullable(
        level.getCapability(Capabilities.ItemHandler.BLOCK, support, face.getOpposite()));
  }

  /** How many items of the type the container holds now, in all of its slots. */
  public static int count(Level level, BlockPos space, Direction face, Item item) {
    return containerOf(level, space, face).map(container -> countIn(container, item)).orElse(0);
  }

  private static int countIn(IItemHandler container, Item item) {
    int total = 0;
    for (int slot = 0; slot < container.getSlots(); slot++) {
      if (container.getStackInSlot(slot).is(item)) {
        total += container.getStackInSlot(slot).getCount();
      }
    }
    return total;
  }
}
