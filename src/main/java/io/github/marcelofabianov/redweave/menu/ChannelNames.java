package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;

/**
 * A channel as the screens show it, beside a square in its color: its number and the name its owner
 * gave, as `01 Door`, or only the number, as `01` (answer 770). A text with no square of the
 * screen, as the action bar, takes a `■` in the color of the channel first (775).
 */
public final class ChannelNames {
  private static final String NO_NAME = "";
  private static final String SQUARE = "■ ";

  private ChannelNames() {}

  /** With the name the owner gave on the Controller Screen, or only the number when it is empty. */
  public static Component of(ChannelColor channel, String name) {
    return Component.literal(ChannelText.of(channel, name));
  }

  public static Component of(ChannelColor channel) {
    return of(channel, NO_NAME);
  }

  /** As `■ 01 Door`, with only the square in the color of the channel (answer 775). */
  public static Component marked(ChannelColor channel, String name) {
    return Component.empty().append(square(channel)).append(of(channel, name));
  }

  /** A `■` in the color of a channel or network; the colors follow the order of the wool (224). */
  public static Component square(ChannelColor color) {
    int rgb = DyeColor.byId(color.ordinal()).getTextColor();
    return Component.literal(SQUARE).withStyle(style -> style.withColor(rgb));
  }
}
