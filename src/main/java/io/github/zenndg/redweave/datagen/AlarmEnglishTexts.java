package io.github.zenndg.redweave.datagen;

import io.github.zenndg.redweave.block.ModuleMessages;
import io.github.zenndg.redweave.menu.AlarmScreenTexts;
import io.github.zenndg.redweave.registry.RedweaveItems;
import java.util.function.BiConsumer;

/** The texts of the Signal Alarm in English (alarme.md and answers 643 to 649). */
final class AlarmEnglishTexts {
  private static final String SIGNAL_ALARM = "Signal Alarm";
  private static final String QUIET = "Quiet";
  private static final String RINGING = "Ringing";
  private static final String LISTENERS = "Heard by";
  private static final String REPEATS = "Rings";
  private static final String INTERVAL = "Interval";
  private static final String DURATION = "Duration";
  private static final String OWNER = "Owner";
  private static final String NETWORK = "Network";
  private static final String RANGE = "Everyone";
  private static final String RING = "Alarm %s: Ringing...";
  private static final String SILENCED = "%s: Silenced";

  private AlarmEnglishTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(RedweaveItems.SIGNAL_ALARM.get().getDescriptionId(), SIGNAL_ALARM);
    add.accept(AlarmScreenTexts.QUIET, QUIET);
    add.accept(AlarmScreenTexts.RINGING, RINGING);
    add.accept(AlarmScreenTexts.LISTENERS, LISTENERS);
    add.accept(AlarmScreenTexts.REPEATS, REPEATS);
    add.accept(AlarmScreenTexts.INTERVAL, INTERVAL);
    add.accept(AlarmScreenTexts.DURATION, DURATION);
    add.accept(AlarmScreenTexts.OWNER, OWNER);
    add.accept(AlarmScreenTexts.NETWORK, NETWORK);
    add.accept(AlarmScreenTexts.RANGE, RANGE);
    add.accept(ModuleMessages.ALARM_RING, RING);
    add.accept(ModuleMessages.ALARM_SILENCED, SILENCED);
  }
}
