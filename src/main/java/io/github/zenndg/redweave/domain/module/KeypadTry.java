package io.github.zenndg.redweave.domain.module;

import io.github.zenndg.redweave.domain.network.PlayerId;
import java.util.Objects;

/**
 * A code typed on a Signal Keypad: by whom, whether that player owns it, whether its input has
 * power and the tick of the world (answers 661, 664, 907 and 914).
 */
public record KeypadTry(
    PlayerId player, boolean owner, KeypadCode code, boolean powered, long now) {
  public KeypadTry {
    Objects.requireNonNull(player);
    Objects.requireNonNull(code);
  }
}
