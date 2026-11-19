package io.github.zenndg.redweave.block.part;

import io.github.zenndg.redweave.RedweaveMod;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

/**
 * How far an arm of a Signal Cable reaches, which the shape and the model follow (answers 571 and
 * 572): to the edge of its space; short of it, to meet the cable of the other side of the space on
 * an inner curve; past it, to the corner of an outer curve or to the plate of a node or a module of
 * the space beside it that exchanges signal with it (answer 575); or up the side of the block of a
 * step. Of two cables that meet on a curve, only the one on the side of the lower ordinal reaches
 * short or past the edge, so the two never draw the same piece twice.
 */
public enum CableReach {
  EDGE(Piece.ARM),
  SHORT(Piece.SHORT),
  CORNER(Piece.CORNER),
  STEP(Piece.STEP),
  PLATE(Piece.PLATE);

  private static final String MODEL_PREFIX = "block/signal_cable_";
  private static final int BITS = 3;
  private static final int MASK = (1 << BITS) - 1;

  private final ResourceLocation model;

  /** The names of the models of the arms, which an enum constant cannot hold before itself. */
  private static final class Piece {
    static final String ARM = "arm";
    static final String SHORT = "short";
    static final String CORNER = "corner";
    static final String STEP = "step";
    static final String PLATE = "plate";
  }

  CableReach(String piece) {
    this.model = ResourceLocation.fromNamespaceAndPath(RedweaveMod.MOD_ID, MODEL_PREFIX + piece);
  }

  /** The model of the arm, drawn on the floor and pointing north. */
  public ResourceLocation model() {
    return model;
  }

  /** The arms in one number, for the client: each direction, 0 for none or its reach plus 1. */
  static int pack(Map<Direction, CableReach> arms) {
    int packed = 0;
    for (Map.Entry<Direction, CableReach> arm : arms.entrySet()) {
      packed |= (arm.getValue().ordinal() + 1) << (arm.getKey().ordinal() * BITS);
    }
    return packed;
  }

  /** The arms back from the number; an unknown reach, as from a newer server, is none. */
  static Map<Direction, CableReach> unpack(int packed) {
    Map<Direction, CableReach> arms = new EnumMap<>(Direction.class);
    CableReach[] reaches = values();
    for (Direction direction : Direction.values()) {
      int reach = ((packed >> (direction.ordinal() * BITS)) & MASK) - 1;
      if (reach >= 0 && reach < reaches.length) {
        arms.put(direction, reaches[reach]);
      }
    }
    return arms;
  }
}
