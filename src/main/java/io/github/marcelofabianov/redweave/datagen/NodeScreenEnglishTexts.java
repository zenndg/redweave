package io.github.marcelofabianov.redweave.datagen;

import io.github.marcelofabianov.redweave.domain.network.Face;
import io.github.marcelofabianov.redweave.menu.NodeScreenTexts;
import java.util.function.BiConsumer;

/** The English texts of the Node Screen, the base of every language. */
final class NodeScreenEnglishTexts {
  private static final String INPUT = "Input";
  private static final String OUTPUT = "Output";
  private static final String DIRECTION = "Direction";
  private static final String BLOCK_FACE = "Block face";
  private static final String FACE_TOOLTIP = "Click a face to choose it. Drag to turn the block.";
  private static final String LOGIC = "Logic";
  private static final String LOGIC_NONE = "None";
  private static final String LOGIC_INVERT = "Invert";
  private static final String LOGIC_PULSE = "Pulse";
  private static final String LOGIC_DELAY = "Delay";
  private static final String LOGIC_THRESHOLD = "Threshold";
  private static final String LOGIC_LATER = "Logic arrives in Phase 2.";
  private static final String LOGIC_TOOLTIP = "Until then, Output repeats the channel as it is.";
  private static final String CHANNELS_ACTIVE = "%s of %s channels active";
  private static final String STATUS = "Status";
  private static final String ENABLED = "Enabled";
  private static final String DISABLED = "Disabled";
  private static final String SIGNAL = "Signal";
  private static final String READ_ONLY = "Only the owner of %s can change this node.";
  private static final String NOT_IN_A_NETWORK = "This node is not in a network.";
  private static final String STATE_ONLINE = "Online";
  private static final String STATE_UNLOADED = "Unloaded";
  private static final String STATE_OUT_OF_RANGE = "Out of range";
  private static final String STATE_NO_NETWORK = "No network";
  private static final String STATE_OFFLINE = "Offline";
  private static final String CHOOSE_NETWORK = "Choose a network";
  private static final String NONE_ONLINE = "No network online.";
  private static final String PLACE_A_CONTROLLER = " Place a Network Controller to create one.";
  private static final String NO_NETWORK_ONLINE = NONE_ONLINE + PLACE_A_CONTROLLER;
  private static final String NODE_COUNT_ONE = "%s node";
  private static final String NODE_COUNT_MANY = "%s nodes";
  private static final String BACK = "Back";
  private static final String RENAME_TOOLTIP = "Click to rename. Enter saves, Esc cancels.";
  private static final String SWITCH_NETWORK_TOOLTIP = "Click to switch networks.";
  private static final String FACE_DOWN = "Down";
  private static final String FACE_UP = "Up";
  private static final String FACE_NORTH = "North";
  private static final String FACE_SOUTH = "South";
  private static final String FACE_WEST = "West";
  private static final String FACE_EAST = "East";

  private NodeScreenEnglishTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    addLabels(add);
    addActions(add);
    addStates(add);
    addFaces(add);
    addNetworkChoice(add);
  }

  private static void addNetworkChoice(BiConsumer<String, String> add) {
    add.accept(NodeScreenTexts.CHOOSE_NETWORK, CHOOSE_NETWORK);
    add.accept(NodeScreenTexts.NO_NETWORK_ONLINE, NO_NETWORK_ONLINE);
    add.accept(NodeScreenTexts.NODE_COUNT_ONE, NODE_COUNT_ONE);
    add.accept(NodeScreenTexts.NODE_COUNT_MANY, NODE_COUNT_MANY);
    add.accept(NodeScreenTexts.BACK, BACK);
    add.accept(NodeScreenTexts.RENAME_TOOLTIP, RENAME_TOOLTIP);
    add.accept(NodeScreenTexts.SWITCH_NETWORK_TOOLTIP, SWITCH_NETWORK_TOOLTIP);
  }

  private static void addLabels(BiConsumer<String, String> add) {
    add.accept(NodeScreenTexts.INPUT, INPUT);
    add.accept(NodeScreenTexts.OUTPUT, OUTPUT);
    add.accept(NodeScreenTexts.DIRECTION, DIRECTION);
    add.accept(NodeScreenTexts.BLOCK_FACE, BLOCK_FACE);
    add.accept(NodeScreenTexts.FACE_TOOLTIP, FACE_TOOLTIP);
    add.accept(NodeScreenTexts.LOGIC, LOGIC);
  }

  private static void addActions(BiConsumer<String, String> add) {
    add.accept(NodeScreenTexts.LOGIC_NONE, LOGIC_NONE);
    add.accept(NodeScreenTexts.LOGIC_INVERT, LOGIC_INVERT);
    add.accept(NodeScreenTexts.LOGIC_PULSE, LOGIC_PULSE);
    add.accept(NodeScreenTexts.LOGIC_DELAY, LOGIC_DELAY);
    add.accept(NodeScreenTexts.LOGIC_THRESHOLD, LOGIC_THRESHOLD);
    add.accept(NodeScreenTexts.LOGIC_LATER, LOGIC_LATER);
    add.accept(NodeScreenTexts.LOGIC_TOOLTIP, LOGIC_TOOLTIP);
  }

  private static void addStates(BiConsumer<String, String> add) {
    add.accept(NodeScreenTexts.SIGNAL, SIGNAL);
    add.accept(NodeScreenTexts.CHANNELS_ACTIVE, CHANNELS_ACTIVE);
    add.accept(NodeScreenTexts.STATUS, STATUS);
    add.accept(NodeScreenTexts.ENABLED, ENABLED);
    add.accept(NodeScreenTexts.DISABLED, DISABLED);
    add.accept(NodeScreenTexts.READ_ONLY, READ_ONLY);
    add.accept(NodeScreenTexts.NOT_IN_A_NETWORK, NOT_IN_A_NETWORK);
    add.accept(NodeScreenTexts.STATE_ONLINE, STATE_ONLINE);
    add.accept(NodeScreenTexts.STATE_UNLOADED, STATE_UNLOADED);
    add.accept(NodeScreenTexts.STATE_OUT_OF_RANGE, STATE_OUT_OF_RANGE);
    add.accept(NodeScreenTexts.STATE_NO_NETWORK, STATE_NO_NETWORK);
    add.accept(NodeScreenTexts.STATE_OFFLINE, STATE_OFFLINE);
  }

  private static void addFaces(BiConsumer<String, String> add) {
    add.accept(NodeScreenTexts.face(Face.DOWN), FACE_DOWN);
    add.accept(NodeScreenTexts.face(Face.UP), FACE_UP);
    add.accept(NodeScreenTexts.face(Face.NORTH), FACE_NORTH);
    add.accept(NodeScreenTexts.face(Face.SOUTH), FACE_SOUTH);
    add.accept(NodeScreenTexts.face(Face.WEST), FACE_WEST);
    add.accept(NodeScreenTexts.face(Face.EAST), FACE_EAST);
  }
}
