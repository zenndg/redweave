package io.github.marcelofabianov.redweave.datagen;

import io.github.marcelofabianov.redweave.domain.network.Face;
import io.github.marcelofabianov.redweave.menu.NodeScreenTexts;
import java.util.function.BiConsumer;

/** The Brazilian Portuguese texts of the Node Screen, with the same keys as English. */
final class NodeScreenPortugueseTexts {
  private static final String INPUT = "Entrada";
  private static final String OUTPUT = "Saída";
  private static final String DIRECTION = "Direção";
  private static final String BLOCK_FACE = "Face do bloco";
  private static final String FACE_CLICK = "Clique numa face para escolhê-la.";
  private static final String FACE_DRAG = " Arraste para girar o bloco.";
  private static final String FACE_TOOLTIP = FACE_CLICK + FACE_DRAG;
  private static final String LOGIC = "Lógica";
  private static final String LOGIC_NONE = "Nenhuma";
  private static final String LOGIC_INVERT = "Inverter";
  private static final String LOGIC_PULSE = "Pulso";
  private static final String LOGIC_DELAY = "Atraso";
  private static final String LOGIC_THRESHOLD = "Limiar";
  private static final String LOGIC_LATER = "A lógica chega na Fase 2.";
  private static final String LOGIC_TOOLTIP = "Até lá, a saída repete o canal como ele está.";
  private static final String CHANNELS_ACTIVE = "%s de %s canais ativos";
  private static final String NAME = "Nome";
  private static final String CLICK_TO_NAME = "Clique para dar um nome";
  private static final String STATUS = "Status";
  private static final String ENABLED = "Ativo";
  private static final String DISABLED = "Inativo";
  private static final String SIGNAL = "Sinal";
  private static final String READ_ONLY = "Só o dono de %s pode alterar este nó.";
  private static final String NOT_IN_A_NETWORK = "Este nó não está numa rede.";
  private static final String STATE_ONLINE = "Online";
  private static final String STATE_UNLOADED = "Descarregado";
  private static final String STATE_OUT_OF_RANGE = "Fora de alcance";
  private static final String STATE_NO_NETWORK = "Sem rede";
  private static final String STATE_OFFLINE = "Offline";
  private static final String CHOOSE_NETWORK = "Escolha uma rede";
  private static final String NONE_ONLINE = "Nenhuma rede ligada.";
  private static final String PLACE_A_CONTROLLER = " Coloque um Controlador de Rede";
  private static final String TO_CREATE_ONE = " para criar uma.";
  private static final String NO_NETWORK_ONLINE = NONE_ONLINE + PLACE_A_CONTROLLER + TO_CREATE_ONE;
  private static final String NODE_COUNT_ONE = "%s nó";
  private static final String NODE_COUNT_MANY = "%s nós";
  private static final String BACK = "Voltar";
  private static final String RENAME_TOOLTIP = "Clique para renomear. Enter salva, Esc cancela.";
  private static final String SWITCH_NETWORK_TOOLTIP = "Clique para trocar de rede.";
  private static final String FACE_DOWN = "Baixo";
  private static final String FACE_UP = "Cima";
  private static final String FACE_NORTH = "Norte";
  private static final String FACE_SOUTH = "Sul";
  private static final String FACE_WEST = "Oeste";
  private static final String FACE_EAST = "Leste";

  private NodeScreenPortugueseTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    addLabels(add);
    addActions(add);
    addStates(add);
    addFaces(add);
    addNetworkChoice(add);
  }

  private static void addNetworkChoice(BiConsumer<String, String> add) {
    add.accept(NodeScreenTexts.CHOOSE_NETWORK, CHOOSE_NETWORK);
    add.accept(NodeScreenTexts.NO_NETWORK_ONLINE, NO_NETWORK_ONLINE);
    add.accept(NodeScreenTexts.NODE_COUNT_ONE, NODE_COUNT_ONE);
    add.accept(NodeScreenTexts.NODE_COUNT_MANY, NODE_COUNT_MANY);
    add.accept(NodeScreenTexts.BACK, BACK);
    add.accept(NodeScreenTexts.RENAME_TOOLTIP, RENAME_TOOLTIP);
    add.accept(NodeScreenTexts.SWITCH_NETWORK_TOOLTIP, SWITCH_NETWORK_TOOLTIP);
  }

  private static void addLabels(BiConsumer<String, String> add) {
    add.accept(NodeScreenTexts.INPUT, INPUT);
    add.accept(NodeScreenTexts.OUTPUT, OUTPUT);
    add.accept(NodeScreenTexts.DIRECTION, DIRECTION);
    add.accept(NodeScreenTexts.BLOCK_FACE, BLOCK_FACE);
    add.accept(NodeScreenTexts.FACE_TOOLTIP, FACE_TOOLTIP);
    add.accept(NodeScreenTexts.LOGIC, LOGIC);
  }

  private static void addActions(BiConsumer<String, String> add) {
    add.accept(NodeScreenTexts.LOGIC_NONE, LOGIC_NONE);
    add.accept(NodeScreenTexts.LOGIC_INVERT, LOGIC_INVERT);
    add.accept(NodeScreenTexts.LOGIC_PULSE, LOGIC_PULSE);
    add.accept(NodeScreenTexts.LOGIC_DELAY, LOGIC_DELAY);
    add.accept(NodeScreenTexts.LOGIC_THRESHOLD, LOGIC_THRESHOLD);
    add.accept(NodeScreenTexts.LOGIC_LATER, LOGIC_LATER);
    add.accept(NodeScreenTexts.LOGIC_TOOLTIP, LOGIC_TOOLTIP);
  }

  private static void addStates(BiConsumer<String, String> add) {
    add.accept(NodeScreenTexts.SIGNAL, SIGNAL);
    add.accept(NodeScreenTexts.CHANNELS_ACTIVE, CHANNELS_ACTIVE);
    add.accept(NodeScreenTexts.NAME, NAME);
    add.accept(NodeScreenTexts.CLICK_TO_NAME, CLICK_TO_NAME);
    add.accept(NodeScreenTexts.STATUS, STATUS);
    add.accept(NodeScreenTexts.ENABLED, ENABLED);
    add.accept(NodeScreenTexts.DISABLED, DISABLED);
    add.accept(NodeScreenTexts.READ_ONLY, READ_ONLY);
    add.accept(NodeScreenTexts.NOT_IN_A_NETWORK, NOT_IN_A_NETWORK);
    add.accept(NodeScreenTexts.STATE_ONLINE, STATE_ONLINE);
    add.accept(NodeScreenTexts.STATE_UNLOADED, STATE_UNLOADED);
    add.accept(NodeScreenTexts.STATE_OUT_OF_RANGE, STATE_OUT_OF_RANGE);
    add.accept(NodeScreenTexts.STATE_NO_NETWORK, STATE_NO_NETWORK);
    add.accept(NodeScreenTexts.STATE_OFFLINE, STATE_OFFLINE);
  }

  private static void addFaces(BiConsumer<String, String> add) {
    add.accept(NodeScreenTexts.face(Face.DOWN), FACE_DOWN);
    add.accept(NodeScreenTexts.face(Face.UP), FACE_UP);
    add.accept(NodeScreenTexts.face(Face.NORTH), FACE_NORTH);
    add.accept(NodeScreenTexts.face(Face.SOUTH), FACE_SOUTH);
    add.accept(NodeScreenTexts.face(Face.WEST), FACE_WEST);
    add.accept(NodeScreenTexts.face(Face.EAST), FACE_EAST);
  }
}
