package io.github.zenndg.redweave.config;

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
  private static final String MAX_PER_PLAYER_KEY = "max_per_player";
  private static final String LOG = "log";
  private static final String SIZE = "size";
  private static final String CONFIGURATOR = "configurator";
  private static final String LABEL_RANGE_KEY = "label_range";
  private static final String REMOTE = "remote";
  private static final String PULSE_TICKS_KEY = "pulse_ticks";
  private static final String EMITTER_RANGE_KEY = "emitter_range";
  private static final String TIMER = "timer";
  private static final String MIN_INTERVAL_KEY = "min_interval_ticks";
  private static final String MAX_INTERVAL_KEY = "max_interval_ticks";
  private static final String MODULE = "module";
  private static final String RANDOMIZER = "randomizer";
  private static final String BUTTON = "button";
  private static final String PRESSURE_PLATE = "pressure_plate";
  private static final String CHECK_TICKS_KEY = "check_ticks";
  private static final String ITEM_SENSOR = "item_sensor";
  private static final String LOCK = "lock";
  private static final String LAMP = "lamp";
  private static final String ALARM = "alarm";
  private static final String ALARM_RANGE_KEY = "range";
  private static final String MAX_REPEATS_KEY = "max_repeats";
  private static final String MAX_DURATION_KEY = "max_duration_ticks";
  private static final String LIGHT_UPDATE_KEY = "min_light_update_ticks";
  private static final String LIGHT_UPDATE_COMMENT = "Fewest ticks between Signal Lamp lights.";
  private static final String MIN_PULSE_KEY = "min_pulse_ticks";
  private static final String MAX_PULSE_KEY = "max_pulse_ticks";
  private static final String MAX_COUNT_KEY = "max_count";
  private static final String INVERTER = "inverter";
  private static final String BURNOUT_TOGGLES_KEY = "burnout_toggles";
  private static final String BURNOUT_WINDOW_KEY = "burnout_window_ticks";
  private static final String BURNOUT_TICKS_KEY = "burnout_ticks";

  private static final String RANGE_RADIUS = "Radius of the reach of a Network Controller,";
  private static final String RANGE_UNIT = " in blocks.";
  private static final String RANGE_REACH = RANGE_RADIUS + RANGE_UNIT;
  private static final String RANGE_ZERO = " 0 reaches the whole dimension.";
  private static final String RANGE_COMMENT = RANGE_REACH + RANGE_ZERO;
  private static final String NETWORK_NAME_COMMENT = "Maximum length of the name of a network.";
  private static final String MAX_PER_PLAYER_COMMENT = "Networks a player may have, of any state.";
  private static final String CHANNEL_NAME_COMMENT = "Maximum length of the name of a channel.";
  private static final String NODE_NAME_COMMENT = "Maximum length of the name of a Signal Node.";
  private static final String LOG_SIZE_COMMENT = "Lines the log of each network keeps in memory.";
  private static final String LABEL_RANGE_COMMENT = "Blocks within which the node labels show.";
  private static final String PULSE_TICKS_COMMENT = "Ticks a Signal Remote button pulse lasts.";
  private static final String EMITTER_REACH = "Blocks a Signal Remote reaches a Signal Emitter.";
  private static final String EMITTER_RANGE_COMMENT = EMITTER_REACH + RANGE_ZERO;
  private static final String MIN_INTERVAL_COMMENT = "Shortest Signal Timer interval, in ticks.";
  private static final String MAX_INTERVAL_COMMENT = "Longest Signal Timer interval, in ticks.";
  private static final String MIN_DRAW_COMMENT = "Shortest Signal Randomizer interval, in ticks.";
  private static final String MAX_DRAW_COMMENT = "Longest Signal Randomizer interval, in ticks.";
  private static final String MIN_PRESS_COMMENT = "Shortest Signal Button on time, in ticks.";
  private static final String MAX_PRESS_COMMENT = "Longest Signal Button on time, in ticks.";
  private static final String CHECK_COMMENT = "Ticks between checks of a Signal Pressure Plate.";
  private static final String MAX_COUNT_COMMENT = "Most items a Signal Item Sensor may expect.";
  private static final String SENSOR_CHECK_COMMENT = "Ticks between reads of a Signal Item Sensor.";
  private static final String MIN_LOCK_COMMENT = "Shortest Signal Lock Toggle pulse, in ticks.";
  private static final String MAX_LOCK_COMMENT = "Longest Signal Lock Toggle pulse, in ticks.";
  private static final String ALARM_RANGE_COMMENT = "Blocks within which a Signal Alarm is heard.";
  private static final String MAX_REPEATS_COMMENT = "Most rings a Signal Alarm may have.";
  private static final String MIN_RING_COMMENT = "Shortest gap of Signal Alarm rings, in ticks.";
  private static final String MAX_RING_COMMENT = "Longest gap of Signal Alarm rings, in ticks.";
  private static final String MAX_DURATION_COMMENT = "Longest a Signal Alarm rings, in ticks.";
  private static final String MODULE_NAME_COMMENT = "Maximum length of a Signal Module name.";
  private static final String MAX_DELAY_COMMENT = "Longest module output delay, ticks; 0 is off.";
  private static final String MAX_DELAY_KEY = "max_output_delay_ticks";
  private static final int DEFAULT_MAX_DELAY = 300;
  private static final int HIGHEST_MAX_DELAY = 1200;
  private static final String MAX_CHANGES_COMMENT = "Most changes a module output delay holds.";
  private static final String MAX_CHANGES_KEY = "max_delay_changes";
  private static final int DEFAULT_MAX_CHANGES = 32;
  private static final int LOWEST_MAX_CHANGES = 2;
  private static final int HIGHEST_MAX_CHANGES = 256;
  private static final String TOGGLES_COMMENT = "Output changes that burn out a Signal Inverter.";
  private static final String WINDOW_COMMENT = "Ticks within which those changes are counted.";
  private static final String BURNOUT_COMMENT = "Ticks a burnt out Signal Inverter stays off.";

  private static final int DEFAULT_RANGE = 128;
  private static final int MAX_RANGE = 30_000_000;
  private static final int DEFAULT_NETWORK_NAME = 32;
  private static final int DEFAULT_PER_PLAYER = 10;
  private static final int MAX_NETWORKS = 100;
  private static final int DEFAULT_CHANNEL_NAME = 16;
  private static final int DEFAULT_NODE_NAME = 16;

  /** The longest name the config allows, also the ceiling of a name in the saves (answer 93). */
  public static final int MAX_NAME = 64;

  private static final int DEFAULT_LOG_SIZE = 100;
  private static final int MIN_LOG_SIZE = 10;
  private static final int MAX_LOG_SIZE = 1000;
  private static final int DEFAULT_LABEL_RANGE = 16;
  private static final int MIN_LABEL_RANGE = 4;
  private static final int MAX_LABEL_RANGE = 64;
  private static final int DEFAULT_PULSE_TICKS = 20;
  private static final int MIN_PULSE_TICKS = 2;
  private static final int MAX_PULSE_TICKS = 200;
  private static final int DEFAULT_MIN_INTERVAL = 4;
  private static final int LOWEST_MIN_INTERVAL = 4;
  private static final int HIGHEST_MIN_INTERVAL = 200;
  private static final int DEFAULT_MAX_INTERVAL = 6000;
  private static final int LOWEST_MAX_INTERVAL = 200;
  private static final int HIGHEST_MAX_INTERVAL = 72_000;
  private static final int DEFAULT_MIN_PRESS = 2;
  private static final int LOWEST_MIN_PRESS = 2;
  private static final int DEFAULT_CHECK = 20;
  private static final int MIN_CHECK = 2;
  private static final int MAX_CHECK = 200;
  private static final int DEFAULT_MAX_COUNT = 3456;
  private static final int HIGHEST_MAX_COUNT = 100_000;
  private static final int DEFAULT_SENSOR_CHECK = 10;
  private static final int MAX_SENSOR_CHECK = 100;
  private static final int DEFAULT_LIGHT_UPDATE = 4;
  private static final int MAX_LIGHT_UPDATE = 20;
  private static final int DEFAULT_MODULE_NAME = 16;
  private static final int DEFAULT_ALARM_RANGE = 16;
  private static final int MIN_ALARM_RANGE = 4;
  private static final int MAX_ALARM_RANGE = 32;
  private static final int DEFAULT_MAX_REPEATS = 10;
  private static final int HIGHEST_MAX_REPEATS = 10;
  private static final int DEFAULT_MIN_RING = 20;
  private static final int HIGHEST_MIN_RING = 200;
  private static final int DEFAULT_MAX_RING = 200;
  private static final int HIGHEST_MAX_RING = 1200;
  private static final int DEFAULT_MAX_DURATION = 600;
  private static final int HIGHEST_MAX_DURATION = 6000;
  private static final int DEFAULT_TOGGLES = 8;
  private static final int MIN_TOGGLES = 2;
  private static final int MAX_TOGGLES = 64;
  private static final int DEFAULT_WINDOW = 60;
  private static final int MIN_WINDOW = 20;
  private static final int MAX_WINDOW = 1200;
  private static final int DEFAULT_BURNOUT = 160;
  private static final int MIN_BURNOUT = 20;
  private static final int MAX_BURNOUT = 6000;

  public static final ModConfigSpec SPEC;
  static final ModConfigSpec.IntValue RANGE_VALUE;
  static final ModConfigSpec.IntValue NETWORK_NAME_LENGTH;
  static final ModConfigSpec.IntValue MAX_PER_PLAYER;
  static final ModConfigSpec.IntValue CHANNEL_NAME_LENGTH;
  static final ModConfigSpec.IntValue NODE_NAME_LENGTH;
  static final ModConfigSpec.IntValue LOG_SIZE;
  static final ModConfigSpec.IntValue LABEL_RANGE;
  static final ModConfigSpec.IntValue PULSE_TICKS;
  static final ModConfigSpec.IntValue EMITTER_RANGE;
  static final ModConfigSpec.IntValue MIN_INTERVAL_TICKS;
  static final ModConfigSpec.IntValue MAX_INTERVAL_TICKS;
  static final ModConfigSpec.IntValue RANDOMIZER_MIN_INTERVAL_TICKS;
  static final ModConfigSpec.IntValue RANDOMIZER_MAX_INTERVAL_TICKS;
  static final ModConfigSpec.IntValue BUTTON_MIN_INTERVAL_TICKS;
  static final ModConfigSpec.IntValue BUTTON_MAX_INTERVAL_TICKS;
  static final ModConfigSpec.IntValue PRESSURE_PLATE_CHECK_TICKS;
  static final ModConfigSpec.IntValue ITEM_SENSOR_MAX_COUNT;
  static final ModConfigSpec.IntValue ITEM_SENSOR_CHECK_TICKS;
  static final ModConfigSpec.IntValue LOCK_MIN_PULSE_TICKS;
  static final ModConfigSpec.IntValue LAMP_MIN_LIGHT_UPDATE_TICKS;
  static final ModConfigSpec.IntValue LOCK_MAX_PULSE_TICKS;
  static final ModConfigSpec.IntValue ALARM_RANGE;
  static final ModConfigSpec.IntValue ALARM_MAX_REPEATS;
  static final ModConfigSpec.IntValue ALARM_MIN_INTERVAL_TICKS;
  static final ModConfigSpec.IntValue ALARM_MAX_INTERVAL_TICKS;
  static final ModConfigSpec.IntValue ALARM_MAX_DURATION_TICKS;
  static final ModConfigSpec.IntValue MODULE_NAME_LENGTH;
  static final ModConfigSpec.IntValue MAX_OUTPUT_DELAY_TICKS;
  static final ModConfigSpec.IntValue MAX_DELAY_CHANGES;
  static final ModConfigSpec.IntValue BURNOUT_TOGGLES;
  static final ModConfigSpec.IntValue BURNOUT_WINDOW_TICKS;
  static final ModConfigSpec.IntValue BURNOUT_TICKS;

  static {
    ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
    builder.push(NETWORK);
    RANGE_VALUE = builder.comment(RANGE_COMMENT).defineInRange(RANGE, DEFAULT_RANGE, 0, MAX_RANGE);
    NETWORK_NAME_LENGTH =
        builder
            .comment(NETWORK_NAME_COMMENT)
            .defineInRange(NAME_LENGTH, DEFAULT_NETWORK_NAME, 1, MAX_NAME);
    MAX_PER_PLAYER =
        builder
            .comment(MAX_PER_PLAYER_COMMENT)
            .defineInRange(MAX_PER_PLAYER_KEY, DEFAULT_PER_PLAYER, 1, MAX_NETWORKS);
    builder.pop();
    builder.push(CHANNEL);
    CHANNEL_NAME_LENGTH =
        builder
            .comment(CHANNEL_NAME_COMMENT)
            .defineInRange(NAME_LENGTH, DEFAULT_CHANNEL_NAME, 1, MAX_NAME);
    builder.pop();
    builder.push(NODE);
    NODE_NAME_LENGTH =
        builder
            .comment(NODE_NAME_COMMENT)
            .defineInRange(NAME_LENGTH, DEFAULT_NODE_NAME, 1, MAX_NAME);
    builder.pop();
    builder.push(LOG);
    LOG_SIZE =
        builder
            .comment(LOG_SIZE_COMMENT)
            .defineInRange(SIZE, DEFAULT_LOG_SIZE, MIN_LOG_SIZE, MAX_LOG_SIZE);
    builder.pop();
    builder.push(CONFIGURATOR);
    LABEL_RANGE =
        builder
            .comment(LABEL_RANGE_COMMENT)
            .defineInRange(LABEL_RANGE_KEY, DEFAULT_LABEL_RANGE, MIN_LABEL_RANGE, MAX_LABEL_RANGE);
    builder.pop();
    builder.push(REMOTE);
    PULSE_TICKS =
        builder
            .comment(PULSE_TICKS_COMMENT)
            .defineInRange(PULSE_TICKS_KEY, DEFAULT_PULSE_TICKS, MIN_PULSE_TICKS, MAX_PULSE_TICKS);
    EMITTER_RANGE =
        builder
            .comment(EMITTER_RANGE_COMMENT)
            .defineInRange(EMITTER_RANGE_KEY, DEFAULT_RANGE, 0, MAX_RANGE);
    builder.pop();
    builder.push(TIMER);
    MIN_INTERVAL_TICKS =
        builder
            .comment(MIN_INTERVAL_COMMENT)
            .defineInRange(
                MIN_INTERVAL_KEY, DEFAULT_MIN_INTERVAL, LOWEST_MIN_INTERVAL, HIGHEST_MIN_INTERVAL);
    MAX_INTERVAL_TICKS =
        builder
            .comment(MAX_INTERVAL_COMMENT)
            .defineInRange(
                MAX_INTERVAL_KEY, DEFAULT_MAX_INTERVAL, LOWEST_MAX_INTERVAL, HIGHEST_MAX_INTERVAL);
    builder.pop();
    builder.push(MODULE);
    MODULE_NAME_LENGTH =
        builder
            .comment(MODULE_NAME_COMMENT)
            .defineInRange(NAME_LENGTH, DEFAULT_MODULE_NAME, 1, MAX_NAME);
    MAX_OUTPUT_DELAY_TICKS =
        builder
            .comment(MAX_DELAY_COMMENT)
            .defineInRange(MAX_DELAY_KEY, DEFAULT_MAX_DELAY, 0, HIGHEST_MAX_DELAY);
    MAX_DELAY_CHANGES =
        builder
            .comment(MAX_CHANGES_COMMENT)
            .defineInRange(
                MAX_CHANGES_KEY, DEFAULT_MAX_CHANGES, LOWEST_MAX_CHANGES, HIGHEST_MAX_CHANGES);
    builder.pop();
    builder.push(RANDOMIZER);
    RANDOMIZER_MIN_INTERVAL_TICKS =
        builder
            .comment(MIN_DRAW_COMMENT)
            .defineInRange(
                MIN_INTERVAL_KEY, DEFAULT_MIN_INTERVAL, LOWEST_MIN_INTERVAL, HIGHEST_MIN_INTERVAL);
    RANDOMIZER_MAX_INTERVAL_TICKS =
        builder
            .comment(MAX_DRAW_COMMENT)
            .defineInRange(
                MAX_INTERVAL_KEY, DEFAULT_MAX_INTERVAL, LOWEST_MAX_INTERVAL, HIGHEST_MAX_INTERVAL);
    builder.pop();
    builder.push(BUTTON);
    BUTTON_MIN_INTERVAL_TICKS =
        builder
            .comment(MIN_PRESS_COMMENT)
            .defineInRange(
                MIN_INTERVAL_KEY, DEFAULT_MIN_PRESS, LOWEST_MIN_PRESS, HIGHEST_MIN_INTERVAL);
    BUTTON_MAX_INTERVAL_TICKS =
        builder
            .comment(MAX_PRESS_COMMENT)
            .defineInRange(
                MAX_INTERVAL_KEY, DEFAULT_MAX_INTERVAL, LOWEST_MAX_INTERVAL, HIGHEST_MAX_INTERVAL);
    builder.pop();
    builder.push(PRESSURE_PLATE);
    PRESSURE_PLATE_CHECK_TICKS =
        builder
            .comment(CHECK_COMMENT)
            .defineInRange(CHECK_TICKS_KEY, DEFAULT_CHECK, MIN_CHECK, MAX_CHECK);
    builder.pop();
    builder.push(ITEM_SENSOR);
    ITEM_SENSOR_MAX_COUNT =
        builder
            .comment(MAX_COUNT_COMMENT)
            .defineInRange(MAX_COUNT_KEY, DEFAULT_MAX_COUNT, 1, HIGHEST_MAX_COUNT);
    ITEM_SENSOR_CHECK_TICKS =
        builder
            .comment(SENSOR_CHECK_COMMENT)
            .defineInRange(CHECK_TICKS_KEY, DEFAULT_SENSOR_CHECK, MIN_CHECK, MAX_SENSOR_CHECK);
    builder.pop();
    builder.push(LOCK);
    LOCK_MIN_PULSE_TICKS =
        builder
            .comment(MIN_LOCK_COMMENT)
            .defineInRange(MIN_PULSE_KEY, MIN_PULSE_TICKS, MIN_PULSE_TICKS, MAX_PULSE_TICKS);
    LOCK_MAX_PULSE_TICKS =
        builder
            .comment(MAX_LOCK_COMMENT)
            .defineInRange(MAX_PULSE_KEY, MAX_PULSE_TICKS, MIN_PULSE_TICKS, MAX_PULSE_TICKS);
    builder.pop();
    builder.push(ALARM);
    ALARM_RANGE =
        builder
            .comment(ALARM_RANGE_COMMENT)
            .defineInRange(ALARM_RANGE_KEY, DEFAULT_ALARM_RANGE, MIN_ALARM_RANGE, MAX_ALARM_RANGE);
    ALARM_MAX_REPEATS =
        builder
            .comment(MAX_REPEATS_COMMENT)
            .defineInRange(MAX_REPEATS_KEY, DEFAULT_MAX_REPEATS, 1, HIGHEST_MAX_REPEATS);
    ALARM_MIN_INTERVAL_TICKS =
        builder
            .comment(MIN_RING_COMMENT)
            .defineInRange(MIN_INTERVAL_KEY, DEFAULT_MIN_RING, MIN_PULSE_TICKS, HIGHEST_MIN_RING);
    ALARM_MAX_INTERVAL_TICKS =
        builder
            .comment(MAX_RING_COMMENT)
            .defineInRange(MAX_INTERVAL_KEY, DEFAULT_MAX_RING, MIN_PULSE_TICKS, HIGHEST_MAX_RING);
    ALARM_MAX_DURATION_TICKS =
        builder
            .comment(MAX_DURATION_COMMENT)
            .defineInRange(
                MAX_DURATION_KEY, DEFAULT_MAX_DURATION, MIN_PULSE_TICKS, HIGHEST_MAX_DURATION);
    builder.pop();
    builder.push(LAMP);
    LAMP_MIN_LIGHT_UPDATE_TICKS =
        builder
            .comment(LIGHT_UPDATE_COMMENT)
            .defineInRange(LIGHT_UPDATE_KEY, DEFAULT_LIGHT_UPDATE, 1, MAX_LIGHT_UPDATE);
    builder.pop();
    builder.push(INVERTER);
    BURNOUT_TOGGLES =
        builder
            .comment(TOGGLES_COMMENT)
            .defineInRange(BURNOUT_TOGGLES_KEY, DEFAULT_TOGGLES, MIN_TOGGLES, MAX_TOGGLES);
    BURNOUT_WINDOW_TICKS =
        builder
            .comment(WINDOW_COMMENT)
            .defineInRange(BURNOUT_WINDOW_KEY, DEFAULT_WINDOW, MIN_WINDOW, MAX_WINDOW);
    BURNOUT_TICKS =
        builder
            .comment(BURNOUT_COMMENT)
            .defineInRange(BURNOUT_TICKS_KEY, DEFAULT_BURNOUT, MIN_BURNOUT, MAX_BURNOUT);
    builder.pop();
    KeypadConfig.define(builder);
    SPEC = builder.build();
  }

  private RedweaveConfig() {}
}
