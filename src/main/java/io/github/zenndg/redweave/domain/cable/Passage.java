package io.github.zenndg.redweave.domain.cable;

/**
 * How a link goes from a cable to the next: to the next block space, losing 1, or by the inner
 * curve to the other face of the same space, losing nothing (answers 499 and 541).
 */
public enum Passage {
  INNER_CURVE(0),
  NEXT_SPACE(1);

  private final int loss;

  Passage(int loss) {
    this.loss = loss;
  }

  public int loss() {
    return loss;
  }
}
