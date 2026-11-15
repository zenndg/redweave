package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.menu.ModuleMenu;
import io.github.marcelofabianov.redweave.menu.NodeScreenTexts;
import io.github.marcelofabianov.redweave.network.ModuleRenamed;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * What every Signal Module Screen has around its body: the window, the title, the name the owner
 * renames, the network choice in place of the body and the footer (modulos-de-sinal.md, Tela comum;
 * answers 378, 400, 404 and 423). Each screen draws only its own body.
 */
final class ModuleFrame {
  static final int WIDTH = 340;
  static final int HEIGHT = 168;
  static final int TITLE_X = 10;
  static final Box BODY = new Box(10, 34, 320, 110);

  private static final int TITLE_Y = 9;
  private static final int NAME_TEXT_Y = 2;
  private static final int ICON_SIZE = 8;
  private static final int ICON_GAP = 4;
  private static final int ICON_Y = 2;
  private static final Box FOOTER = new Box(10, 146, 320, 20);
  private static final Box BACK = new Box(282, 148, 48, 16);

  private final ModuleMenu menu;
  private final BooleanSupplier owner;
  private final NameField name = new NameField();
  private final NetworkChoiceList choices = new NetworkChoiceList();
  private Font font;
  private int left;
  private int top;
  private boolean switching;

  ModuleFrame(ModuleMenu menu, BooleanSupplier owner) {
    this.menu = menu;
    this.owner = owner;
  }

  void attach(Font font, int left, int top, Consumer<EditBox> add) {
    this.font = font;
    this.left = left;
    this.top = top;
    name.attach(font, NodeHeader.fieldBox(left, top), add);
  }

  /** The network choice takes the place of the body. */
  void openChoice() {
    switching = true;
  }

  /** No tooltip while the name is typed or the network is chosen. */
  boolean quiet() {
    return switching || name.editing();
  }

  /** For the owner, how to rename and to switch networks. */
  Optional<Component> tooltipAt(double mouseX, double mouseY) {
    if (owner.getAsBoolean() && nameBox().contains(mouseX, mouseY)) {
      return Optional.of(NodeHeader.renameTooltip());
    }
    Optional<Box> link = ModuleFooter.linkBox(font, at(FOOTER), menu);
    if (owner.getAsBoolean() && link.isPresent() && link.get().contains(mouseX, mouseY)) {
      return Optional.of(NodeHeader.switchTooltip());
    }
    return Optional.empty();
  }

  /** The window, the title and the name, and the network choice; false while choosing. */
  boolean draw(GuiGraphics graphics, Component title, Mouse mouse) {
    graphics.blitSprite(NodeSprites.WINDOW, left, top, WIDTH, HEIGHT);
    graphics.drawString(font, title, left + TITLE_X, top + TITLE_Y, ScreenColors.TEXT, false);
    name.drawBackground(graphics);
    if (!name.editing()) {
      drawName(graphics);
    }
    if (!switching) {
      return true;
    }
    choices.draw(graphics, font, at(BODY), menu.sync().rows(), mouse.x(), mouse.y());
    backButton().draw(graphics, font, mouse);
    return false;
  }

  void drawFooter(GuiGraphics graphics, Mouse mouse) {
    ModuleFooter.draw(graphics, font, at(FOOTER), menu, mouse, owner.getAsBoolean());
  }

  private void drawName(GuiGraphics graphics) {
    Box box = nameBox();
    graphics.drawString(
        font, menu.sync().moduleName(), box.x(), box.y() + NAME_TEXT_Y, ScreenColors.LABEL, false);
    if (owner.getAsBoolean()) {
      graphics.blitSprite(
          NodeSprites.ICON_EDIT, box.right() - ICON_SIZE, box.y() + ICON_Y, ICON_SIZE, ICON_SIZE);
    }
  }

  /** A click away from the name being typed cancels it. */
  void clickAway(double mouseX, double mouseY) {
    if (name.editing() && !name.box().contains(mouseX, mouseY)) {
      name.cancel();
    }
  }

  boolean switching() {
    return switching;
  }

  /** The name or the network, only for the owner (answer 378). */
  boolean clickHeader(double mouseX, double mouseY, Consumer<EditBox> focus) {
    if (!owner.getAsBoolean()) {
      return false;
    }
    if (nameBox().contains(mouseX, mouseY)) {
      name.start(menu.sync().moduleName(), NameField.moduleLimit(), focus);
      return true;
    }
    Optional<Box> link = ModuleFooter.linkBox(font, at(FOOTER), menu);
    if (link.isPresent() && link.get().contains(mouseX, mouseY)) {
      switching = true;
      return true;
    }
    return false;
  }

  /** A row picked, or `Back`; both close the network choice. */
  boolean clickChoice(double mouseX, double mouseY, IntConsumer chosen, Runnable back) {
    Optional<Integer> row = choices.rowAt(at(BODY), menu.sync().rows().size(), mouseX, mouseY);
    if (row.isPresent()) {
      switching = false;
      chosen.accept(row.get());
      return true;
    }
    if (backButton().box().contains(mouseX, mouseY)) {
      switching = false;
      back.run();
      return true;
    }
    return false;
  }

  /** Enter saves the name, Esc cancels it, and no key closes the screen while typing. */
  boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    if (!name.editing()) {
      return false;
    }
    name.keyPressed(
        keyCode,
        scanCode,
        modifiers,
        text -> PacketDistributor.sendToServer(new ModuleRenamed(menu.spot(), text)));
    return true;
  }

  boolean scroll(double mouseX, double mouseY, double scrollY) {
    if (!switching || !at(BODY).contains(mouseX, mouseY)) {
      return false;
    }
    choices.scroll(scrollY, at(BODY), menu.sync().rows().size());
    return true;
  }

  private TimerButton backButton() {
    return new TimerButton(
        at(BACK),
        Component.translatable(NodeScreenTexts.BACK),
        TimerButton.Look.BUTTON,
        () -> switching = false);
  }

  private Box nameBox() {
    int width =
        font.width(menu.sync().moduleName()) + (owner.getAsBoolean() ? ICON_GAP + ICON_SIZE : 0);
    Box field = NodeHeader.fieldBox(left, top);
    return new Box(field.x(), field.y(), width, field.height());
  }

  Box at(Box relative) {
    return new Box(left + relative.x(), top + relative.y(), relative.width(), relative.height());
  }
}
