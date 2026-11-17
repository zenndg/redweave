package io.github.marcelofabianov.redweave.datagen;

import io.github.marcelofabianov.redweave.item.RemoteMessages;
import io.github.marcelofabianov.redweave.menu.RemoteLinkTexts;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import java.util.function.BiConsumer;

/** The texts of the Signal Remote in Portuguese (signal-remote.md). */
final class RemotePortugueseTexts {
  private static final String NAME = "Controle de Sinal";
  private static final String FIRED = "%s: %s";
  private static final String ON = "ligado";
  private static final String OFF = "desligado";
  private static final String PULSE = "%s: pulso";
  private static final String HELD = "%s continua ligado por outra fonte.";
  private static final String LINK_LOST = "Vínculo perdido";
  private static final String OFFLINE = "%s está desligada.";
  private static final String LINKED = "Vinculado: %s · %s · %s";
  private static final String HOW_TO_LINK = "Shift + clique direito para vincular.";
  private static final String LINK = "Vínculo: %s · %s · %s";
  private static final String LINKED_EMITTER = "Vinculado: %s";
  private static final String OUT_OF_REACH = "%s está fora de alcance.";
  private static final String LINK_EMITTER = "Vínculo: %s · %s";
  private static final String TURNS = "Liga e desliga";
  private static final String PRESSES = "Aperta";
  private static final String NO_LINK = "Sem vínculo";
  private static final String MODE = "Modo: %s";
  private static final String LEVER = "Alavanca";
  private static final String BUTTON = "Botão";
  private static final String STATE_ON = "Ligado";
  private static final String STATE_OFF = "Desligado";
  private static final String LINK_NETWORKS = "Redes";
  private static final String LINK_NODES = "Nós com entrada";
  private static final String LINK_SLOTS = "Canais de entrada";
  private static final String LINK_NO_INPUT_NODE = "Nenhum nó com entrada ativa nesta rede.";
  private static final String LINK_NODE_DETAIL = "%s · %s";
  private static final String RULE = "Num canal, vale o sinal mais forte.";

  private RemotePortugueseTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(RedweaveItems.SIGNAL_REMOTE.get().getDescriptionId(), NAME);
    add.accept(RemoteMessages.FIRED, FIRED);
    add.accept(RemoteMessages.ON, ON);
    add.accept(RemoteMessages.OFF, OFF);
    add.accept(RemoteMessages.PULSE, PULSE);
    add.accept(RemoteMessages.HELD, HELD);
    add.accept(RemoteMessages.LINK_LOST, LINK_LOST);
    add.accept(RemoteMessages.OFFLINE, OFFLINE);
    add.accept(RemoteMessages.LINKED, LINKED);
    add.accept(RemoteMessages.HOW_TO_LINK, HOW_TO_LINK);
    add.accept(RemoteMessages.LINK, LINK);
    add.accept(RemoteMessages.NO_LINK, NO_LINK);
    add.accept(RemoteMessages.LINKED_EMITTER, LINKED_EMITTER);
    add.accept(RemoteMessages.OUT_OF_REACH, OUT_OF_REACH);
    add.accept(RemoteMessages.LINK_EMITTER, LINK_EMITTER);
    add.accept(RemoteMessages.TURNS, TURNS);
    add.accept(RemoteMessages.PRESSES, PRESSES);
    add.accept(RemoteMessages.MODE, MODE);
    add.accept(RemoteMessages.LEVER, LEVER);
    add.accept(RemoteMessages.BUTTON, BUTTON);
    add.accept(RemoteMessages.STATE_ON, STATE_ON);
    add.accept(RemoteMessages.STATE_OFF, STATE_OFF);
    add.accept(RemoteMessages.RULE, RULE);
    add.accept(RemoteLinkTexts.NETWORKS, LINK_NETWORKS);
    add.accept(RemoteLinkTexts.NODES, LINK_NODES);
    add.accept(RemoteLinkTexts.SLOTS, LINK_SLOTS);
    add.accept(RemoteLinkTexts.NO_INPUT_NODE, LINK_NO_INPUT_NODE);
    add.accept(RemoteLinkTexts.NODE_DETAIL, LINK_NODE_DETAIL);
  }
}
