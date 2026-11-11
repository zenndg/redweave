package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.menu.NodeAction;
import java.util.Optional;
import net.minecraft.network.chat.Component;

/** One clickable thing of the channel panel: where it is, how it looks and what it asks. */
record Control(
    Box box,
    Look look,
    Optional<Component> label,
    Optional<NodeAction> action,
    Optional<Component> tooltip,
    int color) {
  private static final int NO_COLOR = 0;

  /** How a control is drawn. */
  enum Look {
    BUTTON,
    CHOSEN,
    DISABLED,
    STATUS_ON,
    STATUS_OFF
  }

  static Control button(Box box, Look look, Component label, Optional<NodeAction> action) {
    return new Control(box, look, Optional.of(label), action, Optional.empty(), NO_COLOR);
  }

  Control withTooltip(Component text) {
    return new Control(box, look, label, action, Optional.of(text), color);
  }

  boolean clickable() {
    return action.isPresent();
  }
}
