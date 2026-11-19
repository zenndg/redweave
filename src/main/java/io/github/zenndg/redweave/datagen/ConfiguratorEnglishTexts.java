package io.github.zenndg.redweave.datagen;

import io.github.zenndg.redweave.item.ConfiguratorMessages;
import io.github.zenndg.redweave.item.ConfiguratorMode;
import io.github.zenndg.redweave.registry.RedweaveItems;
import java.util.function.BiConsumer;

/** The texts of the Configurator in English, the base language (answers 108 and 190). */
final class ConfiguratorEnglishTexts {
  private static final String NAME = "Configurator";
  private static final String MODE = "Mode: %s";
  private static final String OPEN = "Open";
  private static final String COPY_MODE = "Copy";
  private static final String TOGGLE = "Toggle";
  private static final String TOGGLE_CLICK = "Click a node to enable its channels,";
  private static final String TOGGLE_SHIFT = " Shift + click to disable them.";
  private static final String HINT_TOGGLE = TOGGLE_CLICK + TOGGLE_SHIFT;
  private static final String ENABLED = "Enabled %s channels";
  private static final String DISABLED = "Disabled %s channels";
  private static final String WRENCH = "Wrench";
  private static final String COPIED = "Copied %s slots";
  private static final String PASTED = "Pasted %s slots";
  private static final String CLEARED = "Copy cleared";
  private static final String PICKED_UP = "Node picked up";
  private static final String WHO_PLACED = "Only the player who placed this node";
  private static final String MAY_PASTE = " can paste a network on it.";
  private static final String PASTE_NOT_PLACER = WHO_PLACED + MAY_PASTE;
  private static final String NOT_PLACER = "Only the player who placed this node can pick it up.";
  private static final String USE_TO_OPEN = "Use a Configurator to open this node.";
  private static final String COPY = "Copy: %s slots";
  private static final String HINT_OPEN = "Click to open a node or module.";
  private static final String ROTATE = "Rotate";
  private static final String ROTATE_CLICK = "Click to turn a module,";
  private static final String ROTATE_SHIFT = " Shift + click to turn it back.";
  private static final String HINT_ROTATE = ROTATE_CLICK + ROTATE_SHIFT;
  private static final String ONLY_MODULES = "Only modules can be turned.";
  private static final String HINT_TOGGLE_MODULE = "Click a module to start or stop it.";
  private static final String COPY_CLICK = "Click a node or module to copy it,";
  private static final String COPY_SHIFT = " Shift + click to paste.";
  private static final String HINT_COPY = COPY_CLICK + COPY_SHIFT;
  private static final String HINT_WRENCH = "Click a node, module or cable to pick it up.";
  private static final String HINT_SCROLL = "Shift + scroll to change the mode.";

  private static final String COPY_ITEM = "Copy: %s";
  private static final String COPIED_ITEM = "Copied %s";
  private static final String PASTED_ITEM = "Pasted";
  private static final String WRONG_TYPE = "This copy is from a %s.";
  private static final String CLEARED_START = "Channels were cleared:";
  private static final String CLEARED_WHY = " the network is not available.";
  private static final String CHANNELS_CLEARED = CLEARED_START + CLEARED_WHY;
  private static final String NOT_COPIABLE = "This item cannot be copied.";

  private ConfiguratorEnglishTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(RedweaveItems.CONFIGURATOR.get().getDescriptionId(), NAME);
    add.accept(ConfiguratorMessages.MODE, MODE);
    add.accept(ConfiguratorMessages.nameOf(ConfiguratorMode.OPEN), OPEN);
    add.accept(ConfiguratorMessages.nameOf(ConfiguratorMode.ROTATE), ROTATE);
    add.accept(ConfiguratorMessages.hintOf(ConfiguratorMode.ROTATE), HINT_ROTATE);
    add.accept(ConfiguratorMessages.ONLY_MODULES, ONLY_MODULES);
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
    addCopy(add);
  }

  /** The texts of the copy of every item (answers 728 and 731). */
  private static void addCopy(BiConsumer<String, String> add) {
    add.accept(ConfiguratorMessages.COPY_ITEM, COPY_ITEM);
    add.accept(ConfiguratorMessages.COPIED_ITEM, COPIED_ITEM);
    add.accept(ConfiguratorMessages.PASTED_ITEM, PASTED_ITEM);
    add.accept(ConfiguratorMessages.WRONG_TYPE, WRONG_TYPE);
    add.accept(ConfiguratorMessages.CHANNELS_CLEARED, CHANNELS_CLEARED);
    add.accept(ConfiguratorMessages.NOT_COPIABLE, NOT_COPIABLE);
  }
}
