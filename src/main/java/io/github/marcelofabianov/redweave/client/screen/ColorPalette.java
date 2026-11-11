package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.client.ChannelColors;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import java.util.Optional;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

/**
 * The palette that floats under the color of the header for the owner: the 16 colors in two rows of
 * 8, in the order of the wool, with the color of the network highlighted (answer 212).
 */
final class ColorPalette {
  private static final int PER_ROW = 8;
  private static final int ROWS = 2;
  private static final int SWATCH = 12;
  private static final int STEP = 13;
  private static final int PADDING = 4;
  private static final int INSET = 2;
  private static final int WIDTH = 2 * PADDING + PER_ROW * STEP - (STEP - SWATCH);
  private static final int HEIGHT = 2 * PADDING + ROWS * STEP - (STEP - SWATCH);

  private ColorPalette() {}

  /** The palette opens right under the color square. */
  static Box box(Box colorSquare) {
    return new Box(colorSquare.x(), colorSquare.bottom(), WIDTH, HEIGHT);
  }

  static void draw(GuiGraphics graphics, Box palette, ChannelColor current, Mouse mouse) {
    graphics.blitSprite(
        ControllerSprites.PALETTE_PANEL,
        palette.x(),
        palette.y(),
        palette.width(),
        palette.height());
    for (ChannelColor color : ChannelColor.values()) {
      Box swatch = swatchBox(palette, color);
      graphics.blitSprite(
          spriteOf(color == current, mouse.hovers(swatch)),
          swatch.x(),
          swatch.y(),
          swatch.width(),
          swatch.height());
      graphics.fill(
          swatch.x() + INSET,
          swatch.y() + INSET,
          swatch.right() - INSET,
          swatch.bottom() - INSET,
          ChannelColors.argb(color));
    }
  }

  private static ResourceLocation spriteOf(boolean chosen, boolean hover) {
    if (chosen) {
      return NodeSprites.SWATCH_SELECTED;
    }
    return hover ? NodeSprites.SWATCH_HOVER : NodeSprites.SWATCH;
  }

  static Optional<ChannelColor> colorAt(Box palette, double mouseX, double mouseY) {
    for (ChannelColor color : ChannelColor.values()) {
      if (swatchBox(palette, color).contains(mouseX, mouseY)) {
        return Optional.of(color);
      }
    }
    return Optional.empty();
  }

  private static Box swatchBox(Box palette, ChannelColor color) {
    int column = color.ordinal() % PER_ROW;
    int row = color.ordinal() / PER_ROW;
    return new Box(
        palette.x() + PADDING + column * STEP, palette.y() + PADDING + row * STEP, SWATCH, SWATCH);
  }
}
