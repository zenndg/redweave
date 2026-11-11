package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.domain.network.NodeState;
import io.github.marcelofabianov.redweave.menu.NodeScreenTexts;
import java.util.Optional;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * The colored state on the top right, as `● Online` (telas.md and answer 29). The network has its
 * own link under the title, in every state (answer 208).
 */
final class StatePill {
  private static final int HEIGHT = 13;
  private static final int DOT_X = 5;
  private static final int DOT_Y = 4;
  private static final int TEXT_X = 13;
  private static final int TEXT_Y = 3;
  private static final int RIGHT_PADDING = 6;
  private static final int MAX_TEXT = 120;

  private StatePill() {}

  /** Draws the pill ending at the right edge given, and gives back where it is. */
  static Box draw(GuiGraphics graphics, Font font, int right, int y, Optional<NodeState> state) {
    return draw(graphics, font, right, y, textOf(state), colorOf(state));
  }

  /** A pill with any text, as `● Online · 6 nodes` of the Controller Screen. */
  static Box draw(GuiGraphics graphics, Font font, int right, int y, Component text, int color) {
    String shown = FittedText.fit(font, text, MAX_TEXT);
    int width = TEXT_X + font.width(shown) + RIGHT_PADDING;
    Box box = new Box(right - width, y, width, HEIGHT);
    graphics.blitSprite(NodeSprites.STATE_PILL, box.x(), box.y(), box.width(), box.height());
    Dots.draw(graphics, box.x() + DOT_X, box.y() + DOT_Y, color);
    graphics.drawString(font, shown, box.x() + TEXT_X, box.y() + TEXT_Y, color, false);
    return box;
  }

  static Component textOf(Optional<NodeState> state) {
    return switch (state.orElse(NodeState.NO_NETWORK)) {
      case ONLINE -> Component.translatable(NodeScreenTexts.STATE_ONLINE);
      case UNLOADED -> Component.translatable(NodeScreenTexts.STATE_UNLOADED);
      case OUT_OF_RANGE -> Component.translatable(NodeScreenTexts.STATE_OUT_OF_RANGE);
      case NO_NETWORK -> Component.translatable(NodeScreenTexts.STATE_NO_NETWORK);
      case OFFLINE -> Component.translatable(NodeScreenTexts.STATE_OFFLINE);
    };
  }

  static int colorOf(Optional<NodeState> state) {
    return switch (state.orElse(NodeState.NO_NETWORK)) {
      case ONLINE -> ScreenColors.ONLINE;
      case UNLOADED -> ScreenColors.UNLOADED;
      case OUT_OF_RANGE -> ScreenColors.OUT_OF_RANGE;
      case NO_NETWORK -> ScreenColors.NO_NETWORK;
      case OFFLINE -> ScreenColors.OFFLINE;
    };
  }
}
