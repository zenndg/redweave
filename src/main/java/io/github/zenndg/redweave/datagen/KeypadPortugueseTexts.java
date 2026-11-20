package io.github.zenndg.redweave.datagen;

import io.github.zenndg.redweave.block.ModuleMessages;
import io.github.zenndg.redweave.menu.KeypadScreenTexts;
import io.github.zenndg.redweave.registry.RedweaveItems;
import java.util.function.BiConsumer;

/** The texts of the Signal Keypad in Portuguese (teclado.md, answers 659 to 667 and 905 to 908). */
final class KeypadPortugueseTexts {
  private static final String SIGNAL_KEYPAD = "Teclado de Sinal";
  private static final String UNLOCKED_MESSAGE = "%s: Destrancado";
  private static final String NO_POWER = "Sem energia.";
  private static final String NO_CODE_MESSAGE = "Este teclado não tem senha.";
  private static final String MISSED = "⚠ %s errou a senha de %s (%s tentativas)";
  private static final String LOCKED = "Trancado";
  private static final String UNLOCKED = "Destrancado";
  private static final String CODE = "Senha";
  private static final String CHANGE = "Trocar";
  private static final String OPEN_TIME = "Tempo aberto";
  private static final String CARDS = "Cartões: %s";
  private static final String NO_CARDS = "Sem cartões";
  private static final String CLEAR = "Limpar";
  private static final String ENTER = "Entrar";
  private static final String GRANTED = "Acesso liberado";
  private static final String WRONG = "Senha errada";
  private static final String WAIT = "Espere %s s.";
  private static final String NEW_CODE = "Nova senha";
  private static final String REPEAT_CODE = "Repita a senha";
  private static final String MISMATCH = "As senhas não batem";
  private static final String NO_CODE = "Sem senha";

  private KeypadPortugueseTexts() {}

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
