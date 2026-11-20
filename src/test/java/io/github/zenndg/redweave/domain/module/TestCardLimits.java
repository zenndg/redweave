package io.github.zenndg.redweave.domain.module;

/** Fixed card limits for the tests, with the starting value of _docs/limites.md (915). */
record TestCardLimits(int maxLinksPerItem) implements CardLimits {
  private static final int LINKS = 8;

  static final TestCardLimits STARTING = new TestCardLimits(LINKS);
}
