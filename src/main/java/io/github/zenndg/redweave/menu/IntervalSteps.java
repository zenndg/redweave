package io.github.zenndg.redweave.menu;

import io.github.zenndg.redweave.domain.module.Interval;

/**
 * The steps of `−` and `+` of an interval on a module screen: a click moves 0.1 s, and Shift +
 * click 1 s, in ticks (answer 398). A button sends the index of its step, which the server checks.
 */
final class IntervalSteps {
  private static final int SECOND = 20;

  private static final int[] STEPS = {-SECOND, -Interval.STEP_TICKS, Interval.STEP_TICKS, SECOND};

  /** How many steps there are, for the server to check an index. */
  static final int COUNT = STEPS.length;

  private IntervalSteps() {}

  /** The index of a step: shorter or longer, by 0.1 s or, with Shift, 1 s. */
  static int indexOf(boolean longer, boolean shift) {
    return (longer ? 2 : 0) + (longer == shift ? 1 : 0);
  }

  /** The ticks a step adds, negative to shorten. */
  static int ticks(int index) {
    return STEPS[index];
  }
}
