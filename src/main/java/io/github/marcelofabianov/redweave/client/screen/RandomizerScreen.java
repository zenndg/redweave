package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.domain.module.RandomizerMode;
import io.github.marcelofabianov.redweave.domain.module.RandomizerState;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.menu.NodeScreenTexts;
import io.github.marcelofabianov.redweave.menu.RandomizerAction;
import io.github.marcelofabianov.redweave.menu.RandomizerMenu;
import io.github.marcelofabianov.redweave.menu.RandomizerScreenTexts;
import io.github.marcelofabianov.redweave.menu.TimerScreenTexts;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/**
 * The Signal Randomizer Screen: telas.md, Tela do Signal Randomizer. The value drawn and the
 * `Running` button on the left, `Mode`, `Interval` and `Output` on the right, in the frame of every
 * module screen. Whoever does not own the plate sees it read only, and the server decides every
 * click (answers 378, 398, 414, 484 and 487, rule 16).
 */
public final class RandomizerScreen extends AbstractContainerScreen<RandomizerMenu> {
  private static final int SEPARATOR_X = 130;
  private static final int MODE_Y = 42;
  private static final int INTERVAL_Y = 60;
  private static final int OUTPUT_Y = 78;
  private static final int GRID_Y = 98;
  private static final Box DISPLAY = new Box(43, 42, 40, 40);
  private static final Box RUN = new Box(20, 120, 98, 16);
  private static final int DISPLAY_FILL = 0xFF16171B;
  private static final int SCALE = 3;
  private static final int[] MODE_X = {184, 226};
  private static final int[] MODE_WIDTH = {40, 40};
  private static final String MINUS = "−";
  private static final String PLUS = "+";

  private final ModuleFrame frame;
  private boolean picking;
  private boolean pending;

  public RandomizerScreen(RandomizerMenu menu, Inventory inventory, Component title) {
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

  /** The ticks of the interval, and for the owner how to rename and to switch networks. */
  private Optional<Component> tooltipAt(double mouseX, double mouseY) {
    if (frame.quiet()) {
      return Optional.empty();
    }
    if (TimerScreenParts.valueBox(leftPos, topPos + INTERVAL_Y).contains(mouseX, mouseY)) {
      return Optional.of(Component.translatable(TimerScreenTexts.TICKS, menu.intervalTicks()));
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
    drawValue(graphics);
    graphics.fill(
        leftPos + SEPARATOR_X,
        body.y() + ModuleFrame.TITLE_X,
        leftPos + SEPARATOR_X + 1,
        body.bottom() - ModuleFrame.TITLE_X,
        ScreenColors.SEPARATOR);
    ModuleColumn.label(graphics, font, leftPos, topPos + MODE_Y, RandomizerScreenTexts.MODE);
    ModuleColumn.label(graphics, font, leftPos, topPos + INTERVAL_Y, TimerScreenTexts.INTERVAL);
    ModuleColumn.label(graphics, font, leftPos, topPos + OUTPUT_Y, NodeScreenTexts.OUTPUT);
    TimerScreenParts.value(graphics, font, leftPos, topPos + INTERVAL_Y, menu.intervalTicks());
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

  /** The value on the output, large, red above 0 and dim at 0 (answer 487). */
  private void drawValue(GuiGraphics graphics) {
    Box display = frame.at(DISPLAY);
    graphics.fill(display.x(), display.y(), display.right(), display.bottom(), DISPLAY_FILL);
    int value = menu.value();
    Component text = Component.literal(Integer.toString(value));
    int color = value > 0 ? ScreenColors.REDSTONE : ScreenColors.DIM;
    graphics.pose().pushPose();
    graphics
        .pose()
        .translate(display.x() + display.width() / 2f, display.y() + display.height() / 2f, 0);
    graphics.pose().scale(SCALE, SCALE, 1);
    graphics.drawString(font, text, -font.width(text) / 2, -font.lineHeight / 2, color, false);
    graphics.pose().popPose();
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
    channel.ifPresent(
        color -> send(new RandomizerAction(RandomizerAction.Kind.OUTPUT, color.ordinal() + 1)));
    return channel.isPresent();
  }

  /** A network picked goes on to the channels the owner was choosing (answer 414). */
  private void chosen(int row) {
    send(RandomizerAction.choose(row));
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

  private void send(RandomizerAction action) {
    if (minecraft != null && minecraft.gameMode != null) {
      minecraft.gameMode.handleInventoryButtonClick(menu.containerId, action.button());
    }
    if (action.kind() == RandomizerAction.Kind.OUTPUT && action.channel().isEmpty()) {
      picking = false;
    }
  }

  /** `Running`, the modes, `−` and `+`, and `Edge` and `Channel` (answers 398 and 487). */
  private List<TimerButton> buttons() {
    List<TimerButton> buttons = new ArrayList<>();
    boolean running = menu.state().equals(Optional.of(RandomizerState.RUNNING));
    buttons.add(
        TimerButton.run(frame.at(RUN), running, owner(), () -> send(RandomizerAction.toggle())));
    for (RandomizerMode mode : RandomizerMode.values()) {
      buttons.add(modeButton(mode));
    }
    buttons.add(stepButton(ModuleColumn.CONTROL_X, MINUS, false));
    buttons.add(stepButton(TimerControls.PLUS_X, PLUS, true));
    ModuleColumn.connection(
        buttons,
        menu.output(),
        new Box(leftPos, topPos + OUTPUT_Y, 0, ModuleColumn.ROW_HEIGHT),
        new ModuleColumn.Choosing(owner(), picking),
        () -> send(new RandomizerAction(RandomizerAction.Kind.OUTPUT, RandomizerAction.EDGE)),
        this::pick);
    return buttons;
  }

  private TimerButton modeButton(RandomizerMode mode) {
    int index = mode.ordinal();
    return new TimerButton(
        new Box(
            leftPos + MODE_X[index], topPos + MODE_Y, MODE_WIDTH[index], ModuleColumn.ROW_HEIGHT),
        Component.translatable(RandomizerScreenTexts.of(mode)),
        ModuleColumn.lookOf(menu.mode().equals(Optional.of(mode)), owner()),
        () -> send(RandomizerAction.mode(mode)));
  }

  /** `−` or `+`: a click moves 0.1 s, and Shift + click 1 s (answer 398). */
  private TimerButton stepButton(int x, String label, boolean longer) {
    return new TimerButton(
        new Box(
            leftPos + x, topPos + INTERVAL_Y, TimerControls.STEP_WIDTH, ModuleColumn.ROW_HEIGHT),
        Component.literal(label),
        owner() ? TimerButton.Look.BUTTON : TimerButton.Look.DISABLED,
        () -> send(RandomizerAction.step(longer, Screen.hasShiftDown())));
  }

  private boolean owner() {
    return menu.viewerIsOwner();
  }
}
