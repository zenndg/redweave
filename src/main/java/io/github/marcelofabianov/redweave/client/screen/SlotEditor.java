package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.client.ChannelColors;
import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.Direction;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.menu.NodeAction;
import io.github.marcelofabianov.redweave.menu.NodeScreenTexts;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

/**
 * The panel of the chosen slot: direction, block face, channel and logic (telas.md). It builds the
 * controls of a frame; drawing and clicking both use the same list, so they never disagree.
 */
final class SlotEditor {
  static final int LABEL_X = 8;
  static final int CONTROL_X = 58;
  static final int TITLE_Y = 7;
  static final int DIRECTION_Y = 24;
  static final int FACE_Y = 40;
  static final int CUBE_SIZE = 30;
  static final int CHANNEL_Y = 76;
  static final int LOGIC_Y = 108;
  private static final int BUTTON_HEIGHT = 14;
  private static final int BUTTON_PADDING = 12;
  private static final int SPACING = 2;
  private static final int SWATCH = 12;
  private static final int SWATCH_STEP = 13;
  private static final int SWATCHES_PER_ROW = 8;
  private static final int RIGHT_PADDING = 6;
  private static final int CLEAR_Y = 4;
  private static final List<String> LATER_LOGIC =
      List.of(
          NodeScreenTexts.LOGIC_INVERT,
          NodeScreenTexts.LOGIC_PULSE,
          NodeScreenTexts.LOGIC_DELAY,
          NodeScreenTexts.LOGIC_THRESHOLD);

  private SlotEditor() {}

  static List<Control> controls(Font font, NodeView view, Box panel, int index, boolean armed) {
    List<Control> controls = new ArrayList<>();
    Optional<ChannelSlot> slot = view.menu().slot(index);
    if (slot.isEmpty()) {
      useButton(font, view, panel, index).ifPresent(controls::add);
      return controls;
    }
    if (view.editable()) {
      controls.add(clearButton(font, panel, index, armed));
    }
    controls.addAll(directionButtons(font, view, panel, index, slot.get()));
    controls.addAll(swatches(view, panel, index, slot.get()));
    controls.addAll(logicButtons(font, panel));
    return controls;
  }

  static Box cubeBox(Box panel) {
    return new Box(panel.x() + CONTROL_X, panel.y() + FACE_Y, CUBE_SIZE, CUBE_SIZE);
  }

  /** "Click to use" on a free slot (answer 83). */
  private static Optional<Control> useButton(Font font, NodeView view, Box panel, int index) {
    if (!view.editable()) {
      return Optional.empty();
    }
    Component label = Component.translatable(NodeScreenTexts.CLICK_TO_USE);
    Box box = button(font, label, panel.x() + CONTROL_X, panel.y() + DIRECTION_Y);
    return Optional.of(
        Control.button(box, Control.Look.CHOSEN, label, Optional.of(NodeAction.use(index))));
  }

  /** The first click arms, the second clears, so a slip costs nothing (answer 84). */
  private static Control clearButton(Font font, Box panel, int index, boolean armed) {
    String key = armed ? NodeScreenTexts.CLEAR_CONFIRM : NodeScreenTexts.CLEAR;
    Component label = Component.translatable(key);
    int width = font.width(label) + BUTTON_PADDING;
    Box box =
        new Box(panel.right() - RIGHT_PADDING - width, panel.y() + CLEAR_Y, width, BUTTON_HEIGHT);
    if (armed) {
      return Control.button(box, Control.Look.CHOSEN, label, Optional.of(NodeAction.clear(index)));
    }
    return Control.button(box, Control.Look.ARMS_CLEAR, label, Optional.empty());
  }

  private static List<Control> directionButtons(
      Font font, NodeView view, Box panel, int index, ChannelSlot slot) {
    List<Control> buttons = new ArrayList<>();
    int x = panel.x() + CONTROL_X;
    for (Direction direction : Direction.values()) {
      Component label = SlotList.directionOf(withDirection(slot, direction));
      Box box = button(font, label, x, panel.y() + DIRECTION_Y);
      ChannelSlot changed = withDirection(slot, direction);
      buttons.add(choice(box, label, direction == slot.direction(), view, index, changed));
      x = box.right();
    }
    return buttons;
  }

  private static List<Control> swatches(NodeView view, Box panel, int index, ChannelSlot slot) {
    List<Control> swatches = new ArrayList<>();
    for (ChannelColor channel : ChannelColor.values()) {
      int column = channel.ordinal() % SWATCHES_PER_ROW;
      int row = channel.ordinal() / SWATCHES_PER_ROW;
      Box box =
          new Box(
              panel.x() + CONTROL_X + column * SWATCH_STEP,
              panel.y() + CHANNEL_Y + row * SWATCH_STEP,
              SWATCH,
              SWATCH);
      ChannelSlot changed = new ChannelSlot(slot.direction(), slot.face(), channel);
      boolean chosen = channel == slot.channel();
      Optional<NodeAction> action =
          view.editable() && !chosen
              ? Optional.of(NodeAction.configure(index, changed))
              : Optional.empty();
      swatches.add(
          Control.swatch(
              box, chosen, ChannelColors.argb(channel), action, ChannelNames.of(channel)));
    }
    return swatches;
  }

  /** Only None works in Phase 1; the others show what arrives in Phase 2 (telas.md). */
  private static List<Control> logicButtons(Font font, Box panel) {
    List<Control> buttons = new ArrayList<>();
    Component none = Component.translatable(NodeScreenTexts.LOGIC_NONE);
    Flow flow = new Flow(panel.x() + CONTROL_X, panel.y() + LOGIC_Y, panel.right() - RIGHT_PADDING);
    buttons.add(Control.button(flow.next(font, none), Control.Look.CHOSEN, none, Optional.empty()));
    Component later = Component.translatable(NodeScreenTexts.LOGIC_TOOLTIP);
    for (String key : LATER_LOGIC) {
      Component label = Component.translatable(key);
      Control disabled =
          Control.button(flow.next(font, label), Control.Look.DISABLED, label, Optional.empty());
      buttons.add(disabled.withTooltip(later));
    }
    return buttons;
  }

  private static Control choice(
      Box box, Component label, boolean chosen, NodeView view, int index, ChannelSlot changed) {
    if (chosen) {
      return Control.button(box, Control.Look.CHOSEN, label, Optional.empty());
    }
    if (!view.editable()) {
      return Control.button(box, Control.Look.DISABLED, label, Optional.empty());
    }
    return Control.button(
        box, Control.Look.BUTTON, label, Optional.of(NodeAction.configure(index, changed)));
  }

  private static ChannelSlot withDirection(ChannelSlot slot, Direction direction) {
    return new ChannelSlot(direction, slot.face(), slot.channel());
  }

  static Box button(Font font, Component label, int x, int y) {
    return new Box(x, y, font.width(label) + BUTTON_PADDING, BUTTON_HEIGHT);
  }

  /** Places buttons left to right and wraps them to a new row at the edge of the panel. */
  private static final class Flow {
    private final int left;
    private final int right;
    private int x;
    private int y;

    Flow(int left, int y, int right) {
      this.left = left;
      this.x = left;
      this.y = y;
      this.right = right;
    }

    Box next(Font font, Component label) {
      Box box = button(font, label, x, y);
      if (box.right() > right && x > left) {
        x = left;
        y += BUTTON_HEIGHT + SPACING;
        box = button(font, label, x, y);
      }
      x = box.right() + SPACING;
      return box;
    }
  }
}
