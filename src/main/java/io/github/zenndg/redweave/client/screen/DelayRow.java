package io.github.zenndg.redweave.client.screen;

import io.github.zenndg.redweave.menu.DelayScreenTexts;
import io.github.zenndg.redweave.menu.DelaySteps;
import io.github.zenndg.redweave.menu.TimerScreenTexts;
import java.util.List;
import java.util.function.IntConsumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * The `Delay` row every module screen with a delay shares: the label, the seconds between `−` and
 * `+`, and the two buttons, only for the owner (answers 830, 840 and 853).
 */
final class DelayRow {
  private static final int TEXT_Y = 3;
  private static final String MINUS = "−";
  private static final String PLUS = "+";

  private DelayRow() {}

  static void draw(GuiGraphics graphics, Font font, int left, int top, int seconds) {
    ModuleColumn.label(graphics, font, left, top, DelayScreenTexts.DELAY);
    Component value = Component.translatable(TimerScreenTexts.SECONDS, seconds);
    int x = left + TimerControls.VALUE_X + (TimerControls.VALUE_WIDTH - font.width(value)) / 2;
    graphics.drawString(font, value, x, top + TEXT_Y, ScreenColors.TEXT, false);
  }

  /** `−` and `+`, which send the value of their step. */
  static void buttons(
      List<TimerButton> buttons, int left, int top, boolean owner, IntConsumer send) {
    buttons.add(button(left + ModuleColumn.CONTROL_X, top, MINUS, owner, send, false));
    buttons.add(button(left + TimerControls.PLUS_X, top, PLUS, owner, send, true));
  }

  private static TimerButton button(
      int x, int top, String label, boolean owner, IntConsumer send, boolean longer) {
    return new TimerButton(
        new Box(x, top, TimerControls.STEP_WIDTH, ModuleColumn.ROW_HEIGHT),
        Component.literal(label),
        owner ? TimerButton.Look.BUTTON : TimerButton.Look.DISABLED,
        () -> send.accept(DelaySteps.of(longer)));
  }
}
