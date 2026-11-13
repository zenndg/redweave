package io.github.marcelofabianov.redweave.item;

import java.util.Arrays;
import java.util.Optional;

/** How a Signal Remote fires: a lever that turns on and off, or a button that pulses (288). */
public enum RemoteMode {
  LEVER(Saved.LEVER),
  BUTTON(Saved.BUTTON);

  private final String serializedName;

  RemoteMode(String serializedName) {
    this.serializedName = serializedName;
  }

  /** The name in the saves, which never changes without a migration (rule 15). */
  public String serializedName() {
    return serializedName;
  }

  public static Optional<RemoteMode> byName(String name) {
    return Arrays.stream(values()).filter(mode -> mode.serializedName.equals(name)).findFirst();
  }

  /** The names in the saves, apart so the constants come before the enum uses them. */
  private static final class Saved {
    static final String LEVER = "lever";
    static final String BUTTON = "button";
  }
}
