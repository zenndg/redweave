package io.github.marcelofabianov.redweave.block.part;

import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

/**
 * The NBT of a part space: `parts`, a list where each part has its `type`, the ID of its item, its
 * `face`, the side of the space it rests on, and its `data`, the NBT of the part, intact (answers
 * 523, 543 and 544). An invalid entry is dropped with a log line, never breaking the game (rule
 * 14).
 */
public final class PartSave {
  static final String PARTS = "parts";
  static final String TYPE = "type";
  static final String FACE = "face";
  static final String DATA = "data";
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String DROPPED = "Dropped an invalid part of a part space: {}";

  private PartSave() {}

  /** Writes the parts; `extra` adds what only the client gets to each entry. */
  public static void write(
      CompoundTag tag, Collection<Part> parts, BiConsumer<Part, CompoundTag> extra) {
    ListTag list = new ListTag();
    for (Part part : parts) {
      CompoundTag entry = new CompoundTag();
      entry.putString(TYPE, PartTypes.typeOf(part).toString());
      entry.putString(FACE, part.face().getSerializedName());
      entry.put(DATA, part.save());
      extra.accept(part, entry);
      list.add(entry);
    }
    tag.put(PARTS, list);
  }

  /** The valid parts by side; a later part on a taken side is dropped too. */
  public static Map<Direction, Part> read(CompoundTag tag, BiConsumer<Part, CompoundTag> extra) {
    Map<Direction, Part> parts = new EnumMap<>(Direction.class);
    ListTag list = tag.getList(PARTS, Tag.TAG_COMPOUND);
    for (int index = 0; index < list.size(); index++) {
      CompoundTag entry = list.getCompound(index);
      Optional<Part> part = partOf(entry).filter(found -> !parts.containsKey(found.face()));
      if (part.isEmpty()) {
        LOGGER.warn(DROPPED, entry);
        continue;
      }
      extra.accept(part.get(), entry);
      parts.put(part.get().face(), part.get());
    }
    return parts;
  }

  private static Optional<Part> partOf(CompoundTag entry) {
    ResourceLocation type = ResourceLocation.tryParse(entry.getString(TYPE));
    Direction face = Direction.byName(entry.getString(FACE));
    if (type == null || face == null || !entry.contains(DATA, Tag.TAG_COMPOUND)) {
      return Optional.empty();
    }
    return PartTypes.read(type, face, entry.getCompound(DATA));
  }
}
