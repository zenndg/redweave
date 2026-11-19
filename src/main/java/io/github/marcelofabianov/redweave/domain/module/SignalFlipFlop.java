package io.github.marcelofabianov.redweave.domain.module;

import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.Objects;
import java.util.Optional;

/**
 * The rule of the Signal Flip-Flop, a T flip-flop: each rising edge of its input, from 0 to above
 * 0, flips its output, in the tick of the module, with no delay of its own (answers 879 and 892).
 * While on, it gives the strength chosen on its screen, from 1 to 15 (answer 880). It is born off
 * with its input unread, so an input already on is not an edge (answers 881 and 893). An input on a
 * channel that is not available keeps the last reading, so turning a network off and on does not
 * flip it (answer 889). It never burns out: fed by its own output, it settles off (answer 883).
 */
public record SignalFlipFlop(boolean on, Signal strength, FlipFlopInput input) {
  private static final String TOO_WEAK = "A flip-flop gives from 1 to 15, got %d";

  public SignalFlipFlop {
    Objects.requireNonNull(strength);
    Objects.requireNonNull(input);
    if (!strength.isOn()) {
      throw new IllegalArgumentException(String.format(TOO_WEAK, strength.strength()));
    }
  }

  /** A new flip-flop: off, giving 15 while on, with its input not read yet (880 and 881). */
  public static SignalFlipFlop placed() {
    return new SignalFlipFlop(false, Signal.MAX, FlipFlopInput.UNREAD);
  }

  /** One tick with the input, or none while its channel is not available (answer 889). */
  public SignalFlipFlop tick(Optional<Signal> reading) {
    if (reading.isEmpty()) {
      return this;
    }
    FlipFlopInput next = FlipFlopInput.of(reading.get());
    return new SignalFlipFlop(on != input.risesTo(next), strength, next);
  }

  /** The `Toggle` of the owner flips it as an edge would, keeping the reading (answer 882). */
  public SignalFlipFlop toggle() {
    return new SignalFlipFlop(!on, strength, input);
  }

  /** Another strength, kept from 1 to 15, as the `−` and `+` of the screen step it (880). */
  public SignalFlipFlop withStrength(int next) {
    return new SignalFlipFlop(on, Emitter.stepped(next), input);
  }

  /** The chosen strength while on, nothing while off (answer 880). */
  public Signal output() {
    return on ? strength : Signal.OFF;
  }
}
