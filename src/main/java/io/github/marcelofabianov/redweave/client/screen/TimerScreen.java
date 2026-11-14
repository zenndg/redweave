package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.domain.module.Connection;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.menu.NodeScreenTexts;
import io.github.marcelofabianov.redweave.menu.TimerAction;
import io.github.marcelofabianov.redweave.menu.TimerMenu;
import io.github.marcelofabianov.redweave.menu.TimerScreenCodes;
import io.github.marcelofabianov.redweave.network.ModuleRenamed;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * The Signal Timer Screen: telas.md, Tela do Signal Timer. The clock on the left, the cycle and the
 * connections on the right and the network in the footer. Whoever does not own the plate sees it
 * read only, and the server decides every click (answers 378, 400, 404 and 414, rule 16).
 */
public final class TimerScreen extends AbstractContainerScreen<TimerMenu> {
  private static final int WIDTH = 340;
  private static final int HEIGHT = 168;
  private static final int TITLE_X = 10;
  private static final int TITLE_Y = 9;
  private static final int NAME_TEXT_Y = 2;
  private static final int ICON_SIZE = 8;
  private static final int ICON_GAP = 4;
  private static final int ICON_Y = 2;
  private static final Box BODY = new Box(10, 34, 320, 110);
  private static final Box FOOTER = new Box(10, 146, 320, 20);
  private static final int DIAL_X = 33;
  private static final int DIAL_Y = 40;
  private static final Box RUN = new Box(20, 120, 98, 16);
  private static final int SEPARATOR_X = 130;
  private static final int GRID_Y = 114;
  private static final Box BACK = new Box(282, 148, 48, 16);

  private final NameField name = new NameField();
  private final NetworkChoiceList choices = new NetworkChoiceList();
  private boolean switching;
  private Optional<TimerAction.Kind> picking = Optional.empty();
  private Optional<TimerAction.Kind> pending = Optional.empty();

  public TimerScreen(TimerMenu menu, Inventory inventory, Component title) {
    super(menu, inventory, title);
    imageWidth = WIDTH;
    imageHeight = HEIGHT;
  }

  @Override
  protected void init() {
    super.init();
    name.attach(font, NodeHeader.fieldBox(leftPos, topPos), this::addRenderableWidget);
  }

  @Override
  public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    super.render(graphics, mouseX, mouseY, partialTick);
    tooltipAt(mouseX, mouseY).ifPresent(text -> graphics.renderTooltip(font, text, mouseX, mouseY));
  }

  /** The ticks of the cycle, and for the owner how to rename and to switch networks. */
  private Optional<Component> tooltipAt(double mouseX, double mouseY) {
    if (switching || name.editing()) {
      return Optional.empty();
    }
    if (owner() && nameBox().contains(mouseX, mouseY)) {
      return Optional.of(NodeHeader.renameTooltip());
    }
    Optional<Box> link = TimerFooter.linkBox(font, at(FOOTER), menu);
    if (owner() && link.isPresent() && link.get().contains(mouseX, mouseY)) {
      return Optional.of(NodeHeader.switchTooltip());
    }
    return TimerScreenParts.ticksAt(leftPos, topPos, menu, mouseX, mouseY);
  }

  @Override
  protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
    Mouse mouse = new Mouse(mouseX, mouseY);
    graphics.blitSprite(NodeSprites.WINDOW, leftPos, topPos, WIDTH, HEIGHT);
    graphics.drawString(font, title, leftPos + TITLE_X, topPos + TITLE_Y, ScreenColors.TEXT, false);
    name.drawBackground(graphics);
    if (!name.editing()) {
      drawName(graphics);
    }
    if (switching) {
      choices.draw(graphics, font, at(BODY), menu.choices(), mouseX, mouseY);
      backButton().draw(graphics, font, mouse);
      return;
    }
    drawBody(graphics, mouse);
    TimerFooter.draw(graphics, font, at(FOOTER), menu, mouse, owner());
  }

  private void drawBody(GuiGraphics graphics, Mouse mouse) {
    Box body = at(BODY);
    graphics.blitSprite(NodeSprites.PANEL, body.x(), body.y(), body.width(), body.height());
    TimerDial.draw(graphics, font, leftPos + DIAL_X, topPos + DIAL_Y, menu);
    runButton().draw(graphics, font, mouse);
    graphics.fill(
        leftPos + SEPARATOR_X,
        body.y() + TITLE_X,
        leftPos + SEPARATOR_X + 1,
        body.bottom() - TITLE_X,
        ScreenColors.SEPARATOR);
    TimerScreenParts.drawLabels(graphics, font, leftPos, topPos, menu);
    controls().forEach(button -> button.draw(graphics, font, mouse));
    picking.ifPresent(kind ->
        ChannelPicker.draw(
            graphics,
            font,
            leftPos + TimerControls.LABEL_X,
            topPos + GRID_Y,
            connectionOf(kind).channel(),
            mouse,
            owner()));
  }

  private void drawName(GuiGraphics graphics) {
    Box box = nameBox();
    graphics.drawString(
        font, menu.moduleName(), box.x(), box.y() + NAME_TEXT_Y, ScreenColors.LABEL, false);
    if (owner()) {
      graphics.blitSprite(
          NodeSprites.ICON_EDIT, box.right() - ICON_SIZE, box.y() + ICON_Y, ICON_SIZE, ICON_SIZE);
    }
  }

  /** Every text is drawn in renderBg, so the game does not draw its own labels on top. */
  @Override
  protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {}

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    if (name.editing() && !name.box().contains(mouseX, mouseY)) {
      name.cancel();
    }
    if (switching) {
      return clickChoice(mouseX, mouseY) || super.mouseClicked(mouseX, mouseY, button);
    }
    return clickOwned(mouseX, mouseY) || super.mouseClicked(mouseX, mouseY, button);
  }

  /** The name, the network, the buttons and the channels, only for the owner (answer 378). */
  private boolean clickOwned(double mouseX, double mouseY) {
    if (!owner()) {
      return false;
    }
    if (nameBox().contains(mouseX, mouseY)) {
      name.start(menu.moduleName(), NameField.moduleLimit(), this::setFocused);
      return true;
    }
    Optional<Box> link = TimerFooter.linkBox(font, at(FOOTER), menu);
    if (link.isPresent() && link.get().contains(mouseX, mouseY)) {
      switching = true;
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
        ChannelPicker.at(leftPos + TimerControls.LABEL_X, topPos + GRID_Y, mouseX, mouseY);
    channel.ifPresent(color -> send(new TimerAction(picking.get(), color.ordinal() + 1)));
    return channel.isPresent();
  }

  /** A network picked goes on to the channels the owner was choosing (answer 414). */
  private boolean clickChoice(double mouseX, double mouseY) {
    Optional<Integer> row = choices.rowAt(at(BODY), menu.choices().size(), mouseX, mouseY);
    if (row.isPresent()) {
      send(TimerAction.choose(row.get()));
      switching = false;
      picking = pending;
      pending = Optional.empty();
      return true;
    }
    if (backButton().box().contains(mouseX, mouseY)) {
      switching = false;
      pending = Optional.empty();
      return true;
    }
    return false;
  }

  /** `Channel` opens the channels, or first the network choice without a network (414). */
  private void pick(TimerAction.Kind kind) {
    if (menu.networkColor().isEmpty()) {
      switching = true;
      pending = Optional.of(kind);
      return;
    }
    picking = Optional.of(kind);
  }

  /** Enter saves the name, Esc cancels it, and no key closes the screen while typing. */
  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    if (name.editing()) {
      name.keyPressed(
          keyCode,
          scanCode,
          modifiers,
          text -> PacketDistributor.sendToServer(new ModuleRenamed(menu.pos(), text)));
      return true;
    }
    return super.keyPressed(keyCode, scanCode, modifiers);
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
    if (switching && at(BODY).contains(mouseX, mouseY)) {
      choices.scroll(scrollY, at(BODY), menu.choices().size());
      return true;
    }
    return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
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
    return TimerScreenParts.runButton(at(RUN), menu, owner(), () -> send(TimerAction.toggle()));
  }

  private TimerButton backButton() {
    return new TimerButton(
        at(BACK),
        Component.translatable(NodeScreenTexts.BACK),
        TimerButton.Look.BUTTON,
        () -> switching = false);
  }

  private Box nameBox() {
    int width = font.width(menu.moduleName()) + (owner() ? ICON_GAP + ICON_SIZE : 0);
    Box field = NodeHeader.fieldBox(leftPos, topPos);
    return new Box(field.x(), field.y(), width, field.height());
  }

  private Connection connectionOf(TimerAction.Kind kind) {
    return kind == TimerAction.Kind.OUTPUT ? menu.output() : menu.pause();
  }

  private Box at(Box relative) {
    return new Box(
        leftPos + relative.x(), topPos + relative.y(), relative.width(), relative.height());
  }

  private boolean owner() {
    return menu.viewerIsOwner();
  }
}
