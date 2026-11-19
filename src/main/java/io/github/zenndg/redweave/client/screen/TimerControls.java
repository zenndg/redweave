package io.github.zenndg.redweave.client.screen;

import io.github.zenndg.redweave.domain.module.Connection;
import io.github.zenndg.redweave.menu.TimerAction;
import io.github.zenndg.redweave.menu.TimerMenu;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * The buttons of the right column of the Signal Timer Screen: `Interval` and `Pulse` with `−` and
 * `+`, and `Output` and `Pause` with `Edge` and `Channel` (answers 398, 400 and 404). A click asks
 * the server, and `Channel` only opens the channels, or first the network choice, on the client.
 */
final class TimerControls {
  static final int INTERVAL_Y = 42;
  static final int PULSE_Y = 60;
  static final int OUTPUT_Y = 78;
  static final int PAUSE_Y = 96;
  static final int VALUE_X = 200;
  static final int VALUE_WIDTH = 40;

  static final int STEP_WIDTH = 14;
  static final int PLUS_X = 242;
  private static final String MINUS = "−";
  private static final String PLUS = "+";

  /** What a click asks: the server for an action, or the screen to open the channels. */
  record Clicks(
      Consumer<TimerAction> send,
      Consumer<TimerAction.Kind> pick,
      Optional<TimerAction.Kind> picking) {}

  private TimerControls() {}

  static List<TimerButton> of(TimerMenu menu, int left, int top, boolean owner, Clicks clicks) {
    List<TimerButton> buttons = new ArrayList<>();
    steps(buttons, TimerAction.Kind.INTERVAL, left, top + INTERVAL_Y, owner, clicks);
    steps(buttons, TimerAction.Kind.PULSE, left, top + PULSE_Y, owner, clicks);
    connection(
        buttons, TimerAction.Kind.OUTPUT, menu.output(), left, top + OUTPUT_Y, owner, clicks);
    connection(buttons, TimerAction.Kind.PAUSE, menu.pause(), left, top + PAUSE_Y, owner, clicks);
    return buttons;
  }

  /** `−` and `+`: a click moves 0.1 s, and Shift + click 1 s (answer 398). */
  private static void steps(
      List<TimerButton> buttons,
      TimerAction.Kind kind,
      int left,
      int y,
      boolean owner,
      Clicks clicks) {
    buttons.add(
        step(
            new Box(left + ModuleColumn.CONTROL_X, y, STEP_WIDTH, ModuleColumn.ROW_HEIGHT),
            MINUS,
            kind,
            false,
            owner,
            clicks));
    buttons.add(
        step(
            new Box(left + PLUS_X, y, STEP_WIDTH, ModuleColumn.ROW_HEIGHT),
            PLUS,
            kind,
            true,
            owner,
            clicks));
  }

  private static TimerButton step(
      Box box, String label, TimerAction.Kind kind, boolean longer, boolean owner, Clicks clicks) {
    Runnable click =
        () -> clicks.send().accept(TimerAction.step(kind, longer, Screen.hasShiftDown()));
    return new TimerButton(
        box,
        Component.literal(label),
        owner ? TimerButton.Look.BUTTON : TimerButton.Look.DISABLED,
        click);
  }

  private static void connection(
      List<TimerButton> buttons,
      TimerAction.Kind kind,
      Connection now,
      int left,
      int y,
      boolean owner,
      Clicks clicks) {
    ModuleColumn.connection(
        buttons,
        now,
        new Box(left, y, 0, ModuleColumn.ROW_HEIGHT),
        new ModuleColumn.Choosing(owner, clicks.picking().equals(Optional.of(kind))),
        () -> clicks.send().accept(new TimerAction(kind, TimerAction.EDGE)),
        () -> clicks.pick().accept(kind));
  }
}
