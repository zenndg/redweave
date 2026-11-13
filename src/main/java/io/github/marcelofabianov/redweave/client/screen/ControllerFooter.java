package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.domain.network.NodeState;
import io.github.marcelofabianov.redweave.menu.ControllerMenu;
import io.github.marcelofabianov.redweave.menu.ControllerScreenRows.ChannelLine;
import io.github.marcelofabianov.redweave.menu.ControllerScreenRows.Header;
import io.github.marcelofabianov.redweave.menu.ControllerScreenTexts;
import java.util.stream.IntStream;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * The footer of the Controller Screen, in two lines: the channels in use and how many nodes are
 * Unloaded, in amber, and Out of range, in red, even at zero and without the word "nodes" to fit
 * the line (answers 219 and 236); then the owner, or the lock and the notice for anyone else
 * (telas.md and answer 22).
 */
final class ControllerFooter {
  private static final int SECOND_LINE = 12;
  private static final int COUNT_GAP = 12;
  private static final int DOT_GAP = 4;
  private static final int DOT_Y = 2;

  private ControllerFooter() {}

  static void draw(GuiGraphics graphics, Font font, Box footer, ControllerMenu menu) {
    long inUse = menu.channels().stream().filter(ChannelLine::inUse).count();
    Component channels =
        Component.translatable(
            ControllerScreenTexts.CHANNELS_IN_USE, inUse, menu.channels().size());
    graphics.drawString(font, channels, footer.x(), footer.y(), ScreenColors.LABEL, false);
    int x = footer.x() + font.width(channels) + COUNT_GAP;
    x = drawCount(graphics, font, x, footer.y(), countOf(menu, NodeState.UNLOADED), true);
    x = drawCount(graphics, font, x, footer.y(), countOf(menu, NodeState.OUT_OF_RANGE), false);
    drawRemotes(graphics, font, x, footer.y(), menu.remotesOn());
    drawOwner(graphics, font, footer.x(), footer.y() + SECOND_LINE, menu.header());
  }

  private static int countOf(ControllerMenu menu, NodeState state) {
    return (int)
        IntStream.range(0, menu.nodes().size()).filter(row -> menu.nodeState(row) == state).count();
  }

  /** Draws one count with its dot and gives back where the next one starts. */
  private static int drawCount(
      GuiGraphics graphics, Font font, int x, int y, int count, boolean unloaded) {
    int color = unloaded ? ScreenColors.UNLOADED : ScreenColors.OUT_OF_RANGE;
    String key = keyOf(unloaded);
    Component text = Component.translatable(key, count);
    Dots.draw(graphics, x, y + DOT_Y, color);
    int textX = x + Dots.SIZE + DOT_GAP;
    graphics.drawString(font, text, textX, y, color, false);
    return textX + font.width(text) + COUNT_GAP;
  }

  /** The Signal Remotes that count now, with a dot in the color of a signal that is on (364). */
  private static void drawRemotes(GuiGraphics graphics, Font font, int x, int y, int count) {
    Component text = Component.translatable(ControllerScreenTexts.REMOTES_ON, count);
    Dots.draw(graphics, x, y + DOT_Y, ScreenColors.ONLINE);
    graphics.drawString(font, text, x + Dots.SIZE + DOT_GAP, y, ScreenColors.LABEL, false);
  }

  private static String keyOf(boolean unloaded) {
    return unloaded ? ControllerScreenTexts.UNLOADED : ControllerScreenTexts.OUT_OF_RANGE;
  }

  private static void drawOwner(GuiGraphics graphics, Font font, int x, int y, Header header) {
    if (header.viewerIsOwner()) {
      Component owner = Component.translatable(ControllerScreenTexts.OWNER_LINE, header.owner());
      graphics.drawString(font, owner, x, y, ScreenColors.LABEL, false);
      return;
    }
    Locks.draw(graphics, x, y);
    Component notice = Component.translatable(ControllerScreenTexts.READ_ONLY);
    graphics.drawString(font, notice, x + Locks.WIDTH + Locks.GAP, y, ScreenColors.LABEL, false);
  }
}
