package io.github.marcelofabianov.redweave.datagen;

import io.github.marcelofabianov.redweave.item.ConfiguratorMessages;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import java.util.function.BiConsumer;

/** The texts of the Configurator in Portuguese, with the same keys as English (rule 1). */
final class ConfiguratorPortugueseTexts {
  private static final String NAME = "Configurador";
  private static final String STORED = "Configurador: %s";
  private static final String NOT_YOURS = "Só o dono pode usar esta rede.";
  private static final String LINKED = "Ligado a %s";
  private static final String NOTHING_STORED = "Clique num Controlador de Rede primeiro.";
  private static final String ALREADY_IN = "Este nó já está em %s.";
  private static final String OWN_BOTH = "Você precisa ser dono das duas redes.";
  private static final String GONE = "A rede guardada não existe mais.";
  private static final String COPIED = "%s slots copiados";
  private static final String PASTED = "%s slots colados";
  private static final String PASTED_LEFT_OUT = "%s slots colados, %s de fora";
  private static final String CLEARED = "Cópia apagada";
  private static final String NETWORK = "Rede: %s";
  private static final String NO_NETWORK_STORED = "Nenhuma rede guardada";
  private static final String COPY = "Cópia: %s slots";
  private static final String HINT_STORE = "Clique num controlador para guardar a rede.";
  private static final String HINT_COPY = "Shift + clique num nó para copiá-lo.";

  private ConfiguratorPortugueseTexts() {}

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
