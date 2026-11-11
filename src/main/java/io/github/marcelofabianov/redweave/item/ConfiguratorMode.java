package io.github.marcelofabianov.redweave.item;

import java.util.Arrays;
import java.util.Optional;

/**
 * The modes of the Configurator, in the order Shift + scroll goes through them (answers 143 and
 * 164). The saved names are the contract of {@code redweave:configurator_mode} (answer 173), and
 * each mode has its own color, always shown with its name (answers 167 to 169).
 */
public enum ConfiguratorMode {
  OPEN(Saved.OPEN, 0x5B9BF0),
  COPY(Saved.COPY, 0x3FAE5A),
  WRENCH(Saved.WRENCH, 0xD94A64);

  /** The mode of a Configurator that was never switched. */
  public static final ConfiguratorMode FIRST = OPEN;

  private static final int STEP = 1;

  private final String serializedName;
  private final int color;

  ConfiguratorMode(String serializedName, int color) {
    this.serializedName = serializedName;
    this.color = color;
  }

  public String serializedName() {
    return serializedName;
  }

  /** The RGB color of the mode, without alpha, as the text styles of the game take it. */
  public int color() {
    return color;
  }

  public ConfiguratorMode next() {
    return values()[(ordinal() + STEP) % values().length];
  }

  public ConfiguratorMode previous() {
    return values()[(ordinal() - STEP + values().length) % values().length];
  }

  /** Like the hotbar: scrolling down, a negative delta, goes forward (answer 192). */
  public ConfiguratorMode scrolled(double delta) {
    return delta < 0 ? next() : previous();
  }

  /** The saved names, in a class of their own because an enum cannot name its own constants. */
  private static final class Saved {
    static final String OPEN = "open";
    static final String COPY = "copy";
    static final String WRENCH = "wrench";

    private Saved() {}
  }

  public static Optional<ConfiguratorMode> byName(String name) {
    return Arrays.stream(values()).filter(mode -> mode.serializedName.equals(name)).findFirst();
  }
}
