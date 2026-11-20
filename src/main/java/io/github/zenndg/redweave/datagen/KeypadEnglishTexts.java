package io.github.zenndg.redweave.datagen;

import io.github.zenndg.redweave.block.ModuleMessages;
import io.github.zenndg.redweave.menu.KeypadScreenTexts;
import io.github.zenndg.redweave.registry.RedweaveItems;
import java.util.function.BiConsumer;

/** The texts of the Signal Keypad in English (teclado.md, answers 659 to 667 and 905 to 908). */
final class KeypadEnglishTexts {
  private static final String SIGNAL_KEYPAD = "Signal Keypad";
  private static final String UNLOCKED_MESSAGE = "%s: Unlocked";
  private static final String NO_POWER = "No power.";
  private static final String NO_CODE_MESSAGE = "This keypad has no code.";
  private static final String MISSED = "⚠ %s missed the code of %s (%s tries)";
  private static final String LOCKED = "Locked";
  private static final String UNLOCKED = "Unlocked";
  private static final String CODE = "Code";
  private static final String CHANGE = "Change";
  private static final String OPEN_TIME = "Open time";
  private static final String CARDS = "Cards: %s";
  private static final String NO_CARDS = "No cards";
  private static final String CLEAR = "Clear";
  private static final String ENTER = "Enter";
  private static final String GRANTED = "Access granted";
  private static final String WRONG = "Wrong code";
  private static final String WAIT = "Wait %s s.";
  private static final String NEW_CODE = "New code";
  private static final String REPEAT_CODE = "Repeat the code";
  private static final String MISMATCH = "The codes differ";
  private static final String NO_CODE = "No code";

  private KeypadEnglishTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(RedweaveItems.SIGNAL_KEYPAD.get().getDescriptionId(), SIGNAL_KEYPAD);
    add.accept(ModuleMessages.KEYPAD_UNLOCKED, UNLOCKED_MESSAGE);
    add.accept(ModuleMessages.KEYPAD_NO_POWER, NO_POWER);
    add.accept(ModuleMessages.KEYPAD_NO_CODE, NO_CODE_MESSAGE);
    add.accept(ModuleMessages.KEYPAD_MISSED, MISSED);
    screen(add);
  }

  private static void screen(BiConsumer<String, String> add) {
    add.accept(KeypadScreenTexts.LOCKED, LOCKED);
    add.accept(KeypadScreenTexts.UNLOCKED, UNLOCKED);
    add.accept(KeypadScreenTexts.CODE, CODE);
    add.accept(KeypadScreenTexts.CHANGE, CHANGE);
    add.accept(KeypadScreenTexts.OPEN_TIME, OPEN_TIME);
    add.accept(KeypadScreenTexts.CARDS, CARDS);
    add.accept(KeypadScreenTexts.NO_CARDS, NO_CARDS);
    add.accept(KeypadScreenTexts.CLEAR, CLEAR);
    add.accept(KeypadScreenTexts.ENTER, ENTER);
    add.accept(KeypadScreenTexts.GRANTED, GRANTED);
    add.accept(KeypadScreenTexts.WRONG, WRONG);
    add.accept(KeypadScreenTexts.WAIT, WAIT);
    add.accept(KeypadScreenTexts.NEW_CODE, NEW_CODE);
    add.accept(KeypadScreenTexts.REPEAT_CODE, REPEAT_CODE);
    add.accept(KeypadScreenTexts.MISMATCH, MISMATCH);
    add.accept(KeypadScreenTexts.NO_CODE, NO_CODE);
  }
}
