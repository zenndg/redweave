package io.github.zenndg.redweave.datagen;

import io.github.zenndg.redweave.menu.ControllerScreenTexts;
import java.util.function.BiConsumer;

/** The English texts of the Controller Screen, the base of every language. */
final class ControllerScreenEnglishTexts {
  private static final String TAB_NODES = "Components";
  private static final String TAB_SIGNALS = "Signals";
  private static final String TAB_ACCESS = "Access";
  private static final String TAB_LOG = "Log";
  private static final String RENAME = "Rename";
  private static final String RECOLOR_TOOLTIP = "Click to change the color.";
  private static final String RENAME_TOOLTIP = "Double-click to rename. Enter saves, Esc cancels.";
  private static final String COLUMN_NODE = "Name";
  private static final String COLUMN_SIGNAL = "Signal";
  private static final String COLUMN_TYPE = "Type";
  private static final String COLUMN_STATE = "State";
  private static final String KIND_INPUT = "In";
  private static final String KIND_OUTPUT = "Out";
  private static final String KIND_BOTH = "In/Out";
  private static final String KIND_NONE = "\u2014";
  private static final String POSITION_LINE = "Position: %s";
  private static final String SLOT_ROW = "%s · %s";
  private static final String NO_ACTIVE_CHANNEL = "No active channel";
  private static final String SLOT_USE = "%s in · %s out";
  private static final String CHANNELS_IN_USE = "%s of %s channels in use";
  private static final String UNLOADED = "%s unloaded";
  private static final String OUT_OF_RANGE = "%s out of range";
  private static final String MODULES_ON = "%s modules on";
  private static final String REMOTES_ON = "%s remotes on";
  private static final String OWNER_LINE = "Owner: %s";
  private static final String OWNER = "Owner";
  private static final String READ_ONLY = "Only the owner can change this network.";
  private static final String NO_NODES = "No component in this network yet.";
  private static final String NO_LOG = "No signal change yet.";
  private static final String CLEAR_LOG = "Clear log";
  private static final String CONFIRM_CLEAR_LOG = "Click again to clear";

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
    add.accept(ControllerScreenTexts.TAB_LOG, TAB_LOG);
    add.accept(ControllerScreenTexts.RENAME, RENAME);
    add.accept(ControllerScreenTexts.RECOLOR_TOOLTIP, RECOLOR_TOOLTIP);
    add.accept(ControllerScreenTexts.RENAME_TOOLTIP, RENAME_TOOLTIP);
  }

  private static void addRows(BiConsumer<String, String> add) {
    add.accept(ControllerScreenTexts.COLUMN_NODE, COLUMN_NODE);
    add.accept(ControllerScreenTexts.COLUMN_SIGNAL, COLUMN_SIGNAL);
    add.accept(ControllerScreenTexts.COLUMN_TYPE, COLUMN_TYPE);
    add.accept(ControllerScreenTexts.COLUMN_STATE, COLUMN_STATE);
    add.accept(ControllerScreenTexts.KIND_INPUT, KIND_INPUT);
    add.accept(ControllerScreenTexts.KIND_OUTPUT, KIND_OUTPUT);
    add.accept(ControllerScreenTexts.KIND_BOTH, KIND_BOTH);
    add.accept(ControllerScreenTexts.KIND_NONE, KIND_NONE);
    add.accept(ControllerScreenTexts.POSITION_LINE, POSITION_LINE);
    add.accept(ControllerScreenTexts.SLOT_ROW, SLOT_ROW);
    add.accept(ControllerScreenTexts.NO_ACTIVE_CHANNEL, NO_ACTIVE_CHANNEL);
    add.accept(ControllerScreenTexts.SLOT_USE, SLOT_USE);
    add.accept(ControllerScreenTexts.NO_NODES, NO_NODES);
    add.accept(ControllerScreenTexts.NO_LOG, NO_LOG);
    add.accept(ControllerScreenTexts.CLEAR_LOG, CLEAR_LOG);
    add.accept(ControllerScreenTexts.CONFIRM_CLEAR_LOG, CONFIRM_CLEAR_LOG);
    add.accept(ControllerScreenTexts.OWNER, OWNER);
  }

  private static void addFooter(BiConsumer<String, String> add) {
    add.accept(ControllerScreenTexts.CHANNELS_IN_USE, CHANNELS_IN_USE);
    add.accept(ControllerScreenTexts.UNLOADED, UNLOADED);
    add.accept(ControllerScreenTexts.OUT_OF_RANGE, OUT_OF_RANGE);
    add.accept(ControllerScreenTexts.REMOTES_ON, REMOTES_ON);
    add.accept(ControllerScreenTexts.MODULES_ON, MODULES_ON);
    add.accept(ControllerScreenTexts.OWNER_LINE, OWNER_LINE);
    add.accept(ControllerScreenTexts.READ_ONLY, READ_ONLY);
  }
}
