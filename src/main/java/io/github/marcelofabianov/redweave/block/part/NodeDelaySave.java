package io.github.marcelofabianov.redweave.block.part;

import io.github.marcelofabianov.redweave.domain.signal.DelayLine;
import io.github.marcelofabianov.redweave.domain.signal.DelayedOutput;
import io.github.marcelofabianov.redweave.domain.signal.OutputDelay;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import net.minecraft.nbt.CompoundTag;

/**
 * The changes the output of a Signal Node still waits for, in the NBT of its part (answers 860, 869
 * and 873): `delay_line`, as in a module, and `delayed_output`, what the node gave before them,
 * both written only while a change waits. The delay itself is saved with the node in its network,
 * so the part takes it with its first emission. A save without the keys has nothing waiting, with
 * no migration (rule 15), and an invalid change is dropped with a warning (rule 14).
 */
final class NodeDelaySave {
  static final String OUTPUT = "delayed_output";
  private static final String NODE = "signal_node";

  private NodeDelaySave() {}

  static void write(CompoundTag tag, DelayedOutput delayed) {
    if (delayed.line().isEmpty()) {
      return;
    }
    DelaySave.writeLine(tag, delayed.line());
    tag.putInt(OUTPUT, delayed.output().strength());
  }

  static DelayedOutput read(CompoundTag tag) {
    if (!tag.contains(DelaySave.LINE)) {
      return DelayedOutput.NONE;
    }
    DelayLine line = DelaySave.readLine(tag, NODE, Signal.OFF);
    return DelayedOutput.resumed(OutputDelay.NONE, line, Signal.clamped(tag.getInt(OUTPUT)));
  }
}
