package io.github.zenndg.redweave.client.screen;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

/** Nothing goes past the border: a long text is cut with an ellipsis (telas.md). */
final class FittedText {
  private static final String ELLIPSIS = "…";

  private FittedText() {}

  static String fit(Font font, Component text, int width) {
    String full = text.getString();
    if (font.width(full) <= width) {
      return full;
    }
    return font.plainSubstrByWidth(full, width - font.width(ELLIPSIS)) + ELLIPSIS;
  }

  static boolean isCut(Font font, Component text, int width) {
    return font.width(text) > width;
  }
}
