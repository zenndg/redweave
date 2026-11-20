package io.github.zenndg.redweave.domain.circuit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.zenndg.redweave.domain.signal.Signal;
import java.util.List;
import org.junit.jupiter.api.Test;

/** The truth tables written row by row, applied to the strengths 0, 1 and 15 (question 327). */
class GateTest {
  private static final boolean OFF = false;
  private static final boolean ON = true;
  private static final int WEAKEST_ON = 1;
  private static final List<Signal> OFF_INPUTS = List.of(Signal.OFF);
  private static final List<Signal> ON_INPUTS = List.of(new Signal(WEAKEST_ON), Signal.MAX);
  private static final String ROW = "%s(%d, %d)";

  @Test
  void andIsOnOnlyWhenBothInputsAreOn() {
    assertRow(Gate.AND, OFF, OFF, Signal.OFF);
    assertRow(Gate.AND, OFF, ON, Signal.OFF);
    assertRow(Gate.AND, ON, OFF, Signal.OFF);
    assertRow(Gate.AND, ON, ON, Signal.MAX);
  }

  @Test
  void orIsOnWhenAnyInputIsOn() {
    assertRow(Gate.OR, OFF, OFF, Signal.OFF);
    assertRow(Gate.OR, OFF, ON, Signal.MAX);
    assertRow(Gate.OR, ON, OFF, Signal.MAX);
    assertRow(Gate.OR, ON, ON, Signal.MAX);
  }

  @Test
  void xorIsOnWhenExactlyOneInputIsOn() {
    assertRow(Gate.XOR, OFF, OFF, Signal.OFF);
    assertRow(Gate.XOR, OFF, ON, Signal.MAX);
    assertRow(Gate.XOR, ON, OFF, Signal.MAX);
    assertRow(Gate.XOR, ON, ON, Signal.OFF);
  }

  @Test
  void nandIsOffOnlyWhenBothInputsAreOn() {
    assertRow(Gate.NAND, OFF, OFF, Signal.MAX);
    assertRow(Gate.NAND, OFF, ON, Signal.MAX);
    assertRow(Gate.NAND, ON, OFF, Signal.MAX);
    assertRow(Gate.NAND, ON, ON, Signal.OFF);
  }

  @Test
  void theGatesFollowTheScrollOrder() {
    assertEquals(List.of(Gate.AND, Gate.OR, Gate.XOR, Gate.NAND), List.of(Gate.values()));
  }

  @Test
  void rejectsAMissingFirstInput() {
    assertThrows(NullPointerException.class, () -> Gate.AND.output(null, Signal.MAX));
  }

  @Test
  void rejectsAMissingSecondInput() {
    assertThrows(NullPointerException.class, () -> Gate.AND.output(Signal.OFF, null));
  }

  private static void assertRow(Gate gate, boolean a, boolean b, Signal expected) {
    for (Signal first : inputs(a)) {
      for (Signal second : inputs(b)) {
        String row = String.format(ROW, gate, first.strength(), second.strength());
        assertEquals(expected, gate.output(first, second), row);
      }
    }
  }

  private static List<Signal> inputs(boolean on) {
    return on ? ON_INPUTS : OFF_INPUTS;
  }
}
