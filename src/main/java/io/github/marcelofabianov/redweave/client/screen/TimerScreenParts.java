package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.domain.module.TimerState;
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

  private TimerScreenParts() {}

  static void drawLabels(GuiGraphics graphics, Font font, int left, int top, TimerMenu menu) {
    ModuleColumn.label(
        graphics, font, left, top + TimerControls.INTERVAL_Y, TimerScreenTexts.INTERVAL);
    ModuleColumn.label(graphics, font, left, top + TimerControls.PULSE_Y, TimerScreenTexts.PULSE);
    ModuleColumn.label(graphics, font, left, top + TimerControls.OUTPUT_Y, TimerScreenTexts.OUTPUT);
    ModuleColumn.label(graphics, font, left, top + TimerControls.PAUSE_Y, TimerScreenTexts.PAUSE);
    value(graphics, font, left, top + TimerControls.INTERVAL_Y, menu.intervalTicks());
    value(graphics, font, left, top + TimerControls.PULSE_Y, menu.pulseTicks());
    ModuleColumn.channel(
        graphics, font, left, top + TimerControls.OUTPUT_Y, menu.sync(), menu.output());
    ModuleColumn.channel(
        graphics, font, left, top + TimerControls.PAUSE_Y, menu.sync(), menu.pause());
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
    return TimerButton.run(box, running, owner, click);
  }

  /** An interval in seconds with tenths, centered between `−` and `+` (answer 382). */
  static void value(GuiGraphics graphics, Font font, int left, int y, int ticks) {
    Component text = TimerDial.seconds(ticks);
    Box box = valueBox(left, y);
    int x = box.x() + (box.width() - font.width(text)) / 2;
    graphics.drawString(font, text, x, y + TEXT_Y, ScreenColors.TEXT, false);
  }

  static Box valueBox(int left, int y) {
    return new Box(
        left + TimerControls.VALUE_X, y, TimerControls.VALUE_WIDTH, ModuleColumn.ROW_HEIGHT);
  }
}
