package io.github.marcelofabianov.redweave.datagen;

import io.github.marcelofabianov.redweave.block.ControllerMessages;
import io.github.marcelofabianov.redweave.block.TerminalMessages;
import io.github.marcelofabianov.redweave.block.part.PartMessages;
import io.github.marcelofabianov.redweave.registry.RedweaveBlocks;
import io.github.marcelofabianov.redweave.registry.RedweaveCreativeTabs;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import java.util.function.BiConsumer;

/** The English texts, the base of every language. */
final class EnglishTexts {
  private static final String PITCH = "Weave your redstone.";
  private static final String NETWORKS = " Wireless signal networks,";
  private static final String FEATURES = " programmable channels and logic chips,";
  private static final String SCREENS = " with screens that are easy to learn";
  private static final String BEAUTY = " and beautiful to use.";

  /** The same text as the description of the neoforge.mods.toml, checked by a test. */
  private static final String MOD_DESCRIPTION = PITCH + NETWORKS + FEATURES + SCREENS + BEAUTY;

  private static final String TAB = "Redweave";
  private static final String SIGNAL_NODE = "Signal Node";
  private static final String FACE_TAKEN = "This face is taken.";
  private static final String CABLES_FULL = "This space already has 2 cables.";
  private static final String NETWORK_CONTROLLER = "Network Controller";
  private static final String NETWORK_TERMINAL = "Network Terminal";
  private static final String TERMINAL_NOT_OWNER = "Only the owner can use this terminal.";
  private static final String TERMINAL_LIMIT = "You already have a Network Terminal at %s, %s, %s.";
  private static final String OFFLINE = "%s is offline. Turn it on at a Network Terminal.";
  private static final String NETWORK_LIMIT_REACHED = "You have reached the limit of %s networks.";
  private static final String NETWORK_LIMIT_HINT = " Delete an unplaced one at a Network Terminal.";
  private static final String NETWORK_LIMIT = NETWORK_LIMIT_REACHED + NETWORK_LIMIT_HINT;
  private static final String NO_NETWORK = "No network";
  private static final String NOT_OWNER = "Only the owner can place this network.";
  private static final String ALREADY_PLACED = "This network already has a controller.";

  private EnglishTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(RedweaveLanguageProvider.MOD_DESCRIPTION_KEY, MOD_DESCRIPTION);
    add.accept(RedweaveBlocks.NETWORK_CONTROLLER.get().getDescriptionId(), NETWORK_CONTROLLER);
    add.accept(RedweaveBlocks.NETWORK_TERMINAL.get().getDescriptionId(), NETWORK_TERMINAL);
    add.accept(TerminalMessages.NOT_OWNER, TERMINAL_NOT_OWNER);
    add.accept(TerminalMessages.LIMIT, TERMINAL_LIMIT);
    add.accept(RedweaveCreativeTabs.TITLE, TAB);
    NodeScreenEnglishTexts.addTo(add);
    ControllerScreenEnglishTexts.addTo(add);
    TerminalEnglishTexts.addTo(add);
    ConfiguratorEnglishTexts.addTo(add);
    RemoteEnglishTexts.addTo(add);
    ModuleEnglishTexts.addTo(add);
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
