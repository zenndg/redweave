package io.github.zenndg.redweave.domain.module;

import io.github.zenndg.redweave.domain.signal.Signal;
import java.util.Objects;
import java.util.Optional;

/**
 * The rule of the Signal Keypad (answers 659 to 667 and 896 to 916): with power on its input, the
 * right code or a linked card opens it for its open time, and it gives the strength chosen on its
 * screen while open. A wrong code counts for the player, never for the owner, and a wait refuses
 * any try without checking it. It has no output delay (answer 900).
 */
public record SignalKeypad(
    Signal strength,
    int openTicks,
    int remaining,
    Optional<KeypadSecret> secret,
    KeypadAttempts attempts,
    KeypadCards cards) {
  /** The open time of a keypad just placed, 3 s (answer 663). */
  public static final int PLACED_OPEN_TICKS = 60;

  /** The shortest open time, whatever the config says: one second (answer 915). */
  public static final int SHORTEST_OPEN_TICKS = 20;

  private static final String INVALID = "A keypad gives 1 to 15, opens 1+ tick and waits 0+";

  public SignalKeypad {
    Objects.requireNonNull(strength);
    Objects.requireNonNull(secret);
    Objects.requireNonNull(attempts);
    Objects.requireNonNull(cards);
    if (!strength.isOn() || openTicks < 1 || remaining < 0) {
      throw new IllegalArgumentException(INVALID);
    }
  }

  /** A new keypad: locked, with no code, giving 15 for 3 s when it opens (660 and 663). */
  public static SignalKeypad placed() {
    return new SignalKeypad(
        Signal.MAX, PLACED_OPEN_TICKS, 0, Optional.empty(), KeypadAttempts.NONE, KeypadCards.NONE);
  }

  /** A code typed on its keypad screen (answers 661 to 665 and 914). */
  public KeypadEntry enter(KeypadTry attempt, KeypadLimits limits) {
    if (!attempt.powered()) {
      return KeypadEntry.refused(KeypadEntry.Kind.NO_POWER, this);
    }
    if (secret.isEmpty()) {
      return KeypadEntry.refused(KeypadEntry.Kind.NO_CODE, this);
    }
    long wait = attempt.owner() ? 0 : attempts.waitTicks(attempt.player(), attempt.now(), limits);
    if (wait > 0) {
      return new KeypadEntry(KeypadEntry.Kind.WAIT, this, 0, wait, false);
    }
    if (secret.get().matches(attempt.code())) {
      SignalKeypad open = withAttempts(attempts.pass(attempt.player())).toggle();
      return KeypadEntry.refused(KeypadEntry.Kind.GRANTED, open);
    }
    return miss(attempt, limits);
  }

  /** A wrong code: nothing for the owner, one more miss for anyone else (664 and 914). */
  private KeypadEntry miss(KeypadTry attempt, KeypadLimits limits) {
    if (attempt.owner()) {
      return KeypadEntry.refused(KeypadEntry.Kind.WRONG, this);
    }
    KeypadAttempts next = attempts.miss(attempt.player(), attempt.now(), limits);
    int misses = next.count(attempt.player(), attempt.now(), limits);
    boolean warns = misses > limits.freeTries();
    return new KeypadEntry(KeypadEntry.Kind.WRONG, withAttempts(next), misses, 0, warns);
  }

  /** A Signal Card on the keypad: a linked one opens it as the code, with power (670, 671). */
  public KeypadEntry useCard(CardId card, boolean powered) {
    if (!powered) {
      return KeypadEntry.refused(KeypadEntry.Kind.NO_POWER, this);
    }
    if (!cards.accepts(card)) {
      return KeypadEntry.refused(KeypadEntry.Kind.NOT_LINKED, this);
    }
    return KeypadEntry.refused(KeypadEntry.Kind.GRANTED, toggle());
  }

  /** Open for its open time, as the right code or the `Toggle` of its owner (answer 667). */
  public SignalKeypad toggle() {
    return new SignalKeypad(strength, openTicks, openTicks, secret, attempts, cards);
  }

  /** One tick with its chunk loaded: the open time runs out (answer 907). */
  public SignalKeypad tick() {
    return new SignalKeypad(
        strength, openTicks, Math.max(0, remaining - 1), secret, attempts, cards);
  }

  public boolean unlocked() {
    return remaining > 0;
  }

  /** The chosen strength while open, nothing while locked (answer 663). */
  public Signal output() {
    return unlocked() ? strength : Signal.OFF;
  }

  /** A new code from its owner, with the salt; it clears the tries of everyone (answer 905). */
  public SignalKeypad withCode(KeypadCode code, String salt) {
    return new SignalKeypad(
        strength,
        openTicks,
        remaining,
        Optional.of(KeypadSecret.of(code, salt)),
        KeypadAttempts.NONE,
        cards);
  }

  /** Another strength, kept from 1 to 15, as the `−` and `+` of the screen step it (663). */
  public SignalKeypad withStrength(int next) {
    return new SignalKeypad(Emitter.stepped(next), openTicks, remaining, secret, attempts, cards);
  }

  /** Another open time, within the limits; with the minimum over the maximum, the minimum (916). */
  public SignalKeypad withOpen(int ticks, KeypadLimits limits) {
    int open = Interval.clamped(ticks, limits, SHORTEST_OPEN_TICKS);
    return new SignalKeypad(strength, open, remaining, secret, attempts, cards);
  }

  /** The tries of the players whose count the reset already cleared dropped (answer 898). */
  public SignalKeypad pruned(long now, KeypadLimits limits) {
    return withAttempts(attempts.pruned(now, limits));
  }

  /** Accepts the card too, or none past the limit (answers 669 and 904). */
  public Optional<SignalKeypad> link(CardId card, CardLimits limits) {
    return cards
        .link(card, limits)
        .map(next -> new SignalKeypad(strength, openTicks, remaining, secret, attempts, next));
  }

  /** The owner revokes a card, with no need of it (answer 669). */
  public SignalKeypad revoke(CardId card) {
    return new SignalKeypad(strength, openTicks, remaining, secret, attempts, cards.revoke(card));
  }

  private SignalKeypad withAttempts(KeypadAttempts next) {
    return new SignalKeypad(strength, openTicks, remaining, secret, next, cards);
  }
}
