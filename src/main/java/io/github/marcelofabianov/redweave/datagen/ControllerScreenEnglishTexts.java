package io.github.marcelofabianov.redweave.datagen;

import io.github.marcelofabianov.redweave.menu.ControllerScreenTexts;
import java.util.function.BiConsumer;

/** The English texts of the Controller Screen, the base of every language. */
final class ControllerScreenEnglishTexts {
  private static final String TAB_NODES = "Nodes";
  private static final String TAB_SIGNALS = "Signals";
  private static final String TAB_ACCESS = "Access";
  private static final String RENAME = "Rename";
  private static final String RECOLOR_TOOLTIP = "Click to change the color.";
  private static final String RENAME_TOOLTIP = "Double-click to rename. Enter saves, Esc cancels.";
  private static final String COLUMN_NODE = "Node";
  private static final String COLUMN_SIGNAL = "Signal";
  private static final String COLUMN_POSITION = "Position";
  private static final String SLOT_ROW = "%s · %s";
  private static final String SLOT_ROW_NAMED = "%s · %s · %s";
  private static final String NO_ACTIVE_CHANNEL = "No active channel";
  private static final String SLOT_USE = "%s in · %s out";
  private static final String CHANNELS_IN_USE = "%s of %s channels in use";
  private static final String UNLOADED = "%s unloaded";
  private static final String OUT_OF_RANGE = "%s out of range";
  private static final String OWNER_LINE = "Owner: %s";
  private static final String OWNER = "Owner";
  private static final String READ_ONLY = "Only the owner can change this network.";
  private static final String NO_NODES = "No node in this network yet.";

  private ControllerScreenEnglishTexts() {}

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
