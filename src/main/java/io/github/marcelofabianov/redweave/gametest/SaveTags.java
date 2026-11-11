package io.github.marcelofabianov.redweave.gametest;

import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;

/**
 * The tags of the save of the networks, written key by key, so the GameTests pin the format: a key
 * renamed in the code fails them before breaking a world (answers 89 and 95, and rule 15).
 */
final class SaveTags {
  static final String VERSION = "version";
  static final String NETWORKS = "networks";
  static final String LAST_USED = "last_used";
  static final String ID = "id";
  static final String OWNER = "owner";
  static final String NAME = "name";
  static final String COLOR = "color";
  static final String CONTROLLER = "controller";
  static final String OFFLINE = "offline";
  static final String CHANNELS = "channels";
  static final String NODES = "nodes";
  static final String DIMENSION = "dimension";
  static final String X = "x";
  static final String Y = "y";
  static final String Z = "z";
  static final String BLOCK = "block";
  static final String FACE = "face";
  static final String SLOT_COUNT = "slot_count";
  static final String SLOTS = "slots";
  static final String INDEX = "index";
  static final String DIRECTION = "direction";
  static final String CHANNEL = "channel";
  static final String READING = "reading";
  static final String PLAYER = "player";
  static final String NETWORK = "network";
  static final String OVERWORLD = "minecraft:overworld";
  static final String INPUT = "input";
  static final String OUTPUT = "output";
  static final String EAST = "east";
  static final String WEST = "west";
  static final String CYAN = "cyan";
  static final String RED = "red";
  static final int HEIGHT = 64;

  private SaveTags() {}

  static CompoundTag save(int version, ListTag networks, ListTag lastUsed) {
    CompoundTag tag = new CompoundTag();
    tag.putInt(VERSION, version);
    tag.put(NETWORKS, networks);
    tag.put(LAST_USED, lastUsed);
    return tag;
  }

  static CompoundTag lastUsed(UUID player, UUID network) {
    CompoundTag tag = new CompoundTag();
    tag.put(PLAYER, NbtUtils.createUUID(player));
    tag.put(NETWORK, NbtUtils.createUUID(network));
    return tag;
  }

  /** An online network, with its controller at the origin, and the given channels and nodes. */
  static CompoundTag network(UUID id, UUID owner, String name, ListTag channels, ListTag nodes) {
    CompoundTag tag = new CompoundTag();
    tag.put(ID, NbtUtils.createUUID(id));
    tag.put(OWNER, NbtUtils.createUUID(owner));
    tag.putString(NAME, name);
    tag.putString(COLOR, CYAN);
    tag.put(CONTROLLER, block(0));
    tag.put(CHANNELS, channels);
    tag.put(NODES, nodes);
    return tag;
  }

  static CompoundTag channel(String color, String name) {
    CompoundTag tag = new CompoundTag();
    tag.putString(COLOR, color);
    tag.putString(NAME, name);
    return tag;
  }

  /** A node on the block at x, on the east face of it. */
  static CompoundTag node(UUID id, int x, int slotCount, ListTag slots) {
    CompoundTag tag = new CompoundTag();
    tag.put(ID, NbtUtils.createUUID(id));
    tag.put(BLOCK, block(x));
    tag.putString(FACE, EAST);
    tag.putInt(SLOT_COUNT, slotCount);
    tag.put(SLOTS, slots);
    return tag;
  }

  /** A network its owner turned off: the key goes to the saves only then (answer 210). */
  static CompoundTag turnedOff(CompoundTag network) {
    network.putBoolean(OFFLINE, true);
    return network;
  }

  /** A node its owner renamed: the name goes to the saves only then (answer 206). */
  static CompoundTag named(CompoundTag node, String name) {
    node.putString(NAME, name);
    return node;
  }

  static CompoundTag slot(int index, String direction, String face, String channel) {
    CompoundTag tag = new CompoundTag();
    tag.putInt(INDEX, index);
    tag.putString(DIRECTION, direction);
    tag.putString(FACE, face);
    tag.putString(CHANNEL, channel);
    return tag;
  }

  static CompoundTag reading(CompoundTag slot, int reading) {
    slot.putInt(READING, reading);
    return slot;
  }

  static ListTag list(CompoundTag... items) {
    ListTag list = new ListTag();
    for (CompoundTag item : items) {
      list.add(item);
    }
    return list;
  }

  private static CompoundTag block(int x) {
    CompoundTag tag = new CompoundTag();
    tag.putString(DIMENSION, OVERWORLD);
    tag.putInt(X, x);
    tag.putInt(Y, HEIGHT);
    tag.putInt(Z, 0);
    return tag;
  }
}
