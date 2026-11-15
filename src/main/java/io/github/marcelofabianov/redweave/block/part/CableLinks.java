package io.github.marcelofabianov.redweave.block.part;

import io.github.marcelofabianov.redweave.block.PartSpaceBlockEntity;
import io.github.marcelofabianov.redweave.domain.cable.CableLink;
import io.github.marcelofabianov.redweave.domain.cable.Passage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.ObserverBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Where a Signal Cable may join the next one and where it points, as the game wire: in the 4
 * directions across its side, straight, by the inner curve to the other side of its space, around
 * the edge of the block it rests on and up or down a step (answers 495, 504, 541 and 569). Only
 * loaded blocks are read (rule 6).
 */
final class CableLinks {
  private static final int ONE = 1;

  private CableLinks() {}

  /** The 4 directions across the side, along the face of the block it rests on. */
  static List<Direction> across(Direction face) {
    return Arrays.stream(Direction.values())
        .filter(direction -> direction.getAxis() != face.getAxis())
        .toList();
  }

  /** Every spot a cable there could join, of any color and owner; the run picks the joined. */
  static List<CableLink<PartSpot>> candidates(Level level, PartSpot spot) {
    List<CableLink<PartSpot>> links = new ArrayList<>();
    for (Direction direction : across(spot.face())) {
      links.addAll(toward(level, spot, direction));
    }
    return links;
  }

  /** The spots in one direction: inner curve, straight, outer curve and the steps (495). */
  static List<CableLink<PartSpot>> toward(Level level, PartSpot spot, Direction direction) {
    BlockPos pos = spot.pos();
    Direction face = spot.face();
    BlockPos side = pos.relative(direction);
    List<CableLink<PartSpot>> links = new ArrayList<>();
    links.add(new CableLink<>(new PartSpot(pos, direction), Passage.INNER_CURVE));
    links.add(new CableLink<>(new PartSpot(side, face), Passage.NEXT_SPACE));
    if (!level.isLoaded(side)) {
      return links;
    }
    if (!conducts(level, side)) {
      BlockPos below = side.relative(face);
      links.add(new CableLink<>(new PartSpot(below, direction.getOpposite()), Passage.NEXT_SPACE));
      links.add(new CableLink<>(new PartSpot(below, face), Passage.NEXT_SPACE));
    } else if (!conducts(level, pos.relative(face.getOpposite()))) {
      BlockPos above = side.relative(face.getOpposite());
      links.add(new CableLink<>(new PartSpot(above, face), Passage.NEXT_SPACE));
    }
    return links;
  }

  /**
   * Where the cable points: to the joined cables and to the components the game wire turns to; a
   * cable with one link runs on through, and a lone one points every way, as a cross (568).
   */
  static Set<Direction> arms(Level level, PartSpot spot, CablePart cable) {
    Set<Direction> linked = EnumSet.noneOf(Direction.class);
    for (Direction direction : across(spot.face())) {
      if (joinsToward(level, spot, cable, direction) || component(level, spot, direction)) {
        linked.add(direction);
      }
    }
    if (linked.isEmpty()) {
      linked.addAll(across(spot.face()));
    } else if (linked.size() == ONE) {
      linked.add(linked.iterator().next().getOpposite());
    }
    return linked;
  }

  static int maskOf(Set<Direction> arms) {
    int mask = 0;
    for (Direction direction : arms) {
      mask |= ONE << direction.ordinal();
    }
    return mask;
  }

  static Set<Direction> fromMask(int mask) {
    Set<Direction> arms = EnumSet.noneOf(Direction.class);
    for (Direction direction : Direction.values()) {
      if ((mask & (ONE << direction.ordinal())) != 0) {
        arms.add(direction);
      }
    }
    return arms;
  }

  private static boolean joinsToward(
      Level level, PartSpot spot, CablePart cable, Direction direction) {
    return toward(level, spot, direction).stream()
        .map(link -> CablePart.at(level, link.piece()))
        .anyMatch(found ->
            found.isPresent()
                && found.get() != cable
                && cable.insulation().joins(found.get().insulation()));
  }

  /**
   * A component the cable turns to, as the game wire: a node of its space, a module whose edge
   * meets it (answer 566), the game wire, a space with a node or a module, or a block that takes
   * redstone from that side (answer 569).
   */
  private static boolean component(Level level, PartSpot spot, Direction direction) {
    if (sharesSpace(level, spot, direction)) {
      return true;
    }
    BlockPos side = spot.pos().relative(direction);
    if (!level.isLoaded(side)) {
      return false;
    }
    if (level.getBlockEntity(side) instanceof PartSpaceBlockEntity space) {
      return space.parts().stream().anyMatch(part -> !(part instanceof CablePart));
    }
    return turnsTo(level, side, level.getBlockState(side), direction);
  }

  private static boolean sharesSpace(Level level, PartSpot spot, Direction direction) {
    return PartSpaceBlockEntity.at(level, spot.pos())
        .flatMap(space -> space.at(direction))
        .map(part -> part instanceof NodePart || meets(part, spot.face()))
        .orElse(false);
  }

  /** A module meets the cable with its back or front edge on the side of the cable (566). */
  static boolean meets(Part part, Direction cableFace) {
    return part instanceof ModulePart module
        && (module.front() == cableFace || module.front().getOpposite() == cableFace);
  }

  private static boolean turnsTo(Level level, BlockPos side, BlockState state, Direction toward) {
    if (state.is(Blocks.REDSTONE_WIRE)) {
      return true;
    }
    if (state.is(Blocks.REPEATER)) {
      return state.getValue(HorizontalDirectionalBlock.FACING).getAxis() == toward.getAxis();
    }
    if (state.is(Blocks.OBSERVER)) {
      return state.getValue(ObserverBlock.FACING) == toward;
    }
    return state.canRedstoneConnectTo(level, side, toward);
  }

  private static boolean conducts(Level level, BlockPos pos) {
    return level.getBlockState(pos).isRedstoneConductor(level, pos);
  }
}
