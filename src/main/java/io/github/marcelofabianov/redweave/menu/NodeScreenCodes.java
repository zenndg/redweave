package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.Direction;
import io.github.marcelofabianov.redweave.domain.network.Face;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import java.util.Optional;

/**
 * The numbers the Node Screen and the server exchange: a slot fits a data slot of the menu, which
 * the game sends as 16 bits, and an action fits a button click of the game. Anything that does not
 * decode is refused, because a client can send any number (rule 16).
 */
public final class NodeScreenCodes {
  private static final int USED = 1;
  private static final int DIRECTION_SHIFT = 1;
  private static final int FACE_SHIFT = 2;
  private static final int FACE_MASK = 0b111;
  private static final int CHANNEL_SHIFT = 5;
  private static final int CHANNEL_MASK = 0b1111;
  private static final int SLOT_BITS = 9;

  private static final int KIND_MASK = 0b11;
  private static final int INDEX_SHIFT = 2;
  private static final int INDEX_MASK = 0b1111;
  private static final int SLOT_SHIFT = 6;

  private NodeScreenCodes() {}

  /** A free slot is 0; a used one carries its direction, face and channel. */
  public static int slot(Optional<ChannelSlot> slot) {
    return slot.map(NodeScreenCodes::used).orElse(0);
  }

  private static int used(ChannelSlot slot) {
    return USED
        | slot.direction().ordinal() << DIRECTION_SHIFT
        | slot.face().ordinal() << FACE_SHIFT
        | slot.channel().ordinal() << CHANNEL_SHIFT;
  }

  public static Optional<ChannelSlot> slotOf(int code) {
    int face = code >> FACE_SHIFT & FACE_MASK;
    if ((code & USED) == 0 || code >> SLOT_BITS != 0 || face >= Face.values().length) {
      return Optional.empty();
    }
    Direction direction = Direction.values()[code >> DIRECTION_SHIFT & USED];
    ChannelColor channel = ChannelColor.values()[code >> CHANNEL_SHIFT & CHANNEL_MASK];
    return Optional.of(new ChannelSlot(direction, Face.values()[face], channel));
  }

  public static int button(NodeAction action) {
    return action.kind().ordinal()
        | action.index() << INDEX_SHIFT
        | slot(action.slot()) << SLOT_SHIFT;
  }

  public static Optional<NodeAction> actionOf(int button) {
    int kind = button & KIND_MASK;
    if (kind >= NodeAction.Kind.values().length) {
      return Optional.empty();
    }
    int index = button >> INDEX_SHIFT & INDEX_MASK;
    int slotCode = button >> SLOT_SHIFT;
    Optional<ChannelSlot> slot = slotOf(slotCode);
    if (kind != NodeAction.Kind.CONFIGURE.ordinal()) {
      return slotCode == 0 ? Optional.of(simple(kind, index)) : Optional.empty();
    }
    return slot.map(configured -> NodeAction.configure(index, configured));
  }

  private static NodeAction simple(int kind, int index) {
    return kind == NodeAction.Kind.USE.ordinal() ? NodeAction.use(index) : NodeAction.clear(index);
  }
}
