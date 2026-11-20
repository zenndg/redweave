package io.github.zenndg.redweave.domain.module;

/**
 * The numbers of the Signal Keypad that the pack sets in the config: the shortest and the longest
 * open time, and the free tries, the window that gives them back, the first and the longest wait
 * and the ticks with no try that clear a count (answers 663, 665, 913 and 915). The domain defines
 * it, and the config implements it.
 */
public interface KeypadLimits extends IntervalLimits {
  int freeWindowTicks();

  int freeTries();

  int firstWaitTicks();

  int maxWaitTicks();

  int resetTicks();
}
