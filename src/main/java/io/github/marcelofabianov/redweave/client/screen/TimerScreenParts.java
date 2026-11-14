package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.client.ChannelColors;
import io.github.marcelofabianov.redweave.domain.module.Connection;
import io.github.marcelofabianov.redweave.domain.module.TimerState;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.menu.ChannelNames;
import io.github.marcelofabianov.redweave.menu.TimerMenu;
import io.github.marcelofabianov.redweave.menu.TimerScreenTexts;
import java.util.Optional;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * The texts of the right column of the Signal Timer Screen, the ticks of the cycle in a tooltip and
 * the `Running` or `Stopped` button (answers 380, 382 and 404).
 */
final class TimerScreenParts {
  private static final int TEXT_Y = 3;
  private static final int SQUARE = 5;
  private static final int SQUARE_Y = 4;
  private static final int SQUARE_GAP = 3;
  private static final int MAX_CHANNEL_TEXT = 50;
  private static final String DOT = "● ";

  private TimerScreenParts() {}

  static void drawLabels(GuiGraphics graphics, Font font, int left, int top, TimerMenu menu) {
    label(graphics, font, left, top + TimerControls.INTERVAL_Y, TimerScreenTexts.INTERVAL);
    label(graphics, font, left, top + TimerControls.PULSE_Y, TimerScreenTexts.PULSE);
    label(graphics, font, left, top + TimerControls.OUTPUT_Y, TimerScreenTexts.OUTPUT);
    label(graphics, font, left, top + TimerControls.PAUSE_Y, TimerScreenTexts.PAUSE);
    value(graphics, font, left, top + TimerControls.INTERVAL_Y, menu.intervalTicks());
    value(graphics, font, left, top + TimerControls.PULSE_Y, menu.pulseTicks());
    channel(graphics, font, left, top + TimerControls.OUTPUT_Y, menu, menu.output());
    channel(graphics, font, left, top + TimerControls.PAUSE_Y, menu, menu.pause());
  }

  /** Over `Interval` or `Pulse`, the ticks of the value (answer 382). */
  static Optional<Component> ticksAt(
      int left, int top, TimerMenu menu, double mouseX, double mouseY) {
    if (valueBox(left, top + TimerControls.INTERVAL_Y).contains(mouseX, mouseY)) {
      return Optional.of(Component.translatable(TimerScreenTexts.TICKS, menu.intervalTicks()));
    }
    if (valueBox(left, top + TimerControls.PULSE_Y).contains(mouseX, mouseY)) {
      return Optional.of(Component.translatable(TimerScreenTexts.TICKS, menu.pulseTicks()));
    }
    return Optional.empty();
  }

  /** `Running`, paused or not, with a green dot, or `Stopped` (answers 380 and 433). */
  static TimerButton runButton(Box box, TimerMenu menu, boolean owner, Runnable click) {
    boolean running = menu.state().filter(state -> state != TimerState.STOPPED).isPresent();
    Component label =
        Component.literal(DOT)
            .withColor(running ? ScreenColors.ONLINE : ScreenColors.DIM)
            .append(
                Component.translatable(
                    running ? TimerScreenTexts.RUNNING : TimerScreenTexts.STOPPED));
    return new TimerButton(
        box, label, owner ? TimerButton.Look.BUTTON : TimerButton.Look.DISABLED, click);
  }

  private static void label(GuiGraphics graphics, Font font, int left, int y, String key) {
    graphics.drawString(
        font,
        Component.translatable(key),
        left + TimerControls.LABEL_X,
        y + TEXT_Y,
        ScreenColors.LABEL,
        false);
  }

  private static void value(GuiGraphics graphics, Font font, int left, int y, int ticks) {
    Component text = TimerDial.seconds(ticks);
    Box box = valueBox(left, y);
    int x = box.x() + (box.width() - font.width(text)) / 2;
    graphics.drawString(font, text, x, y + TEXT_Y, ScreenColors.TEXT, false);
  }

  private static Box valueBox(int left, int y) {
    return new Box(
        left + TimerControls.VALUE_X, y, TimerControls.VALUE_WIDTH, TimerControls.ROW_HEIGHT);
  }

  /** The chosen channel after `Channel`, as `■ 15 Red` (answer 404). */
  private static void channel(
      GuiGraphics graphics, Font font, int left, int y, TimerMenu menu, Connection connection) {
    Optional<ChannelColor> channel = connection.channel();
    if (channel.isEmpty()) {
      return;
    }
    int x = left + TimerControls.CHANNEL_LABEL_X;
    graphics.fill(
        x, y + SQUARE_Y, x + SQUARE, y + SQUARE_Y + SQUARE, ChannelColors.argb(channel.get()));
    Component name = ChannelNames.of(channel.get(), menu.channelName(channel.get()));
    String shown = FittedText.fit(font, name, MAX_CHANNEL_TEXT);
    graphics.drawString(font, shown, x + SQUARE + SQUARE_GAP, y + TEXT_Y, ScreenColors.TEXT, false);
  }
}
