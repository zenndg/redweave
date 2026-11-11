package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.client.ChannelColors;
import io.github.marcelofabianov.redweave.domain.network.Direction;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.menu.ControllerMenu;
import io.github.marcelofabianov.redweave.menu.ControllerScreenRows.SlotLine;
import io.github.marcelofabianov.redweave.menu.ControllerScreenTexts;
import io.github.marcelofabianov.redweave.menu.NodeScreenTexts;
import net.minecraft.network.chat.Component;

/**
 * A slot under an open row of the node list, as `Slot 1 · 15 Red · Input` (answer 179). The numbers
 * come from the server, so one outside the colors or directions shows the first one.
 */
final class SlotLines {
  private static final String NO_NAME = "";

  /** The screens count slots from 1. */
  private static final int FIRST_NUMBER = 1;

  private SlotLines() {}

  static boolean isInput(SlotLine slot) {
    return slot.direction() == Direction.INPUT.ordinal();
  }

  static ChannelColor channelOf(SlotLine slot) {
    ChannelColor[] colors = ChannelColor.values();
    return slot.channel() >= 0 && slot.channel() < colors.length
        ? colors[slot.channel()]
        : colors[0];
  }

  static int argb(SlotLine slot) {
    return ChannelColors.argb(channelOf(slot));
  }

  static Component text(SlotLine slot, ControllerMenu menu) {
    ChannelColor channel = channelOf(slot);
    String name =
        channel.ordinal() < menu.channels().size()
            ? menu.channels().get(channel.ordinal()).name()
            : NO_NAME;
    Component direction =
        Component.translatable(isInput(slot) ? NodeScreenTexts.INPUT : NodeScreenTexts.OUTPUT);
    return Component.translatable(
        ControllerScreenTexts.SLOT_ROW,
        slot.index() + FIRST_NUMBER,
        ChannelNames.of(channel, name),
        direction);
  }
}
