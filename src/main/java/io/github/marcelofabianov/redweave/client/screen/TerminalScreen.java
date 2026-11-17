package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.client.screen.TerminalList.Selection;
import io.github.marcelofabianov.redweave.config.NeoForgeNetworkLimits;
import io.github.marcelofabianov.redweave.domain.network.NetworkState;
import io.github.marcelofabianov.redweave.menu.TerminalAction;
import io.github.marcelofabianov.redweave.menu.TerminalMenu;
import io.github.marcelofabianov.redweave.menu.TerminalScreenCodes;
import io.github.marcelofabianov.redweave.menu.TerminalScreenTexts;
import io.github.marcelofabianov.redweave.network.TerminalRenamed;
import java.util.List;
import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * The screen of the Network Terminal: terminal-de-rede.md and _docs/imagens/telas/
 * tela-do-terminal.png. It only shows what the server sends and asks for actions; the server
 * decides (rule 16). A click selects a network, the bar under the list holds its actions, and a
 * double click on a name renames it (answers 252 and 255).
 */
public final class TerminalScreen extends AbstractContainerScreen<TerminalMenu> {
  /** The width of the Controller and the modules, so the count of components fits (857). */
  private static final int WIDTH = 340;

  private static final int HEIGHT = 176;
  private static final int SIDE = 8;
  private static final int TITLE_Y = 8;
  private static final int LIST_Y = 22;
  private static final int LIST_HEIGHT = 106;
  private static final Box LIST = new Box(SIDE, LIST_Y, WIDTH - 2 * SIDE, LIST_HEIGHT);
  private static final int BUTTONS_Y = 134;
  private static final int HINT_Y = 158;
  private static final int SINGLE = 1;

  private final TerminalList list = new TerminalList();
  private final TerminalButtons buttons = new TerminalButtons();
  private final NameField name = new NameField();
  private final DoubleClick clicks = new DoubleClick(Util::getMillis);
  private Optional<Integer> selected = Optional.empty();
  private int renaming;

  public TerminalScreen(TerminalMenu menu, Inventory inventory, Component title) {
    super(menu, inventory, title);
    imageWidth = WIDTH;
    imageHeight = HEIGHT;
  }

  @Override
  protected void init() {
    super.init();
    name.attach(font, listArea(), this::addRenderableWidget);
  }

  @Override
  protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
    selected = selected.filter(row -> row < menu.networks().size());
    graphics.blitSprite(NodeSprites.WINDOW, leftPos, topPos, WIDTH, HEIGHT);
    drawHeader(graphics);
    Box area = listArea();
    graphics.blitSprite(NodeSprites.PANEL, area.x(), area.y(), area.width(), area.height());
    list.draw(graphics, font, area, menu.networks(), new Selection(selected));
    buttons.draw(graphics, font, buttonsNow(), new Mouse(mouseX, mouseY));
    Component hint = Component.translatable(TerminalScreenTexts.HINT);
    graphics.drawString(font, hint, leftPos + SIDE, topPos + HINT_Y, ScreenColors.LABEL, false);
    name.drawBackground(graphics);
  }

  private void drawHeader(GuiGraphics graphics) {
    graphics.drawString(font, title, leftPos + SIDE, topPos + TITLE_Y, ScreenColors.TEXT, false);
    int count = menu.networks().size();
    String key = count == SINGLE ? TerminalScreenTexts.COUNT_ONE : TerminalScreenTexts.COUNT_MANY;
    Component text = Component.translatable(key, count);
    int x = leftPos + WIDTH - SIDE - font.width(text);
    graphics.drawString(font, text, x, topPos + TITLE_Y, ScreenColors.LABEL, false);
  }

  /** Every text is drawn in renderBg, so the game does not draw its own labels on top. */
  @Override
  protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {}

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    if (name.editing() && !name.box().contains(mouseX, mouseY)) {
      name.cancel();
    }
    Optional<TerminalButtons.Placed> pressed =
        buttonsNow().stream().filter(placed -> placed.box().contains(mouseX, mouseY)).findFirst();
    if (pressed.isPresent()) {
      buttons.click(pressed.get().action()).ifPresent(this::send);
      return true;
    }
    buttons.disarm();
    Optional<Integer> row = list.rowAt(listArea(), menu.networks().size(), mouseX, mouseY);
    if (row.isEmpty()) {
      return super.mouseClicked(mouseX, mouseY, button);
    }
    select(row.get(), mouseX, mouseY);
    return true;
  }

  /** A click selects; a double click on the name renames, for the owner (answers 222 and 252). */
  private void select(int row, double mouseX, double mouseY) {
    selected = Optional.of(row);
    Optional<Box> nameBox = list.nameBox(listArea(), row);
    if (nameBox.isEmpty() || !nameBox.get().contains(mouseX, mouseY)) {
      clicks.reset();
      return;
    }
    if (clicks.click(row)) {
      renaming = row;
      name.moveTo(nameBox.get());
      name.start(
          menu.networks().get(row).name(),
          NameField.limit(NeoForgeNetworkLimits::networkNameLength),
          this::setFocused);
    }
  }

  private void send(TerminalAction action) {
    if (selected.isEmpty() || minecraft == null || minecraft.gameMode == null) {
      return;
    }
    TerminalScreenCodes.Click click = new TerminalScreenCodes.Click(selected.get(), action);
    minecraft.gameMode.handleInventoryButtonClick(
        menu.containerId, TerminalScreenCodes.button(click));
  }

  /** Enter saves the name, Esc cancels it, and no key closes the screen while typing. */
  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    if (!name.editing()) {
      return super.keyPressed(keyCode, scanCode, modifiers);
    }
    name.keyPressed(
        keyCode,
        scanCode,
        modifiers,
        text -> PacketDistributor.sendToServer(new TerminalRenamed(menu.pos(), renaming, text)));
    return true;
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
    if (listArea().contains(mouseX, mouseY)) {
      name.cancel();
      list.scroll(scrollY, listArea(), menu.networks().size());
      return true;
    }
    return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
  }

  /** The bar of the selected network; none without a selection. */
  private List<TerminalButtons.Placed> buttonsNow() {
    return selected
        .map(row -> TerminalList.stateOf(menu.networks().get(row).state()))
        .map(this::buttonsOf)
        .orElse(List.of());
  }

  private List<TerminalButtons.Placed> buttonsOf(NetworkState state) {
    return buttons.placed(font, leftPos + SIDE, topPos + BUTTONS_Y, state);
  }

  private Box listArea() {
    return new Box(leftPos + LIST.x(), topPos + LIST.y(), LIST.width(), LIST.height());
  }
}
