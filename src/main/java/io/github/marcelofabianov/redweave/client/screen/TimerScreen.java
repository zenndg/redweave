package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.domain.module.Connection;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.menu.TimerAction;
import io.github.marcelofabianov.redweave.menu.TimerMenu;
import io.github.marcelofabianov.redweave.menu.TimerScreenCodes;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/**
 * The Signal Timer Screen: telas.md, Tela do Signal Timer. The clock on the left, the cycle and the
 * connections on the right, in the frame of every module screen. Whoever does not own the plate
 * sees it read only, and the server decides every click (answers 378, 400, 404 and 414, rule 16).
 */
public final class TimerScreen extends AbstractContainerScreen<TimerMenu> {
  private static final int DIAL_X = 33;
  private static final int DIAL_Y = 40;
  private static final Box RUN = new Box(20, 120, 98, 16);
  private static final int SEPARATOR_X = 130;
  private static final int GRID_Y = 114;

  private final ModuleFrame frame;
  private Optional<TimerAction.Kind> picking = Optional.empty();
  private Optional<TimerAction.Kind> pending = Optional.empty();

  public TimerScreen(TimerMenu menu, Inventory inventory, Component title) {
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

  /** The ticks of the cycle, and for the owner how to rename and to switch networks. */
  private Optional<Component> tooltipAt(double mouseX, double mouseY) {
    if (frame.quiet()) {
      return Optional.empty();
    }
    return frame
        .tooltipAt(mouseX, mouseY)
        .or(() -> TimerScreenParts.ticksAt(leftPos, topPos, menu, mouseX, mouseY));
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
    TimerDial.draw(graphics, font, leftPos + DIAL_X, topPos + DIAL_Y, menu);
    runButton().draw(graphics, font, mouse);
    graphics.fill(
        leftPos + SEPARATOR_X,
        body.y() + ModuleFrame.TITLE_X,
        leftPos + SEPARATOR_X + 1,
        body.bottom() - ModuleFrame.TITLE_X,
        ScreenColors.SEPARATOR);
    TimerScreenParts.drawLabels(graphics, font, leftPos, topPos, menu);
    controls().forEach(button -> button.draw(graphics, font, mouse));
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
    channel.ifPresent(color -> send(new TimerAction(picking.get(), color.ordinal() + 1)));
    return channel.isPresent();
  }

  /** A network picked goes on to the channels the owner was choosing (answer 414). */
  private void chosen(int row) {
    send(TimerAction.choose(row));
    picking = pending;
    pending = Optional.empty();
  }

  /** `Channel` opens the channels, or first the network choice without a network (414). */
  private void pick(TimerAction.Kind kind) {
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

  private void send(TimerAction action) {
    if (minecraft != null && minecraft.gameMode != null) {
      minecraft.gameMode.handleInventoryButtonClick(
          menu.containerId, TimerScreenCodes.button(action));
    }
    if (action.kind() != TimerAction.Kind.CHOOSE && action.channel().isEmpty()) {
      picking = picking.filter(kind -> kind != action.kind());
    }
  }

  private List<TimerButton> buttons() {
    List<TimerButton> all = new ArrayList<>(controls());
    all.add(runButton());
    return all;
  }

  private List<TimerButton> controls() {
    return TimerControls.of(
        menu, leftPos, topPos, owner(), new TimerControls.Clicks(this::send, this::pick));
  }

  private TimerButton runButton() {
    return TimerScreenParts.runButton(
        frame.at(RUN), menu, owner(), () -> send(TimerAction.toggle()));
  }

  private Connection connectionOf(TimerAction.Kind kind) {
    return kind == TimerAction.Kind.OUTPUT ? menu.output() : menu.pause();
  }

  private boolean owner() {
    return menu.viewerIsOwner();
  }
}
