package io.github.zenndg.redweave.client.screen;

import java.util.Objects;
import java.util.Optional;
import java.util.function.LongSupplier;

/**
 * Tells a double click on the same thing apart from loose clicks, as the owner renames a node or a
 * channel of the Controller Screen (answer 222). The window is the one the game uses for a double
 * click on an item slot.
 */
final class DoubleClick {
  static final long WINDOW_MILLIS = 250;

  private final LongSupplier clock;
  private Optional<Object> last = Optional.empty();
  private long lastAt;

  DoubleClick(LongSupplier clock) {
    this.clock = clock;
  }

  /** Records a click on a target and tells whether it completes a double click. */
  boolean click(Object target) {
    long now = clock.getAsLong();
    boolean isDouble =
        last.filter(previous -> Objects.equals(previous, target)).isPresent()
            && now - lastAt <= WINDOW_MILLIS;
    last = isDouble ? Optional.empty() : Optional.of(target);
    lastAt = now;
    return isDouble;
  }

  /** A click somewhere else breaks the pair. */
  void reset() {
    last = Optional.empty();
  }
}
