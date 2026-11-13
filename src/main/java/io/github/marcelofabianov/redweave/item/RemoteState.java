package io.github.marcelofabianov.redweave.item;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * What a Signal Remote keeps in its item: its ID, given on the first link; the link; the mode; the
 * lever; the game tick its pulse ends; and whether the server found the link lost. Nothing of it
 * goes to the saves of the network (answers 287, 288, 356, 358 and 369).
 */
public record RemoteState(
    Optional<UUID> id,
    Optional<RemoteLink> link,
    RemoteMode mode,
    boolean on,
    long pulseEnd,
    boolean isLost) {
  private static final String MISSING = "A remote state needs every field, empty or not";
  private static final long NO_PULSE = 0;

  /** A tablet out of the crafting table: no link, no ID, a lever turned off. */
  public static final RemoteState NEW =
      new RemoteState(Optional.empty(), Optional.empty(), RemoteMode.LEVER, false, NO_PULSE, false);

  public RemoteState {
    Objects.requireNonNull(id, MISSING);
    Objects.requireNonNull(link, MISSING);
    Objects.requireNonNull(mode, MISSING);
  }

  /**
   * A new link replaces the old one and starts off; the ID stays the one of the first link, or
   * takes the one given (answers 291 and 358).
   */
  public RemoteState linkedTo(RemoteLink newLink, UUID newId) {
    return new RemoteState(
        Optional.of(id.orElse(newId)), Optional.of(newLink), mode, false, NO_PULSE, false);
  }

  /** The lever switches; the button starts its pulse again (answers 288 and 369). */
  public RemoteState fired(long now, int pulseTicks) {
    if (mode == RemoteMode.LEVER) {
      return new RemoteState(id, link, mode, !on, pulseEnd, isLost);
    }
    return new RemoteState(id, link, mode, on, now + pulseTicks, isLost);
  }

  /** Another mode lets go of the lever and the pulse, so the channel never stays stuck. */
  public RemoteState withMode(RemoteMode newMode) {
    return new RemoteState(id, link, newMode, false, NO_PULSE, isLost);
  }

  /** What the server found of the link this tick (answer 287). */
  public RemoteState lost(boolean nowLost) {
    return new RemoteState(id, link, mode, on, pulseEnd, nowLost);
  }

  /** Whether the tablet puts 15 on its channel now, before its reach is checked (356). */
  public boolean counts(long now) {
    return link.isPresent() && id.isPresent() && !isLost && (on || now < pulseEnd);
  }

  public RemoteLook look(long now) {
    if (link.isEmpty()) {
      return RemoteLook.UNLINKED;
    }
    if (isLost) {
      return RemoteLook.LOST;
    }
    return counts(now) ? RemoteLook.ON : RemoteLook.OFF;
  }
}
