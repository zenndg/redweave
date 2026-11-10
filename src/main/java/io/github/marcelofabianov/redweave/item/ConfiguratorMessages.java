package io.github.marcelofabianov.redweave.item;

import io.github.marcelofabianov.redweave.RedweaveMod;

/** The keys of the texts of the Configurator, in the action bar and the tooltip (answer 108). */
public final class ConfiguratorMessages {
  private static final String PREFIX = "message." + RedweaveMod.MOD_ID + ".configurator.";
  private static final String TOOLTIP = "tooltip." + RedweaveMod.MOD_ID + ".configurator.";

  public static final String STORED = PREFIX + "stored";
  public static final String NOT_YOURS = PREFIX + "not_yours";
  public static final String LINKED = PREFIX + "linked";
  public static final String NOTHING_STORED = PREFIX + "nothing_stored";
  public static final String ALREADY_IN = PREFIX + "already_in";
  public static final String OWN_BOTH = PREFIX + "own_both";
  public static final String GONE = PREFIX + "gone";
  public static final String COPIED = PREFIX + "copied";
  public static final String PASTED = PREFIX + "pasted";
  public static final String PASTED_LEFT_OUT = PREFIX + "pasted_left_out";
  public static final String CLEARED = PREFIX + "cleared";
  public static final String NETWORK = TOOLTIP + "network";
  public static final String NO_NETWORK_STORED = TOOLTIP + "no_network";
  public static final String COPY = TOOLTIP + "copy";
  public static final String HINT_STORE = TOOLTIP + "hint_store";
  public static final String HINT_COPY = TOOLTIP + "hint_copy";

  private ConfiguratorMessages() {}
}
