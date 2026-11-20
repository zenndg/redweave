package io.github.zenndg.redweave.domain.module;

import io.github.zenndg.redweave.domain.network.PlayerId;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * The wrong tries of each player on a Signal Keypad, on the time of the world (answers 664, 665,
 * 898, 907, 913 and 915). The first ones are free, and while a player is still in them, the free
 * window with no miss gives them back. Past them, each miss doubles the wait from the first one up
 * to the cap. The reset with no try clears any count.
 */
public record KeypadAttempts(Map<PlayerId, KeypadAttempt> byPlayer) {
  /** A keypad nobody missed. */
  public static final KeypadAttempts NONE = new KeypadAttempts(Map.of());

  public KeypadAttempts {
    byPlayer = Map.copyOf(Objects.requireNonNull(byPlayer));
  }

  /** The misses of the player that still count now. */
  public int count(PlayerId player, long now, KeypadLimits limits) {
    return live(player, now, limits).map(KeypadAttempt::count).orElse(0);
  }

  /** The ticks the player still waits before the next try, 0 with no wait. */
  public long waitTicks(PlayerId player, long now, KeypadLimits limits) {
    return live(player, now, limits)
        .map(found -> Math.max(0, found.lastTick() + waitOf(found.count(), limits) - now))
        .orElse(0L);
  }

  /** One more miss of the player, now. */
  public KeypadAttempts miss(PlayerId player, long now, KeypadLimits limits) {
    int before = live(player, now, limits).map(KeypadAttempt::count).orElse(0);
    Map<PlayerId, KeypadAttempt> next = new HashMap<>(byPlayer);
    next.put(player, new KeypadAttempt(before + 1, now));
    return new KeypadAttempts(next);
  }

  /** The right code clears the count of the player (answer 665). */
  public KeypadAttempts pass(PlayerId player) {
    Map<PlayerId, KeypadAttempt> next = new HashMap<>(byPlayer);
    next.remove(player);
    return new KeypadAttempts(next);
  }

  /** Without the players whose count the reset already cleared, so the list never grows (898). */
  public KeypadAttempts pruned(long now, KeypadLimits limits) {
    Map<PlayerId, KeypadAttempt> next = new HashMap<>(byPlayer);
    next.values().removeIf(found -> now - found.lastTick() >= limits.resetTicks());
    return new KeypadAttempts(next);
  }

  /** The tries of the player, unless the reset or, still free, the free window cleared them. */
  private Optional<KeypadAttempt> live(PlayerId player, long now, KeypadLimits limits) {
    return Optional.ofNullable(byPlayer.get(player))
        .filter(found -> now - found.lastTick() < limits.resetTicks())
        .filter(found -> !freeAgain(found, now, limits));
  }

  private static boolean freeAgain(KeypadAttempt found, long now, KeypadLimits limits) {
    return found.count() <= limits.freeTries()
        && now - found.lastTick() >= limits.freeWindowTicks();
  }

  /** The wait after the count: none while free, then doubling from the first up to the cap. */
  private static long waitOf(int count, KeypadLimits limits) {
    int past = count - limits.freeTries();
    if (past <= 0) {
      return 0;
    }
    long wait = limits.firstWaitTicks();
    for (int step = 1; step < past && wait < limits.maxWaitTicks(); step++) {
      wait *= 2;
    }
    return Math.min(wait, limits.maxWaitTicks());
  }
}
