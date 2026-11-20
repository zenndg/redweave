package io.github.zenndg.redweave.block.part;

import java.util.List;

/**
 * The suffixes of the models of the module plates, one for each look of their top, as the states of
 * their old blocks picked them (answers 401, 452, 471 and 488). Datagen writes a model for each,
 * and the client loads them for the part space (answer 536).
 */
public final class ModuleLooks {
  static final String NONE = "";
  static final String ON = "_on";
  static final String OFF = "_off";
  static final String RUNNING = "_running";
  static final String BURNT = "_burnt";
  static final String SEPARATOR = "_";

  private ModuleLooks() {}

  /** The running LED and the lit arrow, as of the timer and the randomizer (401 and 488). */
  static List<String> leds() {
    return List.of(leds(false, false), leds(false, true), leds(true, false), leds(true, true));
  }

  static String leds(boolean running, boolean powered) {
    return (running ? RUNNING : NONE) + (powered ? ON : NONE);
  }
}
