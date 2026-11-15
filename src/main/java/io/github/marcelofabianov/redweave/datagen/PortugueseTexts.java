package io.github.marcelofabianov.redweave.datagen;

import io.github.marcelofabianov.redweave.block.ControllerMessages;
import io.github.marcelofabianov.redweave.block.TerminalMessages;
import io.github.marcelofabianov.redweave.block.part.PartMessages;
import io.github.marcelofabianov.redweave.registry.RedweaveBlocks;
import io.github.marcelofabianov.redweave.registry.RedweaveCreativeTabs;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import java.util.function.BiConsumer;

/** The Brazilian Portuguese texts, with the same keys as English. */
final class PortugueseTexts {
  private static final String PITCH = "Teça a sua redstone.";
  private static final String NETWORKS = " Redes de sinal sem fio,";
  private static final String FEATURES = " canais programáveis e chips de lógica,";
  private static final String SCREENS = " com telas fáceis de aprender e bonitas de usar.";
  private static final String MOD_DESCRIPTION = PITCH + NETWORKS + FEATURES + SCREENS;

  private static final String TAB = "Redweave";
  private static final String SIGNAL_NODE = "Nó de Sinal";
  private static final String FACE_TAKEN = "Esta face já está ocupada.";
  private static final String CABLES_FULL = "Este espaço já tem 2 cabos.";
  private static final String NETWORK_CONTROLLER = "Controlador de Rede";
  private static final String NETWORK_TERMINAL = "Terminal de Rede";
  private static final String TERMINAL_NOT_OWNER = "Só o dono pode usar este terminal.";
  private static final String TERMINAL_LIMIT = "Você já tem um Terminal de Rede em %s, %s, %s.";
  private static final String OFFLINE = "%s está desligada. Ligue-a num Terminal de Rede.";
  private static final String NETWORK_LIMIT_REACHED = "Você chegou ao limite de %s redes.";
  private static final String NETWORK_LIMIT_HINT = " Apague uma não colocada num Terminal de Rede.";
  private static final String NETWORK_LIMIT = NETWORK_LIMIT_REACHED + NETWORK_LIMIT_HINT;
  private static final String NO_NETWORK = "Sem rede";
  private static final String NOT_OWNER = "Só o dono pode colocar esta rede.";
  private static final String ALREADY_PLACED = "Esta rede já tem um controlador.";

  private PortugueseTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(RedweaveLanguageProvider.MOD_DESCRIPTION_KEY, MOD_DESCRIPTION);
    add.accept(RedweaveBlocks.NETWORK_CONTROLLER.get().getDescriptionId(), NETWORK_CONTROLLER);
    add.accept(RedweaveBlocks.NETWORK_TERMINAL.get().getDescriptionId(), NETWORK_TERMINAL);
    add.accept(TerminalMessages.NOT_OWNER, TERMINAL_NOT_OWNER);
    add.accept(TerminalMessages.LIMIT, TERMINAL_LIMIT);
    add.accept(RedweaveCreativeTabs.TITLE, TAB);
    NodeScreenPortugueseTexts.addTo(add);
    ControllerScreenPortugueseTexts.addTo(add);
    TerminalPortugueseTexts.addTo(add);
    ConfiguratorPortugueseTexts.addTo(add);
    RemotePortugueseTexts.addTo(add);
    ModulePortugueseTexts.addTo(add);
    add.accept(RedweaveItems.SIGNAL_NODE.get().getDescriptionId(), SIGNAL_NODE);
    add.accept(PartMessages.FACE_TAKEN, FACE_TAKEN);
    add.accept(PartMessages.CABLES_FULL, CABLES_FULL);
    add.accept(ControllerMessages.OFFLINE, OFFLINE);
    add.accept(ControllerMessages.NETWORK_LIMIT, NETWORK_LIMIT);
    add.accept(ControllerMessages.NO_NETWORK, NO_NETWORK);
    add.accept(ControllerMessages.NOT_OWNER, NOT_OWNER);
    add.accept(ControllerMessages.ALREADY_PLACED, ALREADY_PLACED);
  }
}
