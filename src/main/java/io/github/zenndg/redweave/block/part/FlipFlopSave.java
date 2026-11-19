package io.github.zenndg.redweave.block.part;

import io.github.zenndg.redweave.domain.module.FlipFlopInput;
import io.github.zenndg.redweave.domain.module.SignalFlipFlop;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

/**
 * The NBT of a Signal Flip-Flop besides its module, part of the save (rule 15): its two
 * connections, whether its output is on, under `on`, the last reading of its input, under
 * `input_on`, and its strength (answer 886). `input_on` is written only once the input was read,
 * and without it the input counts as unread, so the first tick only reads it (answer 893). An
 * invalid strength goes back to 15 with a warning (rule 14).
 */
final class FlipFlopSave {
  static final String INPUT = "input";
  static final String OUTPUT = "output";

  private static final String ON = "on";
  private static final String INPUT_ON = "input_on";
  private static final String MODULE = "Signal Flip-Flop";

  private FlipFlopSave() {}

  static void writeFlipFlop(CompoundTag tag, SignalFlipFlop flipFlop) {
    tag.putBoolean(ON, flipFlop.on());
    StrengthSave.write(tag, flipFlop.strength());
    if (flipFlop.input() != FlipFlopInput.UNREAD) {
      tag.putBoolean(INPUT_ON, flipFlop.input() == FlipFlopInput.ON);
    }
  }

  static SignalFlipFlop readFlipFlop(CompoundTag tag) {
    FlipFlopInput input =
        tag.contains(INPUT_ON, Tag.TAG_BYTE)
            ? FlipFlopInput.read(tag.getBoolean(INPUT_ON))
            : FlipFlopInput.UNREAD;
    return new SignalFlipFlop(tag.getBoolean(ON), StrengthSave.read(tag, MODULE), input);
  }
}
