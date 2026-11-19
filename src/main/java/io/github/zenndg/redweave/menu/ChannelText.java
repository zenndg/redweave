package io.github.zenndg.redweave.menu;

import io.github.zenndg.redweave.domain.signal.ChannelColor;

/**
 * The text of a channel: its number and, after it, the name its owner gave, as `01 Door`, or only
 * the number, as `01`. The name of the color never shows, because a square in the color of the
 * channel goes beside it (answer 770). It needs no class of the game, so it is tested alone (777).
 */
public final class ChannelText {
  private static final String NUMBER = "%02d";
  private static final String NUMBER_AND_NAME = "%02d %s";

  private ChannelText() {}

  public static String of(ChannelColor channel, String name) {
    return name.isEmpty()
        ? String.format(NUMBER, channel.number())
        : String.format(NUMBER_AND_NAME, channel.number(), name);
  }
}
