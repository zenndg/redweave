package io.github.zenndg.redweave.server;

import io.github.zenndg.redweave.application.ServerClock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * The clock of the machine that runs the server, in its time zone and to the second, as the log of
 * the Controller Screen shows it, such as {@code 2026-11-11 14:32:05} (answer 148).
 */
final class SystemServerClock implements ServerClock {
  @Override
  public LocalDateTime now() {
    return LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
  }
}
