package io.github.marcelofabianov.redweave.datagen;

import io.github.marcelofabianov.redweave.item.ConfiguratorMessages;
import io.github.marcelofabianov.redweave.item.ConfiguratorMode;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import java.util.function.BiConsumer;

/** The texts of the Configurator in Portuguese, with the same keys as English (rule 1). */
final class ConfiguratorPortugueseTexts {
  private static final String NAME = "Configurador";
  private static final String MODE = "Modo: %s";
  private static final String OPEN = "Abrir";
  private static final String COPY_MODE = "Copiar";
  private static final String TOGGLE = "Alternar";
  private static final String TOGGLE_CLICK = "Clique num nó para ativar os canais,";
  private static final String TOGGLE_SHIFT = " Shift + clique para desativar.";
  private static final String HINT_TOGGLE = TOGGLE_CLICK + TOGGLE_SHIFT;
  private static final String ENABLED = "%s canais ativados";
  private static final String DISABLED = "%s canais desativados";
  private static final String WRENCH = "Chave";
  private static final String COPIED = "%s slots copiados";
  private static final String PASTED = "%s slots colados";
  private static final String CLEARED = "Cópia apagada";
  private static final String PICKED_UP = "Nó retirado";
  private static final String WHO_PLACED = "Só quem colocou este nó";
  private static final String MAY_PASTE = " pode colar uma rede nele.";
  private static final String PASTE_NOT_PLACER = WHO_PLACED + MAY_PASTE;
  private static final String NOT_PLACER = "Só quem colocou este nó pode retirá-lo.";
  private static final String USE_TO_OPEN = "Use um Configurador para abrir este nó.";
  private static final String COPY = "Cópia: %s slots";
  private static final String OPEN_CLICK = "Clique para abrir um nó ou módulo,";
  private static final String OPEN_SHIFT = " Shift + clique para girar um módulo.";
  private static final String HINT_OPEN = OPEN_CLICK + OPEN_SHIFT;
  private static final String HINT_TOGGLE_MODULE = "Clique num módulo para ligá-lo ou pará-lo.";
  private static final String HINT_COPY = "Clique num nó para copiá-lo, Shift + clique para colar.";
  private static final String HINT_WRENCH = "Clique num nó, módulo ou cabo para retirá-lo.";
  private static final String HINT_SCROLL = "Shift + scroll para trocar o modo.";

  private ConfiguratorPortugueseTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(RedweaveItems.CONFIGURATOR.get().getDescriptionId(), NAME);
    add.accept(ConfiguratorMessages.MODE, MODE);
    add.accept(ConfiguratorMessages.nameOf(ConfiguratorMode.OPEN), OPEN);
    add.accept(ConfiguratorMessages.nameOf(ConfiguratorMode.COPY), COPY_MODE);
    add.accept(ConfiguratorMessages.nameOf(ConfiguratorMode.TOGGLE), TOGGLE);
    add.accept(ConfiguratorMessages.hintOf(ConfiguratorMode.TOGGLE), HINT_TOGGLE);
    add.accept(ConfiguratorMessages.HINT_TOGGLE_MODULE, HINT_TOGGLE_MODULE);
    add.accept(ConfiguratorMessages.ENABLED, ENABLED);
    add.accept(ConfiguratorMessages.DISABLED, DISABLED);
    add.accept(ConfiguratorMessages.nameOf(ConfiguratorMode.WRENCH), WRENCH);
    add.accept(ConfiguratorMessages.COPIED, COPIED);
    add.accept(ConfiguratorMessages.PASTED, PASTED);
    add.accept(ConfiguratorMessages.CLEARED, CLEARED);
    add.accept(ConfiguratorMessages.PICKED_UP, PICKED_UP);
    add.accept(ConfiguratorMessages.NOT_PLACER, NOT_PLACER);
    add.accept(ConfiguratorMessages.PASTE_NOT_PLACER, PASTE_NOT_PLACER);
    add.accept(ConfiguratorMessages.USE_TO_OPEN, USE_TO_OPEN);
    add.accept(ConfiguratorMessages.COPY, COPY);
    add.accept(ConfiguratorMessages.hintOf(ConfiguratorMode.OPEN), HINT_OPEN);
    add.accept(ConfiguratorMessages.hintOf(ConfiguratorMode.COPY), HINT_COPY);
    add.accept(ConfiguratorMessages.hintOf(ConfiguratorMode.WRENCH), HINT_WRENCH);
    add.accept(ConfiguratorMessages.HINT_SCROLL, HINT_SCROLL);
  }
}
