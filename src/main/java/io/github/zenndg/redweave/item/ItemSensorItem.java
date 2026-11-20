package io.github.zenndg.redweave.item;

import io.github.zenndg.redweave.block.part.ItemSensorReading;
import io.github.zenndg.redweave.block.part.PartMessages;
import io.github.zenndg.redweave.block.part.PartSpot;
import java.util.Optional;
import net.minecraft.world.level.Level;

/**
 * The item of the Signal Item Sensor: a part that only goes on a block with a container, as a chest
 * or a barrel, since it reads that container. Elsewhere it is refused, the item stays in the hand
 * and the action bar says why (answer 694).
 */
public final class ItemSensorItem extends PartItem {
  public ItemSensorItem(Properties properties) {
    super(properties);
  }

  @Override
  protected Optional<String> refusalOn(Level level, PartSpot spot) {
    return ItemSensorReading.containerOf(level, spot.pos(), spot.face()).isPresent()
        ? Optional.empty()
        : Optional.of(PartMessages.NEEDS_CONTAINER);
  }
}
