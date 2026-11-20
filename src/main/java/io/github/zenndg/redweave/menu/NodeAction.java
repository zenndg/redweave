package io.github.zenndg.redweave.menu;

import io.github.zenndg.redweave.domain.network.ChannelSlot;
import java.util.Objects;
import java.util.Optional;

/** What the player asks on the Node Screen. The server decides (rule 16). */
public record NodeAction(Kind kind, int index, Optional<ChannelSlot> slot) {
  private static final String MISSING = "An action needs a kind and a slot";
  private static final String MISMATCH = "Only configure carries a slot";

  /**
   * Enable the slot of a color, configure its direction or face, disable it (answers 226 and 228),
   * choose the network at a position of the list the server keeps (139 and 141), or step the output
   * delay of the node one second (866).
   */
  public enum Kind {
    ENABLE,
    CONFIGURE,
    DISABLE,
    CHOOSE,
    DELAY
  }

  public NodeAction {
    Objects.requireNonNull(kind, MISSING);
    Objects.requireNonNull(slot, MISSING);
    if ((kind == Kind.CONFIGURE) != slot.isPresent()) {
      throw new IllegalArgumentException(MISMATCH);
    }
  }

  public static NodeAction enable(int index) {
    return new NodeAction(Kind.ENABLE, index, Optional.empty());
  }

  public static NodeAction configure(int index, ChannelSlot slot) {
    return new NodeAction(Kind.CONFIGURE, index, Optional.of(slot));
  }

  public static NodeAction disable(int index) {
    return new NodeAction(Kind.DISABLE, index, Optional.empty());
  }

  /** The index is the step of the `Delay` row, shorter or longer, not a slot (answer 866). */
  public static NodeAction delay(int step) {
    return new NodeAction(Kind.DELAY, step, Optional.empty());
  }

  /** The index is the position in the network choice, not a slot. */
  public static NodeAction choose(int position) {
    return new NodeAction(Kind.CHOOSE, position, Optional.empty());
  }
}
