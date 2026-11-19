package io.github.zenndg.redweave.client.screen;

import io.github.zenndg.redweave.menu.DelayScreenTexts;
import io.github.zenndg.redweave.menu.DelaySteps;
import io.github.zenndg.redweave.menu.NodeAction;
import io.github.zenndg.redweave.menu.TimerScreenTexts;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * The `Delay` row of the Node Screen, the last row of the panel, under the rows of the open
 * channel, though the delay is of the whole node (answers 861, 866 and 876): the label, the seconds
 * and `−` and `+`, which only the owner of the network clicks.
 */
final class NodeDelayRow {
  private static final int STEP_WIDTH = 14;
  private static final int VALUE_WIDTH = 34;
  private static final int TEXT_Y = 3;
  private static final String MINUS = "−";
  private static final String PLUS = "+";

  private NodeDelayRow() {}

  /** The label and the seconds; the buttons are drawn with the other controls of the screen. */
  static void draw(GuiGraphics graphics, Font font, NodeView view, Box row) {
    Component label = Component.translatable(DelayScreenTexts.DELAY);
    int y = row.y() + TEXT_Y;
    graphics.drawString(font, label, row.x() + SlotEditor.LABEL_X, y, ScreenColors.LABEL, false);
    Component value = Component.translatable(TimerScreenTexts.SECONDS, view.menu().delaySeconds());
    int valueX = minusX(row) + STEP_WIDTH + (VALUE_WIDTH - font.width(value)) / 2;
    graphics.drawString(font, value, valueX, y, ScreenColors.TEXT, false);
  }

  /** `−` and `+`, one second shorter or longer (answer 830). */
  static List<Control> controls(NodeView view, Box row) {
    int plusX = minusX(row) + STEP_WIDTH + VALUE_WIDTH;
    return List.of(
        step(view, minusX(row), row, MINUS, DelaySteps.SHORTER),
        step(view, plusX, row, PLUS, DelaySteps.LONGER));
  }

  private static int minusX(Box row) {
    return row.x() + SlotEditor.CONTROL_X;
  }

  private static Control step(NodeView view, int x, Box row, String label, int value) {
    Box box = new Box(x, row.y(), STEP_WIDTH, row.height());
    Control.Look look = view.editable() ? Control.Look.BUTTON : Control.Look.DISABLED;
    Optional<NodeAction> action =
        view.editable() ? Optional.of(NodeAction.delay(value)) : Optional.empty();
    return Control.button(box, look, Component.literal(label), action);
  }
}
