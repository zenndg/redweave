package io.github.marcelofabianov.redweave.item;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * What a Signal Remote keeps in its item: its ID, given on the first link; the link to an input
 * slot or to a Signal Emitter, never both; the mode; the lever; the game tick its pulse ends; and
 * whether the server found the link lost. Nothing of it goes to the saves of the network (answers
 * 287, 288, 356, 358, 369, 778 and 786).
 */
public record RemoteState(
    Optional<UUID> id,
    Optional<RemoteLink> link,
    Optional<EmitterLink> emitter,
    RemoteMode mode,
    boolean on,
    long pulseEnd,
    boolean isLost) {
  private static final String MISSING = "A remote state needs every field, empty or not";
  private static final String BOTH = "A remote links to a slot or to an emitter, not both";
  private static final long NO_PULSE = 0;

  /** A tablet out of the crafting table: no link, no ID, a lever turned off. */
  public static final RemoteState NEW =
      new RemoteState(
          Optional.empty(),
          Optional.empty(),
          Optional.empty(),
          RemoteMode.LEVER,
          false,
          NO_PULSE,
          false);

  public RemoteState {
    Objects.requireNonNull(id, MISSING);
    Objects.requireNonNull(link, MISSING);
    Objects.requireNonNull(emitter, MISSING);
    Objects.requireNonNull(mode, MISSING);
    if (link.isPresent() && emitter.isPresent()) {
      throw new IllegalArgumentException(BOTH);
    }
  }

  /** From the saves of the item: a damaged one with both links keeps the slot (rule 14). */
  public static RemoteState read(
      Optional<UUID> id,
      Optional<RemoteLink> link,
      Optional<EmitterLink> emitter,
      RemoteMode mode,
      boolean on,
      long pulseEnd,
      boolean isLost) {
    Optional<EmitterLink> kept = link.isPresent() ? Optional.empty() : emitter;
    return new RemoteState(id, link, kept, mode, on, pulseEnd, isLost);
  }

  /**
   * A new link replaces the old one and starts off; the ID stays the one of the first link, or
   * takes the one given (answers 291 and 358).
   */
  public RemoteState linkedTo(RemoteLink newLink, UUID newId) {
    return new RemoteState(
        Optional.of(id.orElse(newId)),
        Optional.of(newLink),
        Optional.empty(),
        mode,
        false,
        NO_PULSE,
        false);
  }

  /**
   * A link to an emitter replaces the one to a slot, so the old channel goes back to 0, and starts
   * off; the ID stays (answers 778 and 791).
   */
  public RemoteState linkedTo(EmitterLink newEmitter) {
    return new RemoteState(
        id, Optional.empty(), Optional.of(newEmitter), mode, false, NO_PULSE, false);
  }

  /** The lever switches; the button starts its pulse again (answers 288 and 369). */
  public RemoteState fired(long now, int pulseTicks) {
    if (mode == RemoteMode.LEVER) {
      return new RemoteState(id, link, emitter, mode, !on, pulseEnd, isLost);
    }
    return new RemoteState(id, link, emitter, mode, on, now + pulseTicks, isLost);
  }

  /** Another mode lets go of the lever and the pulse, so the channel never stays stuck. */
  public RemoteState withMode(RemoteMode newMode) {
    return new RemoteState(id, link, emitter, newMode, false, NO_PULSE, isLost);
  }

  /** What the server found of the link this tick (answer 287). */
  public RemoteState lost(boolean nowLost) {
    return new RemoteState(id, link, emitter, mode, on, pulseEnd, nowLost);
  }

  /** Whether the tablet puts 15 on its channel now, before its reach is checked (356). */
  public boolean counts(long now) {
    return link.isPresent() && id.isPresent() && !isLost && (on || now < pulseEnd);
  }

  /**
   * Linked to an emitter, the tablet always looks off, because the emitter may change by the hand
   * of its owner (answer 788).
   */
  public RemoteLook look(long now) {
    if (link.isEmpty() && emitter.isEmpty()) {
      return RemoteLook.UNLINKED;
    }
    if (isLost) {
      return RemoteLook.LOST;
    }
    return counts(now) ? RemoteLook.ON : RemoteLook.OFF;
  }
}
