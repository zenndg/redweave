package io.github.zenndg.redweave.client.screen;

import io.github.zenndg.redweave.domain.module.CardId;
import io.github.zenndg.redweave.domain.module.KeypadCode;
import io.github.zenndg.redweave.domain.signal.ChannelColor;
import io.github.zenndg.redweave.menu.KeypadAction;
import io.github.zenndg.redweave.menu.KeypadMenu;
import io.github.zenndg.redweave.menu.KeypadScreenTexts;
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
 * The Signal Keypad Screen of the owner: telas.md, Tela do Signal Keypad. The state and the cards
 * on the left, or the keypad while a new code is typed twice; `Code ••••`, `Strength`, the open
 * time, `Input`, only a channel, and `Output` on the right. Nobody ever sees the code, and anyone
 * else sees the screen read only (answers 660 to 663, 669, 904, 905 and 908, rule 16).
 */
public final class KeypadScreen extends AbstractContainerScreen<KeypadMenu> {
  private static final int SEPARATOR_X = 130;
  private static final int CODE_Y = 42;
  private static final int STRENGTH_Y = 60;
  private static final int OPEN_Y = 78;
  private static final int INPUT_Y = 96;
  private static final int OUTPUT_Y = 114;
  private static final int GRID_Y = 132;
  private static final int TEXT_Y = 3;
  private static final int WIDE_X = 222;
  private static final int WIDE_WIDTH = 46;
  private static final int LEFT_X = 17;
  private static final int CARDS_Y = 42;
  private static final int CARD_LINE = 12;
  private static final int SHOWN_CARDS = 3;
  private static final int CARD_TEXT = 8;
  private static final int REVOKE_X = 100;
  private static final int REVOKE_SIZE = 10;
  private static final int PAD_X = 17;
  private static final int PAD_Y = 56;
  private static final int PROMPT_Y = 44;
  private static final String MINUS = "−";
  private static final String PLUS = "+";
  private static final String REVOKE = "×";

  private final ModuleFrame frame;
  private final KeypadPad pad = new KeypadPad();
  private Optional<KeypadAction.Kind> picking = Optional.empty();
  private Optional<KeypadAction.Kind> pending = Optional.empty();
  private Optional<CodeStep> changing = Optional.empty();

  /** The new code while it is typed twice, and whether the two did not match (answer 905). */
  private record CodeStep(Optional<KeypadCode> first, boolean mismatch) {}

  public KeypadScreen(KeypadMenu menu, Inventory inventory, Component title) {
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
    graphics.fill(
        leftPos + SEPARATOR_X,
        body.y() + ModuleFrame.TITLE_X,
        leftPos + SEPARATOR_X + 1,
        body.bottom() - ModuleFrame.TITLE_X,
        ScreenColors.SEPARATOR);
    if (changing.isPresent()) {
      drawChange(graphics, mouse);
    } else {
      drawState(graphics, mouse);
    }
    drawSettings(graphics);
    buttons().forEach(button -> button.draw(graphics, font, mouse));
    picking.ifPresent(kind ->
        ChannelPicker.draw(
            graphics,
            font,
            leftPos + ModuleColumn.LABEL_X,
            topPos + GRID_Y,
            (kind == KeypadAction.Kind.INPUT ? menu.input() : menu.output()).channel(),
            mouse,
            owner()));
  }

  /** `Locked` or `Unlocked`, and the cards it accepts, which the owner revokes (669 and 908). */
  private void drawState(GuiGraphics graphics, Mouse mouse) {
    boolean open = menu.unlocked();
    ModulePill.draw(
        graphics,
        font,
        frame.at(ModulePill.BOX),
        Component.translatable(KeypadScreenTexts.of(open)),
        open ? ScreenColors.ONLINE : ScreenColors.REDSTONE,
        ScreenColors.TEXT);
    List<CardId> cards = menu.cards();
    Component header =
        cards.isEmpty()
            ? Component.translatable(KeypadScreenTexts.NO_CARDS)
            : Component.translatable(KeypadScreenTexts.CARDS, cards.size());
    text(graphics, header, LEFT_X, CARDS_Y, ScreenColors.LABEL);
    for (int index = 0; index < Math.min(cards.size(), SHOWN_CARDS); index++) {
      String id = cards.get(index).value().toString().substring(0, CARD_TEXT);
      text(
          graphics,
          Component.literal(id),
          LEFT_X,
          CARDS_Y + CARD_LINE * (index + 1),
          ScreenColors.TEXT);
    }
    revokes().forEach(button -> button.draw(graphics, font, mouse));
  }

  /** The keypad, with what to type now: the new code, again, or both again (answer 905). */
  private void drawChange(GuiGraphics graphics, Mouse mouse) {
    CodeStep step = changing.get();
    String key =
        step.mismatch()
            ? KeypadScreenTexts.MISMATCH
            : step.first().isPresent() ? KeypadScreenTexts.REPEAT_CODE : KeypadScreenTexts.NEW_CODE;
    int color = step.mismatch() ? ScreenColors.REDSTONE : ScreenColors.TEXT;
    text(graphics, Component.translatable(key), LEFT_X, PROMPT_Y, color);
    pad.draw(graphics, font, leftPos + PAD_X, topPos + PAD_Y, mouse, this::typed);
  }

  private void typed(KeypadCode code) {
    CodeStep step = changing.orElseThrow();
    if (step.first().isEmpty()) {
      changing = Optional.of(new CodeStep(Optional.of(code), false));
      return;
    }
    if (step.first().get().equals(code)) {
      send(KeypadAction.code(code));
      changing = Optional.empty();
      return;
    }
    changing = Optional.of(new CodeStep(Optional.empty(), true));
  }

  private void drawSettings(GuiGraphics graphics) {
    ModuleColumn.label(graphics, font, leftPos, topPos + CODE_Y, KeypadScreenTexts.CODE);
    ModuleColumn.label(graphics, font, leftPos, topPos + STRENGTH_Y, LeverScreenTexts.STRENGTH);
    ModuleColumn.label(graphics, font, leftPos, topPos + OPEN_Y, KeypadScreenTexts.OPEN_TIME);
    ModuleColumn.label(graphics, font, leftPos, topPos + INPUT_Y, NodeScreenTexts.INPUT);
    ModuleColumn.label(graphics, font, leftPos, topPos + OUTPUT_Y, NodeScreenTexts.OUTPUT);
    Component code =
        menu.hasCode()
            ? Component.literal(KeypadScreenTexts.HIDDEN)
            : Component.translatable(KeypadScreenTexts.NO_CODE);
    text(graphics, code, ModuleColumn.CONTROL_X, CODE_Y + TEXT_Y, ScreenColors.TEXT);
    value(graphics, Component.literal(Integer.toString(menu.strength())), STRENGTH_Y);
    value(graphics, Component.translatable(TimerScreenTexts.SECONDS, menu.openSeconds()), OPEN_Y);
    ModuleColumn.channel(graphics, font, leftPos, topPos + INPUT_Y, menu.sync(), menu.input());
    ModuleColumn.channel(graphics, font, leftPos, topPos + OUTPUT_Y, menu.sync(), menu.output());
  }

  private void value(GuiGraphics graphics, Component value, int y) {
    int x = TimerControls.VALUE_X + (TimerControls.VALUE_WIDTH - font.width(value)) / 2;
    text(graphics, value, x, y + TEXT_Y, ScreenColors.TEXT);
  }

  private void text(GuiGraphics graphics, Component text, int x, int y, int color) {
    graphics.drawString(font, text, leftPos + x, topPos + y, color, false);
  }

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

  /** The name, the network, the buttons, the keypad and the channels, only for the owner. */
  private boolean clickOwned(double mouseX, double mouseY) {
    if (!owner()) {
      return false;
    }
    if (frame.clickHeader(mouseX, mouseY, this::setFocused)) {
      return true;
    }
    if (changing.isPresent()
        && pad.click(leftPos + PAD_X, topPos + PAD_Y, mouseX, mouseY, this::typed)) {
      return true;
    }
    List<TimerButton> clickable = new ArrayList<>(buttons());
    if (changing.isEmpty()) {
      clickable.addAll(revokes());
    }
    for (TimerButton button : clickable) {
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
    channel.ifPresent(color -> {
      send(new KeypadAction(picking.get(), color.ordinal() + 1));
      picking = Optional.empty();
    });
    return channel.isPresent();
  }

  private void chosen(int row) {
    send(KeypadAction.choose(row));
    picking = pending;
    pending = Optional.empty();
  }

  /** `Channel` opens the channels, or first the network choice without a network (414). */
  private void pick(KeypadAction.Kind kind) {
    if (menu.sync().networkColor().isEmpty()) {
      frame.openChoice();
      pending = Optional.of(kind);
      return;
    }
    picking = Optional.of(kind);
  }

  private void send(KeypadAction action) {
    if (minecraft != null && minecraft.gameMode != null) {
      minecraft.gameMode.handleInventoryButtonClick(menu.containerId, action.button());
    }
  }

  /** `Change`, the `−` and `+` of `Strength` and the open time, and the connections. */
  private List<TimerButton> buttons() {
    List<TimerButton> buttons = new ArrayList<>();
    buttons.add(wide(CODE_Y, KeypadScreenTexts.CHANGE, changing.isPresent(), this::startChange));
    buttons.add(step(ModuleColumn.CONTROL_X, STRENGTH_Y, MINUS, KeypadAction.strength(false)));
    buttons.add(step(TimerControls.PLUS_X, STRENGTH_Y, PLUS, KeypadAction.strength(true)));
    buttons.add(step(ModuleColumn.CONTROL_X, OPEN_Y, MINUS, KeypadAction.open(false)));
    buttons.add(step(TimerControls.PLUS_X, OPEN_Y, PLUS, KeypadAction.open(true)));
    boolean pickingInput = picking.equals(Optional.of(KeypadAction.Kind.INPUT));
    buttons.add(
        wide(
            INPUT_Y,
            TimerScreenTexts.CHANNEL,
            pickingInput || !menu.input().isEdge(),
            () -> pick(KeypadAction.Kind.INPUT)));
    ModuleColumn.connection(
        buttons,
        menu.output(),
        new Box(leftPos, topPos + OUTPUT_Y, 0, ModuleColumn.ROW_HEIGHT),
        new ModuleColumn.Choosing(owner(), picking.equals(Optional.of(KeypadAction.Kind.OUTPUT))),
        () -> send(new KeypadAction(KeypadAction.Kind.OUTPUT, KeypadAction.EDGE)),
        () -> pick(KeypadAction.Kind.OUTPUT));
    return buttons;
  }

  private void startChange() {
    pad.clear();
    changing = Optional.of(new CodeStep(Optional.empty(), false));
  }

  /** The `×` beside each card shown, which revokes it (answer 669). */
  private List<TimerButton> revokes() {
    List<TimerButton> revokes = new ArrayList<>();
    List<CardId> cards = menu.cards();
    for (int index = 0; index < Math.min(cards.size(), SHOWN_CARDS); index++) {
      int position = index;
      CardId card = cards.get(index);
      Box box =
          new Box(
              leftPos + REVOKE_X,
              topPos + CARDS_Y + CARD_LINE * (index + 1),
              REVOKE_SIZE,
              REVOKE_SIZE);
      revokes.add(
          new TimerButton(
              box,
              Component.literal(REVOKE),
              owner() ? TimerButton.Look.BUTTON : TimerButton.Look.DISABLED,
              () -> {
                send(KeypadAction.revoke(position));
                menu.forget(card);
              }));
    }
    return revokes;
  }

  private TimerButton wide(int y, String key, boolean chosen, Runnable click) {
    return new TimerButton(
        new Box(leftPos + WIDE_X, topPos + y, WIDE_WIDTH, ModuleColumn.ROW_HEIGHT),
        Component.translatable(key),
        ModuleColumn.lookOf(chosen, owner()),
        click);
  }

  private TimerButton step(int x, int y, String label, KeypadAction action) {
    return new TimerButton(
        new Box(leftPos + x, topPos + y, TimerControls.STEP_WIDTH, ModuleColumn.ROW_HEIGHT),
        Component.literal(label),
        owner() ? TimerButton.Look.BUTTON : TimerButton.Look.DISABLED,
        () -> send(action));
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

  private boolean owner() {
    return menu.viewerIsOwner();
  }
}
