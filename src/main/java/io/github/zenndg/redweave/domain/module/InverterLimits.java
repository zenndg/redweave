package io.github.zenndg.redweave.domain.module;

/**
 * The numbers of the burnout of the Signal Inverter that the pack sets in the config: how many
 * changes of the output, within how many ticks, burn it out, and for how long (answer 449). The
 * domain defines it, and the config implements it.
 */
public interface InverterLimits {
  int burnoutToggles();

  int burnoutWindowTicks();

  int burnoutTicks();
}
