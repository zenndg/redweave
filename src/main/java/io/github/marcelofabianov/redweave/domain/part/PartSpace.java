package io.github.marcelofabianov.redweave.domain.part;

import io.github.marcelofabianov.redweave.domain.network.Face;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * The parts of one block space, by face: at most one part on each of the 6 faces, and at most 2
 * cables, so a node and a cable, for example, share a space (answers 498, 511 and 513). A space is
 * never changed in place: placing and removing give a new one.
 */
public record PartSpace(Map<Face, PartKind> parts) {
  /** The cables a space holds, one on each of two faces (answers 498 and 504). */
  public static final int MAX_CABLES = 2;

  public static final PartSpace EMPTY = new PartSpace(Map.of());

  private static final String MISSING = "A part space needs its parts";
  private static final String TOO_MANY_CABLES = "A part space holds at most %d cables";
  private static final String DOES_NOT_FIT = "The part does not fit: %s";

  public PartSpace {
    Objects.requireNonNull(parts, MISSING);
    parts = Map.copyOf(parts);
    if (cablesIn(parts) > MAX_CABLES) {
      throw new IllegalArgumentException(String.format(TOO_MANY_CABLES, MAX_CABLES));
    }
  }

  public boolean isEmpty() {
    return parts.isEmpty();
  }

  /** The part on the face, if any. */
  public Optional<PartKind> at(Face face) {
    return Optional.ofNullable(parts.get(face));
  }

  /** Whether a part of the kind fits on the face, or why not; a taken face comes first (533). */
  public Fit fit(Face face, PartKind kind) {
    if (parts.containsKey(face)) {
      return Fit.FACE_TAKEN;
    }
    if (kind == PartKind.CABLE && cablesIn(parts) >= MAX_CABLES) {
      return Fit.CABLES_FULL;
    }
    return Fit.FITS;
  }

  /** The space with the part placed on the face, which must fit there. */
  public PartSpace with(Face face, PartKind kind) {
    Fit fit = fit(face, kind);
    if (fit != Fit.FITS) {
      throw new IllegalArgumentException(String.format(DOES_NOT_FIT, fit));
    }
    Map<Face, PartKind> next = new EnumMap<>(Face.class);
    next.putAll(parts);
    next.put(face, kind);
    return new PartSpace(next);
  }

  /** The space without the part on the face; a free face changes nothing. */
  public PartSpace without(Face face) {
    if (!parts.containsKey(face)) {
      return this;
    }
    Map<Face, PartKind> next = new EnumMap<>(Face.class);
    next.putAll(parts);
    next.remove(face);
    return new PartSpace(next);
  }

  private static long cablesIn(Map<Face, PartKind> parts) {
    return parts.values().stream().filter(kind -> kind == PartKind.CABLE).count();
  }
}
