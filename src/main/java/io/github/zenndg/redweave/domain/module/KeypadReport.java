package io.github.zenndg.redweave.domain.module;

import io.github.zenndg.redweave.domain.network.ModuleId;
import io.github.zenndg.redweave.domain.network.PlayerId;
import java.util.Objects;

/**
 * The misses of one keypad an offline owner gets when joining: its name, the last player who missed
 * and the total of misses (answers 666, 903 and 911).
 */
public record KeypadReport(ModuleId keypad, String name, PlayerId lastPlayer, int misses) {
  private static final String INVALID = "A report needs a keypad, a name, a player and 1+ miss";

  public KeypadReport {
    Objects.requireNonNull(keypad, INVALID);
    Objects.requireNonNull(name, INVALID);
    Objects.requireNonNull(lastPlayer, INVALID);
    if (misses < 1) {
      throw new IllegalArgumentException(INVALID);
    }
  }
}
