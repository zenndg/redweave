package io.github.marcelofabianov.redweave.datagen;

import io.github.marcelofabianov.redweave.block.ControllerMessages;
import io.github.marcelofabianov.redweave.registry.RedweaveBlocks;
import io.github.marcelofabianov.redweave.registry.RedweaveCreativeTabs;
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
  private static final String NETWORK_CONTROLLER = "Network Controller";
  private static final String OWNED_BY = "%s · owner: %s";
  private static final String NO_NETWORK = "No network";
  private static final String NOT_OWNER = "Only the owner can place this network.";
  private static final String ALREADY_PLACED = "This network already has a controller.";

  private EnglishTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(RedweaveLanguageProvider.MOD_DESCRIPTION_KEY, MOD_DESCRIPTION);
    add.accept(RedweaveBlocks.NETWORK_CONTROLLER.get().getDescriptionId(), NETWORK_CONTROLLER);
    add.accept(RedweaveCreativeTabs.TITLE, TAB);
    NodeScreenEnglishTexts.addTo(add);
    ConfiguratorEnglishTexts.addTo(add);
    add.accept(RedweaveBlocks.SIGNAL_NODE.get().getDescriptionId(), SIGNAL_NODE);
    add.accept(ControllerMessages.OWNED_BY, OWNED_BY);
    add.accept(ControllerMessages.NO_NETWORK, NO_NETWORK);
    add.accept(ControllerMessages.NOT_OWNER, NOT_OWNER);
    add.accept(ControllerMessages.ALREADY_PLACED, ALREADY_PLACED);
  }
}
