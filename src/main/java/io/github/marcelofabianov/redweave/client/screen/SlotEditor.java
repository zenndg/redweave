package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.Direction;
import io.github.marcelofabianov.redweave.domain.network.Face;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.menu.NodeAction;
import io.github.marcelofabianov.redweave.menu.NodeScreenTexts;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

/**
 * The panel of the open channel: its status, direction, block face and logic (telas.md). It builds
 * the controls of a frame; drawing and clicking both use the same list, so they never disagree. An
 * enabled channel is locked, and a disabled one is set up (answers 228 and 237).
 */
final class SlotEditor {
  static final int LABEL_X = 8;
  static final int CONTROL_X = 58;
  static final int TITLE_Y = 7;
  static final int STATUS_Y = 22;
  static final int DIRECTION_Y = 40;
  static final int FACE_Y = 58;
  static final int CUBE_SIZE = 24;
  static final int LOGIC_Y = 88;
  static final int ROW_HEIGHT = 14;
  private static final int BUTTON_PADDING = 12;
  private static final int SPACING = 2;
  private static final int RIGHT_PADDING = 6;
  private static final List<String> LATER_LOGIC =
      List.of(
          NodeScreenTexts.LOGIC_INVERT,
          NodeScreenTexts.LOGIC_PULSE,
          NodeScreenTexts.LOGIC_DELAY,
          NodeScreenTexts.LOGIC_THRESHOLD);

  private SlotEditor() {}

  static List<Control> controls(Font font, NodeView view, Box panel, int index, Face stuck) {
    List<Control> controls = new ArrayList<>();
    Optional<ChannelSlot> slot = view.menu().slot(index);
    controls.add(statusButton(font, view, panel, index, slot));
    controls.addAll(directionButtons(font, view, panel, index, setUpOf(slot, index, stuck)));
    controls.addAll(logicButtons(font, panel));
    return controls;
  }

  static Box cubeBox(Box panel) {
    return new Box(panel.x() + CONTROL_X, panel.y() + FACE_Y, CUBE_SIZE, CUBE_SIZE);
  }

  static boolean isEnabled(Optional<ChannelSlot> slot) {
    return slot.filter(ChannelSlot::active).isPresent();
  }

  /** Enabled or Disabled; the owner's click turns it the other way (answers 226 and 228). */
  private static Control statusButton(
      Font font, NodeView view, Box panel, int index, Optional<ChannelSlot> slot) {
    boolean enabled = isEnabled(slot);
    Component label =
        Component.translatable(enabled ? NodeScreenTexts.ENABLED : NodeScreenTexts.DISABLED);
    int width = ControlPainter.statusWidth(font, label);
    Box box = new Box(panel.x() + CONTROL_X, panel.y() + STATUS_Y, width, ROW_HEIGHT);
    Optional<NodeAction> action =
        view.editable()
            ? Optional.of(enabled ? NodeAction.disable(index) : NodeAction.enable(index))
            : Optional.empty();
    Control.Look look = enabled ? Control.Look.STATUS_ON : Control.Look.STATUS_OFF;
    return Control.button(box, look, label, action);
  }

  /**
   * The slot as the panel shows it: its own, or, never enabled, an input on the face the node
   * sticks to, as enabling it would make (answer 228).
   */
  static ChannelSlot setUpOf(Optional<ChannelSlot> slot, int index, Face stuck) {
    return slot.orElseGet(
        () -> new ChannelSlot(Direction.INPUT, stuck, ChannelColor.values()[index], false));
  }

  /** An enabled slot is locked; the owner sets up a disabled one (answer 237). */
  static boolean isSettable(NodeView view, ChannelSlot slot) {
    return view.editable() && !slot.active();
  }

  private static List<Control> directionButtons(
      Font font, NodeView view, Box panel, int index, ChannelSlot slot) {
    List<Control> buttons = new ArrayList<>();
    int x = panel.x() + CONTROL_X;
    for (Direction direction : Direction.values()) {
      Component label = directionOf(direction);
      Box box = button(font, label, x, panel.y() + DIRECTION_Y);
      ChannelSlot changed = withDirection(slot, direction);
      buttons.add(choice(box, label, direction == slot.direction(), view, index, changed));
      x = box.right();
    }
    return buttons;
  }

  static Component directionOf(Direction direction) {
    return Component.translatable(
        direction == Direction.INPUT ? NodeScreenTexts.INPUT : NodeScreenTexts.OUTPUT);
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
    if (!isSettable(view, changed)) {
      return Control.button(box, Control.Look.DISABLED, label, Optional.empty());
    }
    return Control.button(
        box, Control.Look.BUTTON, label, Optional.of(NodeAction.configure(index, changed)));
  }

  private static ChannelSlot withDirection(ChannelSlot slot, Direction direction) {
    return new ChannelSlot(direction, slot.face(), slot.channel(), slot.active());
  }

  static Box button(Font font, Component label, int x, int y) {
    return new Box(x, y, font.width(label) + BUTTON_PADDING, ROW_HEIGHT);
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
        y += ROW_HEIGHT + SPACING;
        box = button(font, label, x, y);
      }
      x = box.right() + SPACING;
      return box;
    }
  }
}
