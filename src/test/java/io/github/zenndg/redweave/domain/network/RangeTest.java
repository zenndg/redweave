package io.github.zenndg.redweave.domain.network;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** A sphere around the controller, in the same dimension (answers 23 and 36). */
class RangeTest {
  private static final String OVERWORLD = "minecraft:overworld";
  private static final String NETHER = "minecraft:the_nether";
  private static final int RADIUS = 128;
  private static final int UNLIMITED = 0;
  private static final int NEGATIVE = -1;
  private static final int WORLD_BORDER = 29_999_984;
  private static final BlockLocation CENTER = new BlockLocation(OVERWORLD, 0, 64, 0);
  private static final BlockLocation ON_THE_EDGE = new BlockLocation(OVERWORLD, 128, 64, 0);
  private static final BlockLocation JUST_PAST_THE_EDGE = new BlockLocation(OVERWORLD, 129, 64, 0);

  /** 72 blocks across and 96 up: exactly 120 blocks away, as a 3-4-5 triangle. */
  private static final BlockLocation DIAGONAL_INSIDE = new BlockLocation(OVERWORLD, 72, 160, 0);

  /** A cube of side 128 reaches this corner, and a sphere of radius 128 does not. */
  private static final BlockLocation CUBE_CORNER = new BlockLocation(OVERWORLD, 100, 164, 100);

  private static final BlockLocation SAME_SPOT_IN_THE_NETHER = new BlockLocation(NETHER, 0, 64, 0);
  private static final BlockLocation FAR_AWAY =
      new BlockLocation(OVERWORLD, WORLD_BORDER, 64, WORLD_BORDER);

  @Test
  void reachesANodeInsideTheSphere() {
    assertTrue(new Range(RADIUS).reaches(CENTER, DIAGONAL_INSIDE));
  }

  @Test
  void reachesANodeExactlyOnTheRadius() {
    assertTrue(new Range(RADIUS).reaches(CENTER, ON_THE_EDGE));
  }

  @Test
  void doesNotReachANodeOneBlockPastTheRadius() {
    assertFalse(new Range(RADIUS).reaches(CENTER, JUST_PAST_THE_EDGE));
  }

  @Test
  void isASphereAndNotACube() {
    assertFalse(new Range(RADIUS).reaches(CENTER, CUBE_CORNER));
  }

  @Test
  void neverReachesAnotherDimension() {
    assertFalse(new Range(RADIUS).reaches(CENTER, SAME_SPOT_IN_THE_NETHER));
    assertFalse(new Range(UNLIMITED).reaches(CENTER, SAME_SPOT_IN_THE_NETHER));
  }

  @Test
  void aRadiusOfZeroReachesTheWholeDimension() {
    assertTrue(new Range(UNLIMITED).reaches(CENTER, FAR_AWAY));
  }

  /** The squares of the coordinates near the border pass the size of an int. */
  @Test
  void farCoordinatesDoNotOverflowTheDistance() {
    assertFalse(new Range(RADIUS).reaches(CENTER, FAR_AWAY));
  }

  @Test
  void rejectsANegativeRadius() {
    assertThrows(IllegalArgumentException.class, () -> new Range(NEGATIVE));
  }
}
