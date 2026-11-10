package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.menu.NodeScreenTexts;
import net.minecraft.network.chat.Component;

/**
 * A channel as the screens show it: its number and its name, as `15 Red`. The name is the color,
 * translated by the game, until the Controller Screen renames channels (#20, answer 26).
 */
final class ChannelNames {
  private static final String NUMBER_AND_NAME = "%02d ";

  private ChannelNames() {}

  static Component of(ChannelColor channel) {
    return Component.literal(String.format(NUMBER_AND_NAME, channel.number()))
        .append(Component.translatable(NodeScreenTexts.color(channel.key())));
  }
}
