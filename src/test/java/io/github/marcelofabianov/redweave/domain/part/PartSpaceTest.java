package io.github.marcelofabianov.redweave.domain.part;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.marcelofabianov.redweave.domain.network.Face;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/**
 * The occupation of a block space: one part per face, of the 6, and at most 2 cables, and a part
 * that does not fit is refused with the reason the action bar shows (answers 498, 513 and 533).
 */
class PartSpaceTest {
  @Test
  void anEmptySpaceHasNoPart() {
    PartSpace space = PartSpace.EMPTY;

    assertTrue(space.isEmpty());
    assertEquals(Optional.empty(), space.at(Face.UP));
  }

  @ParameterizedTest
  @EnumSource(PartKind.class)
  void anyPartFitsOnAFreeFace(PartKind kind) {
    assertEquals(Fit.FITS, PartSpace.EMPTY.fit(Face.DOWN, kind));
  }

  @Test
  void aPlacedPartTakesItsFace() {
    PartSpace space = PartSpace.EMPTY.with(Face.NORTH, PartKind.PLATE);

    assertFalse(space.isEmpty());
    assertEquals(Optional.of(PartKind.PLATE), space.at(Face.NORTH));
    assertEquals(Optional.empty(), space.at(Face.SOUTH));
  }

  @ParameterizedTest
  @EnumSource(PartKind.class)
  void aTakenFaceRefusesAnyPart(PartKind kind) {
    PartSpace space = PartSpace.EMPTY.with(Face.DOWN, PartKind.CABLE);

    assertEquals(Fit.FACE_TAKEN, space.fit(Face.DOWN, kind));
  }

  @Test
  void twoCablesFitOnDifferentFaces() {
    PartSpace space = PartSpace.EMPTY.with(Face.DOWN, PartKind.CABLE);

    assertEquals(Fit.FITS, space.fit(Face.NORTH, PartKind.CABLE));
  }

  @Test
  void aThirdCableIsRefused() {
    PartSpace space =
        PartSpace.EMPTY.with(Face.DOWN, PartKind.CABLE).with(Face.NORTH, PartKind.CABLE);

    assertEquals(Fit.CABLES_FULL, space.fit(Face.UP, PartKind.CABLE));
  }

  @Test
  void twoCablesStillLeaveRoomForPlates() {
    PartSpace space =
        PartSpace.EMPTY.with(Face.DOWN, PartKind.CABLE).with(Face.NORTH, PartKind.CABLE);

    assertEquals(Fit.FITS, space.fit(Face.UP, PartKind.PLATE));
  }

  @Test
  void aTakenFaceIsTheReasonEvenWithTwoCables() {
    PartSpace space =
        PartSpace.EMPTY.with(Face.DOWN, PartKind.CABLE).with(Face.NORTH, PartKind.CABLE);

    assertEquals(Fit.FACE_TAKEN, space.fit(Face.DOWN, PartKind.CABLE));
  }

  @Test
  void sixPlatesFillTheSpace() {
    PartSpace space = PartSpace.EMPTY;
    for (Face face : Face.values()) {
      space = space.with(face, PartKind.PLATE);
    }

    for (Face face : Face.values()) {
      assertEquals(Fit.FACE_TAKEN, space.fit(face, PartKind.PLATE));
    }
  }

  @Test
  void aPartThatDoesNotFitIsNeverPlaced() {
    PartSpace space = PartSpace.EMPTY.with(Face.DOWN, PartKind.PLATE);

    assertThrows(IllegalArgumentException.class, () -> space.with(Face.DOWN, PartKind.CABLE));
  }

  @Test
  void removingAPartFreesItsFace() {
    PartSpace space = PartSpace.EMPTY.with(Face.DOWN, PartKind.CABLE).without(Face.DOWN);

    assertTrue(space.isEmpty());
    assertEquals(Fit.FITS, space.fit(Face.DOWN, PartKind.PLATE));
  }

  @Test
  void removingAFreeFaceChangesNothing() {
    PartSpace space = PartSpace.EMPTY.with(Face.UP, PartKind.PLATE);

    assertEquals(space, space.without(Face.DOWN));
  }

  @Test
  void aSpaceIsNeverChangedInPlace() {
    PartSpace empty = PartSpace.EMPTY;

    empty.with(Face.UP, PartKind.PLATE);

    assertTrue(empty.isEmpty());
  }

  @Test
  void aSpaceWithThreeCablesIsInvalid() {
    Map<Face, PartKind> parts = new EnumMap<>(Face.class);
    parts.put(Face.DOWN, PartKind.CABLE);
    parts.put(Face.UP, PartKind.CABLE);
    parts.put(Face.NORTH, PartKind.CABLE);

    assertThrows(IllegalArgumentException.class, () -> new PartSpace(parts));
  }

  @Test
  void theSpaceKeepsItsOwnCopyOfTheParts() {
    Map<Face, PartKind> parts = new EnumMap<>(Face.class);
    PartSpace space = new PartSpace(parts);

    parts.put(Face.UP, PartKind.PLATE);

    assertTrue(space.isEmpty());
  }

  @Test
  void aSpaceNeedsItsParts() {
    assertThrows(NullPointerException.class, () -> new PartSpace(null));
  }
}
