package io.github.zenndg.redweave.client.screen;

import io.github.zenndg.redweave.client.screen.ControllerTabs.Tab;
import io.github.zenndg.redweave.config.NeoForgeNetworkLimits;
import io.github.zenndg.redweave.domain.signal.ChannelColor;
import io.github.zenndg.redweave.menu.ControllerMenu;
import io.github.zenndg.redweave.menu.ControllerScreenCodes;
import io.github.zenndg.redweave.menu.ControllerScreenTexts;
import io.github.zenndg.redweave.menu.RenameTarget;
import io.github.zenndg.redweave.network.ControllerRenamed;
import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * The Controller Screen: _docs/funcionalidades/telas.md and _docs/imagens/telas/
 * tela-do-controlador.png. It only shows what the server sends and asks for actions; the server
 * decides (rule 16). Anyone may look, and only the owner sees the Rename button, the palette and
 * the names to double-click (answers 22, 156, 212, 217 and 222).
 */
public final class ControllerScreen extends AbstractContainerScreen<ControllerMenu> {
  private static final int WIDTH = 340;
  private static final int HEIGHT = 228;
  private static final Box PANEL = new Box(8, 46, 324, 150);
  private static final int INSET = 3;
  private static final int FOOTER_Y = 201;

  /** The network is one, so its rename carries no index. */
  private static final int NO_INDEX = 0;

  private final NodeTable table = new NodeTable();
  private final LogTable log = new LogTable();
  private final ClearLogButton clearLog = new ClearLogButton();
  private final NameField name = new NameField();
  private final DoubleClick clicks = new DoubleClick(Util::getMillis);
  private Tab tab = Tab.NODES;
  private boolean paletteOpen;
  private RenameTarget target = RenameTarget.NETWORK;
  private int targetIndex;

  public ControllerScreen(ControllerMenu menu, Inventory inventory, Component title) {
    super(menu, inventory, title);
    imageWidth = WIDTH;
    imageHeight = HEIGHT;
  }

  @Override
  protected void init() {
    super.init();
    name.attach(font, ControllerHeader.fieldBox(leftPos, topPos), this::addRenderableWidget);
  }

  @Override
  public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    super.render(graphics, mouseX, mouseY, partialTick);
    tooltipAt(mouseX, mouseY).ifPresent(text -> graphics.renderTooltip(font, text, mouseX, mouseY));
  }

  @Override
  protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
    Mouse mouse = new Mouse(mouseX, mouseY);
    Box window = new Box(leftPos, topPos, WIDTH, HEIGHT);
    graphics.blitSprite(NodeSprites.WINDOW, leftPos, topPos, WIDTH, HEIGHT);
    ControllerHeader.draw(graphics, font, window, menu, mouse, editing(RenameTarget.NETWORK));
    ControllerTabs.draw(graphics, font, leftPos, topPos, tab, mouse);
    Box panel = panel();
    graphics.blitSprite(NodeSprites.PANEL, panel.x(), panel.y(), panel.width(), panel.height());
    switch (tab) {
      case NODES -> table.draw(graphics, font, content(), menu, mouse);
      case SIGNALS -> ChannelGrid.draw(graphics, font, content(), menu, mouse);
      case ACCESS -> AccessPanel.draw(graphics, font, content(), menu.header());
      case LOG -> drawLog(graphics, mouse);
    }
    ControllerFooter.draw(
        graphics, font, new Box(panel.x(), topPos + FOOTER_Y, panel.width(), HEIGHT), menu);
    name.drawBackground(graphics);
    if (paletteOpen) {
      ColorPalette.draw(graphics, palette(), ControllerHeader.colorOf(menu.header()), mouse);
    }
  }

  /** The owner has the Clear log button under the rows (answers 264 and 265). */
  private void drawLog(GuiGraphics graphics, Mouse mouse) {
    if (!owner()) {
      log.draw(graphics, font, content(), menu);
      return;
    }
    log.draw(graphics, font, ClearLogButton.rowsOf(content()), menu);
    clearLog.draw(graphics, font, content(), mouse);
  }

  /** Every text is drawn in renderBg, so the game does not draw its own labels on top. */
  @Override
  protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {}

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    if (name.editing() && !name.box().contains(mouseX, mouseY)) {
      name.cancel();
    }
    if (paletteOpen) {
      ColorPalette.colorAt(palette(), mouseX, mouseY).ifPresent(this::recolor);
      paletteOpen = false;
      return true;
    }
    if (clickHeader(mouseX, mouseY)) {
      return true;
    }
    Optional<Tab> clicked = ControllerTabs.tabAt(leftPos, topPos, mouseX, mouseY);
    if (clicked.isPresent()) {
      tab = clicked.get();
      return true;
    }
    return clickContent(mouseX, mouseY) || super.mouseClicked(mouseX, mouseY, button);
  }

  /** The owner opens the palette or renames the network from the header (156 and 212). */
  private boolean clickHeader(double mouseX, double mouseY) {
    if (!owner()) {
      return false;
    }
    if (ControllerHeader.colorSquare(leftPos, topPos).contains(mouseX, mouseY)) {
      paletteOpen = true;
      return true;
    }
    if (ControllerHeader.renameBox(font, leftPos, topPos, menu.header()).contains(mouseX, mouseY)) {
      edit(
          RenameTarget.NETWORK,
          NO_INDEX,
          ControllerHeader.fieldBox(leftPos, topPos),
          menu.header().name(),
          NameField.limit(NeoForgeNetworkLimits::networkNameLength));
      return true;
    }
    return false;
  }

  private boolean clickContent(double mouseX, double mouseY) {
    return switch (tab) {
      case NODES -> clickNodes(mouseX, mouseY);
      case SIGNALS -> clickChannels(mouseX, mouseY);
      case ACCESS -> false;
      case LOG -> clickLog(mouseX, mouseY);
    };
  }

  /** The second click on Clear log asks the server, which checks the owner (rule 17). */
  private boolean clickLog(double mouseX, double mouseY) {
    if (!owner()) {
      return false;
    }
    boolean onButton = clearLog.contains(font, content(), mouseX, mouseY);
    if (clearLog.click(font, content(), mouseX, mouseY)
        && minecraft != null
        && minecraft.gameMode != null) {
      minecraft.gameMode.handleInventoryButtonClick(
          menu.containerId, ControllerScreenCodes.CLEAR_LOG);
    }
    return onButton;
  }

  /**
   * A click opens or closes the row; a double click on the name renames the node, for the owner,
   * and its second click undoes the first, so the row stays as it was (answers 217 and 222). A
   * module is renamed only on its own screen (answer 813).
   */
  private boolean clickNodes(double mouseX, double mouseY) {
    Optional<NodeTable.Placed> row = table.rowAt(content(), menu, mouseX, mouseY);
    if (row.isEmpty()) {
      return false;
    }
    int index = row.get().item().row();
    Box nameBox = NodeTable.nameBox(row.get().box());
    table.toggle(index);
    if (!owner() || index >= menu.nodes().size() || !nameBox.contains(mouseX, mouseY)) {
      clicks.reset();
      return true;
    }
    if (!clicks.click(index)) {
      return true;
    }
    edit(
        RenameTarget.NODE,
        index,
        nameBox,
        menu.nodes().get(index).name(),
        NameField.limit(NeoForgeNetworkLimits::nodeNameLength));
    return true;
  }

  private boolean clickChannels(double mouseX, double mouseY) {
    Optional<ChannelColor> channel = ChannelGrid.nameAt(content(), mouseX, mouseY);
    if (!owner() || channel.isEmpty()) {
      clicks.reset();
      return false;
    }
    if (!clicks.click(channel.get())) {
      return true;
    }
    edit(
        RenameTarget.CHANNEL,
        channel.get().ordinal(),
        ChannelGrid.nameBox(ChannelGrid.rowBox(content(), channel.get())),
        menu.channels().get(channel.get().ordinal()).name(),
        NameField.limit(NeoForgeNetworkLimits::channelNameLength));
    return true;
  }

  private void edit(RenameTarget what, int index, Box where, String current, int maxLength) {
    target = what;
    targetIndex = index;
    name.moveTo(where);
    name.start(current, maxLength, this::setFocused);
  }

  private boolean editing(RenameTarget what) {
    return name.editing() && target == what;
  }

  /** Enter saves the name, Esc cancels it, and no key closes the screen while typing. */
  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    if (!name.editing()) {
      return super.keyPressed(keyCode, scanCode, modifiers);
    }
    name.keyPressed(
        keyCode,
        scanCode,
        modifiers,
        text ->
            PacketDistributor.sendToServer(
                ControllerRenamed.of(menu.pos(), target, targetIndex, text)));
    return true;
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
    if (tab == Tab.NODES && content().contains(mouseX, mouseY)) {
      name.cancel();
      table.scroll(scrollY);
      return true;
    }
    if (tab == Tab.LOG && content().contains(mouseX, mouseY)) {
      log.scroll(scrollY);
      return true;
    }
    return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
  }

  private Optional<Component> tooltipAt(double mouseX, double mouseY) {
    if (!owner() || name.editing() || paletteOpen) {
      return Optional.empty();
    }
    if (ControllerHeader.colorSquare(leftPos, topPos).contains(mouseX, mouseY)) {
      return Optional.of(Component.translatable(ControllerScreenTexts.RECOLOR_TOOLTIP));
    }
    boolean overName =
        switch (tab) {
          case NODES ->
              table
                  .rowAt(content(), menu, mouseX, mouseY)
                  .filter(row -> NodeTable.nameBox(row.box()).contains(mouseX, mouseY))
                  .isPresent();
          case SIGNALS -> ChannelGrid.nameAt(content(), mouseX, mouseY).isPresent();
          case ACCESS, LOG -> false;
        };
    return overName
        ? Optional.of(Component.translatable(ControllerScreenTexts.RENAME_TOOLTIP))
        : Optional.empty();
  }

  private void recolor(ChannelColor color) {
    if (minecraft != null && minecraft.gameMode != null) {
      minecraft.gameMode.handleInventoryButtonClick(
          menu.containerId, ControllerScreenCodes.button(color));
    }
  }

  private boolean owner() {
    return menu.header().viewerIsOwner();
  }

  private Box palette() {
    return ColorPalette.box(ControllerHeader.colorSquare(leftPos, topPos));
  }

  private Box panel() {
    return new Box(leftPos + PANEL.x(), topPos + PANEL.y(), PANEL.width(), PANEL.height());
  }

  private Box content() {
    Box panel = panel();
    return new Box(
        panel.x() + INSET,
        panel.y() + INSET,
        panel.width() - 2 * INSET,
        panel.height() - 2 * INSET);
  }
}
