package io.github.zenndg.redweave.application;

import io.github.zenndg.redweave.domain.module.AlarmLimits;
import io.github.zenndg.redweave.domain.module.AlarmSettings;
import io.github.zenndg.redweave.domain.module.SignalAlarm;
import io.github.zenndg.redweave.domain.module.SignalModule;
import io.github.zenndg.redweave.domain.network.PlayerId;
import java.util.Optional;

/**
 * The owner of a Signal Alarm changes who hears it, its rings, its interval and its duration from
 * its screen,. Anyone else changes nothing, and the numbers stay within the limits of the config
 * (answers 378, 645, 646 and 648). The alarm lives in its part, so the caller keeps what comes
 * back.
 */
public final class ChangeAlarm {
  public Optional<SignalAlarm> listeners(
      PlayerId requester, SignalModule module, SignalAlarm alarm) {
    return owned(requester, module)
        .map(owner ->
            set(alarm, alarm.settings().withListeners(alarm.settings().listeners().next())));
  }

  public Optional<SignalAlarm> repeats(
      PlayerId requester, SignalModule module, SignalAlarm alarm, int repeats, AlarmLimits limits) {
    return owned(requester, module)
        .map(owner -> set(alarm, alarm.settings().withRepeats(repeats, limits)));
  }

  public Optional<SignalAlarm> interval(
      PlayerId requester, SignalModule module, SignalAlarm alarm, int ticks, AlarmLimits limits) {
    return owned(requester, module)
        .map(owner -> set(alarm, alarm.settings().withInterval(ticks, limits)));
  }

  public Optional<SignalAlarm> duration(
      PlayerId requester, SignalModule module, SignalAlarm alarm, int ticks, AlarmLimits limits) {
    return owned(requester, module)
        .map(owner -> set(alarm, alarm.settings().withDuration(ticks, limits)));
  }

  private static SignalAlarm set(SignalAlarm alarm, AlarmSettings settings) {
    return alarm.withSettings(settings);
  }

  private static Optional<PlayerId> owned(PlayerId requester, SignalModule module) {
    return module.isOwner(requester) ? Optional.of(requester) : Optional.empty();
  }
}
