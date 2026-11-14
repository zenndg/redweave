package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.block.ModuleMessages;
import io.github.marcelofabianov.redweave.client.ChannelColors;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.menu.ModuleMenu;
import io.github.marcelofabianov.redweave.menu.NodeScreenTexts;
import java.util.Optional;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * The footer of every Signal Module Screen: the network of the module, which its owner clicks to
 * choose another, or `No network`, and, for whoever does not own the plate, the lock (answers 378,
 * 400, 404 and 423).
 */
final class ModuleFooter {
  private static final int SEPARATOR_Y = 2;
  private static final int LINK_Y = 5;
  private static final int LINK_HEIGHT = 12;
  private static final int LINK_PADDING = 4;
  private static final int SWATCH = 6;
  private static final int SWATCH_Y = 3;
  private static final int SWATCH_GAP = 4;
  private static final int TEXT_Y = 2;
  private static final int MAX_NAME = 120;
  private static final int MAX_LOCK_TEXT = 180;
  private static final int LOCK_WIDTH = 7;
  private static final int LOCK_HEIGHT = 9;
  private static final int LOCK_GAP = 3;

  private ModuleFooter() {}

  static void draw(
      GuiGraphics graphics, Font font, Box footer, ModuleMenu menu, Mouse mouse, boolean owner) {
    int y = footer.y() + SEPARATOR_Y;
    graphics.fill(footer.x(), y, footer.right(), y + 1, ScreenColors.SEPARATOR);
    Optional<Box> link = linkBox(font, footer, menu);
    if (link.isPresent()) {
      drawLink(graphics, font, link.get(), menu, owner && mouse.hovers(link.get()));
    } else {
      Component none = Component.translatable(NodeScreenTexts.STATE_NO_NETWORK);
      graphics.drawString(
          font, none, footer.x(), footer.y() + LINK_Y + TEXT_Y, ScreenColors.LABEL, false);
    }
    if (!owner) {
      drawLock(graphics, font, footer);
    }
  }

  /** Where the network is, or none for a module with no network. */
  static Optional<Box> linkBox(Font font, Box footer, ModuleMenu menu) {
    if (menu.sync().networkColor().isEmpty()) {
      return Optional.empty();
    }
    String shown = FittedText.fit(font, Component.literal(menu.sync().networkName()), MAX_NAME);
    int width = LINK_PADDING + SWATCH + SWATCH_GAP + font.width(shown) + LINK_PADDING;
    return Optional.of(new Box(footer.x(), footer.y() + LINK_Y, width, LINK_HEIGHT));
  }

  private static void drawLink(
      GuiGraphics graphics, Font font, Box box, ModuleMenu menu, boolean hover) {
    graphics.blitSprite(
        hover ? NodeSprites.NETWORK_NAME_LINK_HOVER : NodeSprites.NETWORK_NAME_LINK,
        box.x(),
        box.y(),
        box.width(),
        box.height());
    ChannelColor color = menu.sync().networkColor().orElseThrow();
    int swatchX = box.x() + LINK_PADDING;
    int swatchY = box.y() + SWATCH_Y;
    graphics.fill(swatchX, swatchY, swatchX + SWATCH, swatchY + SWATCH, ChannelColors.argb(color));
    String shown = FittedText.fit(font, Component.literal(menu.sync().networkName()), MAX_NAME);
    graphics.drawString(
        font, shown, swatchX + SWATCH + SWATCH_GAP, box.y() + TEXT_Y, ScreenColors.TEXT, false);
  }

  private static void drawLock(GuiGraphics graphics, Font font, Box footer) {
    Component text = Component.translatable(ModuleMessages.NOT_OWNER);
    String shown = FittedText.fit(font, text, MAX_LOCK_TEXT);
    int x = footer.right() - font.width(shown);
    int y = footer.y() + LINK_Y + TEXT_Y;
    graphics.drawString(font, shown, x, y, ScreenColors.LABEL, false);
    graphics.blitSprite(
        NodeSprites.LOCK, x - LOCK_GAP - LOCK_WIDTH, y - 1, LOCK_WIDTH, LOCK_HEIGHT);
  }
}
