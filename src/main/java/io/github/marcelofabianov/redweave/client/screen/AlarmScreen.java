package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.menu.AlarmAction;
import io.github.marcelofabianov.redweave.menu.AlarmMenu;
import io.github.marcelofabianov.redweave.menu.AlarmScreenTexts;
import io.github.marcelofabianov.redweave.menu.NodeScreenTexts;
import io.github.marcelofabianov.redweave.menu.TimerScreenTexts;
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
 * The Signal Alarm Screen: alarme.md. The state, `Ringing` or `Quiet`, large on the left, with its
 * duration under it, and who hears it, its rings, its interval and its `Input` on the right, in the
 * frame of every module screen. Anyone else sees it read only, and the server decides every click
 * (answers 378, 643 and 645 to 648, rule 16).
 */
public final class AlarmScreen extends AbstractContainerScreen<AlarmMenu> {
  private static final int SEPARATOR_X = 130;
  private static final int LISTENERS_Y = 42;
  private static final int REPEATS_Y = 60;
  private static final int INTERVAL_Y = 78;
  private static final int INPUT_Y = 96;
  private static final int GRID_Y = 114;
  private static final int DURATION_LABEL_Y = 82;
  private static final int DURATION_Y = 96;
  private static final int DURATION_LABEL_X = 22;
  private static final int DURATION_MINUS_X = 22;
  private static final int DURATION_PLUS_X = 80;

  /** Where the value of the duration sits, as the value of the timer moved to the left column. */
  private static final int DURATION_SHIFT = 38 - TimerControls.VALUE_X;

  private static final int TEXT_Y = 3;
  private static final Box DISPLAY = new Box(29, 44, 68, 30);
  private static final int DISPLAY_FILL = 0xFF16171B;
  private static final int SCALE = 2;
  private static final String MINUS = "−";
  private static final String PLUS = "+";

  private final ModuleFrame frame;
  private boolean picking;
  private boolean pending;

  public AlarmScreen(AlarmMenu menu, Inventory inventory, Component title) {
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

  /** The ticks of the interval and of the duration, and the tooltips of the frame. */
  private Optional<Component> tooltipAt(double mouseX, double mouseY) {
    if (frame.quiet()) {
      return Optional.empty();
    }
    if (TimerScreenParts.valueBox(leftPos, topPos + INTERVAL_Y).contains(mouseX, mouseY)) {
      return Optional.of(Component.translatable(TimerScreenTexts.TICKS, menu.intervalTicks()));
    }
    if (TimerScreenParts.valueBox(durationLeft(), topPos + DURATION_Y).contains(mouseX, mouseY)) {
      return Optional.of(Component.translatable(TimerScreenTexts.TICKS, menu.durationTicks()));
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
    drawDuration(graphics);
    graphics.fill(
        leftPos + SEPARATOR_X,
        body.y() + ModuleFrame.TITLE_X,
        leftPos + SEPARATOR_X + 1,
        body.bottom() - ModuleFrame.TITLE_X,
        ScreenColors.SEPARATOR);
    ModuleColumn.label(graphics, font, leftPos, topPos + LISTENERS_Y, AlarmScreenTexts.LISTENERS);
    ModuleColumn.label(graphics, font, leftPos, topPos + REPEATS_Y, AlarmScreenTexts.REPEATS);
    ModuleColumn.label(graphics, font, leftPos, topPos + INTERVAL_Y, AlarmScreenTexts.INTERVAL);
    ModuleColumn.label(graphics, font, leftPos, topPos + INPUT_Y, NodeScreenTexts.INPUT);
    drawNumber(graphics, REPEATS_Y, Integer.toString(menu.repeats()));
    TimerScreenParts.value(graphics, font, leftPos, topPos + INTERVAL_Y, menu.intervalTicks());
    ModuleColumn.channel(graphics, font, leftPos, topPos + INPUT_Y, menu.sync(), menu.input());
    buttons().forEach(button -> button.draw(graphics, font, mouse));
    if (picking) {
      ChannelPicker.draw(
          graphics,
          font,
          leftPos + ModuleColumn.LABEL_X,
          topPos + GRID_Y,
          menu.input().channel(),
          mouse,
          owner());
    }
  }

  /** `Ringing` in red or `Quiet` dim, large, as the warning light (answer 649). */
  private void drawState(GuiGraphics graphics) {
    Box display = frame.at(DISPLAY);
    graphics.fill(display.x(), display.y(), display.right(), display.bottom(), DISPLAY_FILL);
    Optional<Boolean> ringing = menu.ringing();
    if (ringing.isEmpty()) {
      return;
    }
    Component text = Component.translatable(AlarmScreenTexts.of(ringing.get()));
    int color = ringing.get() ? ScreenColors.REDSTONE : ScreenColors.DIM;
    graphics.pose().pushPose();
    graphics
        .pose()
        .translate(display.x() + display.width() / 2f, display.y() + display.height() / 2f, 0);
    graphics.pose().scale(SCALE, SCALE, 1);
    graphics.drawString(font, text, -font.width(text) / 2, -font.lineHeight / 2, color, false);
    graphics.pose().popPose();
  }

  /** The duration, under the state, between its `−` and `+` (answer 648). */
  private void drawDuration(GuiGraphics graphics) {
    graphics.drawString(
        font,
        Component.translatable(AlarmScreenTexts.DURATION),
        leftPos + DURATION_LABEL_X,
        topPos + DURATION_LABEL_Y,
        ScreenColors.LABEL,
        false);
    TimerScreenParts.value(
        graphics, font, durationLeft(), topPos + DURATION_Y, menu.durationTicks());
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
    if (frame.clickHeader(mouseX, mouseY, this::setFocused) || clickChannel(mouseX, mouseY)) {
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
    if (!picking) {
      return false;
    }
    Optional<ChannelColor> channel =
        ChannelPicker.at(leftPos + ModuleColumn.LABEL_X, topPos + GRID_Y, mouseX, mouseY);
    channel.ifPresent(color -> send(AlarmAction.connect(Optional.of(color.ordinal()))));
    return channel.isPresent();
  }

  /** A network picked goes on to the channels the owner was choosing (answer 414). */
  private void chosen(int row) {
    send(AlarmAction.choose(row));
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

  private void send(AlarmAction action) {
    if (minecraft != null && minecraft.gameMode != null) {
      minecraft.gameMode.handleInventoryButtonClick(menu.containerId, action.button());
    }
    if (action.kind() == AlarmAction.Kind.INPUT) {
      picking = false;
    }
  }

  /** Who hears it, `−` and `+` of the rings, the interval and the duration, and the input. */
  private List<TimerButton> buttons() {
    List<TimerButton> buttons = new ArrayList<>();
    buttons.add(listenersButton());
    buttons.add(step(ModuleColumn.CONTROL_X, REPEATS_Y, MINUS, () -> AlarmAction.repeats(false)));
    buttons.add(step(TimerControls.PLUS_X, REPEATS_Y, PLUS, () -> AlarmAction.repeats(true)));
    buttons.add(
        step(
            ModuleColumn.CONTROL_X, INTERVAL_Y, MINUS, () -> AlarmAction.interval(false, shift())));
    buttons.add(
        step(TimerControls.PLUS_X, INTERVAL_Y, PLUS, () -> AlarmAction.interval(true, shift())));
    buttons.add(
        step(DURATION_MINUS_X, DURATION_Y, MINUS, () -> AlarmAction.duration(false, shift())));
    buttons.add(step(DURATION_PLUS_X, DURATION_Y, PLUS, () -> AlarmAction.duration(true, shift())));
    ModuleColumn.connection(
        buttons,
        menu.input(),
        new Box(leftPos, topPos + INPUT_Y, 0, ModuleColumn.ROW_HEIGHT),
        new ModuleColumn.Choosing(owner(), picking),
        () -> send(AlarmAction.connect(Optional.empty())),
        this::pick);
    return buttons;
  }

  /** One button with the listeners of now, which the owner clicks to cycle them (answer 645). */
  private TimerButton listenersButton() {
    int width = TimerControls.PLUS_X + TimerControls.STEP_WIDTH - ModuleColumn.CONTROL_X;
    return new TimerButton(
        new Box(
            leftPos + ModuleColumn.CONTROL_X, topPos + LISTENERS_Y, width, ModuleColumn.ROW_HEIGHT),
        Component.translatable(AlarmScreenTexts.of(menu.listeners())),
        owner() ? TimerButton.Look.BUTTON : TimerButton.Look.DISABLED,
        () -> send(AlarmAction.listeners()));
  }

  private TimerButton step(int x, int y, String label, Supplier<AlarmAction> action) {
    return new TimerButton(
        new Box(leftPos + x, topPos + y, TimerControls.STEP_WIDTH, ModuleColumn.ROW_HEIGHT),
        Component.literal(label),
        owner() ? TimerButton.Look.BUTTON : TimerButton.Look.DISABLED,
        () -> send(action.get()));
  }

  private static boolean shift() {
    return Screen.hasShiftDown();
  }

  private int durationLeft() {
    return leftPos + DURATION_SHIFT;
  }

  private boolean owner() {
    return menu.viewerIsOwner();
  }
}
