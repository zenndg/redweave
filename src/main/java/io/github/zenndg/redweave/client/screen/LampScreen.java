package io.github.zenndg.redweave.client.screen;

import io.github.zenndg.redweave.domain.module.Connection;
import io.github.zenndg.redweave.domain.module.LampMode;
import io.github.zenndg.redweave.domain.signal.ChannelColor;
import io.github.zenndg.redweave.menu.LampAction;
import io.github.zenndg.redweave.menu.LampMenu;
import io.github.zenndg.redweave.menu.LampScreenTexts;
import io.github.zenndg.redweave.menu.LeverScreenTexts;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/**
 * The Signal Lamp Screen: lampada.md. The state, `On` or `Off`, large on the left, with the light
 * under it, and `Strength`, the intensity channel, the control channel and the mode of the control
 * on the right, in the frame of every module screen. Anyone else sees it read only, and the server
 * decides every click (answers 378, 650, 651 and 656, rule 16).
 */
public final class LampScreen extends AbstractContainerScreen<LampMenu> {
  private static final int SEPARATOR_X = 130;
  private static final int STRENGTH_Y = 42;
  private static final int INTENSITY_Y = 78;
  private static final int CONTROL_Y = 96;
  private static final int MODE_Y = 114;
  private static final int GRID_Y = 132;
  private static final int DELAY_Y = 60;
  private static final int LIGHT_X = 22;
  private static final int LIGHT_Y = 82;
  private static final int MODE_WIDTH = 40;
  private static final int MODE_GAP = 2;
  private static final int TEXT_Y = 3;
  private static final Box DISPLAY = new Box(29, 44, 68, 30);
  private static final int DISPLAY_FILL = 0xFF16171B;
  private static final int SCALE = 2;
  private static final String MINUS = "−";
  private static final String PLUS = "+";
  private static final String LIGHT_FORMAT = "%s: %d";

  private final ModuleFrame frame;
  private Optional<LampAction.Kind> picking = Optional.empty();
  private Optional<LampAction.Kind> pending = Optional.empty();

  public LampScreen(LampMenu menu, Inventory inventory, Component title) {
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
    drawState(graphics);
    graphics.drawString(
        font,
        String.format(
            LIGHT_FORMAT, Component.translatable(LampScreenTexts.LIGHT).getString(), menu.light()),
        leftPos + LIGHT_X,
        topPos + LIGHT_Y,
        ScreenColors.LABEL,
        false);
    graphics.fill(
        leftPos + SEPARATOR_X,
        body.y() + ModuleFrame.TITLE_X,
        leftPos + SEPARATOR_X + 1,
        body.bottom() - ModuleFrame.TITLE_X,
        ScreenColors.SEPARATOR);
    drawLabels(graphics);
    DelayRow.draw(graphics, font, leftPos, topPos + DELAY_Y, menu.delaySeconds());
    buttons().forEach(button -> button.draw(graphics, font, mouse));
    picking.ifPresent(kind -> drawPicker(graphics, kind, mouse));
  }

  private void drawLabels(GuiGraphics graphics) {
    ModuleColumn.label(graphics, font, leftPos, topPos + STRENGTH_Y, LeverScreenTexts.STRENGTH);
    ModuleColumn.label(graphics, font, leftPos, topPos + INTENSITY_Y, LampScreenTexts.INTENSITY);
    ModuleColumn.label(graphics, font, leftPos, topPos + CONTROL_Y, LampScreenTexts.CONTROL);
    ModuleColumn.label(graphics, font, leftPos, topPos + MODE_Y, LampScreenTexts.MODE);
    Component strength = Component.literal(Integer.toString(menu.strength()));
    int x =
        leftPos + TimerControls.VALUE_X + (TimerControls.VALUE_WIDTH - font.width(strength)) / 2;
    graphics.drawString(font, strength, x, topPos + STRENGTH_Y + TEXT_Y, ScreenColors.TEXT, false);
    ModuleColumn.channel(
        graphics, font, leftPos, topPos + INTENSITY_Y, menu.sync(), menu.intensity());
    ModuleColumn.channel(graphics, font, leftPos, topPos + CONTROL_Y, menu.sync(), menu.control());
  }

  private void drawPicker(GuiGraphics graphics, LampAction.Kind kind, Mouse mouse) {
    ChannelPicker.draw(
        graphics,
        font,
        leftPos + ModuleColumn.LABEL_X,
        topPos + GRID_Y,
        connectionOf(kind).channel(),
        mouse,
        owner());
  }

  /** `On` in green or `Off` dim, large, as the light of the lamp (answer 651). */
  private void drawState(GuiGraphics graphics) {
    Box display = frame.at(DISPLAY);
    graphics.fill(display.x(), display.y(), display.right(), display.bottom(), DISPLAY_FILL);
    Optional<Boolean> lit = menu.lit();
    if (lit.isEmpty()) {
      return;
    }
    Component text = Component.translatable(LeverScreenTexts.of(lit.get()));
    int color = lit.get() ? ScreenColors.ONLINE : ScreenColors.DIM;
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
    if (picking.isEmpty()) {
      return false;
    }
    Optional<ChannelColor> channel =
        ChannelPicker.at(leftPos + ModuleColumn.LABEL_X, topPos + GRID_Y, mouseX, mouseY);
    channel.ifPresent(
        color -> send(LampAction.connect(picking.get(), Optional.of(color.ordinal()))));
    return channel.isPresent();
  }

  /** A network picked goes on to the channels the owner was choosing (answer 414). */
  private void chosen(int row) {
    send(LampAction.choose(row));
    picking = pending;
    pending = Optional.empty();
  }

  /** `Channel` opens the channels, or first the network choice without a network (414). */
  private void pick(LampAction.Kind kind) {
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

  private void send(LampAction action) {
    if (minecraft != null && minecraft.gameMode != null) {
      minecraft.gameMode.handleInventoryButtonClick(menu.containerId, action.button());
    }
    if (action.kind() == LampAction.Kind.INTENSITY || action.kind() == LampAction.Kind.CONTROL) {
      picking = Optional.empty();
    }
  }

  /** `−` and `+` of `Strength`, `None` and `Channel` of both channels, and the two modes. */
  private List<TimerButton> buttons() {
    List<TimerButton> buttons = new ArrayList<>();
    DelayRow.buttons(
        buttons,
        leftPos,
        topPos + DELAY_Y,
        owner(),
        value -> send(new LampAction(LampAction.Kind.DELAY, value)));
    buttons.add(step(ModuleColumn.CONTROL_X, MINUS, () -> LampAction.strength(false)));
    buttons.add(step(TimerControls.PLUS_X, PLUS, () -> LampAction.strength(true)));
    connection(buttons, LampAction.Kind.INTENSITY, menu.intensity(), INTENSITY_Y);
    connection(buttons, LampAction.Kind.CONTROL, menu.control(), CONTROL_Y);
    for (LampMode mode : LampMode.values()) {
      int x = leftPos + ModuleColumn.CONTROL_X + mode.ordinal() * (MODE_WIDTH + MODE_GAP);
      buttons.add(
          new TimerButton(
              new Box(x, topPos + MODE_Y, MODE_WIDTH, ModuleColumn.ROW_HEIGHT),
              Component.translatable(LampScreenTexts.of(mode)),
              ModuleColumn.lookOf(menu.mode() == mode, owner()),
              () -> send(LampAction.mode(mode.ordinal()))));
    }
    return buttons;
  }

  private void connection(List<TimerButton> buttons, LampAction.Kind kind, Connection now, int y) {
    ModuleColumn.connection(
        buttons,
        now,
        new Box(leftPos, topPos + y, 0, ModuleColumn.ROW_HEIGHT),
        new ModuleColumn.Choosing(owner(), picking.equals(Optional.of(kind))),
        LampScreenTexts.NONE,
        () -> send(LampAction.connect(kind, Optional.empty())),
        () -> pick(kind));
  }

  private TimerButton step(int x, String label, Supplier<LampAction> action) {
    return new TimerButton(
        new Box(
            leftPos + x, topPos + STRENGTH_Y, TimerControls.STEP_WIDTH, ModuleColumn.ROW_HEIGHT),
        Component.literal(label),
        owner() ? TimerButton.Look.BUTTON : TimerButton.Look.DISABLED,
        () -> send(action.get()));
  }

  private Connection connectionOf(LampAction.Kind kind) {
    return kind == LampAction.Kind.INTENSITY ? menu.intensity() : menu.control();
  }

  private boolean owner() {
    return menu.viewerIsOwner();
  }
}
