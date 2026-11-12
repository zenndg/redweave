package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import java.util.Optional;

/**
 * The numbers the Controller Screen and the server exchange. A button click is a color of the
 * palette (answer 212); anything else is refused, because a client can send any number (rule 16).
 */
public final class ControllerScreenCodes {
  /** The button that clears the log, after the 16 colors of the palette (answer 264). */
  public static final int CLEAR_LOG = ChannelColor.values().length;

  private ControllerScreenCodes() {}

  public static int button(ChannelColor color) {
    return color.ordinal();
  }

  /** The color of a number, or none outside the 16 colors. */
  public static Optional<ChannelColor> colorOf(int code) {
    ChannelColor[] colors = ChannelColor.values();
    return code >= 0 && code < colors.length ? Optional.of(colors[code]) : Optional.empty();
  }
}
