package io.github.marcelofabianov.redweave.datagen;

import io.github.marcelofabianov.redweave.menu.TerminalScreenTexts;
import java.util.function.BiConsumer;

/**
 * The texts of the screen of the Network Terminal in portuguese (answers 146, 255, 256 and 261).
 */
final class TerminalPortugueseTexts {
  private static final String COUNT_ONE = "%s rede";
  private static final String COUNT_MANY = "%s redes";
  private static final String STATE_UNPLACED = "Não colocada";
  private static final String OPEN = "Abrir";
  private static final String TURN_OFF = "Desligar";
  private static final String TURN_ON = "Ligar";
  private static final String CLEAR_NODES = "Limpar nós";
  private static final String DELETE = "Apagar";
  private static final String CONFIRM_CLEAR = "Clique de novo para limpar";
  private static final String CONFIRM_DELETE = "Clique de novo para apagar";
  private static final String HINT = "Clique duas vezes num nome para renomear.";
  private static final String NO_NETWORKS_YET = "Nenhuma rede ainda.";
  private static final String NO_NETWORKS_HINT = " Coloque um Controlador de Rede para criar uma.";
  private static final String NO_NETWORKS = NO_NETWORKS_YET + NO_NETWORKS_HINT;

  private TerminalPortugueseTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(TerminalScreenTexts.COUNT_ONE, COUNT_ONE);
    add.accept(TerminalScreenTexts.COUNT_MANY, COUNT_MANY);
    add.accept(TerminalScreenTexts.STATE_UNPLACED, STATE_UNPLACED);
    add.accept(TerminalScreenTexts.OPEN, OPEN);
    add.accept(TerminalScreenTexts.TURN_OFF, TURN_OFF);
    add.accept(TerminalScreenTexts.TURN_ON, TURN_ON);
    add.accept(TerminalScreenTexts.CLEAR_NODES, CLEAR_NODES);
    add.accept(TerminalScreenTexts.DELETE, DELETE);
    add.accept(TerminalScreenTexts.CONFIRM_CLEAR, CONFIRM_CLEAR);
    add.accept(TerminalScreenTexts.CONFIRM_DELETE, CONFIRM_DELETE);
    add.accept(TerminalScreenTexts.HINT, HINT);
    add.accept(TerminalScreenTexts.NO_NETWORKS, NO_NETWORKS);
  }
}
