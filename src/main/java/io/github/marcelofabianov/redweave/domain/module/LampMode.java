package io.github.marcelofabianov.redweave.domain.module;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

/**
 * How the control channel of a Signal Lamp turns it off (answer 656): `HOLD` keeps it off while the
 * control has a signal, as a lever; `PULSE` turns it off or on again at each pulse that starts, as
 * a T flip-flop. The key is part of the save (rule 15).
 */
public enum LampMode {
  HOLD,
  PULSE;

  public String key() {
    return name().toLowerCase(Locale.ROOT);
  }

  public static Optional<LampMode> fromKey(String key) {
    return Arrays.stream(values()).filter(mode -> mode.key().equals(key)).findFirst();
  }
}
