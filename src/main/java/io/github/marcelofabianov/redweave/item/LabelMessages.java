package io.github.marcelofabianov.redweave.item;

import io.github.marcelofabianov.redweave.RedweaveMod;

/** The keys of the texts of the Label (answers 119 and 122). */
public final class LabelMessages {
  private static final String PREFIX = "message." + RedweaveMod.MOD_ID + ".label.";
  private static final String TOOLTIP = "tooltip." + RedweaveMod.MOD_ID + ".label.";

  public static final String WRITTEN = PREFIX + "written";
  public static final String SLOT = TOOLTIP + "slot";
  public static final String EMPTY = TOOLTIP + "empty";
  public static final String HINT_APPLY = TOOLTIP + "hint_apply";
  public static final String HINT_WRITE = TOOLTIP + "hint_write";

  private LabelMessages() {}
}
