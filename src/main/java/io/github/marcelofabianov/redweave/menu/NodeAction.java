package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import java.util.Objects;
import java.util.Optional;

/** What the player asks on the Node Screen. The server decides (rule 16). */
public record NodeAction(Kind kind, int index, Optional<ChannelSlot> slot) {
  private static final String MISSING = "An action needs a kind and a slot";
  private static final String MISMATCH = "Only configure carries a slot";

  /** Use a free slot (83), configure a slot, or clear it (84). */
  public enum Kind {
    USE,
    CONFIGURE,
    CLEAR
  }

  public NodeAction {
    Objects.requireNonNull(kind, MISSING);
    Objects.requireNonNull(slot, MISSING);
    if ((kind == Kind.CONFIGURE) != slot.isPresent()) {
      throw new IllegalArgumentException(MISMATCH);
    }
  }

  public static NodeAction use(int index) {
    return new NodeAction(Kind.USE, index, Optional.empty());
  }

  public static NodeAction configure(int index, ChannelSlot slot) {
    return new NodeAction(Kind.CONFIGURE, index, Optional.of(slot));
  }

  public static NodeAction clear(int index) {
    return new NodeAction(Kind.CLEAR, index, Optional.empty());
  }
}
