package io.github.zenndg.redweave.item;

import io.github.zenndg.redweave.domain.network.BlockLocation;
import io.github.zenndg.redweave.domain.network.Face;
import java.util.Objects;
import java.util.UUID;

/**
 * The Signal Emitter a Signal Remote is linked to: its place, the face of its space and the ID of
 * its module, so another module in the same place is not it (answers 778, 785 and 786).
 */
public record EmitterLink(BlockLocation place, Face face, UUID module) {
  private static final String MISSING = "A link to an emitter needs its place, face and module";

  public EmitterLink {
    Objects.requireNonNull(place, MISSING);
    Objects.requireNonNull(face, MISSING);
    Objects.requireNonNull(module, MISSING);
  }
}
