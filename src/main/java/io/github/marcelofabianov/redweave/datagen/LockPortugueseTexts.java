package io.github.marcelofabianov.redweave.datagen;

import io.github.marcelofabianov.redweave.block.ModuleMessages;
import io.github.marcelofabianov.redweave.menu.LockScreenTexts;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import java.util.function.BiConsumer;

/** The texts of the Signal Lock in Portuguese (fechadura.md and answers 633 to 641). */
final class LockPortugueseTexts {
  private static final String SIGNAL_LOCK = "Fechadura de Sinal";
  private static final String LOCKED = "Trancada";
  private static final String UNLOCKED = "Aberta";
  private static final String KEY = "Chave";
  private static final String PULSE = "Pulso";
  private static final String UNLOCKED_MESSAGE = "%s: Aberta";
  private static final String WRENCH_ONLY = "Só o dono pode retirar isto com um Configurator.";

  private LockPortugueseTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(RedweaveItems.SIGNAL_LOCK.get().getDescriptionId(), SIGNAL_LOCK);
    add.accept(LockScreenTexts.LOCKED, LOCKED);
    add.accept(LockScreenTexts.UNLOCKED, UNLOCKED);
    add.accept(LockScreenTexts.KEY, KEY);
    add.accept(LockScreenTexts.PULSE, PULSE);
    add.accept(ModuleMessages.LOCK_UNLOCKED, UNLOCKED_MESSAGE);
    add.accept(ModuleMessages.WRENCH_ONLY, WRENCH_ONLY);
  }
}
