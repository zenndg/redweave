package io.github.zenndg.redweave.client.screen;

import io.github.zenndg.redweave.client.ChannelColors;
import io.github.zenndg.redweave.domain.network.Direction;
import io.github.zenndg.redweave.domain.signal.ChannelColor;
import io.github.zenndg.redweave.menu.ChannelNames;
import io.github.zenndg.redweave.menu.ControllerMenu;
import io.github.zenndg.redweave.menu.ControllerScreenRows.SlotLine;
import io.github.zenndg.redweave.menu.ControllerScreenTexts;
import io.github.zenndg.redweave.menu.NodeScreenTexts;
import net.minecraft.network.chat.Component;

/**
 * An enabled slot under an open row of the node list, as `15 Red · Input · Redstone from lever 1`
 * (answers 179 and 234). The numbers come from the server, so one outside the colors or directions
 * shows the first one.
 */
final class SlotLines {
  private static final String NO_NAME = "";

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

  /** As `15 Red · Input` (answers 234 and 277). */
  static Component text(SlotLine slot, ControllerMenu menu) {
    ChannelColor channel = channelOf(slot);
    String name =
        channel.ordinal() < menu.channels().size()
            ? menu.channels().get(channel.ordinal()).name()
            : NO_NAME;
    Component direction =
        Component.translatable(isInput(slot) ? NodeScreenTexts.INPUT : NodeScreenTexts.OUTPUT);
    return Component.translatable(
        ControllerScreenTexts.SLOT_ROW, ChannelNames.of(channel, name), direction);
  }
}
