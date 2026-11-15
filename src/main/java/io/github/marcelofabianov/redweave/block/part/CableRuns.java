package io.github.marcelofabianov.redweave.block.part;

import io.github.marcelofabianov.redweave.block.PartSpaceBlockEntity;
import io.github.marcelofabianov.redweave.domain.cable.CableLink;
import io.github.marcelofabianov.redweave.domain.cable.CableRun;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import io.github.marcelofabianov.redweave.registry.RedweaveBlocks;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

/**
 * Works the runs of Signal Cable out on the server, in the same tick as the change, in one pass of
 * the domain, and wakes only the neighbors of the cables whose strength or arms changed, with no
 * cascade of updates (answer 509 and rule 18). A change heard while a run is applied waits for the
 * next pass of the same call, and the passes are bounded, so a loop of cables never hangs a tick.
 */
public final class CableRuns {
  private static final int MAX_PASSES = 4;
  private static final Set<PartSpot> WAITING = new LinkedHashSet<>();
  private static boolean running;

  private CableRuns() {}

  /** Works out the runs around the spots, which changed. */
  static void update(Level level, Set<PartSpot> changed) {
    if (level.isClientSide) {
      return;
    }
    if (running) {
      WAITING.addAll(changed);
      return;
    }
    running = true;
    try {
      Set<PartSpot> next = changed;
      for (int pass = 0; pass < MAX_PASSES && !next.isEmpty(); pass++) {
        apply(level, CableRun.around(next, new WorldCableGrid(level)));
        next = new LinkedHashSet<>(WAITING);
        WAITING.clear();
      }
    } finally {
      running = false;
      WAITING.clear();
    }
  }

  /** A cable gone: the cables it could join work their strength out without it. */
  static void removed(Level level, PartSpot spot) {
    update(
        level,
        CableLinks.candidates(level, spot).stream()
            .map(CableLink::piece)
            .collect(Collectors.toCollection(LinkedHashSet::new)));
  }

  /** A neighbor changed: the arms follow it, and the run works again only if the input moved. */
  static void heard(Level level, PartSpot spot, CablePart cable) {
    if (cable.setArms(CableLinks.arms(level, spot, cable))) {
      changedLook(level, spot);
      wake(level, spot, cable);
    }
    if (CableInputs.read(level, spot) != cable.input()) {
      update(level, Set.of(spot));
    }
  }

  private static void apply(Level level, Map<PartSpot, Signal> strengths) {
    List<PartSpot> moved = new ArrayList<>();
    for (Map.Entry<PartSpot, Signal> entry : strengths.entrySet()) {
      PartSpot spot = entry.getKey();
      CablePart.at(level, spot)
          .ifPresent(cable -> {
            boolean arms = cable.setArms(CableLinks.arms(level, spot, cable));
            if (cable.setStrength(entry.getValue().strength()) || arms) {
              changedLook(level, spot);
              moved.add(spot);
            }
          });
    }
    for (PartSpot spot : moved) {
      CablePart.at(level, spot).ifPresent(cable -> wake(level, spot, cable));
    }
  }

  private static void changedLook(Level level, PartSpot spot) {
    PartSpaceBlockEntity.at(level, spot.pos()).ifPresent(PartSpaceBlockEntity::changed);
  }

  /**
   * The neighbors of the space and of the block it rests on, which the cable powers, and the other
   * parts of its space, which the game never wakes (answer 514).
   */
  private static void wake(Level level, PartSpot spot, CablePart cable) {
    Block block = RedweaveBlocks.PART_SPACE.get();
    level.updateNeighborsAt(spot.pos(), block);
    if (level.isLoaded(spot.support())) {
      level.updateNeighborsAt(spot.support(), block);
    }
    PartSpaceBlockEntity.at(level, spot.pos()).ifPresent(space -> space.wakeOthers(cable));
  }
}
