package io.github.marcelofabianov.redweave.domain.cable;

import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The strength of each Signal Cable a change can reach, in one pass and in the same tick, with the
 * result of the game wire and without its cascade (answers 499, 509 and rule 18): each piece gets
 * the strongest input of its run less the distance, never below 0. A change reaches only 15 spaces,
 * and the sources of those pieces are within 15 more, so the walk stops at 30 (answer 530).
 */
public final class CableRun {
  /** How far a strength goes: from 15 down to 0, one per space (answer 499). */
  public static final int REACH = Signal.MAX.strength();

  private static final int SEARCH = REACH + REACH;

  private CableRun() {}

  /**
   * The new strength of every joined piece up to 15 spaces from the changed ones, as a placed or
   * broken cable, or a cable whose input changed. A gone or unloaded start gives nothing.
   */
  public static <K> Map<K, Signal> around(Set<K> changed, CableGrid<K> grid) {
    CableRegion<K> region = CableRegion.explore(changed, grid, SEARCH);
    Map<K, Integer> strengths = spread(region);
    Map<K, Signal> written = new HashMap<>();
    for (K key : region.keys()) {
      if (region.distance(key) <= REACH) {
        written.put(key, new Signal(strengths.get(key)));
      }
    }
    return written;
  }

  /** Spreads from the strongest input down, so each piece is settled once, at its best. */
  private static <K> Map<K, Integer> spread(CableRegion<K> region) {
    Map<K, Integer> best = new HashMap<>();
    List<Deque<K>> waiting = new ArrayList<>();
    for (int strength = 0; strength <= REACH; strength++) {
      waiting.add(new ArrayDeque<>());
    }
    for (K key : region.keys()) {
      int input = region.piece(key).input().strength();
      best.put(key, input);
      waiting.get(input).add(key);
    }
    for (int strength = REACH; strength > 0; strength--) {
      Deque<K> now = waiting.get(strength);
      while (!now.isEmpty()) {
        feed(region, now.poll(), strength, best, waiting);
      }
    }
    return best;
  }

  private static <K> void feed(
      CableRegion<K> region, K key, int strength, Map<K, Integer> best, List<Deque<K>> waiting) {
    if (best.get(key) != strength) {
      return;
    }
    for (CableLink<K> link : region.links(key)) {
      int next = strength - link.passage().loss();
      if (next > best.get(link.piece())) {
        best.put(link.piece(), next);
        waiting.get(next).add(link.piece());
      }
    }
  }
}
