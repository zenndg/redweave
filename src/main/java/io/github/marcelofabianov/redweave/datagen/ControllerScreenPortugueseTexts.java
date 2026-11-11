package io.github.marcelofabianov.redweave.datagen;

import io.github.marcelofabianov.redweave.menu.ControllerScreenTexts;
import java.util.function.BiConsumer;

/** The Portuguese texts of the Controller Screen, with the same keys as the English ones. */
final class ControllerScreenPortugueseTexts {
  private static final String TAB_NODES = "Nós";
  private static final String TAB_SIGNALS = "Sinais";
  private static final String TAB_ACCESS = "Acesso";
  private static final String RENAME = "Renomear";
  private static final String RECOLOR_TOOLTIP = "Clique para trocar a cor.";
  private static final String DOUBLE_CLICK = "Clique duas vezes para renomear.";
  private static final String ENTER_OR_ESC = " Enter salva, Esc cancela.";
  private static final String RENAME_TOOLTIP = DOUBLE_CLICK + ENTER_OR_ESC;
  private static final String COLUMN_NODE = "Nó";
  private static final String COLUMN_SIGNAL = "Sinal";
  private static final String COLUMN_POSITION = "Posição";
  private static final String SLOT_ROW = "%s · %s";
  private static final String SLOT_ROW_NAMED = "%s · %s · %s";
  private static final String NO_ACTIVE_CHANNEL = "Nenhum canal ativo";
  private static final String SLOT_USE = "%s ent. · %s saí.";
  private static final String CHANNELS_IN_USE = "%s de %s canais em uso";
  private static final String UNLOADED = "%s descarregados";
  private static final String OUT_OF_RANGE = "%s fora de alcance";
  private static final String OWNER_LINE = "Dono: %s";
  private static final String OWNER = "Dono";
  private static final String READ_ONLY = "Só o dono pode alterar esta rede.";
  private static final String NO_NODES = "Nenhum nó nesta rede ainda.";

  private ControllerScreenPortugueseTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    addHeader(add);
    addRows(add);
    addFooter(add);
  }

  private static void addHeader(BiConsumer<String, String> add) {
    add.accept(ControllerScreenTexts.TAB_NODES, TAB_NODES);
    add.accept(ControllerScreenTexts.TAB_SIGNALS, TAB_SIGNALS);
    add.accept(ControllerScreenTexts.TAB_ACCESS, TAB_ACCESS);
    add.accept(ControllerScreenTexts.RENAME, RENAME);
    add.accept(ControllerScreenTexts.RECOLOR_TOOLTIP, RECOLOR_TOOLTIP);
    add.accept(ControllerScreenTexts.RENAME_TOOLTIP, RENAME_TOOLTIP);
  }

  private static void addRows(BiConsumer<String, String> add) {
    add.accept(ControllerScreenTexts.COLUMN_NODE, COLUMN_NODE);
    add.accept(ControllerScreenTexts.COLUMN_SIGNAL, COLUMN_SIGNAL);
    add.accept(ControllerScreenTexts.COLUMN_POSITION, COLUMN_POSITION);
    add.accept(ControllerScreenTexts.SLOT_ROW, SLOT_ROW);
    add.accept(ControllerScreenTexts.SLOT_ROW_NAMED, SLOT_ROW_NAMED);
    add.accept(ControllerScreenTexts.NO_ACTIVE_CHANNEL, NO_ACTIVE_CHANNEL);
    add.accept(ControllerScreenTexts.SLOT_USE, SLOT_USE);
    add.accept(ControllerScreenTexts.NO_NODES, NO_NODES);
    add.accept(ControllerScreenTexts.OWNER, OWNER);
  }

  private static void addFooter(BiConsumer<String, String> add) {
    add.accept(ControllerScreenTexts.CHANNELS_IN_USE, CHANNELS_IN_USE);
    add.accept(ControllerScreenTexts.UNLOADED, UNLOADED);
    add.accept(ControllerScreenTexts.OUT_OF_RANGE, OUT_OF_RANGE);
    add.accept(ControllerScreenTexts.OWNER_LINE, OWNER_LINE);
    add.accept(ControllerScreenTexts.READ_ONLY, READ_ONLY);
  }
}
