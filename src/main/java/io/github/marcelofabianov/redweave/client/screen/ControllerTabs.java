package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.menu.ControllerScreenTexts;
import java.util.Optional;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/** The tabs of the Controller Screen, with the Log of issue #90 (answers 214 and 215). */
final class ControllerTabs {
  private static final int LEFT = 8;
  private static final int Y = 28;

  /** Wide enough for `Components` and `Componentes` (answer 856). */
  private static final int WIDTH = 72;

  private static final int HEIGHT = 18;
  private static final int GAP = 2;
  private static final int TEXT_Y = 5;

  /** The tabs, in the order of the screen. */
  enum Tab {
    NODES(ControllerScreenTexts.TAB_NODES),
    SIGNALS(ControllerScreenTexts.TAB_SIGNALS),
    ACCESS(ControllerScreenTexts.TAB_ACCESS),
    LOG(ControllerScreenTexts.TAB_LOG);

    private final String key;

    Tab(String key) {
      this.key = key;
    }
  }

  private ControllerTabs() {}

  static void draw(GuiGraphics graphics, Font font, int left, int top, Tab selected, Mouse mouse) {
    for (Tab tab : Tab.values()) {
      Box box = boxOf(left, top, tab);
      graphics.blitSprite(
          spriteOf(tab == selected, mouse.hovers(box)),
          box.x(),
          box.y(),
          box.width(),
          box.height());
      Component text = Component.translatable(tab.key);
      int x = box.x() + (box.width() - font.width(text)) / 2;
      int color = tab == selected ? ScreenColors.TEXT : ScreenColors.LABEL;
      graphics.drawString(font, text, x, box.y() + TEXT_Y, color, false);
    }
  }

  private static ResourceLocation spriteOf(boolean selected, boolean hover) {
    if (selected) {
      return NodeSprites.TAB_SELECTED;
    }
    return hover ? ControllerSprites.TAB_HOVER : ControllerSprites.TAB;
  }

  static Optional<Tab> tabAt(int left, int top, double mouseX, double mouseY) {
    for (Tab tab : Tab.values()) {
      if (boxOf(left, top, tab).contains(mouseX, mouseY)) {
        return Optional.of(tab);
      }
    }
    return Optional.empty();
  }

  private static Box boxOf(int left, int top, Tab tab) {
    return new Box(left + LEFT + tab.ordinal() * (WIDTH + GAP), top + Y, WIDTH, HEIGHT);
  }
}
