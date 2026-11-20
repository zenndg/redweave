package io.github.zenndg.redweave.block.part;

import io.github.zenndg.redweave.domain.signal.DelayedOutput;
import io.github.zenndg.redweave.domain.signal.OutputDelay;
import io.github.zenndg.redweave.domain.signal.Signal;
import net.minecraft.nbt.CompoundTag;

/**
 * The delayed output a part holds, written once for every module with a delay: placed, loaded,
 * pasted, saved and moved one tick at a time (answers 830 to 854). The rule stays in the domain.
 */
final class OutputDelaySlot {
  /** What the Configurator copies of the delay: the delay, never what waits (answer 841). */
  static final String COPIED = DelaySave.DELAY;

  private DelayedOutput delayed = DelayedOutput.NONE;

  /** A new module has no delay (answer 844). */
  void place() {
    delayed = DelayedOutput.NONE;
  }

  /** Reads the delay and what waits, as the part loads and as it is pasted. */
  void load(CompoundTag tag, String module, Signal strength) {
    delayed = DelaySave.read(tag, module, strength, delayed);
  }

  void save(CompoundTag tag) {
    DelaySave.write(tag, delayed);
  }

  /** One tick, at the strength of the module; true when what waits changed, to be saved. */
  boolean tick(Signal value, Signal strength) {
    return moveTo(ModuleDelays.tick(delayed, value, strength));
  }

  /** One tick of a module whose strength is what it draws; true when what waits changed. */
  boolean tick(Signal value) {
    return moveTo(ModuleDelays.tick(delayed, value));
  }

  private boolean moveTo(DelayedOutput next) {
    boolean changed = !next.line().equals(delayed.line());
    delayed = next;
    return changed;
  }

  /** The owner changed the delay; what waits keeps its tick (answer 839). */
  void set(OutputDelay next) {
    delayed = delayed.withDelay(next);
  }

  Signal output() {
    return delayed.output();
  }

  DelayedOutput delayed() {
    return delayed;
  }
}
