package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.client.ChannelColors;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.menu.NodeMenu;
import io.github.marcelofabianov.redweave.menu.NodeScreenTexts;
import java.util.Optional;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * The line under the title: the name of the node, which its owner renames with a click (answer
 * 204), and the network on the right, whose click switches networks (answers 141 and 208).
 */
final class NodeHeader {
  static final int Y = 21;
  private static final int LEFT = 10;
  private static final int RIGHT = 10;
  private static final int HEIGHT = 12;
  private static final int TEXT_Y = 2;
  private static final int ICON_GAP = 3;
  private static final int ICON_SIZE = 8;
  private static final int ICON_Y = 2;
  private static final int LINK_PADDING = 4;
  private static final int SWATCH_SIZE = 6;
  private static final int SWATCH_Y = 3;
  private static final int SWATCH_GAP = 4;
  private static final int MAX_LINK_TEXT = 120;
  private static final int FIELD_WIDTH = 110;

  private NodeHeader() {}

  /** Where the name is, with the pencil for the owner. */
  static Box nameBox(Font font, int left, int top, NodeMenu menu, boolean owner) {
    int width = font.width(menu.nodeName()) + (owner ? ICON_GAP + ICON_SIZE : 0);
    return new Box(left + LEFT, top + Y, width, HEIGHT);
  }

  /** Where the text field is while the owner renames the node. */
  static Box fieldBox(int left, int top) {
    return new Box(left + LEFT, top + Y, FIELD_WIDTH, HEIGHT);
  }

  /** Where the network is, or none for a node with no network. */
  static Optional<Box> linkBox(Font font, int left, int top, int width, NodeMenu menu) {
    if (menu.networkColor().isEmpty()) {
      return Optional.empty();
    }
    String shown = FittedText.fit(font, Component.literal(menu.networkName()), MAX_LINK_TEXT);
    int linkWidth = LINK_PADDING + SWATCH_SIZE + SWATCH_GAP + font.width(shown) + LINK_PADDING;
    return Optional.of(new Box(left + width - RIGHT - linkWidth, top + Y, linkWidth, HEIGHT));
  }

  static void drawName(
      GuiGraphics graphics, Font font, int left, int top, NodeMenu menu, boolean owner) {
    Box box = nameBox(font, left, top, menu, owner);
    graphics.drawString(
        font, menu.nodeName(), box.x(), box.y() + TEXT_Y, ScreenColors.LABEL, false);
    if (owner) {
      graphics.blitSprite(
          NodeSprites.ICON_EDIT, box.right() - ICON_SIZE, box.y() + ICON_Y, ICON_SIZE, ICON_SIZE);
    }
  }

  static void drawLink(
      GuiGraphics graphics, Font font, int left, int top, int width, NodeMenu menu, NodeView view) {
    Optional<Box> found = linkBox(font, left, top, width, menu);
    Optional<ChannelColor> color = menu.networkColor();
    if (found.isEmpty() || color.isEmpty()) {
      return;
    }
    Box box = found.get();
    boolean hover = view.hovers(box);
    graphics.blitSprite(
        hover ? NodeSprites.NETWORK_NAME_LINK_HOVER : NodeSprites.NETWORK_NAME_LINK,
        box.x(),
        box.y(),
        box.width(),
        box.height());
    int swatchX = box.x() + LINK_PADDING;
    graphics.fill(
        swatchX,
        box.y() + SWATCH_Y,
        swatchX + SWATCH_SIZE,
        box.y() + SWATCH_Y + SWATCH_SIZE,
        ChannelColors.argb(color.get()));
    String shown = FittedText.fit(font, Component.literal(menu.networkName()), MAX_LINK_TEXT);
    int textX = swatchX + SWATCH_SIZE + SWATCH_GAP;
    graphics.drawString(font, shown, textX, box.y() + TEXT_Y, ScreenColors.TEXT, false);
  }

  static Component renameTooltip() {
    return Component.translatable(NodeScreenTexts.RENAME_TOOLTIP);
  }

  static Component switchTooltip() {
    return Component.translatable(NodeScreenTexts.SWITCH_NETWORK_TOOLTIP);
  }
}
