package io.github.zenndg.redweave.datagen;

import io.github.zenndg.redweave.block.ModuleMessages;
import io.github.zenndg.redweave.menu.LockScreenTexts;
import io.github.zenndg.redweave.registry.RedweaveItems;
import java.util.function.BiConsumer;

/** The texts of the Signal Lock in English (fechadura.md and answers 633 to 641). */
final class LockEnglishTexts {
  private static final String SIGNAL_LOCK = "Signal Lock";
  private static final String LOCKED = "Locked";
  private static final String UNLOCKED = "Unlocked";
  private static final String KEY = "Key";
  private static final String PULSE = "Pulse";
  private static final String UNLOCKED_MESSAGE = "%s: Unlocked";
  private static final String WRENCH_ONLY = "Only the owner can remove this with a Configurator.";

  private LockEnglishTexts() {}

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
