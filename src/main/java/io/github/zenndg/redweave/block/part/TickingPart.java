package io.github.zenndg.redweave.block.part;

import io.github.zenndg.redweave.block.PartSpaceBlockEntity;

/**
 * A part that works on every tick of the server while its space is loaded, as a Signal Module did
 * with the ticker of its block; an unloaded part gets no tick and freezes (answer 381).
 */
public interface TickingPart {
  void serverTick(PartSpaceBlockEntity space);
}
