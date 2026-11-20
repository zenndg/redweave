package io.github.zenndg.redweave.block.part;

import net.minecraft.nbt.CompoundTag;

/** A part that sends the client more than its save, as the owner of the network of a node. */
public interface ClientPart {
  void writeClient(CompoundTag tag);

  void readClient(CompoundTag tag);
}
