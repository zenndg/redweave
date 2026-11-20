package io.github.zenndg.redweave.datagen;

import io.github.zenndg.redweave.menu.TerminalScreenTexts;
import java.util.function.BiConsumer;

/** The texts of the screen of the Network Terminal in english (answers 146, 255, 256 and 261). */
final class TerminalEnglishTexts {
  private static final String COUNT_ONE = "%s network";
  private static final String COUNT_MANY = "%s networks";
  private static final String STATE_UNPLACED = "Unplaced";
  private static final String OPEN = "Open";
  private static final String TURN_OFF = "Turn off";
  private static final String TURN_ON = "Turn on";
  private static final String CLEAR_COMPONENTS = "Clear components";
  private static final String DELETE = "Delete";
  private static final String CONFIRM_CLEAR = "Click again to clear";
  private static final String CONFIRM_DELETE = "Click again to delete";
  private static final String HINT = "Double-click a name to rename it.";
  private static final String NO_NETWORKS_YET = "No network yet.";
  private static final String NO_NETWORKS_HINT = " Place a Network Controller to create one.";
  private static final String NO_NETWORKS = NO_NETWORKS_YET + NO_NETWORKS_HINT;

  private TerminalEnglishTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(TerminalScreenTexts.COUNT_ONE, COUNT_ONE);
    add.accept(TerminalScreenTexts.COUNT_MANY, COUNT_MANY);
    add.accept(TerminalScreenTexts.STATE_UNPLACED, STATE_UNPLACED);
    add.accept(TerminalScreenTexts.OPEN, OPEN);
    add.accept(TerminalScreenTexts.TURN_OFF, TURN_OFF);
    add.accept(TerminalScreenTexts.TURN_ON, TURN_ON);
    add.accept(TerminalScreenTexts.CLEAR_COMPONENTS, CLEAR_COMPONENTS);
    add.accept(TerminalScreenTexts.DELETE, DELETE);
    add.accept(TerminalScreenTexts.CONFIRM_CLEAR, CONFIRM_CLEAR);
    add.accept(TerminalScreenTexts.CONFIRM_DELETE, CONFIRM_DELETE);
    add.accept(TerminalScreenTexts.HINT, HINT);
    add.accept(TerminalScreenTexts.NO_NETWORKS, NO_NETWORKS);
  }
}
