package io.github.marcelofabianov.redweave.block.part;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.domain.part.Fit;

/**
 * The keys of the texts of placing a part and of picking a cable up, which the clients write in
 * their language (answers 525 and 533).
 */
public final class PartMessages {
  private static final String PREFIX = "message." + RedweaveMod.MOD_ID + ".part.";
  private static final String CABLE = "message." + RedweaveMod.MOD_ID + ".signal_cable.";
  private static final String PLATE = "message." + RedweaveMod.MOD_ID + ".signal_pressure_plate.";
  private static final String SENSOR = "message." + RedweaveMod.MOD_ID + ".signal_item_sensor.";
  public static final String FACE_TAKEN = PREFIX + "face_taken";
  public static final String CABLES_FULL = PREFIX + "cables_full";

  /** The Wrench picked a Signal Cable up (answer 525). */
  public static final String CABLE_PICKED_UP = CABLE + "picked_up";

  /** Only the owner picks a Signal Cable up in survival (answer 526). */
  public static final String CABLE_NOT_OWNER = CABLE + "not_owner";

  /** The Signal Pressure Plate only goes on the floor (answer 611). */
  public static final String FLOOR_ONLY = PLATE + "floor_only";

  /** A Signal Item Sensor only goes on a container (answer 694). */
  public static final String NEEDS_CONTAINER = SENSOR + "needs_container";

  private PartMessages() {}

  /** The text of a refusal; only a refusal has one. */
  public static String of(Fit refusal) {
    return refusal == Fit.CABLES_FULL ? CABLES_FULL : FACE_TAKEN;
  }
}
