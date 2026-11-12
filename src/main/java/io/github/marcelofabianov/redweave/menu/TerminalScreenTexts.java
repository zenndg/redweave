package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.RedweaveMod;

/**
 * The translation keys of the screen of the Network Terminal (answers 146, 255, 256 and 261). They
 * live outside the client package, because the data generation writes the texts (rule 19).
 */
public final class TerminalScreenTexts {
  private static final String PREFIX = "screen." + RedweaveMod.MOD_ID + ".terminal.";

  public static final String COUNT_ONE = PREFIX + "count.one";
  public static final String COUNT_MANY = PREFIX + "count.many";
  public static final String STATE_UNPLACED = PREFIX + "state.unplaced";
  public static final String OPEN = PREFIX + "open";
  public static final String TURN_OFF = PREFIX + "turn_off";
  public static final String TURN_ON = PREFIX + "turn_on";
  public static final String CLEAR_NODES = PREFIX + "clear_nodes";
  public static final String DELETE = PREFIX + "delete";
  public static final String CONFIRM_CLEAR = PREFIX + "confirm_clear";
  public static final String CONFIRM_DELETE = PREFIX + "confirm_delete";
  public static final String HINT = PREFIX + "hint";
  public static final String NO_NETWORKS = PREFIX + "no_networks";

  private TerminalScreenTexts() {}
}
