package io.github.zenndg.redweave.client.screen;

import io.github.zenndg.redweave.client.ChannelColors;
import io.github.zenndg.redweave.domain.network.NodeState;
import io.github.zenndg.redweave.domain.signal.ChannelColor;
import io.github.zenndg.redweave.item.RemoteMessages;
import io.github.zenndg.redweave.item.RemoteMode;
import io.github.zenndg.redweave.menu.NodeScreenTexts;
import io.github.zenndg.redweave.menu.RemoteLinkCodes;
import io.github.zenndg.redweave.menu.RemoteLinkLine;
import io.github.zenndg.redweave.menu.RemoteLinkMenu;
import io.github.zenndg.redweave.menu.RemoteLinkStep;
import io.github.zenndg.redweave.menu.RemoteLinkTexts;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

/**
 * The screen of the link of a Signal Remote (telas.md and answer 365): the link of now and the mode
 * on top, the lines of the step and Back. It only shows what the server sends and asks by position;
 * the server decides (rule 16).
 */
public final class RemoteLinkScreen extends AbstractContainerScreen<RemoteLinkMenu> {
  private static final int WIDTH = 300;
  private static final int HEIGHT = 176;
  private static final int SIDE = 8;
  private static final int TITLE_Y = 8;
  private static final int CURRENT_Y = 24;
  private static final int STEP_Y = 40;
  private static final Box LIST = new Box(SIDE, 52, 284, 96);
  private static final int ROW_HEIGHT = 20;
  private static final int ROW_GAP = 1;
  private static final int ROW_PADDING = 6;
  private static final int SWATCH = 7;
  private static final int SWATCH_Y = 6;
  private static final int NAME_X = 18;
  private static final int TEXT_Y = 6;
  private static final int BUTTON_HEIGHT = 14;
  private static final int BUTTON_PADDING = 12;
  private static final int BUTTON_GAP = 2;
  private static final int BACK_Y = 154;
  private static final int LIST_Y_OF_EMPTY = 2;
  private static final int LABEL_NUDGE = 1;
  private static final String STATE_GAP = "  ";

  private int scroll;

  public RemoteLinkScreen(RemoteLinkMenu menu, Inventory inventory, Component title) {
    super(menu, inventory, title);
    imageWidth = WIDTH;
    imageHeight = HEIGHT;
  }

  @Override
  protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
    graphics.blitSprite(NodeSprites.WINDOW, leftPos, topPos, WIDTH, HEIGHT);
    graphics.drawString(font, title, leftPos + SIDE, topPos + TITLE_Y, ScreenColors.TEXT, false);
    Mouse mouse = new Mouse(mouseX, mouseY);
    for (ModeButton button : modeButtons()) {
      drawButton(graphics, button.box(), button.label(), button.chosen(), mouse);
    }
    graphics.drawString(
        font, menu.rows().current(), leftPos + SIDE, topPos + CURRENT_Y, ScreenColors.LABEL, false);
    graphics.drawString(
        font, stepTitle(), leftPos + SIDE, topPos + STEP_Y, ScreenColors.TEXT, false);
    Box area = listArea();
    graphics.blitSprite(NodeSprites.PANEL, area.x(), area.y(), area.width(), area.height());
    drawLines(graphics, area, mouse);
    if (step() != RemoteLinkStep.NETWORKS) {
      drawButton(graphics, backBox(), Component.translatable(NodeScreenTexts.BACK), false, mouse);
    }
  }

  /** Every text is drawn in renderBg, so the game does not draw its own labels on top. */
  @Override
  protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {}

  private void drawLines(GuiGraphics graphics, Box area, Mouse mouse) {
    List<RemoteLinkLine> lines = menu.rows().lines();
    if (lines.isEmpty()) {
      Component empty = Component.translatable(emptyKey());
      int x = area.x() + (area.width() - font.width(empty)) / 2;
      int y = area.y() + area.height() / LIST_Y_OF_EMPTY;
      graphics.drawString(font, empty, x, y, ScreenColors.LABEL, false);
      return;
    }
    for (int shown = 0; shown < visible() && scroll + shown < lines.size(); shown++) {
      Box box = rowBox(area, shown);
      ResourceLocation sprite =
          mouse.hovers(box) ? NodeSprites.NETWORK_ROW_HOVER : NodeSprites.NETWORK_ROW;
      graphics.blitSprite(sprite, box.x(), box.y(), box.width(), box.height());
      drawLine(graphics, box, lines.get(scroll + shown));
    }
  }

  private void drawLine(GuiGraphics graphics, Box box, RemoteLinkLine line) {
    if (line.swatch() != RemoteLinkLine.NONE) {
      int x = box.x() + ROW_PADDING;
      int y = box.y() + SWATCH_Y;
      int color = ChannelColors.argb(ChannelColor.values()[line.swatch()]);
      graphics.fill(x, y, x + SWATCH, y + SWATCH, color);
    }
    int y = box.y() + TEXT_Y;
    graphics.drawString(font, line.name(), box.x() + NAME_X, y, ScreenColors.TEXT, false);
    Component right = rightOf(line);
    int x = box.right() - ROW_PADDING - font.width(right);
    graphics.drawString(font, right, x, y, colorOf(line), false);
  }

  /** The detail, and for a node its state too, so it never stands on the color alone (284). */
  private static Component rightOf(RemoteLinkLine line) {
    if (line.state() == RemoteLinkLine.NONE) {
      return line.detail();
    }
    return line.detail().copy().append(STATE_GAP).append(StatePill.textOf(stateOf(line)));
  }

  private static int colorOf(RemoteLinkLine line) {
    return line.state() == RemoteLinkLine.NONE
        ? ScreenColors.LABEL
        : StatePill.colorOf(stateOf(line));
  }

  private static Optional<NodeState> stateOf(RemoteLinkLine line) {
    NodeState[] states = NodeState.values();
    return line.state() >= 0 && line.state() < states.length
        ? Optional.of(states[line.state()])
        : Optional.empty();
  }

  private void drawButton(
      GuiGraphics graphics, Box box, Component label, boolean chosen, Mouse mouse) {
    ResourceLocation sprite;
    if (chosen) {
      sprite = mouse.hovers(box) ? NodeSprites.BUTTON_SELECTED_HOVER : NodeSprites.BUTTON_SELECTED;
    } else {
      sprite = mouse.hovers(box) ? NodeSprites.BUTTON_HOVER : NodeSprites.BUTTON;
    }
    graphics.blitSprite(sprite, box.x(), box.y(), box.width(), box.height());
    int x = box.x() + (box.width() - font.width(label)) / 2;
    int color = chosen ? ScreenColors.ON_COPPER : ScreenColors.TEXT;
    int y = box.y() + (BUTTON_HEIGHT - font.lineHeight) / 2 + LABEL_NUDGE;
    graphics.drawString(font, label, x, y, color, false);
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    Mouse mouse = new Mouse(mouseX, mouseY);
    for (ModeButton mode : modeButtons()) {
      if (mouse.hovers(mode.box())) {
        return press(mode.code());
      }
    }
    if (step() != RemoteLinkStep.NETWORKS && mouse.hovers(backBox())) {
      scroll = 0;
      return press(RemoteLinkCodes.BACK);
    }
    Box area = listArea();
    for (int shown = 0; shown < visible(); shown++) {
      int line = scroll + shown;
      if (line < menu.rows().lines().size() && mouse.hovers(rowBox(area, shown))) {
        scroll = 0;
        return press(RemoteLinkCodes.line(line));
      }
    }
    return super.mouseClicked(mouseX, mouseY, button);
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
    int last = Math.max(0, menu.rows().lines().size() - visible());
    scroll = Mth.clamp(scroll - (int) Math.signum(scrollY), 0, last);
    return true;
  }

  private boolean press(int code) {
    minecraft.gameMode.handleInventoryButtonClick(menu.containerId, code);
    return true;
  }

  /** One mode button on the top right: Lever, then Button (answer 365). */
  private record ModeButton(Box box, Component label, boolean chosen, int code) {}

  private List<ModeButton> modeButtons() {
    Component lever = Component.translatable(RemoteMessages.LEVER);
    Component press = Component.translatable(RemoteMessages.BUTTON);
    int pressWidth = font.width(press) + BUTTON_PADDING;
    int leverWidth = font.width(lever) + BUTTON_PADDING;
    int right = leftPos + WIDTH - SIDE;
    int y = topPos + TITLE_Y - BUTTON_GAP;
    Box pressBox = new Box(right - pressWidth, y, pressWidth, BUTTON_HEIGHT);
    Box leverBox = new Box(pressBox.x() - BUTTON_GAP - leverWidth, y, leverWidth, BUTTON_HEIGHT);
    int mode = menu.rows().mode();
    return List.of(
        new ModeButton(leverBox, lever, mode == RemoteMode.LEVER.ordinal(), RemoteLinkCodes.LEVER),
        new ModeButton(
            pressBox, press, mode == RemoteMode.BUTTON.ordinal(), RemoteLinkCodes.BUTTON));
  }

  private Box backBox() {
    Component back = Component.translatable(NodeScreenTexts.BACK);
    return new Box(
        leftPos + SIDE, topPos + BACK_Y, font.width(back) + BUTTON_PADDING, BUTTON_HEIGHT);
  }

  private Box listArea() {
    return new Box(leftPos + LIST.x(), topPos + LIST.y(), LIST.width(), LIST.height());
  }

  private static Box rowBox(Box area, int shown) {
    int y = area.y() + ROW_GAP + shown * (ROW_HEIGHT + ROW_GAP);
    return new Box(area.x() + ROW_GAP, y, area.width() - 2 * ROW_GAP, ROW_HEIGHT);
  }

  private static int visible() {
    return LIST.height() / (ROW_HEIGHT + ROW_GAP);
  }

  private RemoteLinkStep step() {
    RemoteLinkStep[] steps = RemoteLinkStep.values();
    int step = menu.rows().step();
    return step >= 0 && step < steps.length ? steps[step] : RemoteLinkStep.NETWORKS;
  }

  private Component stepTitle() {
    return Component.translatable(
        switch (step()) {
          case NETWORKS -> RemoteLinkTexts.NETWORKS;
          case NODES -> RemoteLinkTexts.NODES;
          case SLOTS -> RemoteLinkTexts.SLOTS;
        });
  }

  private String emptyKey() {
    return step() == RemoteLinkStep.NETWORKS
        ? NodeScreenTexts.NO_NETWORK_ONLINE
        : RemoteLinkTexts.NO_INPUT_NODE;
  }
}
