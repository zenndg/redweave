package io.github.marcelofabianov.redweave.item;

import io.github.marcelofabianov.redweave.block.part.PartMessages;
import java.util.Optional;
import net.minecraft.core.Direction;

/**
 * The item of the Signal Pressure Plate: a part that only goes on the floor, as the plate of the
 * game. On a wall or on the ceiling it is refused, the item stays in the hand and the action bar
 * says why (answers 551 and 611).
 */
public final class PressurePlateItem extends PartItem {
  public PressurePlateItem(Properties properties) {
    super(properties);
  }

  @Override
  protected Optional<String> refusalOn(Direction face) {
    return face == Direction.DOWN ? Optional.empty() : Optional.of(PartMessages.FLOOR_ONLY);
  }
}
