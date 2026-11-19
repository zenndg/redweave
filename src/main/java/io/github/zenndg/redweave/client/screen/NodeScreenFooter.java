package io.github.zenndg.redweave.client.screen;

import io.github.zenndg.redweave.menu.NodeScreenTexts;
import java.util.Optional;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * The bottom of the Node Screen, one line: how many channels are active on the left and, for
 * whoever is not the owner, the lock and who can change the node on the right (answers 85 and 347).
 * The meter lives in the channel panel (answer 346).
 */
final class NodeScreenFooter {
  private static final int SEPARATOR_Y = 2;
  private static final int TEXT_Y = 7;
  private static final int LOCK_WIDTH = 7;
  private static final int LOCK_HEIGHT = 9;
  private static final int LOCK_GAP = 4;
  private static final int LOCK_Y = 6;
  private static final int MAX_RIGHT_TEXT = 150;

  private Box rightText = new Box(0, 0, 0, 0);
  private Optional<Component> cutText = Optional.empty();

  void draw(GuiGraphics graphics, Font font, NodeView view, Box footer) {
    int y = footer.y() + SEPARATOR_Y;
    graphics.fill(footer.x(), y, footer.right(), y + 1, ScreenColors.SEPARATOR);
    graphics.drawString(
        font, inUse(view), footer.x(), footer.y() + TEXT_Y, ScreenColors.LABEL, false);
    if (view.editable()) {
      rightText = new Box(0, 0, 0, 0);
      cutText = Optional.empty();
      return;
    }
    drawLock(graphics, font, view, footer);
  }

  private void drawLock(GuiGraphics graphics, Font font, NodeView view, Box footer) {
    Component text = Component.translatable(NodeScreenTexts.READ_ONLY, view.menu().networkName());
    String shown = FittedText.fit(font, text, MAX_RIGHT_TEXT);
    int x = footer.right() - font.width(shown);
    rightText = new Box(x, footer.y() + TEXT_Y, font.width(shown), font.lineHeight);
    cutText = FittedText.isCut(font, text, MAX_RIGHT_TEXT) ? Optional.of(text) : Optional.empty();
    graphics.drawString(font, shown, x, footer.y() + TEXT_Y, ScreenColors.LABEL, false);
    graphics.blitSprite(
        NodeSprites.LOCK, x - LOCK_GAP - LOCK_WIDTH, footer.y() + LOCK_Y, LOCK_WIDTH, LOCK_HEIGHT);
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
