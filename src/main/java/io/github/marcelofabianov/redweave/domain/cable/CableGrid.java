package io.github.marcelofabianov.redweave.domain.cable;

import java.util.Collection;
import java.util.Optional;

/**
 * The cables of the world as a run sees them, by a key the adapter picks. The domain defines it,
 * and the adapter implements it with only the loaded blocks, so a run stops at the edge of an
 * unloaded chunk instead of loading it (rule 6 and answer 529).
 */
public interface CableGrid<K> {
  /** The piece at the key, or none when it is gone or not loaded. */
  Optional<CablePiece> piece(K key);

  /** The links of the piece to the cables next to it, of any color and owner. */
  Collection<CableLink<K>> links(K key);
}
