package io.github.marcelofabianov.redweave.block.part;

import io.github.marcelofabianov.redweave.block.PartSpaceBlockEntity;
import net.minecraft.world.entity.player.Player;

/** A part that reacts to the world on the server, as the Signal Node reads and emits. */
public interface LivePart {
  void placed(PartSpaceBlockEntity space, Player placer);

  void neighborChanged(PartSpaceBlockEntity space);

  /** The tick scheduled when the space loads or a part asks for it. */
  void tick(PartSpaceBlockEntity space);

  /** The space loaded on the server, before its first tick, as a block entity loads. */
  default void loaded(PartSpaceBlockEntity space) {}

  /** Gone by any means: broken, picked up, fallen or replaced. */
  void removed(PartSpaceBlockEntity space);
}
