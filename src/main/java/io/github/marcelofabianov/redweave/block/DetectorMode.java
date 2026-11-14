package io.github.marcelofabianov.redweave.block;

import io.github.marcelofabianov.redweave.domain.module.WeatherMode;
import net.minecraft.util.StringRepresentable;

/**
 * The mode of a Weather Detector as the state `mode` of its block, which picks the symbol of its
 * top (answer 471). The names are part of the save (rule 15) and the same as the keys of the mode.
 */
public enum DetectorMode implements StringRepresentable {
  RAIN(WeatherMode.RAIN),
  THUNDER(WeatherMode.THUNDER),
  CLEAR(WeatherMode.CLEAR);

  private final WeatherMode mode;

  DetectorMode(WeatherMode mode) {
    this.mode = mode;
  }

  static DetectorMode of(WeatherMode mode) {
    for (DetectorMode found : values()) {
      if (found.mode == mode) {
        return found;
      }
    }
    return RAIN;
  }

  @Override
  public String getSerializedName() {
    return mode.key();
  }
}
