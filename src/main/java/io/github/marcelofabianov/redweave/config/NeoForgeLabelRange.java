package io.github.marcelofabianov.redweave.config;

import java.util.function.IntSupplier;

/** The reach of the labels over the nodes, from the server config, read every time (268). */
public final class NeoForgeLabelRange implements IntSupplier {
  @Override
  public int getAsInt() {
    return RedweaveConfig.LABEL_RANGE.get();
  }
}
