package io.github.zenndg.redweave.application;

import io.github.zenndg.redweave.domain.module.CardId;
import io.github.zenndg.redweave.domain.module.KeypadCode;
import io.github.zenndg.redweave.domain.module.KeypadLimits;
import io.github.zenndg.redweave.domain.module.SignalKeypad;
import io.github.zenndg.redweave.domain.module.SignalModule;
import io.github.zenndg.redweave.domain.network.PlayerId;
import java.util.Optional;

/**
 * The owner of a Signal Keypad changes its strength, its open time and its code, with a new salt,
 * and revokes a card from its screen. Anyone else changes nothing (answers 663, 669, 897, 905 and
 * 915). The keypad lives in its part, so the caller keeps what comes back.
 */
public final class ChangeKeypad {
  private final KeypadSalts salts;

  public ChangeKeypad(KeypadSalts salts) {
    this.salts = salts;
  }

  public Optional<SignalKeypad> strength(
      PlayerId requester, SignalModule module, SignalKeypad keypad, int strength) {
    return owned(requester, module).map(owner -> keypad.withStrength(strength));
  }

  public Optional<SignalKeypad> open(
      PlayerId requester,
      SignalModule module,
      SignalKeypad keypad,
      int ticks,
      KeypadLimits limits) {
    return owned(requester, module).map(owner -> keypad.withOpen(ticks, limits));
  }

  /** A new code, which clears the tries of everyone (answer 905). */
  public Optional<SignalKeypad> code(
      PlayerId requester, SignalModule module, SignalKeypad keypad, KeypadCode code) {
    return owned(requester, module).map(owner -> keypad.withCode(code, salts.next()));
  }

  public Optional<SignalKeypad> revoke(
      PlayerId requester, SignalModule module, SignalKeypad keypad, CardId card) {
    return owned(requester, module).map(owner -> keypad.revoke(card));
  }

  private static Optional<PlayerId> owned(PlayerId requester, SignalModule module) {
    return module.isOwner(requester) ? Optional.of(requester) : Optional.empty();
  }
}
