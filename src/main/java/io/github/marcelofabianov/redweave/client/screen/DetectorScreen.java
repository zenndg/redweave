package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.domain.module.DetectorState;
import io.github.marcelofabianov.redweave.domain.module.Weather;
import io.github.marcelofabianov.redweave.domain.module.WeatherMode;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.menu.DetectorAction;
import io.github.marcelofabianov.redweave.menu.DetectorMenu;
import io.github.marcelofabianov.redweave.menu.DetectorScreenTexts;
import io.github.marcelofabianov.redweave.menu.NodeScreenTexts;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * The Weather Detector Screen: telas.md, Tela do Weather Detector. The icon of the weather and the
 * state on the left, `Mode` and `Output` on the right, in the frame of every module screen. Whoever
 * does not own the plate sees it read only, and the server decides every click (answers 378, 414,
 * 465, 470 and 474, rule 16).
 */
public final class DetectorScreen extends AbstractContainerScreen<DetectorMenu> {
  private static final int SEPARATOR_X = 130;
  private static final int MODE_Y = 42;
  private static final int OUTPUT_Y = 78;
  private static final int GRID_Y = 100;
  private static final int DELAY_Y = 60;
  private static final Box ICON_BACK = new Box(43, 42, 40, 40);
  private static final int ICON_SIZE = 32;
  private static final int ICON_INSET = 4;
  private static final int ICON_FILL = 0xFF2A2D33;
  private static final int[] MODE_X = {184, 218, 264};
  private static final int[] MODE_WIDTH = {32, 44, 36};
  private static final String ICON_FOLDER = "weather_detector_screen/";
  private static final String CLEAR = "clear";
  private static final String RAIN = "rain";
  private static final String THUNDER = "thunder";
  private static final String NO_WEATHER = "no_weather";

  /** One sprite for each weather, in the order of Weather (answer 474). */
  private static final ResourceLocation[] ICONS = {
    icon(CLEAR), icon(RAIN), icon(THUNDER), icon(NO_WEATHER)
  };

  private final ModuleFrame frame;
  private boolean picking;
  private boolean pending;

  public DetectorScreen(DetectorMenu menu, Inventory inventory, Component title) {
    super(menu, inventory, title);
    imageWidth = ModuleFrame.WIDTH;
    imageHeight = ModuleFrame.HEIGHT;
    frame = new ModuleFrame(menu, menu::viewerIsOwner);
  }

  private static ResourceLocation icon(String name) {
    return ResourceLocation.fromNamespaceAndPath(RedweaveMod.MOD_ID, ICON_FOLDER + name);
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
    drawIcon(graphics);
    menu.state().ifPresent(state -> drawPill(graphics, state));
    graphics.fill(
        leftPos + SEPARATOR_X,
        body.y() + ModuleFrame.TITLE_X,
        leftPos + SEPARATOR_X + 1,
        body.bottom() - ModuleFrame.TITLE_X,
        ScreenColors.SEPARATOR);
    ModuleColumn.label(graphics, font, leftPos, topPos + MODE_Y, DetectorScreenTexts.MODE);
    ModuleColumn.label(graphics, font, leftPos, topPos + OUTPUT_Y, NodeScreenTexts.OUTPUT);
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

  /** The sprite of the weather of the last tick, on a dark square (answers 470 and 474). */
  private void drawIcon(GuiGraphics graphics) {
    Box back = frame.at(ICON_BACK);
    graphics.fill(back.x(), back.y(), back.right(), back.bottom(), ICON_FILL);
    Optional<Weather> weather = menu.weather();
    if (weather.isPresent()) {
      graphics.blitSprite(
          ICONS[weather.get().ordinal()],
          back.x() + ICON_INSET,
          back.y() + ICON_INSET,
          ICON_SIZE,
          ICON_SIZE);
    }
  }

  /** `● On`, `● Off` or `● No weather`, dimmed (answer 465). */
  private void drawPill(GuiGraphics graphics, DetectorState state) {
    int dot = state == DetectorState.ON ? ScreenColors.REDSTONE : ScreenColors.DIM;
    int text = state == DetectorState.NO_WEATHER ? ScreenColors.DIM : ScreenColors.TEXT;
    ModulePill.draw(
        graphics,
        font,
        frame.at(ModulePill.BOX),
        Component.translatable(DetectorScreenTexts.of(state)),
        dot,
        text);
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
        color -> send(new DetectorAction(DetectorAction.Kind.OUTPUT, color.ordinal() + 1)));
    return channel.isPresent();
  }

  /** A network picked goes on to the channels the owner was choosing (answer 414). */
  private void chosen(int row) {
    send(DetectorAction.choose(row));
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

  private void send(DetectorAction action) {
    if (minecraft != null && minecraft.gameMode != null) {
      minecraft.gameMode.handleInventoryButtonClick(menu.containerId, action.button());
    }
    if (action.kind() == DetectorAction.Kind.OUTPUT && action.channel().isEmpty()) {
      picking = false;
    }
  }

  /** `Rain`, `Thunder` and `Clear`, and `Edge` and `Channel` for `Output` (459 and 470). */
  private List<TimerButton> buttons() {
    List<TimerButton> buttons = new ArrayList<>();
    DelayRow.buttons(
        buttons,
        leftPos,
        topPos + DELAY_Y,
        owner(),
        value -> send(new DetectorAction(DetectorAction.Kind.DELAY, value)));
    for (WeatherMode mode : WeatherMode.values()) {
      buttons.add(modeButton(mode));
    }
    ModuleColumn.connection(
        buttons,
        menu.output(),
        new Box(leftPos, topPos + OUTPUT_Y, 0, ModuleColumn.ROW_HEIGHT),
        new ModuleColumn.Choosing(owner(), picking),
        () -> send(new DetectorAction(DetectorAction.Kind.OUTPUT, DetectorAction.EDGE)),
        this::pick);
    return buttons;
  }

  private TimerButton modeButton(WeatherMode mode) {
    int index = mode.ordinal();
    return new TimerButton(
        new Box(
            leftPos + MODE_X[index], topPos + MODE_Y, MODE_WIDTH[index], ModuleColumn.ROW_HEIGHT),
        Component.translatable(DetectorScreenTexts.of(mode)),
        ModuleColumn.lookOf(menu.mode().equals(Optional.of(mode)), owner()),
        () -> send(DetectorAction.mode(mode)));
  }

  private boolean owner() {
    return menu.viewerIsOwner();
  }
}
