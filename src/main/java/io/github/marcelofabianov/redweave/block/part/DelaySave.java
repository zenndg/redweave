package io.github.marcelofabianov.redweave.block.part;

import com.mojang.logging.LogUtils;
import io.github.marcelofabianov.redweave.domain.module.DelayLine;
import io.github.marcelofabianov.redweave.domain.module.OutputDelay;
import io.github.marcelofabianov.redweave.domain.module.OutputDelayLimits;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.slf4j.Logger;

/**
 * The output delay of a module in its NBT, the same in every module with a delay (answer 844):
 * `output_delay_ticks`, the delay in ticks, and `delay_line`, the changes still waiting, each `{on,
 * ticks}` with the ticks it still waits. A save without them has no delay and nothing waiting, with
 * no migration (rule 15); an invalid delay is fitted to whole seconds within the config, and an
 * invalid change is dropped, both with a warning (rule 14).
 */
final class DelaySave {
  static final String DELAY = "output_delay_ticks";
  static final String LINE = "delay_line";

  private static final String ON = "on";
  private static final String TICKS = "ticks";
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String FITTED = "Fitted an invalid output delay of a {}: {}";
  private static final String DROPPED = "Dropped an invalid delayed change of a {}: {}";

  private DelaySave() {}

  static void writeDelay(CompoundTag tag, OutputDelay delay) {
    tag.putInt(DELAY, delay.ticks());
  }

  static void writeLine(CompoundTag tag, DelayLine line) {
    ListTag list = new ListTag();
    for (DelayLine.Pending change : line.pending()) {
      CompoundTag entry = new CompoundTag();
      entry.putBoolean(ON, change.on());
      entry.putInt(TICKS, change.ticks());
      list.add(entry);
    }
    tag.put(LINE, list);
  }

  /** The saved delay of the module, named in the warning, within the config; none if missing. */
  static OutputDelay readDelay(CompoundTag tag, String module, OutputDelayLimits limits) {
    int ticks = tag.getInt(DELAY);
    OutputDelay fitted = OutputDelay.within(ticks, limits);
    if (ticks < 0 || ticks % OutputDelay.STEP_TICKS != 0) {
      LOGGER.warn(FITTED, module, ticks);
    }
    return fitted;
  }

  /** The saved changes of the module still waiting, in order; empty if missing. */
  static DelayLine readLine(CompoundTag tag, String module) {
    ListTag list = tag.getList(LINE, Tag.TAG_COMPOUND);
    List<DelayLine.Pending> changes = new ArrayList<>();
    for (int index = 0; index < list.size(); index++) {
      CompoundTag entry = list.getCompound(index);
      int ticks = entry.getInt(TICKS);
      if (!entry.contains(ON, Tag.TAG_BYTE) || ticks < 0) {
        LOGGER.warn(DROPPED, module, entry);
        continue;
      }
      changes.add(new DelayLine.Pending(entry.getBoolean(ON), ticks));
    }
    return DelayLine.restore(changes);
  }
}
