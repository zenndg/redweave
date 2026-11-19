package io.github.zenndg.redweave.client.screen;

import io.github.zenndg.redweave.menu.ModuleMenu;
import io.github.zenndg.redweave.menu.NodeScreenTexts;
import io.github.zenndg.redweave.network.ModuleRenamed;
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

  /** The frame of every module screen, one row taller for the `Delay` row (answer 853). */
  static final int HEIGHT = 186;

  /** The frame of the Signal Item Sensor, which has the inventory under it (answer 855). */
  static final int COMPACT_HEIGHT = 168;

  static final int TITLE_X = 10;

  private static final int TITLE_Y = 9;
  private static final int NAME_TEXT_Y = 2;
  private static final int ICON_SIZE = 8;
  private static final int ICON_GAP = 4;
  private static final int ICON_Y = 2;
  private static final int BODY_X = 10;
  private static final int BODY_Y = 34;
  private static final int BODY_WIDTH = 320;
  private static final int BODY_BELOW = 24;
  private static final int FOOTER_ABOVE = 22;
  private static final int FOOTER_HEIGHT = 20;
  private static final int BACK_X = 282;
  private static final int BACK_ABOVE = 20;
  private static final int BACK_WIDTH = 48;
  private static final int BACK_HEIGHT = 16;

  private final ModuleMenu menu;
  private final BooleanSupplier owner;
  private final NameField name = new NameField();
  private final NetworkChoiceList choices = new NetworkChoiceList();
  private Font font;
  private int left;
  private int top;
  private boolean switching;

  private final int height;

  ModuleFrame(ModuleMenu menu, BooleanSupplier owner) {
    this(menu, owner, HEIGHT);
  }

  private ModuleFrame(ModuleMenu menu, BooleanSupplier owner, int height) {
    this.menu = menu;
    this.owner = owner;
    this.height = height;
  }

  /** The frame of the Signal Item Sensor, 168 px tall, with the inventory under it (855). */
  static ModuleFrame compact(ModuleMenu menu, BooleanSupplier owner) {
    return new ModuleFrame(menu, owner, COMPACT_HEIGHT);
  }

  /** The body of the screen, where each screen draws its own rows. */
  Box body() {
    return at(bodyBox());
  }

  private Box bodyBox() {
    return new Box(BODY_X, BODY_Y, BODY_WIDTH, height - BODY_Y - BODY_BELOW);
  }

  private Box footerBox() {
    return new Box(BODY_X, height - FOOTER_ABOVE, BODY_WIDTH, FOOTER_HEIGHT);
  }

  private Box backBox() {
    return new Box(BACK_X, height - BACK_ABOVE, BACK_WIDTH, BACK_HEIGHT);
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
    Optional<Box> link = ModuleFooter.linkBox(font, at(footerBox()), menu);
    if (owner.getAsBoolean() && link.isPresent() && link.get().contains(mouseX, mouseY)) {
      return Optional.of(NodeHeader.switchTooltip());
    }
    return Optional.empty();
  }

  /** The window, the title and the name, and the network choice; false while choosing. */
  boolean draw(GuiGraphics graphics, Component title, Mouse mouse) {
    graphics.blitSprite(NodeSprites.WINDOW, left, top, WIDTH, height);
    graphics.drawString(font, title, left + TITLE_X, top + TITLE_Y, ScreenColors.TEXT, false);
    name.drawBackground(graphics);
    if (!name.editing()) {
      drawName(graphics);
    }
    if (!switching) {
      return true;
    }
    choices.draw(graphics, font, at(bodyBox()), menu.sync().rows(), mouse.x(), mouse.y());
    backButton().draw(graphics, font, mouse);
    return false;
  }

  void drawFooter(GuiGraphics graphics, Mouse mouse) {
    ModuleFooter.draw(graphics, font, at(footerBox()), menu, mouse, owner.getAsBoolean());
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
    Optional<Box> link = ModuleFooter.linkBox(font, at(footerBox()), menu);
    if (link.isPresent() && link.get().contains(mouseX, mouseY)) {
      switching = true;
      return true;
    }
    return false;
  }

  /** A row picked, or `Back`; both close the network choice. */
  boolean clickChoice(double mouseX, double mouseY, IntConsumer chosen, Runnable back) {
    Optional<Integer> row = choices.rowAt(at(bodyBox()), menu.sync().rows().size(), mouseX, mouseY);
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
    if (!switching || !at(bodyBox()).contains(mouseX, mouseY)) {
      return false;
    }
    choices.scroll(scrollY, at(bodyBox()), menu.sync().rows().size());
    return true;
  }

  private TimerButton backButton() {
    return new TimerButton(
        at(backBox()),
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
