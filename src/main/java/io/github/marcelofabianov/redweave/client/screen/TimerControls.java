package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.domain.module.Connection;
import io.github.marcelofabianov.redweave.menu.TimerAction;
import io.github.marcelofabianov.redweave.menu.TimerMenu;
import io.github.marcelofabianov.redweave.menu.TimerScreenTexts;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * The buttons of the right column of the Signal Timer Screen: `Interval` and `Pulse` with `−` and
 * `+`, and `Output` and `Pause` with `Edge` and `Channel` (answers 398, 400 and 404). A click asks
 * the server, and `Channel` only opens the channels, or first the network choice, on the client.
 */
final class TimerControls {
  static final int LABEL_X = 138;
  static final int CONTROL_X = 184;
  static final int INTERVAL_Y = 42;
  static final int PULSE_Y = 60;
  static final int OUTPUT_Y = 78;
  static final int PAUSE_Y = 96;
  static final int VALUE_X = 200;
  static final int VALUE_WIDTH = 40;
  static final int CHANNEL_LABEL_X = 272;
  static final int ROW_HEIGHT = 14;

  private static final int STEP_WIDTH = 14;
  private static final int PLUS_X = 242;
  private static final int EDGE_WIDTH = 36;
  private static final int CHANNEL_X = 222;
  private static final int CHANNEL_WIDTH = 46;
  private static final String MINUS = "−";
  private static final String PLUS = "+";

  /** What a click asks: the server for an action, or the screen to open the channels. */
  record Clicks(Consumer<TimerAction> send, Consumer<TimerAction.Kind> pick) {}

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
            new Box(left + CONTROL_X, y, STEP_WIDTH, ROW_HEIGHT),
            MINUS,
            kind,
            false,
            owner,
            clicks));
    buttons.add(
        step(new Box(left + PLUS_X, y, STEP_WIDTH, ROW_HEIGHT), PLUS, kind, true, owner, clicks));
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
    Runnable edge = () -> clicks.send().accept(new TimerAction(kind, TimerAction.EDGE));
    Runnable channel = () -> clicks.pick().accept(kind);
    buttons.add(
        new TimerButton(
            new Box(left + CONTROL_X, y, EDGE_WIDTH, ROW_HEIGHT),
            Component.translatable(TimerScreenTexts.EDGE),
            lookOf(now.isEdge(), owner),
            edge));
    buttons.add(
        new TimerButton(
            new Box(left + CHANNEL_X, y, CHANNEL_WIDTH, ROW_HEIGHT),
            Component.translatable(TimerScreenTexts.CHANNEL),
            lookOf(!now.isEdge(), owner),
            channel));
  }

  private static TimerButton.Look lookOf(boolean chosen, boolean owner) {
    if (chosen) {
      return owner ? TimerButton.Look.CHOSEN : TimerButton.Look.LOCKED;
    }
    return owner ? TimerButton.Look.BUTTON : TimerButton.Look.DISABLED;
  }
}
