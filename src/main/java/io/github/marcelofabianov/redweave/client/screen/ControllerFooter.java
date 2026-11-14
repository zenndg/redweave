package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.domain.network.NodeState;
import io.github.marcelofabianov.redweave.menu.ControllerMenu;
import io.github.marcelofabianov.redweave.menu.ControllerScreenRows.ChannelLine;
import io.github.marcelofabianov.redweave.menu.ControllerScreenRows.Header;
import io.github.marcelofabianov.redweave.menu.ControllerScreenTexts;
import java.util.List;
import java.util.stream.IntStream;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * The footer of the Controller Screen, in two lines, as its sheet (answer 373). On the left, the
 * channels in use and, below, the owner, or the lock and the notice for anyone else (answer 22). On
 * the right, the nodes Unloaded, in amber, and the Signal Remotes on, then the nodes Out of range,
 * in red, even at zero and without the word "nodes" to fit (answers 219, 236 and 364).
 */
final class ControllerFooter {
  private static final int SECOND_LINE = 12;
  private static final int DOT_GAP = 4;
  private static final int DOT_Y = 2;
  private static final String SEPARATOR = " · ";

  private ControllerFooter() {}

  /** One count of the right side: its text and the color of its dot and text. */
  private record Count(Component text, int color) {}

  static void draw(GuiGraphics graphics, Font font, Box footer, ControllerMenu menu) {
    long inUse = menu.channels().stream().filter(ChannelLine::inUse).count();
    Component channels =
        Component.translatable(
            ControllerScreenTexts.CHANNELS_IN_USE, inUse, menu.channels().size());
    graphics.drawString(font, channels, footer.x(), footer.y(), ScreenColors.LABEL, false);
    Count unloaded =
        new Count(
            Component.translatable(
                ControllerScreenTexts.UNLOADED, countOf(menu, NodeState.UNLOADED)),
            ScreenColors.UNLOADED);
    Count remotes =
        new Count(
            Component.translatable(ControllerScreenTexts.REMOTES_ON, menu.remotesOn()),
            ScreenColors.ONLINE);
    Count outOfRange =
        new Count(
            Component.translatable(
                ControllerScreenTexts.OUT_OF_RANGE, countOf(menu, NodeState.OUT_OF_RANGE)),
            ScreenColors.OUT_OF_RANGE);
    drawRight(graphics, font, footer.right(), footer.y(), List.of(unloaded, remotes));
    Count modules =
        new Count(
            Component.translatable(ControllerScreenTexts.MODULES_ON, menu.modulesOn()),
            ScreenColors.ONLINE);
    drawRight(
        graphics, font, footer.right(), footer.y() + SECOND_LINE, List.of(outOfRange, modules));
    drawOwner(graphics, font, footer.x(), footer.y() + SECOND_LINE, menu.header());
  }

  private static int countOf(ControllerMenu menu, NodeState state) {
    return (int)
        IntStream.range(0, menu.nodes().size()).filter(row -> menu.nodeState(row) == state).count();
  }

  /** Draws the counts ending at the right edge, apart by a dot, each with its own dot. */
  private static void drawRight(
      GuiGraphics graphics, Font font, int right, int y, List<Count> counts) {
    int x = right - widthOf(font, counts);
    for (int index = 0; index < counts.size(); index++) {
      if (index > 0) {
        graphics.drawString(font, SEPARATOR, x, y, ScreenColors.LABEL, false);
        x += font.width(SEPARATOR);
      }
      Count count = counts.get(index);
      Dots.draw(graphics, x, y + DOT_Y, count.color());
      int textX = x + Dots.SIZE + DOT_GAP;
      graphics.drawString(font, count.text(), textX, y, count.color(), false);
      x = textX + font.width(count.text());
    }
  }

  private static int widthOf(Font font, List<Count> counts) {
    int width = font.width(SEPARATOR) * (counts.size() - 1);
    for (Count count : counts) {
      width += Dots.SIZE + DOT_GAP + font.width(count.text());
    }
    return width;
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
