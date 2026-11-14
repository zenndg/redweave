package io.github.marcelofabianov.redweave.datagen;

import io.github.marcelofabianov.redweave.block.ModuleMessages;
import io.github.marcelofabianov.redweave.menu.InverterScreenTexts;
import io.github.marcelofabianov.redweave.menu.TimerScreenTexts;
import io.github.marcelofabianov.redweave.registry.RedweaveBlocks;
import java.util.function.BiConsumer;

/**
 * The texts of the Signal Modules in Portuguese (modulos-de-sinal.md, signal-timer.md and
 * signal-inverter.md).
 */
final class ModulePortugueseTexts {
  private static final String SIGNAL_TIMER = "Temporizador de Sinal";
  private static final String USE_TO_OPEN = "Use um Configurador para abrir este módulo.";
  private static final String PICKED_UP = "Módulo recolhido";
  private static final String NOT_OWNER = "Só quem colocou este módulo pode alterá-lo.";
  private static final String OUTPUT = "Saída: %s";
  private static final String NOT_COPIED = "Módulos ainda não podem ser copiados.";
  private static final String TIMER_RUNNING = "%s: rodando";
  private static final String TIMER_STOPPED = "%s: parado";
  private static final String SCREEN_INTERVAL = "Intervalo";
  private static final String SCREEN_PULSE = "Pulso";
  private static final String SCREEN_OUTPUT = "Saída";
  private static final String SCREEN_PAUSE = "Pausa";
  private static final String SCREEN_EDGE = "Borda";
  private static final String SCREEN_CHANNEL = "Canal";
  private static final String SCREEN_RUNNING = "Rodando";
  private static final String SCREEN_STOPPED = "Parado";
  private static final String SCREEN_PAUSED = "Pausado";
  private static final String SCREEN_SECONDS = "%s s";
  private static final String SIGNAL_INVERTER = "Inversor de Sinal";
  private static final String INVERTER_NOTHING = "O Inversor de Sinal não tem o que alternar.";
  private static final String INVERTER_ON = "Ligado";
  private static final String INVERTER_OFF = "Desligado";
  private static final String INVERTER_BURNT_OUT = "Queimado";
  private static final String SCREEN_TICKS = "%s ticks";

  private ModulePortugueseTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(RedweaveBlocks.SIGNAL_TIMER.get().getDescriptionId(), SIGNAL_TIMER);
    add.accept(ModuleMessages.USE_TO_OPEN, USE_TO_OPEN);
    add.accept(ModuleMessages.PICKED_UP, PICKED_UP);
    add.accept(ModuleMessages.NOT_OWNER, NOT_OWNER);
    add.accept(ModuleMessages.OUTPUT, OUTPUT);
    add.accept(ModuleMessages.NOT_COPIED, NOT_COPIED);
    add.accept(ModuleMessages.TIMER_RUNNING, TIMER_RUNNING);
    add.accept(ModuleMessages.TIMER_STOPPED, TIMER_STOPPED);
    add.accept(TimerScreenTexts.INTERVAL, SCREEN_INTERVAL);
    add.accept(TimerScreenTexts.PULSE, SCREEN_PULSE);
    add.accept(TimerScreenTexts.OUTPUT, SCREEN_OUTPUT);
    add.accept(TimerScreenTexts.PAUSE, SCREEN_PAUSE);
    add.accept(TimerScreenTexts.EDGE, SCREEN_EDGE);
    add.accept(TimerScreenTexts.CHANNEL, SCREEN_CHANNEL);
    add.accept(TimerScreenTexts.RUNNING, SCREEN_RUNNING);
    add.accept(TimerScreenTexts.STOPPED, SCREEN_STOPPED);
    add.accept(TimerScreenTexts.PAUSED, SCREEN_PAUSED);
    add.accept(TimerScreenTexts.SECONDS, SCREEN_SECONDS);
    add.accept(TimerScreenTexts.TICKS, SCREEN_TICKS);
    add.accept(RedweaveBlocks.SIGNAL_INVERTER.get().getDescriptionId(), SIGNAL_INVERTER);
    add.accept(ModuleMessages.INVERTER_NOTHING_TO_TOGGLE, INVERTER_NOTHING);
    add.accept(InverterScreenTexts.ON, INVERTER_ON);
    add.accept(InverterScreenTexts.OFF, INVERTER_OFF);
    add.accept(InverterScreenTexts.BURNT_OUT, INVERTER_BURNT_OUT);
  }
}
