package io.github.zenndg.redweave.domain.module;

import io.github.zenndg.redweave.domain.signal.Signal;
import java.util.Objects;
import java.util.Optional;

/**
 * The rule of the Signal Item Sensor: on only when the container it sticks on holds exactly the
 * expected count of the expected item, summing every slot, and giving its strength, from 1 to 15,
 * while on (answers 689, 692 and 696). The item is the key of its type, as `minecraft:diamond`,
 * compared without its components (answer 697). A new sensor expects no item yet, so it never turns
 * on until its owner picks one.
 */
public record ItemSensor(Optional<String> item, int count, Signal strength) {
  /** The smallest count a sensor expects (answer 693). */
  public static final int FEWEST = 1;

  /** The weakest signal it gives while on (answer 696). */
  public static final int WEAKEST = 1;

  private static final String NO_COUNT = "A sensor expects at least 1 item, got %d";
  private static final String TOO_WEAK = "A sensor gives from 1 to 15, got %d";
  private static final String BLANK = "The key of an item cannot be blank";

  public ItemSensor {
    Objects.requireNonNull(item);
    Objects.requireNonNull(strength);
    if (item.isPresent() && item.get().isBlank()) {
      throw new IllegalArgumentException(BLANK);
    }
    if (count < FEWEST) {
      throw new IllegalArgumentException(String.format(NO_COUNT, count));
    }
    if (!strength.isOn()) {
      throw new IllegalArgumentException(String.format(TOO_WEAK, strength.strength()));
    }
  }

  /** A new sensor: no item, expecting 1, with the strongest signal (answer 696). */
  public static ItemSensor placed() {
    return new ItemSensor(Optional.empty(), FEWEST, Signal.MAX);
  }

  /** Whether the count found in the container is exactly the expected one (answer 692). */
  public boolean on(int counted) {
    return item.isPresent() && counted == count;
  }

  /** Its strength with exactly the expected count, nothing otherwise. */
  public Signal output(int counted) {
    return on(counted) ? strength : Signal.OFF;
  }

  /** Another expected item, or none, as the owner picks on the screen (answer 696). */
  public ItemSensor withItem(Optional<String> next) {
    return new ItemSensor(next, count, strength);
  }

  /** Another count, kept from 1 to the most the config allows (answer 693). */
  public ItemSensor withCount(int next, int most) {
    return new ItemSensor(item, Math.clamp(next, FEWEST, Math.max(most, FEWEST)), strength);
  }

  /** Another strength, kept from 1 to 15, as the `−` and `+` of the screen step it (696). */
  public ItemSensor withStrength(int next) {
    return new ItemSensor(item, count, Signal.clamped(Math.max(next, WEAKEST)));
  }
}
