package io.github.marcelofabianov.redweave.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * The server config, redweave-server.toml: it holds per world and is sent to the clients. The keys
 * are part of the save and the ranges come from answer 66 (AGENTS.md, rule 15, and _docs/limites).
 */
public final class RedweaveConfig {
  private static final String NETWORK = "network";
  private static final String CHANNEL = "channel";
  private static final String NODE = "node";
  private static final String RANGE = "range";
  private static final String NAME_LENGTH = "name_length";
  private static final String SLOTS = "slots";

  private static final String RANGE_RADIUS = "Radius of the reach of a Network Controller,";
  private static final String RANGE_UNIT = " in blocks.";
  private static final String RANGE_REACH = RANGE_RADIUS + RANGE_UNIT;
  private static final String RANGE_ZERO = " 0 reaches the whole dimension.";
  private static final String RANGE_COMMENT = RANGE_REACH + RANGE_ZERO;
  private static final String NETWORK_NAME_COMMENT = "Maximum length of the name of a network.";
  private static final String CHANNEL_NAME_COMMENT = "Maximum length of the name of a channel.";
  private static final String SLOTS_COMMENT = "Channel slots of a Signal Node.";

  private static final int DEFAULT_RANGE = 128;
  private static final int MAX_RANGE = 30_000_000;
  private static final int DEFAULT_NETWORK_NAME = 32;
  private static final int DEFAULT_CHANNEL_NAME = 16;
  private static final int MAX_NAME = 64;
  private static final int DEFAULT_SLOTS = 4;
  private static final int MAX_SLOTS = 9;

  public static final ModConfigSpec SPEC;
  static final ModConfigSpec.IntValue RANGE_VALUE;
  static final ModConfigSpec.IntValue NETWORK_NAME_LENGTH;
  static final ModConfigSpec.IntValue CHANNEL_NAME_LENGTH;
  static final ModConfigSpec.IntValue SLOTS_PER_NODE;

  static {
    ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
    builder.push(NETWORK);
    RANGE_VALUE = builder.comment(RANGE_COMMENT).defineInRange(RANGE, DEFAULT_RANGE, 0, MAX_RANGE);
    NETWORK_NAME_LENGTH =
        builder
            .comment(NETWORK_NAME_COMMENT)
            .defineInRange(NAME_LENGTH, DEFAULT_NETWORK_NAME, 1, MAX_NAME);
    builder.pop();
    builder.push(CHANNEL);
    CHANNEL_NAME_LENGTH =
        builder
            .comment(CHANNEL_NAME_COMMENT)
            .defineInRange(NAME_LENGTH, DEFAULT_CHANNEL_NAME, 1, MAX_NAME);
    builder.pop();
    builder.push(NODE);
    SLOTS_PER_NODE =
        builder.comment(SLOTS_COMMENT).defineInRange(SLOTS, DEFAULT_SLOTS, 1, MAX_SLOTS);
    builder.pop();
    SPEC = builder.build();
  }

  private RedweaveConfig() {}
}
