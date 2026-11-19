package io.github.zenndg.redweave.domain.cable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

/**
 * The joined cables up to a distance from where something changed, with their links: what a run
 * reads. The distance counts as the strength falls, so an inner curve adds nothing (answer 541),
 * and a piece that is gone, unloaded or of another color or owner is left out (answer 529).
 */
final class CableRegion<K> {
  private final Map<K, Integer> distances = new HashMap<>();
  private final Map<K, CablePiece> pieces = new HashMap<>();
  private final Map<K, List<CableLink<K>>> links = new HashMap<>();

  private CableRegion() {}

  /** Walks the grid from the starts, nearest first, never past the limit. */
  static <K> CableRegion<K> explore(Set<K> starts, CableGrid<K> grid, int limit) {
    CableRegion<K> region = new CableRegion<>();
    Deque<K> queue = new ArrayDeque<>();
    for (K start : starts) {
      grid.piece(start).ifPresent(piece -> region.reach(start, piece, 0, queue::addFirst));
    }
    while (!queue.isEmpty()) {
      region.visit(queue.pollFirst(), grid, limit, queue);
    }
    return region;
  }

  Set<K> keys() {
    return pieces.keySet();
  }

  CablePiece piece(K key) {
    return pieces.get(key);
  }

  int distance(K key) {
    return distances.get(key);
  }

  /** The links of the piece to joined pieces of the region. */
  Collection<CableLink<K>> links(K key) {
    return links.getOrDefault(key, List.of()).stream()
        .filter(link -> pieces.containsKey(link.piece()))
        .toList();
  }

  /** Keeps the joined links of the piece and reaches its neighbors, as a 0-1 breadth search. */
  private void visit(K key, CableGrid<K> grid, int limit, Deque<K> queue) {
    CablePiece piece = pieces.get(key);
    int here = distances.get(key);
    List<CableLink<K>> joined = new ArrayList<>();
    for (CableLink<K> link : grid.links(key)) {
      Optional<CablePiece> next =
          grid.piece(link.piece()).filter(found -> piece.insulation().joins(found.insulation()));
      if (next.isEmpty()) {
        continue;
      }
      joined.add(link);
      int far = here + link.passage().loss();
      if (far <= limit && far < distances.getOrDefault(link.piece(), Integer.MAX_VALUE)) {
        boolean free = link.passage().loss() == 0;
        reach(link.piece(), next.get(), far, free ? queue::addFirst : queue::addLast);
      }
    }
    links.put(key, joined);
  }

  private void reach(K key, CablePiece piece, int distance, Consumer<K> queue) {
    distances.put(key, distance);
    pieces.put(key, piece);
    queue.accept(key);
  }
}
