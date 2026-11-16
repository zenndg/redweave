package io.github.marcelofabianov.redweave.config;

/**
 * The numbers of the Signal Lamp, read from the server config every time: the fewest ticks between
 * two changes of its light, so a channel that changes often does not light the world again each
 * tick (answer 655, rules 3 and 18).
 */
public final class NeoForgeLampLimits {
  private NeoForgeLampLimits() {}

  public static int minLightUpdateTicks() {
    return RedweaveConfig.LAMP_MIN_LIGHT_UPDATE_TICKS.get();
  }
}
