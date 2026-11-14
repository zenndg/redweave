package io.github.marcelofabianov.redweave.datagen;

import io.github.marcelofabianov.redweave.block.ModuleMessages;
import io.github.marcelofabianov.redweave.registry.RedweaveBlocks;
import java.util.function.BiConsumer;

/** The texts of the Signal Modules in English (modulos-de-sinal.md and signal-timer.md). */
final class ModuleEnglishTexts {
  private static final String SIGNAL_TIMER = "Signal Timer";
  private static final String USE_TO_OPEN = "Use a Configurator to open this module.";
  private static final String PICKED_UP = "Module picked up";
  private static final String NOT_OWNER = "Only the player who placed this module can change it.";
  private static final String OUTPUT = "Output: %s";
  private static final String NOT_COPIED = "Modules cannot be copied yet.";
  private static final String TIMER_RUNNING = "%s: Running";
  private static final String TIMER_STOPPED = "%s: Stopped";

  private ModuleEnglishTexts() {}

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
