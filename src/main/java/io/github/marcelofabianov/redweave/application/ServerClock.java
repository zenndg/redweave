package io.github.marcelofabianov.redweave.application;

import java.time.LocalDateTime;

/**
 * The real date and time of the server, for the lines of the log. The domain does not read the
 * clock, so the game gives it through this port (answer 148).
 */
public interface ServerClock {
  LocalDateTime now();
}
