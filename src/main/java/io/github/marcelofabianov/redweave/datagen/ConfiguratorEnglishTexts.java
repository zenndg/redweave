package io.github.marcelofabianov.redweave.datagen;

import io.github.marcelofabianov.redweave.item.ConfiguratorMessages;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import java.util.function.BiConsumer;

/** The texts of the Configurator in English, the base language (answer 108). */
final class ConfiguratorEnglishTexts {
  private static final String NAME = "Configurator";
  private static final String STORED = "Configurator: %s";
  private static final String NOT_YOURS = "Only the owner can use this network.";
  private static final String LINKED = "Linked to %s";
  private static final String NOTHING_STORED = "Click a Network Controller first.";
  private static final String ALREADY_IN = "This node is already in %s.";
  private static final String OWN_BOTH = "You must own both networks.";
  private static final String GONE = "The stored network no longer exists.";
  private static final String COPIED = "Copied %s slots";
  private static final String PASTED = "Pasted %s slots";
  private static final String PASTED_LEFT_OUT = "Pasted %s slots, %s left out";
  private static final String CLEARED = "Copy cleared";
  private static final String NETWORK = "Network: %s";
  private static final String NO_NETWORK_STORED = "No network stored";
  private static final String COPY = "Copy: %s slots";
  private static final String HINT_STORE = "Click a controller to store its network.";
  private static final String HINT_COPY = "Shift + click a node to copy it.";

  private ConfiguratorEnglishTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(RedweaveItems.CONFIGURATOR.get().getDescriptionId(), NAME);
    add.accept(ConfiguratorMessages.STORED, STORED);
    add.accept(ConfiguratorMessages.NOT_YOURS, NOT_YOURS);
    add.accept(ConfiguratorMessages.LINKED, LINKED);
    add.accept(ConfiguratorMessages.NOTHING_STORED, NOTHING_STORED);
    add.accept(ConfiguratorMessages.ALREADY_IN, ALREADY_IN);
    add.accept(ConfiguratorMessages.OWN_BOTH, OWN_BOTH);
    add.accept(ConfiguratorMessages.GONE, GONE);
    add.accept(ConfiguratorMessages.COPIED, COPIED);
    add.accept(ConfiguratorMessages.PASTED, PASTED);
    add.accept(ConfiguratorMessages.PASTED_LEFT_OUT, PASTED_LEFT_OUT);
    add.accept(ConfiguratorMessages.CLEARED, CLEARED);
    add.accept(ConfiguratorMessages.NETWORK, NETWORK);
    add.accept(ConfiguratorMessages.NO_NETWORK_STORED, NO_NETWORK_STORED);
    add.accept(ConfiguratorMessages.COPY, COPY);
    add.accept(ConfiguratorMessages.HINT_STORE, HINT_STORE);
    add.accept(ConfiguratorMessages.HINT_COPY, HINT_COPY);
  }
}
