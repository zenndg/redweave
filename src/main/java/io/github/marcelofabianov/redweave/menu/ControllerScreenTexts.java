package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.RedweaveMod;

/**
 * The translation keys of the Controller Screen. They live outside the client package, because the
 * data generation, which is common code, writes the texts (AGENTS.md, rules 1 and 19).
 */
public final class ControllerScreenTexts {
  private static final String PREFIX = "screen." + RedweaveMod.MOD_ID + ".controller.";

  public static final String TAB_NODES = PREFIX + "tab.nodes";
  public static final String TAB_SIGNALS = PREFIX + "tab.signals";
  public static final String TAB_ACCESS = PREFIX + "tab.access";
  public static final String RENAME = PREFIX + "rename";
  public static final String RECOLOR_TOOLTIP = PREFIX + "recolor_tooltip";
  public static final String RENAME_TOOLTIP = PREFIX + "rename_tooltip";
  public static final String COLUMN_NODE = PREFIX + "column.node";
  public static final String COLUMN_SIGNAL = PREFIX + "column.signal";
  public static final String COLUMN_POSITION = PREFIX + "column.position";
  public static final String SLOT_ROW = PREFIX + "slot_row";
  public static final String SLOT_ROW_NAMED = PREFIX + "slot_row_named";
  public static final String NO_ACTIVE_CHANNEL = PREFIX + "no_active_channel";
  public static final String SLOT_USE = PREFIX + "slot_use";
  public static final String CHANNELS_IN_USE = PREFIX + "channels_in_use";
  public static final String UNLOADED = PREFIX + "unloaded";
  public static final String OUT_OF_RANGE = PREFIX + "out_of_range";
  public static final String OWNER_LINE = PREFIX + "owner_line";
  public static final String OWNER = PREFIX + "owner";
  public static final String READ_ONLY = PREFIX + "read_only";
  public static final String NO_NODES = PREFIX + "no_nodes";

  private ControllerScreenTexts() {}
}
