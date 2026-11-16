package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.menu.ButtonScreenTexts;
import io.github.marcelofabianov.redweave.menu.ItemSensorAction;
import io.github.marcelofabianov.redweave.menu.ItemSensorMenu;
import io.github.marcelofabianov.redweave.menu.ItemSensorScreenTexts;
import io.github.marcelofabianov.redweave.menu.LeverScreenTexts;
import io.github.marcelofabianov.redweave.menu.NodeScreenTexts;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * The Signal Item Sensor Screen: sensor-de-itens.md. The state, `On` or `Off`, large on the left,
 * with the count found, as `7 / 10`; the ghost slot of the item, the count, `Strength` and `Output`
 * on the right, in the frame of every module screen; and the inventory of the player under it, so
 * the owner can take an item on the cursor to pick it. Whoever does not own the sensor sees it read
 * only, and the server decides every click (answers 378, 414 and 696, rule 16).
 */
public final class ItemSensorScreen extends AbstractContainerScreen<ItemSensorMenu> {
  private static final int SEPARATOR_X = 130;
  private static final int ITEM_Y = 40;
  private static final int COUNT_Y = 60;
  private static final int STRENGTH_Y = 78;
  private static final int OUTPUT_Y = 96;
  private static final int GRID_Y = 114;
  private static final int GHOST_SIZE = 18;
  private static final int NAME_GAP = 4;
  private static final int TEXT_Y = 3;
  private static final int LABEL_Y = 5;
  private static final int COUNTED_Y = 6;
  private static final int INVENTORY_HEIGHT = 92;
  private static final int INVENTORY_MARGIN = 7;
  private static final int HOTBAR_GAP = 4;
  private static final Box DISPLAY = new Box(29, 50, 68, 40);
  private static final int DISPLAY_FILL = 0xFF16171B;
  private static final int SCALE = 2;
  private static final String MINUS = "−";
  private static final String PLUS = "+";
  private static final String OF = " / ";

  private final ModuleFrame frame;
  private boolean picking;
  private boolean pending;

  public ItemSensorScreen(ItemSensorMenu menu, Inventory inventory, Component title) {
    super(menu, inventory, title);
    imageWidth = ModuleFrame.WIDTH;
    imageHeight = ModuleFrame.HEIGHT + INVENTORY_HEIGHT;
    frame = new ModuleFrame(menu, menu::viewerIsOwner);
  }

  @Override
  protected void init() {
    super.init();
    frame.attach(font, leftPos, topPos, this::addRenderableWidget);
  }

  @Override
  public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    super.render(graphics, mouseX, mouseY, partialTick);
    renderTooltip(graphics, mouseX, mouseY);
    if (frame.quiet()) {
      return;
    }
    Optional<Component> tooltip = frame.tooltipAt(mouseX, mouseY);
    if (tooltip.isEmpty() && owner() && ghost().contains(mouseX, mouseY)) {
      tooltip = Optional.of(Component.translatable(ItemSensorScreenTexts.GHOST_HINT));
    }
    tooltip.ifPresent(text -> graphics.renderTooltip(font, text, mouseX, mouseY));
  }

  @Override
  protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
    Mouse mouse = new Mouse(mouseX, mouseY);
    if (frame.draw(graphics, title, mouse)) {
      drawBody(graphics, mouse);
      frame.drawFooter(graphics, mouse);
    }
    drawInventory(graphics);
  }

  private void drawBody(GuiGraphics graphics, Mouse mouse) {
    Box body = frame.at(ModuleFrame.BODY);
    graphics.blitSprite(NodeSprites.PANEL, body.x(), body.y(), body.width(), body.height());
    drawState(graphics);
    graphics.fill(
        leftPos + SEPARATOR_X,
        body.y() + ModuleFrame.TITLE_X,
        leftPos + SEPARATOR_X + 1,
        body.bottom() - ModuleFrame.TITLE_X,
        ScreenColors.SEPARATOR);
    drawLabels(graphics);
    drawGhost(graphics);
    drawValue(graphics, COUNT_Y, menu.count());
    drawValue(graphics, STRENGTH_Y, menu.strength());
    ModuleColumn.channel(graphics, font, leftPos, topPos + OUTPUT_Y, menu.sync(), menu.output());
    buttons().forEach(button -> button.draw(graphics, font, mouse));
    if (picking) {
      ChannelPicker.draw(
          graphics,
          font,
          leftPos + ModuleColumn.LABEL_X,
          topPos + GRID_Y,
          menu.output().channel(),
          mouse,
          owner());
    }
  }

  private void drawLabels(GuiGraphics graphics) {
    ModuleColumn.label(
        graphics, font, leftPos, topPos + ITEM_Y + LABEL_Y - TEXT_Y, ItemSensorScreenTexts.ITEM);
    ModuleColumn.label(graphics, font, leftPos, topPos + COUNT_Y, ItemSensorScreenTexts.COUNT);
    ModuleColumn.label(graphics, font, leftPos, topPos + STRENGTH_Y, LeverScreenTexts.STRENGTH);
    ModuleColumn.label(graphics, font, leftPos, topPos + OUTPUT_Y, NodeScreenTexts.OUTPUT);
  }

  /** `On` in green or `Off` dim, large, as the light of the sensor, and `7 / 10` under it. */
  private void drawState(GuiGraphics graphics) {
    Box display = frame.at(DISPLAY);
    graphics.fill(display.x(), display.y(), display.right(), display.bottom(), DISPLAY_FILL);
    Optional<Boolean> on = menu.on();
    if (on.isEmpty()) {
      return;
    }
    Component text = Component.translatable(ButtonScreenTexts.of(on.get()));
    int color = on.get() ? ScreenColors.ONLINE : ScreenColors.DIM;
    graphics.pose().pushPose();
    graphics
        .pose()
        .translate(display.x() + display.width() / 2f, display.y() + display.height() / 2f, 0);
    graphics.pose().scale(SCALE, SCALE, 1);
    graphics.drawString(font, text, -font.width(text) / 2, -font.lineHeight / 2, color, false);
    graphics.pose().popPose();
    Component counted = Component.literal(menu.counted() + OF + menu.count());
    graphics.drawString(
        font,
        counted,
        display.x() + (display.width() - font.width(counted)) / 2,
        display.bottom() + COUNTED_Y,
        ScreenColors.TEXT,
        false);
  }

  /** The ghost slot with the expected item, and its name beside it, or `None` (answer 696). */
  private void drawGhost(GuiGraphics graphics) {
    Box ghost = ghost();
    graphics.blitSprite(NodeSprites.SLOT, ghost.x(), ghost.y(), ghost.width(), ghost.height());
    Optional<Item> item = menu.item();
    item.ifPresent(
        found -> graphics.renderFakeItem(new ItemStack(found), ghost.x() + 1, ghost.y() + 1));
    Component name =
        item.map(Item::getDescription).orElse(Component.translatable(ItemSensorScreenTexts.NONE));
    graphics.drawString(
        font,
        name,
        ghost.right() + NAME_GAP,
        ghost.y() + LABEL_Y,
        item.isPresent() ? ScreenColors.TEXT : ScreenColors.DIM,
        false);
  }

  private void drawValue(GuiGraphics graphics, int y, int value) {
    Component text = Component.literal(Integer.toString(value));
    int x = leftPos + TimerControls.VALUE_X + (TimerControls.VALUE_WIDTH - font.width(text)) / 2;
    graphics.drawString(font, text, x, topPos + y + TEXT_Y, ScreenColors.TEXT, false);
  }

  /** A panel under the frame, with the slots of the inventory and of the hotbar. */
  private void drawInventory(GuiGraphics graphics) {
    int left = leftPos + ItemSensorMenu.INVENTORY_X - INVENTORY_MARGIN - 1;
    int top = topPos + ItemSensorMenu.INVENTORY_Y - INVENTORY_MARGIN - 1;
    int width = ItemSensorMenu.COLUMNS * ItemSensorMenu.SLOT_SIZE + INVENTORY_MARGIN * SCALE;
    int height =
        (ItemSensorMenu.ROWS + 1) * ItemSensorMenu.SLOT_SIZE
            + HOTBAR_GAP
            + INVENTORY_MARGIN * SCALE;
    graphics.blitSprite(NodeSprites.PANEL, left, top, width, height);
    menu.slots.forEach(slot ->
        graphics.blitSprite(
            NodeSprites.SLOT,
            leftPos + slot.x - 1,
            topPos + slot.y - 1,
            ItemSensorMenu.SLOT_SIZE,
            ItemSensorMenu.SLOT_SIZE));
  }

  /** Every text is drawn in renderBg, so the game does not draw its own labels on top. */
  @Override
  protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {}

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    frame.clickAway(mouseX, mouseY);
    if (frame.switching()) {
      return frame.clickChoice(mouseX, mouseY, this::chosen, () -> pending = false)
          || super.mouseClicked(mouseX, mouseY, button);
    }
    return clickOwned(mouseX, mouseY) || super.mouseClicked(mouseX, mouseY, button);
  }

  /** The name, the network, the ghost slot, the buttons and the channels, only for the owner. */
  private boolean clickOwned(double mouseX, double mouseY) {
    if (!owner()) {
      return false;
    }
    if (frame.clickHeader(mouseX, mouseY, this::setFocused)) {
      return true;
    }
    if (ghost().contains(mouseX, mouseY)) {
      send(ItemSensorAction.ghost());
      return true;
    }
    for (TimerButton button : buttons()) {
      if (button.clickable() && button.box().contains(mouseX, mouseY)) {
        button.click().run();
        return true;
      }
    }
    return clickChannel(mouseX, mouseY);
  }

  private boolean clickChannel(double mouseX, double mouseY) {
    if (!picking) {
      return false;
    }
    Optional<ChannelColor> channel =
        ChannelPicker.at(leftPos + ModuleColumn.LABEL_X, topPos + GRID_Y, mouseX, mouseY);
    channel.ifPresent(color -> send(ItemSensorAction.output(Optional.of(color.ordinal()))));
    return channel.isPresent();
  }

  /** A network picked goes on to the channels the owner was choosing (answer 414). */
  private void chosen(int row) {
    send(ItemSensorAction.choose(row));
    picking = pending;
    pending = false;
  }

  /** `Channel` opens the channels, or first the network choice without a network (414). */
  private void pick() {
    if (menu.sync().networkColor().isEmpty()) {
      frame.openChoice();
      pending = true;
      return;
    }
    picking = true;
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    return frame.keyPressed(keyCode, scanCode, modifiers)
        || super.keyPressed(keyCode, scanCode, modifiers);
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
    return frame.scroll(mouseX, mouseY, scrollY)
        || super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
  }

  private void send(ItemSensorAction action) {
    if (minecraft != null && minecraft.gameMode != null) {
      minecraft.gameMode.handleInventoryButtonClick(menu.containerId, action.button());
    }
    if (action.kind() == ItemSensorAction.Kind.OUTPUT && action.channel().isEmpty()) {
      picking = false;
    }
  }

  private Box ghost() {
    return new Box(
        leftPos + ModuleColumn.CONTROL_X, topPos + ITEM_Y - TEXT_Y, GHOST_SIZE, GHOST_SIZE);
  }

  /** `−` and `+` of the count and of `Strength`, and `Edge` and `Channel` of `Output` (696). */
  private List<TimerButton> buttons() {
    List<TimerButton> buttons = new ArrayList<>();
    buttons.add(step(ModuleColumn.CONTROL_X, COUNT_Y, MINUS, () -> countStep(false)));
    buttons.add(step(TimerControls.PLUS_X, COUNT_Y, PLUS, () -> countStep(true)));
    buttons.add(
        step(
            ModuleColumn.CONTROL_X,
            STRENGTH_Y,
            MINUS,
            () -> send(ItemSensorAction.strength(false))));
    buttons.add(
        step(TimerControls.PLUS_X, STRENGTH_Y, PLUS, () -> send(ItemSensorAction.strength(true))));
    ModuleColumn.connection(
        buttons,
        menu.output(),
        new Box(leftPos, topPos + OUTPUT_Y, 0, ModuleColumn.ROW_HEIGHT),
        new ModuleColumn.Choosing(owner(), picking),
        () -> send(ItemSensorAction.output(Optional.empty())),
        this::pick);
    return buttons;
  }

  /** One more or one less, or ten with Shift held (answer 696). */
  private void countStep(boolean more) {
    send(ItemSensorAction.count(more, Screen.hasShiftDown()));
  }

  private TimerButton step(int x, int y, String label, Runnable click) {
    return new TimerButton(
        new Box(leftPos + x, topPos + y, TimerControls.STEP_WIDTH, ModuleColumn.ROW_HEIGHT),
        Component.literal(label),
        owner() ? TimerButton.Look.BUTTON : TimerButton.Look.DISABLED,
        click);
  }

  private boolean owner() {
    return menu.viewerIsOwner();
  }
}
