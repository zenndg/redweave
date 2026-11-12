package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.domain.network.NetworkState;
import io.github.marcelofabianov.redweave.menu.TerminalAction;
import io.github.marcelofabianov.redweave.menu.TerminalScreenTexts;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * The bar under the list: only the actions that hold for the state of the selected network (answer
 * 252). Clear nodes and Delete wait for a second click, shown on the button itself (answer 255).
 */
final class TerminalButtons {
  private static final int HEIGHT = 14;
  private static final int PADDING = 6;
  private static final int GAP = 4;
  private static final int TEXT_Y = 3;

  private Optional<TerminalAction> armed = Optional.empty();

  /** The actions of a state, in the order of the bar (answers 131 to 133, 158 and 166). */
  static List<TerminalAction> actionsOf(NetworkState state) {
    return switch (state) {
      case ONLINE ->
          List.of(TerminalAction.OPEN, TerminalAction.TURN_OFF, TerminalAction.CLEAR_NODES);
      case OFFLINE -> List.of(TerminalAction.TURN_ON, TerminalAction.CLEAR_NODES);
      case UNPLACED -> List.of(TerminalAction.CLEAR_NODES, TerminalAction.DELETE);
    };
  }

  /** A button and where it is drawn now. */
  record Placed(TerminalAction action, Box box) {}

  List<Placed> placed(Font font, int x, int y, NetworkState state) {
    List<Placed> placed = new ArrayList<>();
    int left = x;
    for (TerminalAction action : actionsOf(state)) {
      int width = font.width(textOf(action)) + 2 * PADDING;
      placed.add(new Placed(action, new Box(left, y, width, HEIGHT)));
      left += width + GAP;
    }
    return placed;
  }

  void draw(GuiGraphics graphics, Font font, List<Placed> buttons, Mouse mouse) {
    for (Placed button : buttons) {
      Box box = button.box();
      graphics.blitSprite(
          spriteOf(button.action(), mouse.hovers(box)), box.x(), box.y(), box.width(), HEIGHT);
      Component text = textOf(button.action());
      int textX = box.x() + (box.width() - font.width(text)) / 2;
      int color = isDanger(button.action()) ? ScreenColors.TEXT : ScreenColors.ON_COPPER;
      graphics.drawString(font, text, textX, box.y() + TEXT_Y, color, false);
    }
  }

  /**
   * A click on a button: the action to send now, or none while a dangerous one waits for its second
   * click (answers 133 and 166).
   */
  Optional<TerminalAction> click(TerminalAction action) {
    if (!isDanger(action) || armed.filter(action::equals).isPresent()) {
      armed = Optional.empty();
      return Optional.of(action);
    }
    armed = Optional.of(action);
    return Optional.empty();
  }

  /** Any other click lets a waiting action go. */
  void disarm() {
    armed = Optional.empty();
  }

  private Component textOf(TerminalAction action) {
    boolean waiting = armed.filter(action::equals).isPresent();
    return Component.translatable(
        switch (action) {
          case OPEN -> TerminalScreenTexts.OPEN;
          case TURN_OFF -> TerminalScreenTexts.TURN_OFF;
          case TURN_ON -> TerminalScreenTexts.TURN_ON;
          case CLEAR_NODES ->
              waiting ? TerminalScreenTexts.CONFIRM_CLEAR : TerminalScreenTexts.CLEAR_NODES;
          case DELETE -> waiting ? TerminalScreenTexts.CONFIRM_DELETE : TerminalScreenTexts.DELETE;
        });
  }

  private ResourceLocation spriteOf(TerminalAction action, boolean hover) {
    if (armed.filter(action::equals).isPresent()) {
      return TerminalSprites.BUTTON_CONFIRM;
    }
    if (isDanger(action)) {
      return hover ? TerminalSprites.BUTTON_DANGER_HOVER : TerminalSprites.BUTTON_DANGER;
    }
    return hover ? NodeSprites.BUTTON_SELECTED_HOVER : NodeSprites.BUTTON_SELECTED;
  }

  private static boolean isDanger(TerminalAction action) {
    return action == TerminalAction.CLEAR_NODES || action == TerminalAction.DELETE;
  }
}
