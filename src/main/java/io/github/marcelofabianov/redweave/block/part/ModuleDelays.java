package io.github.marcelofabianov.redweave.block.part;

import io.github.marcelofabianov.redweave.config.NeoForgeDelayLineLimits;
import io.github.marcelofabianov.redweave.domain.signal.DelayLineLimits;
import io.github.marcelofabianov.redweave.domain.signal.DelayedOutput;
import io.github.marcelofabianov.redweave.domain.signal.Signal;

/**
 * The tick of a delayed output in a part: what the rule decides now, at the strength of the module,
 * enters the line, and what comes out is written (answers 843, 847 and 854). The rule stays in the
 * domain; this only reads the config.
 */
final class ModuleDelays {
  static final DelayLineLimits LINE_LIMITS = new NeoForgeDelayLineLimits();

  private ModuleDelays() {}

  /** The output after a tick, with the strength of the module taken at once (answer 843). */
  static DelayedOutput tick(DelayedOutput now, Signal value, Signal strength) {
    return now.withStrength(strength).tick(value, LINE_LIMITS);
  }

  /** The output after a tick, for a module whose strength is what it draws (answer 854). */
  static DelayedOutput tick(DelayedOutput now, Signal value) {
    return now.tick(value, LINE_LIMITS);
  }
}
