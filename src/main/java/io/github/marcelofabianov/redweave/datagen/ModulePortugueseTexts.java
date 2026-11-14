package io.github.marcelofabianov.redweave.datagen;

import io.github.marcelofabianov.redweave.block.ModuleMessages;
import io.github.marcelofabianov.redweave.registry.RedweaveBlocks;
import java.util.function.BiConsumer;

/** The texts of the Signal Modules in Portuguese (modulos-de-sinal.md and signal-timer.md). */
final class ModulePortugueseTexts {
  private static final String SIGNAL_TIMER = "Temporizador de Sinal";
  private static final String USE_TO_OPEN = "Use um Configurador para abrir este módulo.";
  private static final String PICKED_UP = "Módulo recolhido";
  private static final String NOT_OWNER = "Só quem colocou este módulo pode alterá-lo.";
  private static final String OUTPUT = "Saída: %s";
  private static final String NOT_COPIED = "Módulos ainda não podem ser copiados.";
  private static final String TIMER_RUNNING = "%s: rodando";
  private static final String TIMER_STOPPED = "%s: parado";

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
  }
}
