package io.github.marcelofabianov.redweave.item;

import io.github.marcelofabianov.redweave.RedweaveMod;

/** The keys of the texts of the Signal Remote, in the action bar and the tooltip (366, 370). */
public final class RemoteMessages {
  private static final String PREFIX = "message." + RedweaveMod.MOD_ID + ".signal_remote.";
  private static final String TOOLTIP = "tooltip." + RedweaveMod.MOD_ID + ".signal_remote.";

  public static final String ON = PREFIX + "on";
  public static final String OFF = PREFIX + "off";
  public static final String PULSE = PREFIX + "pulse";
  public static final String HELD = PREFIX + "held";
  public static final String LINK_LOST = PREFIX + "link_lost";
  public static final String OFFLINE = PREFIX + "offline";
  public static final String LINKED = PREFIX + "linked";
  public static final String HOW_TO_LINK = TOOLTIP + "how_to_link";
  public static final String LINK = TOOLTIP + "link";
  public static final String NO_LINK = TOOLTIP + "no_link";
  public static final String MODE = TOOLTIP + "mode";
  public static final String LEVER = TOOLTIP + "mode.lever";
  public static final String BUTTON = TOOLTIP + "mode.button";
  public static final String STATE_ON = TOOLTIP + "state_on";
  public static final String STATE_OFF = TOOLTIP + "state_off";
  public static final String RULE = TOOLTIP + "rule";

  private RemoteMessages() {}

  public static String nameOf(RemoteMode mode) {
    return mode == RemoteMode.LEVER ? LEVER : BUTTON;
  }
}
