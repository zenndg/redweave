package io.github.marcelofabianov.redweave.server;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.marcelofabianov.redweave.domain.network.Direction;
import io.github.marcelofabianov.redweave.domain.network.Face;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** The keys of the saves are a contract, so renaming a constant fails here first (rule 15). */
class SaveKeysTest {
  private static final String DOWN = "down";
  private static final String UP = "up";
  private static final String NORTH = "north";
  private static final String SOUTH = "south";
  private static final String WEST = "west";
  private static final String EAST = "east";
  private static final List<String> FACE_KEYS = List.of(DOWN, UP, NORTH, SOUTH, WEST, EAST);
  private static final List<String> DIRECTION_KEYS = List.of("input", "output");
  private static final String UNKNOWN = "sideways";

  @Test
  void theFacesKeepTheirKeys() {
    assertEquals(FACE_KEYS, Arrays.stream(Face.values()).map(SaveKeys::of).toList());
  }

  @Test
  void theDirectionsKeepTheirKeys() {
    assertEquals(DIRECTION_KEYS, Arrays.stream(Direction.values()).map(SaveKeys::of).toList());
  }

  @Test
  void aKeyGivesBackItsValue() {
    for (Face face : Face.values()) {
      assertEquals(Optional.of(face), SaveKeys.parse(Face.class, SaveKeys.of(face)));
    }
  }

  @Test
  void anUnknownKeyGivesNothing() {
    assertEquals(Optional.empty(), SaveKeys.parse(Face.class, UNKNOWN));
  }
}
