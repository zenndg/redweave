package io.github.marcelofabianov.redweave.domain.module;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/** Draws from a fixed list, in order, so a test knows every value the randomizer gets. */
final class FixedDraws implements Draws {
  private final Deque<Integer> next;

  FixedDraws(Integer... values) {
    this.next = new ArrayDeque<>(List.of(values));
  }

  @Override
  public int next(int bound) {
    return next.removeFirst() % bound;
  }
}
