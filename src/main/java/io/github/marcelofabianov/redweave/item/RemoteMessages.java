package io.github.marcelofabianov.redweave.item;

import io.github.marcelofabianov.redweave.RedweaveMod;
import net.minecraft.network.chat.Component;

/**
 * The keys of the texts of the Signal Remote, in the action bar and the tooltip (366, 370, 785 and
 * 789).
 */
public final class RemoteMessages {
  private static final String PREFIX = "message." + RedweaveMod.MOD_ID + ".signal_remote.";
  private static final String TOOLTIP = "tooltip." + RedweaveMod.MOD_ID + ".signal_remote.";

  public static final String FIRED = PREFIX + "fired";
  public static final String ON = PREFIX + "on";
  public static final String OFF = PREFIX + "off";
  public static final String PULSE = PREFIX + "pulse";
  public static final String HELD = PREFIX + "held";
  public static final String LINK_LOST = PREFIX + "link_lost";
  public static final String OFFLINE = PREFIX + "offline";
  public static final String LINKED = PREFIX + "linked";
  public static final String LINKED_EMITTER = PREFIX + "linked_emitter";
  public static final String OUT_OF_REACH = PREFIX + "out_of_reach";
  public static final String HOW_TO_LINK = TOOLTIP + "how_to_link";
  public static final String LINK = TOOLTIP + "link";
  public static final String NO_LINK = TOOLTIP + "no_link";
  public static final String LINK_EMITTER = TOOLTIP + "link_emitter";
  public static final String TURNS = TOOLTIP + "turns";
  public static final String PRESSES = TOOLTIP + "presses";
  public static final String MODE = TOOLTIP + "mode";
  public static final String LEVER = TOOLTIP + "mode.lever";
  public static final String BUTTON = TOOLTIP + "mode.button";
  public static final String STATE_ON = TOOLTIP + "state_on";
  public static final String STATE_OFF = TOOLTIP + "state_off";
  public static final String RULE = TOOLTIP + "rule";

  /** Lit, in bold green, and off, in bold red, the colors of the palette (436 and 437). */
  private static final int LIT = 0x6BD48F;

  private static final int UNLIT = 0xE2392F;

  private RemoteMessages() {}

  /** The word of the state, in bold and in its color, so it is told apart at a glance (436). */
  public static Component state(boolean on, String onKey, String offKey) {
    return Component.translatable(on ? onKey : offKey)
        .withStyle(style -> style.withBold(true).withColor(on ? LIT : UNLIT));
  }

  public static String nameOf(RemoteMode mode) {
    return mode == RemoteMode.LEVER ? LEVER : BUTTON;
  }
}
