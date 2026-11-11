package io.github.marcelofabianov.redweave.item;

import io.github.marcelofabianov.redweave.RedweaveMod;

/** The keys of the texts of the Configurator, in the action bar and the tooltip (190). */
public final class ConfiguratorMessages {
  private static final String PREFIX = "message." + RedweaveMod.MOD_ID + ".configurator.";
  private static final String TOOLTIP = "tooltip." + RedweaveMod.MOD_ID + ".configurator.";
  private static final String MODE_NAME = PREFIX + "mode.";
  private static final String MODE_HINT = TOOLTIP + "hint.";

  public static final String MODE = PREFIX + "mode";
  public static final String COPIED = PREFIX + "copied";
  public static final String PASTED = PREFIX + "pasted";
  public static final String ENABLED = PREFIX + "enabled";
  public static final String DISABLED = PREFIX + "disabled";
  public static final String CLEARED = PREFIX + "cleared";
  public static final String PICKED_UP = PREFIX + "picked_up";
  public static final String NOT_PLACER = PREFIX + "not_placer";
  public static final String PASTE_NOT_PLACER = PREFIX + "paste_not_placer";
  public static final String USE_TO_OPEN = PREFIX + "use_to_open";
  public static final String COPY = TOOLTIP + "copy";
  public static final String HINT_SCROLL = TOOLTIP + "hint_scroll";

  private ConfiguratorMessages() {}

  /** The name of a mode, such as "Open", shown in its color (answer 168). */
  public static String nameOf(ConfiguratorMode mode) {
    return MODE_NAME + mode.serializedName();
  }

  /** The hint of the tooltip for a mode (answer 190). */
  public static String hintOf(ConfiguratorMode mode) {
    return MODE_HINT + mode.serializedName();
  }
}
