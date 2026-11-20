package io.github.zenndg.redweave.domain.cable;

import io.github.zenndg.redweave.domain.network.PlayerId;
import io.github.zenndg.redweave.domain.signal.ChannelColor;
import java.util.Objects;

/**
 * What keeps a Signal Cable apart from the others: its color, one of the 16 of wool, and its owner.
 * The color only insulates, with no tie to the channels of a network (answers 497, 501 and 528).
 */
public record Insulation(ChannelColor color, PlayerId owner) {
  private static final String MISSING = "An insulation needs a color and an owner";

  public Insulation {
    Objects.requireNonNull(color, MISSING);
    Objects.requireNonNull(owner, MISSING);
  }

  /** Whether a cable joins the other: only with the same color and the same owner. */
  public boolean joins(Insulation other) {
    return color == other.color && owner.equals(other.owner);
  }
}
