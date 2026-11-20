package io.github.zenndg.redweave.config;

import io.github.zenndg.redweave.domain.module.CardLimits;
import io.github.zenndg.redweave.domain.module.KeypadLimits;

/** The limits of the Signal Keypad and of its cards, read from the server config (answer 915). */
public final class NeoForgeKeypadLimits implements KeypadLimits, CardLimits {
  @Override
  public int minIntervalTicks() {
    return KeypadConfig.minOpenTicks.get();
  }

  @Override
  public int maxIntervalTicks() {
    return KeypadConfig.maxOpenTicks.get();
  }

  @Override
  public int freeWindowTicks() {
    return KeypadConfig.freeWindowTicks.get();
  }

  @Override
  public int freeTries() {
    return KeypadConfig.freeTries.get();
  }

  @Override
  public int firstWaitTicks() {
    return KeypadConfig.firstWaitTicks.get();
  }

  @Override
  public int maxWaitTicks() {
    return KeypadConfig.maxWaitTicks.get();
  }

  @Override
  public int resetTicks() {
    return KeypadConfig.resetTicks.get();
  }

  @Override
  public int maxLinksPerItem() {
    return KeypadConfig.maxLinksPerItem.get();
  }

  /** Signal Keypads in the summary of an offline owner (answers 911 and 915). */
  public int maxOfflineReports() {
    return KeypadConfig.maxOfflineReports.get();
  }
}
