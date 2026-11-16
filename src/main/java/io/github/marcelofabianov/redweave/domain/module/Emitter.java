package io.github.marcelofabianov.redweave.domain.module;

import io.github.marcelofabianov.redweave.domain.signal.Signal;

/**
 * A Signal Emitter: a module with no input, where the signal starts, on when its trigger happens
 * and giving its strength, from 1 to 15, while on (answers 552, 627 and 629). The Signal Lever, the
 * Signal Button and the Signal Pressure Plate are emitters.
 */
public interface Emitter {
  /** The weakest signal an emitter gives while on (answer 552). */
  int WEAKEST = 1;

  boolean on();

  Signal strength();

  /** The strength while on, nothing while off. */
  default Signal output() {
    return on() ? strength() : Signal.OFF;
  }

  /** A strength the `−` and `+` of a screen step to, kept from 1 to 15 (answer 552). */
  static Signal stepped(int next) {
    return Signal.clamped(Math.max(next, WEAKEST));
  }
}
