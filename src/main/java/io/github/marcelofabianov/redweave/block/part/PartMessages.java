package io.github.marcelofabianov.redweave.block.part;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.domain.part.Fit;

/** The keys of the texts of placing a part, which the clients write in their language (533). */
public final class PartMessages {
  private static final String PREFIX = "message." + RedweaveMod.MOD_ID + ".part.";
  public static final String FACE_TAKEN = PREFIX + "face_taken";
  public static final String CABLES_FULL = PREFIX + "cables_full";

  private PartMessages() {}

  /** The text of a refusal; only a refusal has one. */
  public static String of(Fit refusal) {
    return refusal == Fit.CABLES_FULL ? CABLES_FULL : FACE_TAKEN;
  }
}
