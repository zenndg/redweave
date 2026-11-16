package io.github.marcelofabianov.redweave.menu;

import java.util.Optional;

/**
 * What a button of the Signal Item Sensor Screen asks the server: the ghost slot, which takes the
 * item on the cursor, a step of the count or of `Strength`, a connection of `Output`, or the
 * position in the network choice (answers 414 and 696).
 */
public record ItemSensorAction(Kind kind, int value) {
  /** The value of a connection on the edge; a channel is its color plus one. */
  public static final int EDGE = ModuleScreenValues.EDGE;

  /** A step of the count by 10, with Shift held (answer 696). */
  public static final int SHIFT_STEP = 10;

  private static final int LESS = 0;
  private static final int MORE = 1;
  private static final int MUCH_LESS = 2;
  private static final int MUCH_MORE = 3;
  private static final int GHOST = 0;
  private static final String INVALID = "Invalid value %d for %s";

  public enum Kind {
    ITEM,
    COUNT,
    STRENGTH,
    OUTPUT,
    CHOOSE
  }

  public ItemSensorAction {
    if (!fits(kind, value)) {
      throw new IllegalArgumentException(String.format(INVALID, value, kind));
    }
  }

  /** A click on the ghost slot: the item on the cursor, or none with an empty cursor. */
  public static ItemSensorAction ghost() {
    return new ItemSensorAction(Kind.ITEM, GHOST);
  }

  /** A step of the count: one, or ten with Shift, up or down (answer 696). */
  public static ItemSensorAction count(boolean more, boolean shift) {
    int code = shift ? (more ? MUCH_MORE : MUCH_LESS) : (more ? MORE : LESS);
    return new ItemSensorAction(Kind.COUNT, code);
  }

  /** A step of `Strength`: `−` one weaker and `+` one stronger. */
  public static ItemSensorAction strength(boolean stronger) {
    return new ItemSensorAction(Kind.STRENGTH, stronger ? MORE : LESS);
  }

  public static ItemSensorAction output(Optional<Integer> channel) {
    return new ItemSensorAction(Kind.OUTPUT, channel.map(index -> index + 1).orElse(EDGE));
  }

  public static ItemSensorAction choose(int position) {
    return new ItemSensorAction(Kind.CHOOSE, position);
  }

  /** The code the game sends with the click. */
  public int button() {
    return ModuleScreenCodes.button(kind.ordinal(), value);
  }

  /** The action of a code, or none for a code no button sends. */
  public static Optional<ItemSensorAction> of(int button) {
    int kind = ModuleScreenCodes.kindOf(button);
    int value = ModuleScreenCodes.valueOf(button);
    if (button < 0 || kind >= Kind.values().length || !fits(Kind.values()[kind], value)) {
      return Optional.empty();
    }
    return Optional.of(new ItemSensorAction(Kind.values()[kind], value));
  }

  /** What a step adds: one or ten, more or less. */
  public int step() {
    int size = value >= MUCH_LESS ? SHIFT_STEP : 1;
    return value == MORE || value == MUCH_MORE ? size : -size;
  }

  /** The channel of a connection, by its color, or none on the edge. */
  public Optional<Integer> channel() {
    return value == ModuleScreenValues.EDGE ? Optional.empty() : Optional.of(value - 1);
  }

  private static boolean fits(Kind kind, int value) {
    return switch (kind) {
      case ITEM -> value == GHOST;
      case COUNT -> value >= LESS && value <= MUCH_MORE;
      case STRENGTH -> value == LESS || value == MORE;
      case OUTPUT -> value >= ModuleScreenValues.EDGE && value < ModuleScreenValues.CONNECTIONS;
      case CHOOSE -> value >= 0;
    };
  }
}
