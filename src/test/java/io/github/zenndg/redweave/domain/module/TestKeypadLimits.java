package io.github.zenndg.redweave.domain.module;

/** Fixed keypad limits for the tests, with the starting values of _docs/limites.md (915). */
record TestKeypadLimits(
    int minIntervalTicks,
    int maxIntervalTicks,
    int freeWindowTicks,
    int freeTries,
    int firstWaitTicks,
    int maxWaitTicks,
    int resetTicks)
    implements KeypadLimits {
  private static final int MIN_OPEN = 20;
  private static final int MAX_OPEN = 600;
  private static final int FREE_WINDOW = 1200;
  private static final int FREE_TRIES = 3;
  private static final int FIRST_WAIT = 100;
  private static final int MAX_WAIT = 6000;
  private static final int RESET = 12000;

  static final TestKeypadLimits STARTING =
      new TestKeypadLimits(
          MIN_OPEN, MAX_OPEN, FREE_WINDOW, FREE_TRIES, FIRST_WAIT, MAX_WAIT, RESET);
}
