package io.github.zenndg.redweave.domain.circuit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.zenndg.redweave.domain.signal.Signal;
import org.junit.jupiter.api.Test;

/** The NOT truth table, applied to the strengths 0, 1 and 15 (question 327). */
class NotTest {
  private static final int WEAKEST_ON = 1;

  @Test
  void anInputThatIsOffGivesTheStrongestSignal() {
    assertEquals(Signal.MAX, Not.output(Signal.OFF));
  }

  @Test
  void theWeakestInputThatIsOnTurnsTheOutputOff() {
    assertEquals(Signal.OFF, Not.output(new Signal(WEAKEST_ON)));
  }

  @Test
  void theStrongestInputTurnsTheOutputOff() {
    assertEquals(Signal.OFF, Not.output(Signal.MAX));
  }

  @Test
  void rejectsAMissingInput() {
    assertThrows(NullPointerException.class, () -> Not.output(null));
  }
}
