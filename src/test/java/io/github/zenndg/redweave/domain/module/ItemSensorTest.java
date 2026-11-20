package io.github.zenndg.redweave.domain.module;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zenndg.redweave.domain.signal.Signal;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * The rule of the Signal Item Sensor: no item, 1 and 15 when placed, on only with exactly the
 * expected count of the expected item, the count kept from 1 to the most of the config, and the
 * strength from 1 to 15 (answers 692, 693 and 696).
 */
class ItemSensorTest {
  private static final String DIAMOND = "minecraft:diamond";
  private static final int TEN = 10;
  private static final int NINE = 9;
  private static final int ELEVEN = 11;
  private static final int SEVEN = 7;
  private static final int MOST = 64;
  private static final int TOO_MANY = 100;
  private static final int TOO_STRONG = 20;
  private static final String BLANK = " ";

  private final ItemSensor tenDiamonds =
      ItemSensor.placed().withItem(Optional.of(DIAMOND)).withCount(TEN, MOST);

  @Test
  void aPlacedSensorExpectsNothingYet() {
    ItemSensor placed = ItemSensor.placed();

    assertTrue(placed.item().isEmpty());
    assertEquals(ItemSensor.FEWEST, placed.count());
    assertEquals(Signal.MAX, placed.strength());
    assertFalse(placed.on(ItemSensor.FEWEST));
  }

  @Test
  void exactlyTheExpectedCountTurnsItOn() {
    assertTrue(tenDiamonds.on(TEN));
    assertEquals(Signal.MAX, tenDiamonds.output(TEN));
  }

  @Test
  void oneLessOrOneMoreLeavesItOff() {
    assertEquals(Signal.OFF, tenDiamonds.output(NINE));
    assertEquals(Signal.OFF, tenDiamonds.output(ELEVEN));
  }

  @Test
  void withoutAnItemItNeverTurnsOn() {
    assertFalse(tenDiamonds.withItem(Optional.empty()).on(TEN));
  }

  @Test
  void theCountStaysFromOneToTheMost() {
    assertEquals(MOST, tenDiamonds.withCount(TOO_MANY, MOST).count());
    assertEquals(ItemSensor.FEWEST, tenDiamonds.withCount(0, MOST).count());
    assertEquals(ItemSensor.FEWEST, tenDiamonds.withCount(TEN, 0).count());
  }

  @Test
  void theStrengthStaysFromOneToFifteen() {
    assertEquals(new Signal(SEVEN), tenDiamonds.withStrength(SEVEN).output(TEN));
    assertEquals(Signal.MAX, tenDiamonds.withStrength(TOO_STRONG).strength());
    assertEquals(new Signal(ItemSensor.WEAKEST), tenDiamonds.withStrength(0).strength());
  }

  @Test
  void anInvalidSensorIsRefused() {
    Optional<String> blank = Optional.of(BLANK);
    assertThrows(IllegalArgumentException.class, () -> new ItemSensor(blank, 1, Signal.MAX));
    Optional<String> none = Optional.empty();
    assertThrows(IllegalArgumentException.class, () -> new ItemSensor(none, 0, Signal.MAX));
    assertThrows(IllegalArgumentException.class, () -> new ItemSensor(none, 1, Signal.OFF));
  }
}
