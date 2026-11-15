package io.github.marcelofabianov.redweave.block.part;

import io.github.marcelofabianov.redweave.domain.cable.CableGrid;
import io.github.marcelofabianov.redweave.domain.cable.CableLink;
import io.github.marcelofabianov.redweave.domain.cable.CablePiece;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.Collection;
import java.util.Optional;
import net.minecraft.world.level.Level;

/**
 * The cables of a level as a run sees them, by their spot: only loaded spaces give a piece, so a
 * run stops at the edge of an unloaded chunk and never loads it (rule 6 and answer 529). Each piece
 * reads its input from the world as it is asked.
 */
final class WorldCableGrid implements CableGrid<PartSpot> {
  private final Level level;

  WorldCableGrid(Level level) {
    this.level = level;
  }

  @Override
  public Optional<CablePiece> piece(PartSpot spot) {
    return CablePart.at(level, spot)
        .map(cable -> {
          cable.setInput(CableInputs.read(level, spot));
          return new CablePiece(cable.insulation(), Signal.clamped(cable.input()));
        });
  }

  @Override
  public Collection<CableLink<PartSpot>> links(PartSpot spot) {
    return CableLinks.candidates(level, spot);
  }
}
