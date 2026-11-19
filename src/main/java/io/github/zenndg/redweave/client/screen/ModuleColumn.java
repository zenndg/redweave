package io.github.zenndg.redweave.client.screen;

import io.github.zenndg.redweave.client.ChannelColors;
import io.github.zenndg.redweave.domain.module.Connection;
import io.github.zenndg.redweave.domain.signal.ChannelColor;
import io.github.zenndg.redweave.menu.ChannelNames;
import io.github.zenndg.redweave.menu.ModuleScreenSync;
import io.github.zenndg.redweave.menu.TimerScreenTexts;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * The right column every Signal Module Screen has: a label per row and, for a connection, `Edge` or
 * `Channel` and the chosen channel, as `■ 15 Red` (answers 400, 404 and 451). Whoever does not own
 * the plate sees what is chosen, dimmed (answer 378).
 */
final class ModuleColumn {
  static final int LABEL_X = 138;
  static final int CONTROL_X = 184;
  static final int ROW_HEIGHT = 14;

  private static final int CHANNEL_LABEL_X = 272;
  private static final int EDGE_WIDTH = 36;
  private static final int CHANNEL_X = 222;
  private static final int CHANNEL_WIDTH = 46;
  private static final int TEXT_Y = 3;
  private static final int SQUARE = 5;
  private static final int SQUARE_Y = 4;
  private static final int SQUARE_GAP = 3;
  private static final int MAX_CHANNEL_TEXT = 50;

  /** Who looks at the row, and whether the channels of this connection are open. */
  record Choosing(boolean owner, boolean picking) {}

  private ModuleColumn() {}

  /**
   * `Edge`, which asks the server, and `Channel`, which opens the channels on the client. While the
   * channels are open, `Channel` is the one chosen, though the server still keeps the edge until a
   * channel is picked (answer 456).
   */
  static void connection(
      List<TimerButton> buttons,
      Connection now,
      Box row,
      Choosing choosing,
      Runnable edge,
      Runnable channel) {
    connection(buttons, now, row, choosing, TimerScreenTexts.EDGE, edge, channel);
  }

  /** The same choice, with another name for the edge, as `None` of the Signal Lamp (650). */
  static void connection(
      List<TimerButton> buttons,
      Connection now,
      Box row,
      Choosing choosing,
      String edgeKey,
      Runnable edge,
      Runnable channel) {
    boolean onChannel = choosing.picking() || !now.isEdge();
    buttons.add(
        new TimerButton(
            new Box(row.x() + CONTROL_X, row.y(), EDGE_WIDTH, ROW_HEIGHT),
            Component.translatable(edgeKey),
            lookOf(!onChannel, choosing.owner()),
            edge));
    buttons.add(
        new TimerButton(
            new Box(row.x() + CHANNEL_X, row.y(), CHANNEL_WIDTH, ROW_HEIGHT),
            Component.translatable(TimerScreenTexts.CHANNEL),
            lookOf(onChannel, choosing.owner()),
            channel));
  }

  /** Copper when chosen; dimmed for whoever does not own the plate (answer 378). */
  static TimerButton.Look lookOf(boolean chosen, boolean owner) {
    if (chosen) {
      return owner ? TimerButton.Look.CHOSEN : TimerButton.Look.LOCKED;
    }
    return owner ? TimerButton.Look.BUTTON : TimerButton.Look.DISABLED;
  }

  static void label(GuiGraphics graphics, Font font, int left, int y, String key) {
    graphics.drawString(
        font, Component.translatable(key), left + LABEL_X, y + TEXT_Y, ScreenColors.LABEL, false);
  }

  /** The chosen channel after `Channel`, as `■ 15 Red`, with the name the owner gave it (404). */
  static void channel(
      GuiGraphics graphics,
      Font font,
      int left,
      int y,
      ModuleScreenSync sync,
      Connection connection) {
    Optional<ChannelColor> channel = connection.channel();
    if (channel.isEmpty()) {
      return;
    }
    int x = left + CHANNEL_LABEL_X;
    graphics.fill(
        x, y + SQUARE_Y, x + SQUARE, y + SQUARE_Y + SQUARE, ChannelColors.argb(channel.get()));
    Component name = ChannelNames.of(channel.get(), sync.channelName(channel.get()));
    String shown = FittedText.fit(font, name, MAX_CHANNEL_TEXT);
    graphics.drawString(font, shown, x + SQUARE + SQUARE_GAP, y + TEXT_Y, ScreenColors.TEXT, false);
  }
}
