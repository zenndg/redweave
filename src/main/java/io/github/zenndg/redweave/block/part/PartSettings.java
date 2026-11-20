package io.github.zenndg.redweave.block.part;

import net.minecraft.nbt.CompoundTag;

/**
 * The settings of a Signal Module that the Configurator copies and pastes: the keys of its save
 * that its type names, never its owner, its name nor what it is doing now (answers 725 and 726).
 */
public final class PartSettings {
  private PartSettings() {}

  /** Whether the module can be copied: the Signal Lock never is (answer 731). */
  public static boolean copiable(ModulePart part) {
    return !part.copiedKeys().isEmpty();
  }

  /** The settings of the module, from its save. */
  public static CompoundTag copy(ModulePart part) {
    CompoundTag saved = part.save();
    CompoundTag settings = new CompoundTag();
    for (String key : part.copiedKeys()) {
      if (saved.contains(key)) {
        settings.put(key, saved.get(key).copy());
      }
    }
    return settings;
  }

  /**
   * Puts the settings in the module, keeping the rest of its save, and takes its connections off
   * their channels when it is not in the network of the copy; true when one was taken off (732).
   */
  public static boolean paste(ModulePart part, CompoundTag settings, boolean inNetwork) {
    CompoundTag merged = part.save();
    for (String key : part.copiedKeys()) {
      merged.remove(key);
      if (settings.contains(key)) {
        merged.put(key, settings.get(key).copy());
      }
    }
    part.readRule(merged);
    ChannelDrop drop = new ChannelDrop();
    if (!inNetwork) {
      part.dropChannels(drop);
    }
    part.changed();
    return drop.dropped();
  }
}
