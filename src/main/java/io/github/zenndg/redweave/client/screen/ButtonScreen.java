package io.github.zenndg.redweave.client.screen;

import io.github.zenndg.redweave.domain.signal.ChannelColor;
import io.github.zenndg.redweave.menu.ButtonAction;
import io.github.zenndg.redweave.menu.ButtonMenu;
import io.github.zenndg.redweave.menu.ButtonScreenTexts;
import io.github.zenndg.redweave.menu.LeverScreenTexts;
import io.github.zenndg.redweave.menu.NodeScreenTexts;
import io.github.zenndg.redweave.menu.TimerScreenTexts;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/**
 * The Signal Button Screen: botao.md. The state, `On` or `Off`, large on the left, and `Strength`,
 * the on time and `Output` on the right, in the frame of every module screen. There is no button to
 * press it: the hand and the `Toggle` do it. Whoever does not own the plate sees it read only, and
 * the server decides every click (answers 378, 414, 556, 588 and 595, rule 16).
 */
public final class ButtonScreen extends AbstractContainerScreen<ButtonMenu> {
  private static final int SEPARATOR_X = 130;
  private static final int STRENGTH_Y = 42;
  private static final int TIME_Y = 60;
  private static final int OUTPUT_Y = 96;
  private static final int GRID_Y = 116;
  private static final int DELAY_Y = 78;
  private static final int TEXT_Y = 3;
  private static final Box DISPLAY = new Box(29, 50, 68, 40);
  private static final int DISPLAY_FILL = 0xFF16171B;
  private static final int SCALE = 2;
  private static final String MINUS = "−";
  private static final String PLUS = "+";

  private final ModuleFrame frame;
  private boolean picking;
  private boolean pending;

  public ButtonScreen(ButtonMenu menu, Inventory inventory, Component title) {
    super(menu, inventory, title);
    imageWidth = ModuleFrame.WIDTH;
    imageHeight = ModuleFrame.HEIGHT;
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
    tooltipAt(mouseX, mouseY).ifPresent(text -> graphics.renderTooltip(font, text, mouseX, mouseY));
  }

  /** The ticks of the on time, and for the owner how to rename and to switch networks. */
  private Optional<Component> tooltipAt(double mouseX, double mouseY) {
    if (frame.quiet()) {
      return Optional.empty();
    }
    if (TimerScreenParts.valueBox(leftPos, topPos + TIME_Y).contains(mouseX, mouseY)) {
      return Optional.of(Component.translatable(TimerScreenTexts.TICKS, menu.timeTicks()));
    }
    return frame.tooltipAt(mouseX, mouseY);
  }

  @Override
  protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
    Mouse mouse = new Mouse(mouseX, mouseY);
    if (frame.draw(graphics, title, mouse)) {
      drawBody(graphics, mouse);
      frame.drawFooter(graphics, mouse);
    }
  }

  private void drawBody(GuiGraphics graphics, Mouse mouse) {
    Box body = frame.body();
    graphics.blitSprite(NodeSprites.PANEL, body.x(), body.y(), body.width(), body.height());
    drawState(graphics);
    graphics.fill(
        leftPos + SEPARATOR_X,
        body.y() + ModuleFrame.TITLE_X,
        leftPos + SEPARATOR_X + 1,
        body.bottom() - ModuleFrame.TITLE_X,
        ScreenColors.SEPARATOR);
    ModuleColumn.label(graphics, font, leftPos, topPos + STRENGTH_Y, LeverScreenTexts.STRENGTH);
    ModuleColumn.label(graphics, font, leftPos, topPos + TIME_Y, ButtonScreenTexts.ON_TIME);
    ModuleColumn.label(graphics, font, leftPos, topPos + OUTPUT_Y, NodeScreenTexts.OUTPUT);
    drawStrength(graphics);
    TimerScreenParts.value(graphics, font, leftPos, topPos + TIME_Y, menu.timeTicks());
    ModuleColumn.channel(graphics, font, leftPos, topPos + OUTPUT_Y, menu.sync(), menu.output());
    DelayRow.draw(graphics, font, leftPos, topPos + DELAY_Y, menu.delaySeconds());
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

  /** `On` in green or `Off` dim, large, as the light of the button (answers 554 and 595). */
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
  }

  /** The strength, from 1 to 15, centered between `−` and `+`. */
  private void drawStrength(GuiGraphics graphics) {
    Component text = Component.literal(Integer.toString(menu.strength()));
    int x = leftPos + TimerControls.VALUE_X + (TimerControls.VALUE_WIDTH - font.width(text)) / 2;
    graphics.drawString(font, text, x, topPos + STRENGTH_Y + TEXT_Y, ScreenColors.TEXT, false);
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

  /** The name, the network, the buttons and the channels, only for the owner (answer 378). */
  private boolean clickOwned(double mouseX, double mouseY) {
    if (!owner()) {
      return false;
    }
    if (frame.clickHeader(mouseX, mouseY, this::setFocused)) {
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
    channel.ifPresent(color -> send(ButtonAction.output(Optional.of(color.ordinal()))));
    return channel.isPresent();
  }

  /** A network picked goes on to the channels the owner was choosing (answer 414). */
  private void chosen(int row) {
    send(ButtonAction.choose(row));
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

  private void send(ButtonAction action) {
    if (minecraft != null && minecraft.gameMode != null) {
      minecraft.gameMode.handleInventoryButtonClick(menu.containerId, action.button());
    }
    if (action.kind() == ButtonAction.Kind.OUTPUT && action.channel().isEmpty()) {
      picking = false;
    }
  }

  /** `−` and `+` of `Strength` and of the on time, and `Edge` and `Channel` of `Output`. */
  private List<TimerButton> buttons() {
    List<TimerButton> buttons = new ArrayList<>();
    DelayRow.buttons(
        buttons,
        leftPos,
        topPos + DELAY_Y,
        owner(),
        value -> send(new ButtonAction(ButtonAction.Kind.DELAY, value)));
    buttons.add(
        step(ModuleColumn.CONTROL_X, STRENGTH_Y, MINUS, () -> ButtonAction.strength(false)));
    buttons.add(step(TimerControls.PLUS_X, STRENGTH_Y, PLUS, () -> ButtonAction.strength(true)));
    buttons.add(
        step(ModuleColumn.CONTROL_X, TIME_Y, MINUS, () -> ButtonAction.time(false, shift())));
    buttons.add(step(TimerControls.PLUS_X, TIME_Y, PLUS, () -> ButtonAction.time(true, shift())));
    ModuleColumn.connection(
        buttons,
        menu.output(),
        new Box(leftPos, topPos + OUTPUT_Y, 0, ModuleColumn.ROW_HEIGHT),
        new ModuleColumn.Choosing(owner(), picking),
        () -> send(ButtonAction.output(Optional.empty())),
        this::pick);
    return buttons;
  }

  /** `−` or `+` of a row; on the on time, Shift + click moves 1 s (answer 398). */
  private TimerButton step(int x, int y, String label, Supplier<ButtonAction> action) {
    return new TimerButton(
        new Box(leftPos + x, topPos + y, TimerControls.STEP_WIDTH, ModuleColumn.ROW_HEIGHT),
        Component.literal(label),
        owner() ? TimerButton.Look.BUTTON : TimerButton.Look.DISABLED,
        () -> send(action.get()));
  }

  private static boolean shift() {
    return Screen.hasShiftDown();
  }

  private boolean owner() {
    return menu.viewerIsOwner();
  }
}
