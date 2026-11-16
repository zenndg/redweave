package io.github.marcelofabianov.redweave.config;

import io.github.marcelofabianov.redweave.domain.module.AlarmLimits;

/** The limits of the Signal Alarm, read from the server config (answers 646 and 648). */
public final class NeoForgeAlarmLimits implements AlarmLimits {
  @Override
  public int minIntervalTicks() {
    return RedweaveConfig.ALARM_MIN_INTERVAL_TICKS.get();
  }

  @Override
  public int maxIntervalTicks() {
    return RedweaveConfig.ALARM_MAX_INTERVAL_TICKS.get();
  }

  @Override
  public int maxRepeats() {
    return RedweaveConfig.ALARM_MAX_REPEATS.get();
  }

  @Override
  public int maxDurationTicks() {
    return RedweaveConfig.ALARM_MAX_DURATION_TICKS.get();
  }

  /** The blocks within which a listener hears the alarm (answer 646). */
  public static int range() {
    return RedweaveConfig.ALARM_RANGE.get();
  }
}
