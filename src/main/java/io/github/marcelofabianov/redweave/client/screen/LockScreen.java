package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.domain.module.Connection;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.menu.LeverScreenTexts;
import io.github.marcelofabianov.redweave.menu.LockAction;
import io.github.marcelofabianov.redweave.menu.LockMenu;
import io.github.marcelofabianov.redweave.menu.LockScreenTexts;
import io.github.marcelofabianov.redweave.menu.NodeScreenTexts;
import io.github.marcelofabianov.redweave.menu.TimerScreenTexts;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/**
 * The Signal Lock Screen: fechadura.md. The state, `Locked` or `Unlocked`, large on the left, with
 * the pulse of the `Toggle` under it, and the key, `Strength`, `Input` and `Output` on the right,
 * in the frame of every module screen. Only the owner sees the key; anyone else sees `?` and the
 * screen read only, and the server decides every click (answers 378, 635 to 637 and 640, rule 16).
 */
public final class LockScreen extends AbstractContainerScreen<LockMenu> {
  private static final int SEPARATOR_X = 130;
  private static final int KEY_Y = 42;
  private static final int STRENGTH_Y = 60;
  private static final int INPUT_Y = 78;
  private static final int OUTPUT_Y = 96;
  private static final int GRID_Y = 114;
  private static final int PULSE_LABEL_Y = 82;
  private static final int PULSE_Y = 96;
  private static final int PULSE_LABEL_X = 22;
  private static final int PULSE_MINUS_X = 22;
  private static final int PULSE_PLUS_X = 80;

  /** Where the value of the pulse sits, as the value of the timer moved to the left column. */
  private static final int PULSE_SHIFT = 38 - TimerControls.VALUE_X;

  private static final int TEXT_Y = 3;
  private static final Box DISPLAY = new Box(29, 44, 68, 30);
  private static final int DISPLAY_FILL = 0xFF16171B;
  private static final int SCALE = 2;
  private static final String MINUS = "−";
  private static final String PLUS = "+";

  private final ModuleFrame frame;
  private Optional<LockAction.Kind> picking = Optional.empty();
  private Optional<LockAction.Kind> pending = Optional.empty();

  public LockScreen(LockMenu menu, Inventory inventory, Component title) {
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

  /** The ticks of the pulse, and for the owner how to rename and to switch networks. */
  private Optional<Component> tooltipAt(double mouseX, double mouseY) {
    if (frame.quiet()) {
      return Optional.empty();
    }
    if (TimerScreenParts.valueBox(pulseLeft(), topPos + PULSE_Y).contains(mouseX, mouseY)) {
      return Optional.of(Component.translatable(TimerScreenTexts.TICKS, menu.pulseTicks()));
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
    Box body = frame.at(ModuleFrame.BODY);
    graphics.blitSprite(NodeSprites.PANEL, body.x(), body.y(), body.width(), body.height());
    drawState(graphics);
    drawPulse(graphics);
    graphics.fill(
        leftPos + SEPARATOR_X,
        body.y() + ModuleFrame.TITLE_X,
        leftPos + SEPARATOR_X + 1,
        body.bottom() - ModuleFrame.TITLE_X,
        ScreenColors.SEPARATOR);
    ModuleColumn.label(graphics, font, leftPos, topPos + KEY_Y, LockScreenTexts.KEY);
    ModuleColumn.label(graphics, font, leftPos, topPos + STRENGTH_Y, LeverScreenTexts.STRENGTH);
    ModuleColumn.label(graphics, font, leftPos, topPos + INPUT_Y, NodeScreenTexts.INPUT);
    ModuleColumn.label(graphics, font, leftPos, topPos + OUTPUT_Y, NodeScreenTexts.OUTPUT);
    drawNumber(graphics, KEY_Y, menu.key().map(String::valueOf).orElse(LockScreenTexts.HIDDEN));
    drawNumber(graphics, STRENGTH_Y, Integer.toString(menu.strength()));
    ModuleColumn.channel(graphics, font, leftPos, topPos + INPUT_Y, menu.sync(), menu.input());
    ModuleColumn.channel(graphics, font, leftPos, topPos + OUTPUT_Y, menu.sync(), menu.output());
    buttons().forEach(button -> button.draw(graphics, font, mouse));
    picking.ifPresent(kind -> drawPicker(graphics, kind, mouse));
  }

  private void drawPicker(GuiGraphics graphics, LockAction.Kind kind, Mouse mouse) {
    ChannelPicker.draw(
        graphics,
        font,
        leftPos + ModuleColumn.LABEL_X,
        topPos + GRID_Y,
        connectionOf(kind).channel(),
        mouse,
        owner());
  }

  /** `Unlocked` in green or `Locked` dim, large, as the light of the lock (answer 638). */
  private void drawState(GuiGraphics graphics) {
    Box display = frame.at(DISPLAY);
    graphics.fill(display.x(), display.y(), display.right(), display.bottom(), DISPLAY_FILL);
    Optional<Boolean> open = menu.open();
    if (open.isEmpty()) {
      return;
    }
    Component text = Component.translatable(LockScreenTexts.of(open.get()));
    int color = open.get() ? ScreenColors.ONLINE : ScreenColors.DIM;
    graphics.pose().pushPose();
    graphics
        .pose()
        .translate(display.x() + display.width() / 2f, display.y() + display.height() / 2f, 0);
    graphics.pose().scale(SCALE, SCALE, 1);
    graphics.drawString(font, text, -font.width(text) / 2, -font.lineHeight / 2, color, false);
    graphics.pose().popPose();
  }

  /** The pulse of the `Toggle`, under the state, between its `−` and `+` (answer 640). */
  private void drawPulse(GuiGraphics graphics) {
    graphics.drawString(
        font,
        Component.translatable(LockScreenTexts.PULSE),
        leftPos + PULSE_LABEL_X,
        topPos + PULSE_LABEL_Y,
        ScreenColors.LABEL,
        false);
    TimerScreenParts.value(graphics, font, pulseLeft(), topPos + PULSE_Y, menu.pulseTicks());
  }

  /** A number of a row, centered between its `−` and `+`. */
  private void drawNumber(GuiGraphics graphics, int y, String number) {
    Component text = Component.literal(number);
    int x = leftPos + TimerControls.VALUE_X + (TimerControls.VALUE_WIDTH - font.width(text)) / 2;
    graphics.drawString(font, text, x, topPos + y + TEXT_Y, ScreenColors.TEXT, false);
  }

  /** Every text is drawn in renderBg, so the game does not draw its own labels on top. */
  @Override
  protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {}

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    frame.clickAway(mouseX, mouseY);
    if (frame.switching()) {
      return frame.clickChoice(mouseX, mouseY, this::chosen, () -> pending = Optional.empty())
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
    if (clickChannel(mouseX, mouseY)) {
      return true;
    }
    for (TimerButton button : buttons()) {
      if (button.clickable() && button.box().contains(mouseX, mouseY)) {
        button.click().run();
        return true;
      }
    }
    return false;
  }

  private boolean clickChannel(double mouseX, double mouseY) {
    if (picking.isEmpty()) {
      return false;
    }
    Optional<ChannelColor> channel =
        ChannelPicker.at(leftPos + ModuleColumn.LABEL_X, topPos + GRID_Y, mouseX, mouseY);
    channel.ifPresent(
        color -> send(LockAction.connect(picking.get(), Optional.of(color.ordinal()))));
    return channel.isPresent();
  }

  /** A network picked goes on to the channels the owner was choosing (answer 414). */
  private void chosen(int row) {
    send(LockAction.choose(row));
    picking = pending;
    pending = Optional.empty();
  }

  /** `Channel` opens the channels, or first the network choice without a network (414). */
  private void pick(LockAction.Kind kind) {
    if (menu.sync().networkColor().isEmpty()) {
      frame.openChoice();
      pending = Optional.of(kind);
      return;
    }
    picking = Optional.of(kind);
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

  private void send(LockAction action) {
    if (minecraft != null && minecraft.gameMode != null) {
      minecraft.gameMode.handleInventoryButtonClick(menu.containerId, action.button());
    }
    if (isConnection(action.kind())) {
      picking = Optional.empty();
    }
  }

  private static boolean isConnection(LockAction.Kind kind) {
    return kind == LockAction.Kind.INPUT || kind == LockAction.Kind.OUTPUT;
  }

  /** `−` and `+` of the key, `Strength` and the pulse, and `Edge` and `Channel` of both ends. */
  private List<TimerButton> buttons() {
    List<TimerButton> buttons = new ArrayList<>();
    steps(buttons, KEY_Y, LockAction::key);
    steps(buttons, STRENGTH_Y, LockAction::strength);
    buttons.add(
        step(PULSE_MINUS_X, PULSE_Y, MINUS, () -> LockAction.pulse(false, Screen.hasShiftDown())));
    buttons.add(
        step(PULSE_PLUS_X, PULSE_Y, PLUS, () -> LockAction.pulse(true, Screen.hasShiftDown())));
    connection(buttons, LockAction.Kind.INPUT, menu.input(), INPUT_Y);
    connection(buttons, LockAction.Kind.OUTPUT, menu.output(), OUTPUT_Y);
    return buttons;
  }

  private void steps(List<TimerButton> buttons, int y, Function<Boolean, LockAction> action) {
    buttons.add(step(ModuleColumn.CONTROL_X, y, MINUS, () -> action.apply(false)));
    buttons.add(step(TimerControls.PLUS_X, y, PLUS, () -> action.apply(true)));
  }

  private void connection(List<TimerButton> buttons, LockAction.Kind kind, Connection now, int y) {
    ModuleColumn.connection(
        buttons,
        now,
        new Box(leftPos, topPos + y, 0, ModuleColumn.ROW_HEIGHT),
        new ModuleColumn.Choosing(owner(), picking.equals(Optional.of(kind))),
        () -> send(LockAction.connect(kind, Optional.empty())),
        () -> pick(kind));
  }

  private TimerButton step(int x, int y, String label, Supplier<LockAction> action) {
    return new TimerButton(
        new Box(leftPos + x, topPos + y, TimerControls.STEP_WIDTH, ModuleColumn.ROW_HEIGHT),
        Component.literal(label),
        owner() ? TimerButton.Look.BUTTON : TimerButton.Look.DISABLED,
        () -> send(action.get()));
  }

  private Connection connectionOf(LockAction.Kind kind) {
    return kind == LockAction.Kind.INPUT ? menu.input() : menu.output();
  }

  private int pulseLeft() {
    return leftPos + PULSE_SHIFT;
  }

  private boolean owner() {
    return menu.viewerIsOwner();
  }
}
