package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.domain.module.Connection;
import io.github.marcelofabianov.redweave.domain.module.InverterState;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.menu.InverterAction;
import io.github.marcelofabianov.redweave.menu.InverterMenu;
import io.github.marcelofabianov.redweave.menu.InverterScreenTexts;
import io.github.marcelofabianov.redweave.menu.NodeScreenTexts;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/**
 * The Signal Inverter Screen: telas.md, Tela do Signal Inverter. The diagram A → NOT → B and the
 * state of B on the left, `Input` and `Output` on the right, in the frame of every module screen.
 * Whoever does not own the plate sees it read only, and the server decides every click (answers
 * 378, 414, 448 and 451, rule 16).
 */
public final class InverterScreen extends AbstractContainerScreen<InverterMenu> {
  private static final int SEPARATOR_X = 130;
  private static final int INPUT_Y = 42;
  private static final int OUTPUT_Y = 60;
  private static final int GRID_Y = 82;
  private static final Box PILL = new Box(17, 90, 92, 13);
  private static final int DOT_X = 5;
  private static final int DOT_Y = 4;
  private static final int PILL_TEXT_Y = 3;

  private final ModuleFrame frame;
  private Optional<InverterAction.Kind> picking = Optional.empty();
  private Optional<InverterAction.Kind> pending = Optional.empty();

  public InverterScreen(InverterMenu menu, Inventory inventory, Component title) {
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
    Box body = frame.at(ModuleFrame.BODY);
    graphics.blitSprite(NodeSprites.PANEL, body.x(), body.y(), body.width(), body.height());
    InverterDiagram.draw(graphics, font, leftPos, topPos, menu.inputOn(), menu.state());
    menu.state().ifPresent(state -> drawPill(graphics, state));
    graphics.fill(
        leftPos + SEPARATOR_X,
        body.y() + ModuleFrame.TITLE_X,
        leftPos + SEPARATOR_X + 1,
        body.bottom() - ModuleFrame.TITLE_X,
        ScreenColors.SEPARATOR);
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

  /** `● On`, `● Off` or `● Burnt out`, in amber (answer 448). */
  private void drawPill(GuiGraphics graphics, InverterState state) {
    Box box = frame.at(PILL);
    graphics.blitSprite(NodeSprites.STATE_PILL, box.x(), box.y(), box.width(), box.height());
    Component text = Component.translatable(textOf(state));
    int width = DOT_X + font.width(TimerButton.DOT) + font.width(text);
    int x = box.x() + (box.width() - width) / 2;
    Dots.draw(graphics, x + DOT_X, box.y() + DOT_Y, dotOf(state));
    int color = state == InverterState.BURNT_OUT ? ScreenColors.BURNT_OUT : ScreenColors.TEXT;
    graphics.drawString(
        font, text, x + DOT_X + font.width(TimerButton.DOT), box.y() + PILL_TEXT_Y, color, false);
  }

  private static String textOf(InverterState state) {
    return switch (state) {
      case ON -> InverterScreenTexts.ON;
      case OFF -> InverterScreenTexts.OFF;
      case BURNT_OUT -> InverterScreenTexts.BURNT_OUT;
    };
  }

  private static int dotOf(InverterState state) {
    return switch (state) {
      case ON -> ScreenColors.REDSTONE;
      case OFF -> ScreenColors.DIM;
      case BURNT_OUT -> ScreenColors.BURNT_OUT;
    };
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
    channel.ifPresent(color -> send(new InverterAction(picking.get(), color.ordinal() + 1)));
    return channel.isPresent();
  }

  /** A network picked goes on to the channels the owner was choosing (answer 414). */
  private void chosen(int row) {
    send(InverterAction.choose(row));
    picking = pending;
    pending = Optional.empty();
  }

  /** `Channel` opens the channels, or first the network choice without a network (414). */
  private void pick(InverterAction.Kind kind) {
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

  private void send(InverterAction action) {
    if (minecraft != null && minecraft.gameMode != null) {
      minecraft.gameMode.handleInventoryButtonClick(menu.containerId, action.button());
    }
    if (action.kind() != InverterAction.Kind.CHOOSE && action.channel().isEmpty()) {
      picking = picking.filter(kind -> kind != action.kind());
    }
  }

  /** `Edge` and `Channel` for `Input` and for `Output` (answers 441 and 451). */
  private List<TimerButton> buttons() {
    List<TimerButton> buttons = new ArrayList<>();
    connection(buttons, InverterAction.Kind.INPUT, menu.input(), INPUT_Y);
    connection(buttons, InverterAction.Kind.OUTPUT, menu.output(), OUTPUT_Y);
    return buttons;
  }

  private void connection(
      List<TimerButton> buttons, InverterAction.Kind kind, Connection now, int y) {
    ModuleColumn.connection(
        buttons,
        now,
        new Box(leftPos, topPos + y, 0, ModuleColumn.ROW_HEIGHT),
        new ModuleColumn.Choosing(owner(), picking.equals(Optional.of(kind))),
        () -> send(new InverterAction(kind, InverterAction.EDGE)),
        () -> pick(kind));
  }

  private Connection connectionOf(InverterAction.Kind kind) {
    return kind == InverterAction.Kind.INPUT ? menu.input() : menu.output();
  }

  private boolean owner() {
    return menu.viewerIsOwner();
  }
}
