package io.github.zenndg.redweave.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * The keys of the server config of the Signal Keypad and of the Signal Card, with the defaults and
 * the ranges of answer 915: _docs/limites.md. The config of the mod defines them in its spec.
 */
final class KeypadConfig {
  private static final String KEYPAD = "keypad";
  private static final String CARD = "card";

  private static final String MAX_LINKS_PER_ITEM_KEY = "max_links_per_item";
  private static final String MIN_OPEN_TICKS_KEY = "min_open_ticks";
  private static final String MAX_OPEN_TICKS_KEY = "max_open_ticks";
  private static final String MAX_OFFLINE_REPORTS_KEY = "max_offline_reports";
  private static final String FREE_WINDOW_TICKS_KEY = "free_window_ticks";
  private static final String FREE_TRIES_KEY = "free_tries";
  private static final String FIRST_WAIT_TICKS_KEY = "first_wait_ticks";
  private static final String MAX_WAIT_TICKS_KEY = "max_wait_ticks";
  private static final String RESET_TICKS_KEY = "reset_ticks";
  private static final String MAX_LINKS_PER_ITEM_COMMENT = "Signal Cards each item accepts.";
  private static final String MIN_OPEN_NOTE = "Shortest Signal Keypad open time, in ticks.";
  private static final String MAX_OPEN_TICKS_COMMENT = "Longest Signal Keypad open time, in ticks.";
  private static final String REPORTS_NOTE = "Signal Keypads in an offline owner summary.";
  private static final String WINDOW_NOTE = "Ticks with no miss that free the tries again.";
  private static final String FREE_TRIES_COMMENT = "Wrong codes a player tries before waiting.";
  private static final String FIRST_WAIT_NOTE = "First wait after the free tries, in ticks.";
  private static final String MAX_WAIT_TICKS_COMMENT = "Longest wait after the misses, in ticks.";
  private static final String RESET_TICKS_COMMENT = "Ticks with no try that clear the misses.";
  private static final int MIN_OPEN = 20;
  private static final int MAX_OPEN = 600;
  private static final int HIGHEST_OPEN = 6000;
  private static final int FREE_WINDOW = 1200;
  private static final int MIN_FREE_WINDOW = 200;
  private static final int MAX_FREE_WINDOW = 12000;
  private static final int FREE_TRIES = 3;
  private static final int MAX_FREE_TRIES = 10;
  private static final int FIRST_WAIT = 100;
  private static final int MIN_FIRST_WAIT = 20;
  private static final int MAX_FIRST_WAIT = 1200;
  private static final int MAX_WAIT = 6000;
  private static final int MIN_MAX_WAIT = 100;
  private static final int HIGHEST_WAIT = 72000;
  private static final int RESET = 12000;
  private static final int MIN_RESET = 1200;
  private static final int MAX_RESET = 72000;
  private static final int REPORTS = 16;
  private static final int MAX_REPORTS = 64;
  private static final int LINKS = 8;
  private static final int MAX_LINKS = 64;

  static ModConfigSpec.IntValue minOpenTicks;
  static ModConfigSpec.IntValue maxOpenTicks;
  static ModConfigSpec.IntValue freeWindowTicks;
  static ModConfigSpec.IntValue freeTries;
  static ModConfigSpec.IntValue firstWaitTicks;
  static ModConfigSpec.IntValue maxWaitTicks;
  static ModConfigSpec.IntValue resetTicks;
  static ModConfigSpec.IntValue maxOfflineReports;
  static ModConfigSpec.IntValue maxLinksPerItem;

  private KeypadConfig() {}

  /** Defines the keys of the keypad and of the card, in their sections. */
  static void define(ModConfigSpec.Builder builder) {
    builder.push(KEYPAD);
    defineOpen(builder);
    defineTries(builder);
    builder.pop();
    builder.push(CARD);
    maxLinksPerItem =
        builder
            .comment(MAX_LINKS_PER_ITEM_COMMENT)
            .defineInRange(MAX_LINKS_PER_ITEM_KEY, LINKS, 1, MAX_LINKS);
    builder.pop();
  }

  private static void defineOpen(ModConfigSpec.Builder builder) {
    minOpenTicks =
        builder
            .comment(MIN_OPEN_NOTE)
            .defineInRange(MIN_OPEN_TICKS_KEY, MIN_OPEN, MIN_OPEN, MAX_OPEN);
    maxOpenTicks =
        builder
            .comment(MAX_OPEN_TICKS_COMMENT)
            .defineInRange(MAX_OPEN_TICKS_KEY, MAX_OPEN, MIN_OPEN, HIGHEST_OPEN);
    maxOfflineReports =
        builder
            .comment(REPORTS_NOTE)
            .defineInRange(MAX_OFFLINE_REPORTS_KEY, REPORTS, 1, MAX_REPORTS);
  }

  private static void defineTries(ModConfigSpec.Builder builder) {
    freeWindowTicks =
        builder
            .comment(WINDOW_NOTE)
            .defineInRange(FREE_WINDOW_TICKS_KEY, FREE_WINDOW, MIN_FREE_WINDOW, MAX_FREE_WINDOW);
    freeTries =
        builder
            .comment(FREE_TRIES_COMMENT)
            .defineInRange(FREE_TRIES_KEY, FREE_TRIES, 1, MAX_FREE_TRIES);
    firstWaitTicks =
        builder
            .comment(FIRST_WAIT_NOTE)
            .defineInRange(FIRST_WAIT_TICKS_KEY, FIRST_WAIT, MIN_FIRST_WAIT, MAX_FIRST_WAIT);
    maxWaitTicks =
        builder
            .comment(MAX_WAIT_TICKS_COMMENT)
            .defineInRange(MAX_WAIT_TICKS_KEY, MAX_WAIT, MIN_MAX_WAIT, HIGHEST_WAIT);
    resetTicks =
        builder
            .comment(RESET_TICKS_COMMENT)
            .defineInRange(RESET_TICKS_KEY, RESET, MIN_RESET, MAX_RESET);
  }
}
