package io.github.zenndg.redweave.client.screen;

import io.github.zenndg.redweave.domain.module.Connection;
import io.github.zenndg.redweave.domain.signal.ChannelColor;
import io.github.zenndg.redweave.menu.DelayScreenTexts;
import io.github.zenndg.redweave.menu.FlipFlopAction;
import io.github.zenndg.redweave.menu.FlipFlopMenu;
import io.github.zenndg.redweave.menu.FlipFlopScreenTexts;
import io.github.zenndg.redweave.menu.LeverScreenTexts;
import io.github.zenndg.redweave.menu.NodeScreenTexts;
import io.github.zenndg.redweave.menu.TimerScreenTexts;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/**
 * The Signal Flip-Flop Screen: telas.md, Tela do Signal Flip-Flop. The diagram A → T → B and the
 * state kept on the left, `Strength`, `Delay`, `Input` and `Output` on the right, in the frame of
 * every module screen, as the Signal Inverter Screen (answers 884 and 890). Whoever does not own
 * the plate sees it read only, and the server decides every click (answer 378, rule 16).
 */
public final class FlipFlopScreen extends AbstractContainerScreen<FlipFlopMenu> {
  private static final int SEPARATOR_X = 130;
  private static final int STRENGTH_Y = 42;
  private static final int DELAY_Y = 60;
  private static final int INPUT_Y = 78;
  private static final int OUTPUT_Y = 96;
  private static final int GRID_Y = 114;
  private static final int TEXT_Y = 3;
  private static final String MINUS = "−";
  private static final String PLUS = "+";

  private final ModuleFrame frame;
  private Optional<FlipFlopAction.Kind> picking = Optional.empty();
  private Optional<FlipFlopAction.Kind> pending = Optional.empty();

  public FlipFlopScreen(FlipFlopMenu menu, Inventory inventory, Component title) {
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
    if (!frame.quiet()) {
      frame
          .tooltipAt(mouseX, mouseY)
          .ifPresent(text -> graphics.renderTooltip(font, text, mouseX, mouseY));
    }
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
    FlipFlopDiagram.draw(graphics, font, leftPos, topPos, menu.inputOn(), menu.state());
    menu.state().ifPresent(state -> drawPill(graphics, state));
    graphics.fill(
        leftPos + SEPARATOR_X,
        body.y() + ModuleFrame.TITLE_X,
        leftPos + SEPARATOR_X + 1,
        body.bottom() - ModuleFrame.TITLE_X,
        ScreenColors.SEPARATOR);
    drawSettings(graphics);
    ModuleColumn.label(graphics, font, leftPos, topPos + INPUT_Y, NodeScreenTexts.INPUT);
    ModuleColumn.label(graphics, font, leftPos, topPos + OUTPUT_Y, NodeScreenTexts.OUTPUT);
    ModuleColumn.channel(graphics, font, leftPos, topPos + INPUT_Y, menu.sync(), menu.input());
    ModuleColumn.channel(graphics, font, leftPos, topPos + OUTPUT_Y, menu.sync(), menu.output());
    buttons().forEach(button -> button.draw(graphics, font, mouse));
    picking.ifPresent(kind ->
        ChannelPicker.draw(
            graphics,
            font,
            leftPos + ModuleColumn.LABEL_X,
            topPos + GRID_Y,
            connectionOf(kind).channel(),
            mouse,
            owner()));
  }

  /** `Strength`, from 1 to 15, and `Delay`, in whole seconds, between `−` and `+` (830, 880). */
  private void drawSettings(GuiGraphics graphics) {
    ModuleColumn.label(graphics, font, leftPos, topPos + STRENGTH_Y, LeverScreenTexts.STRENGTH);
    ModuleColumn.label(graphics, font, leftPos, topPos + DELAY_Y, DelayScreenTexts.DELAY);
    drawValue(graphics, Component.literal(Integer.toString(menu.strength())), STRENGTH_Y);
    drawValue(
        graphics, Component.translatable(TimerScreenTexts.SECONDS, menu.delaySeconds()), DELAY_Y);
  }

  private void drawValue(GuiGraphics graphics, Component value, int y) {
    int x = leftPos + TimerControls.VALUE_X + (TimerControls.VALUE_WIDTH - font.width(value)) / 2;
    graphics.drawString(font, value, x, topPos + y + TEXT_Y, ScreenColors.TEXT, false);
  }

  /** `● On` or `● Off`, the state kept by the flip-flop (answer 890). */
  private void drawPill(GuiGraphics graphics, boolean on) {
    ModulePill.draw(
        graphics,
        font,
        frame.at(ModulePill.BOX),
        Component.translatable(FlipFlopScreenTexts.of(on)),
        on ? ScreenColors.REDSTONE : ScreenColors.DIM,
        ScreenColors.TEXT);
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
    for (TimerButton button : buttons()) {
      if (button.clickable() && button.box().contains(mouseX, mouseY)) {
        button.click().run();
        return true;
      }
    }
    return clickChannel(mouseX, mouseY);
  }

  private boolean clickChannel(double mouseX, double mouseY) {
    if (picking.isEmpty()) {
      return false;
    }
    Optional<ChannelColor> channel =
        ChannelPicker.at(leftPos + ModuleColumn.LABEL_X, topPos + GRID_Y, mouseX, mouseY);
    channel.ifPresent(color -> send(new FlipFlopAction(picking.get(), color.ordinal() + 1)));
    return channel.isPresent();
  }

  /** A network picked goes on to the channels the owner was choosing (answer 414). */
  private void chosen(int row) {
    send(FlipFlopAction.choose(row));
    picking = pending;
    pending = Optional.empty();
  }

  /** `Channel` opens the channels, or first the network choice without a network (414). */
  private void pick(FlipFlopAction.Kind kind) {
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

  private void send(FlipFlopAction action) {
    if (minecraft != null && minecraft.gameMode != null) {
      minecraft.gameMode.handleInventoryButtonClick(menu.containerId, action.button());
    }
    if (connects(action.kind()) && action.channel().isEmpty()) {
      picking = picking.filter(kind -> kind != action.kind());
    }
  }

  /** `−` and `+` of `Strength` and `Delay`, `Edge` and `Channel` of the connections (884). */
  private List<TimerButton> buttons() {
    List<TimerButton> buttons = new ArrayList<>();
    buttons.add(step(ModuleColumn.CONTROL_X, STRENGTH_Y, MINUS, FlipFlopAction.strength(false)));
    buttons.add(step(TimerControls.PLUS_X, STRENGTH_Y, PLUS, FlipFlopAction.strength(true)));
    buttons.add(step(ModuleColumn.CONTROL_X, DELAY_Y, MINUS, FlipFlopAction.delay(false)));
    buttons.add(step(TimerControls.PLUS_X, DELAY_Y, PLUS, FlipFlopAction.delay(true)));
    connection(buttons, FlipFlopAction.Kind.INPUT, menu.input(), INPUT_Y);
    connection(buttons, FlipFlopAction.Kind.OUTPUT, menu.output(), OUTPUT_Y);
    return buttons;
  }

  private void connection(
      List<TimerButton> buttons, FlipFlopAction.Kind kind, Connection now, int y) {
    ModuleColumn.connection(
        buttons,
        now,
        new Box(leftPos, topPos + y, 0, ModuleColumn.ROW_HEIGHT),
        new ModuleColumn.Choosing(owner(), picking.equals(Optional.of(kind))),
        () -> send(new FlipFlopAction(kind, FlipFlopAction.EDGE)),
        () -> pick(kind));
  }

  private TimerButton step(int x, int y, String label, FlipFlopAction action) {
    return new TimerButton(
        new Box(leftPos + x, topPos + y, TimerControls.STEP_WIDTH, ModuleColumn.ROW_HEIGHT),
        Component.literal(label),
        owner() ? TimerButton.Look.BUTTON : TimerButton.Look.DISABLED,
        () -> send(action));
  }

  private Connection connectionOf(FlipFlopAction.Kind kind) {
    return kind == FlipFlopAction.Kind.INPUT ? menu.input() : menu.output();
  }

  private static boolean connects(FlipFlopAction.Kind kind) {
    return kind == FlipFlopAction.Kind.INPUT || kind == FlipFlopAction.Kind.OUTPUT;
  }

  private boolean owner() {
    return menu.viewerIsOwner();
  }
}
