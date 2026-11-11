package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.menu.NodeScreenTexts;
import java.util.Optional;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * The bottom of the Node Screen: the 15-bar meter of the chosen slot, and how many slots are in
 * use, or, for whoever is not the owner, the lock and who can change the node (answer 85).
 */
final class NodeScreenFooter {
  private static final int SEPARATOR_Y = 6;
  private static final int TEXT_Y = 17;
  private static final int METER_X = 42;
  private static final int BASELINE = 26;
  private static final int NUMBER_GAP = 6;
  private static final int LOCK_WIDTH = 7;
  private static final int LOCK_HEIGHT = 9;
  private static final int LOCK_GAP = 4;
  private static final int LOCK_Y = 16;
  private static final int MAX_RIGHT_TEXT = 150;

  private Box rightText = new Box(0, 0, 0, 0);
  private Optional<Component> cutText = Optional.empty();

  void draw(GuiGraphics graphics, Font font, NodeView view, Box footer, int index) {
    int y = footer.y() + SEPARATOR_Y;
    graphics.fill(footer.x(), y, footer.right(), y + 1, ScreenColors.SEPARATOR);
    Component signal = Component.translatable(NodeScreenTexts.SIGNAL);
    graphics.drawString(font, signal, footer.x(), footer.y() + TEXT_Y, ScreenColors.LABEL, false);
    int strength = SlotEditor.isEnabled(view.menu().slot(index)) ? view.menu().signal(index) : 0;
    SignalMeter.draw(graphics, footer.x() + METER_X, footer.y() + BASELINE, strength);
    int numberX = footer.x() + METER_X + SignalMeter.WIDTH + NUMBER_GAP;
    graphics.drawString(
        font, String.valueOf(strength), numberX, footer.y() + TEXT_Y, ScreenColors.TEXT, false);
    drawRight(graphics, font, view, footer);
  }

  private void drawRight(GuiGraphics graphics, Font font, NodeView view, Box footer) {
    Component text =
        view.editable()
            ? inUse(view)
            : Component.translatable(NodeScreenTexts.READ_ONLY, view.menu().networkName());
    String shown = FittedText.fit(font, text, MAX_RIGHT_TEXT);
    int x = footer.right() - font.width(shown);
    rightText = new Box(x, footer.y() + TEXT_Y, font.width(shown), font.lineHeight);
    cutText = FittedText.isCut(font, text, MAX_RIGHT_TEXT) ? Optional.of(text) : Optional.empty();
    graphics.drawString(font, shown, x, footer.y() + TEXT_Y, ScreenColors.LABEL, false);
    if (!view.editable()) {
      graphics.blitSprite(
          NodeSprites.LOCK,
          x - LOCK_GAP - LOCK_WIDTH,
          footer.y() + LOCK_Y,
          LOCK_WIDTH,
          LOCK_HEIGHT);
    }
  }

  /** How many of the 16 channels of the node are enabled, as `3 of 16 channels active`. */
  private static Component inUse(NodeView view) {
    int count = view.menu().slotCount();
    int enabled = 0;
    for (int index = 0; index < count; index++) {
      enabled += SlotEditor.isEnabled(view.menu().slot(index)) ? 1 : 0;
    }
    return Component.translatable(NodeScreenTexts.CHANNELS_ACTIVE, enabled, count);
  }

  /** A text cut with an ellipsis shows whole on hover (telas.md). */
  Optional<Component> tooltipAt(NodeView view) {
    return rightText.contains(view.mouseX(), view.mouseY()) ? cutText : Optional.empty();
  }
}
