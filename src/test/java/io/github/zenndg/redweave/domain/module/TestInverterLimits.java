package io.github.zenndg.redweave.domain.module;

/** Fixed inverter limits for the tests, with the starting values of _docs/limites.md (449). */
record TestInverterLimits(int burnoutToggles, int burnoutWindowTicks, int burnoutTicks)
    implements InverterLimits {
  private static final int TOGGLES = 8;
  private static final int WINDOW = 60;
  private static final int BURNOUT = 160;

  static final TestInverterLimits STARTING = new TestInverterLimits(TOGGLES, WINDOW, BURNOUT);
}
