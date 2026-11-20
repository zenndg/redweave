package io.github.zenndg.redweave.client.screen;

import io.github.zenndg.redweave.domain.module.KeypadCode;
import io.github.zenndg.redweave.domain.module.KeypadEntry;
import io.github.zenndg.redweave.menu.KeypadCodes;
import io.github.zenndg.redweave.menu.KeypadEntryMenu;
import io.github.zenndg.redweave.menu.KeypadScreenTexts;
import java.util.Optional;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/**
 * The keypad any player opens with an empty hand: 3 × 4 keys, the code as `•`, and what came of the
 * last try, `Access granted` in green, a miss or the wait in red (answers 662 to 665 and 914). The
 * server checks the code; the client only types it (rule 16).
 */
public final class KeypadEntryScreen extends AbstractContainerScreen<KeypadEntryMenu> {
  private static final int WIDTH = 130;
  private static final int HEIGHT = 150;
  private static final int PAD_X = (WIDTH - KeypadPad.WIDTH) / 2;
  private static final int TITLE_Y = 8;
  private static final int PAD_Y = 22;
  private static final int RESULT_Y = PAD_Y + KeypadPad.HEIGHT + 8;
  private static final int GRANTED = 0xFF6BD48F;

  private final KeypadPad pad = new KeypadPad();

  public KeypadEntryScreen(KeypadEntryMenu menu, Inventory inventory, Component title) {
    super(menu, inventory, title);
    imageWidth = WIDTH;
    imageHeight = HEIGHT;
  }

  @Override
  protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
    graphics.blitSprite(NodeSprites.WINDOW, leftPos, topPos, imageWidth, imageHeight);
    int titleX = leftPos + (WIDTH - font.width(title)) / 2;
    graphics.drawString(font, title, titleX, topPos + TITLE_Y, ScreenColors.TEXT, false);
    pad.draw(
        graphics, font, leftPos + PAD_X, topPos + PAD_Y, new Mouse(mouseX, mouseY), this::enter);
    result()
        .ifPresent(text -> {
          int x = leftPos + (WIDTH - font.width(text)) / 2;
          graphics.drawString(font, text, x, topPos + RESULT_Y, colorOf(), false);
        });
  }

  /** The line under the keys, from the last try (answers 663 to 665). */
  private Optional<Component> result() {
    return menu.result()
        .flatMap(kind ->
            switch (kind) {
              case GRANTED -> Optional.of(Component.translatable(KeypadScreenTexts.GRANTED));
              case WRONG -> Optional.of(Component.translatable(KeypadScreenTexts.WRONG));
              case WAIT ->
                  Optional.of(Component.translatable(KeypadScreenTexts.WAIT, menu.waitSeconds()));
              default -> Optional.empty();
            });
  }

  private int colorOf() {
    return menu.result().filter(KeypadEntry.Kind.GRANTED::equals).isPresent()
        ? GRANTED
        : ScreenColors.REDSTONE;
  }

  private void enter(KeypadCode code) {
    if (minecraft != null && minecraft.gameMode != null) {
      minecraft.gameMode.handleInventoryButtonClick(menu.containerId, KeypadCodes.encode(code));
    }
  }

  @Override
  protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {}

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    return pad.click(leftPos + PAD_X, topPos + PAD_Y, mouseX, mouseY, this::enter)
        || super.mouseClicked(mouseX, mouseY, button);
  }
}
