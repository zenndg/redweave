package io.github.marcelofabianov.redweave.domain.cable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/** Cable pieces laid by a test, by an index, with links both ways, as the world would give them. */
final class TestGrid implements CableGrid<Integer> {
  private final Map<Integer, CablePiece> pieces = new HashMap<>();
  private final Map<Integer, List<CableLink<Integer>>> links = new HashMap<>();
  private final Set<Integer> asked = new HashSet<>();

  TestGrid put(int key, CablePiece piece) {
    pieces.put(key, piece);
    return this;
  }

  TestGrid link(int one, int other, Passage passage) {
    links.computeIfAbsent(one, key -> new ArrayList<>()).add(new CableLink<>(other, passage));
    links.computeIfAbsent(other, key -> new ArrayList<>()).add(new CableLink<>(one, passage));
    return this;
  }

  /** A straight cable of the pieces, from 0 to count - 1, one block apart. */
  static TestGrid line(int count, CablePiece piece) {
    TestGrid grid = new TestGrid();
    for (int key = 0; key < count; key++) {
      grid.put(key, piece);
      if (key > 0) {
        grid.link(key - 1, key, Passage.NEXT_SPACE);
      }
    }
    return grid;
  }

  /** Whether the run ever asked for the piece, to show how far it walked. */
  boolean wasAsked(int key) {
    return asked.contains(key);
  }

  @Override
  public Optional<CablePiece> piece(Integer key) {
    asked.add(key);
    return Optional.ofNullable(pieces.get(key));
  }

  @Override
  public Collection<CableLink<Integer>> links(Integer key) {
    return links.getOrDefault(key, List.of());
  }
}
