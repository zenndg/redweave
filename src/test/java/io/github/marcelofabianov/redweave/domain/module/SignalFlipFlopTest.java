package io.github.marcelofabianov.redweave.domain.module;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * The rule of the Signal Flip-Flop: each rising edge of its input flips its output, which gives the
 * strength of its screen while on (answers 879 to 882, 889 and 893).
 */
class SignalFlipFlopTest {
  private static final int SEVEN = 7;
  private static final int TOO_STRONG = 20;
  private static final int TOO_WEAK = 0;
  private static final Signal WEAK = new Signal(SEVEN);
  private static final Optional<Signal> HIGH = Optional.of(Signal.MAX);
  private static final Optional<Signal> FAINT = Optional.of(new Signal(1));
  private static final Optional<Signal> LOW = Optional.of(Signal.OFF);
  private static final Optional<Signal> UNAVAILABLE = Optional.empty();

  @Test
  void aPlacedFlipFlopIsOffGivesFifteenAndHasNotReadItsInput() {
    SignalFlipFlop placed = SignalFlipFlop.placed();

    assertFalse(placed.on());
    assertEquals(Signal.MAX, placed.strength());
    assertEquals(FlipFlopInput.UNREAD, placed.input());
    assertEquals(Signal.OFF, placed.output());
  }

  @Test
  void anInputAlreadyOnWhenPlacedDoesNotFlipIt() {
    SignalFlipFlop read = SignalFlipFlop.placed().tick(HIGH);

    assertFalse(read.on());
    assertEquals(FlipFlopInput.ON, read.input());
  }

  @Test
  void aRisingEdgeFlipsItOn() {
    SignalFlipFlop flipped = SignalFlipFlop.placed().tick(LOW).tick(HIGH);

    assertTrue(flipped.on());
    assertEquals(Signal.MAX, flipped.output());
  }

  @Test
  void anInputThatStaysOffKeepsItOff() {
    SignalFlipFlop idle = SignalFlipFlop.placed().tick(LOW).tick(LOW);

    assertFalse(idle.on());
    assertEquals(FlipFlopInput.OFF, idle.input());
  }

  @Test
  void anyStrengthAboveZeroIsAnEdge() {
    assertTrue(SignalFlipFlop.placed().tick(LOW).tick(FAINT).on());
  }

  @Test
  void holdingTheInputDoesNotFlipItAgain() {
    SignalFlipFlop held = SignalFlipFlop.placed().tick(LOW).tick(HIGH).tick(HIGH).tick(HIGH);

    assertTrue(held.on());
  }

  @Test
  void aFallingEdgeChangesNothing() {
    SignalFlipFlop released = SignalFlipFlop.placed().tick(LOW).tick(HIGH).tick(LOW);

    assertTrue(released.on());
    assertEquals(FlipFlopInput.OFF, released.input());
  }

  @Test
  void theNextPulseFlipsItOffAgain() {
    SignalFlipFlop twice = SignalFlipFlop.placed().tick(LOW).tick(HIGH).tick(LOW).tick(HIGH);

    assertFalse(twice.on());
    assertEquals(Signal.OFF, twice.output());
  }

  @Test
  void anUnavailableInputFreezesTheLastReading() {
    SignalFlipFlop on = SignalFlipFlop.placed().tick(LOW).tick(HIGH);

    SignalFlipFlop frozen = on.tick(UNAVAILABLE);
    assertEquals(on, frozen);
    assertTrue(frozen.tick(HIGH).on());
  }

  @Test
  void anUnavailableInputBeforeTheFirstReadingKeepsItUnread() {
    SignalFlipFlop frozen = SignalFlipFlop.placed().tick(UNAVAILABLE);

    assertEquals(FlipFlopInput.UNREAD, frozen.input());
    assertFalse(frozen.tick(HIGH).on());
  }

  @Test
  void aRestoredFlipFlopWithItsInputOnDoesNotFlipWhenItLoads() {
    SignalFlipFlop restored = new SignalFlipFlop(true, Signal.MAX, FlipFlopInput.ON);

    assertTrue(restored.tick(HIGH).on());
  }

  @Test
  void theToggleFlipsItAndKeepsTheReading() {
    SignalFlipFlop read = SignalFlipFlop.placed().tick(HIGH);

    SignalFlipFlop toggled = read.toggle();
    assertTrue(toggled.on());
    assertEquals(FlipFlopInput.ON, toggled.input());
    assertFalse(toggled.toggle().on());
  }

  @Test
  void itGivesItsChosenStrengthWhileOn() {
    SignalFlipFlop weak = SignalFlipFlop.placed().withStrength(SEVEN).toggle();

    assertEquals(WEAK, weak.output());
  }

  @Test
  void theStrengthIsKeptFromOneToFifteen() {
    assertEquals(Signal.MAX, SignalFlipFlop.placed().withStrength(TOO_STRONG).strength());
    assertEquals(new Signal(1), SignalFlipFlop.placed().withStrength(TOO_WEAK).strength());
  }

  @Test
  void aStrengthOfZeroIsRejected() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new SignalFlipFlop(false, Signal.OFF, FlipFlopInput.UNREAD));
  }

  @Test
  void theReadingIsRequired() {
    assertThrows(NullPointerException.class, () -> new SignalFlipFlop(false, Signal.MAX, null));
  }

  @Test
  void aSavedReadingBecomesOnOrOff() {
    assertEquals(FlipFlopInput.ON, FlipFlopInput.read(true));
    assertEquals(FlipFlopInput.OFF, FlipFlopInput.read(false));
  }
}
