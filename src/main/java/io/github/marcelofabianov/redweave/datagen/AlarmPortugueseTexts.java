package io.github.marcelofabianov.redweave.datagen;

import io.github.marcelofabianov.redweave.block.ModuleMessages;
import io.github.marcelofabianov.redweave.menu.AlarmScreenTexts;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import java.util.function.BiConsumer;

/** The texts of the Signal Alarm in Portuguese (alarme.md and answers 643 to 649). */
final class AlarmPortugueseTexts {
  private static final String SIGNAL_ALARM = "Alarme de Sinal";
  private static final String QUIET = "Quieto";
  private static final String RINGING = "Tocando";
  private static final String LISTENERS = "Quem ouve";
  private static final String REPEATS = "Toques";
  private static final String INTERVAL = "Intervalo";
  private static final String DURATION = "Duração";
  private static final String OWNER = "Dono";
  private static final String NETWORK = "Rede";
  private static final String RANGE = "Todos";
  private static final String RING = "Alarme %s: Disparando...";
  private static final String SILENCED = "%s: Silenciado";

  private AlarmPortugueseTexts() {}

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
