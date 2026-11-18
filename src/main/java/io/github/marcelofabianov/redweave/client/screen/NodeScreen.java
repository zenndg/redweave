package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.block.Faces;
import io.github.marcelofabianov.redweave.config.NeoForgeNetworkLimits;
import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.Face;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.menu.NodeAction;
import io.github.marcelofabianov.redweave.menu.NodeMenu;
import io.github.marcelofabianov.redweave.menu.NodeScreenCodes;
import io.github.marcelofabianov.redweave.menu.NodeScreenTexts;
import io.github.marcelofabianov.redweave.network.NodeRenamed;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * The Node Screen: _docs/funcionalidades/telas.md, _docs/imagens/telas/tela-do-no.png and
 * tela-do-no-escolha-de-rede.png. It only shows what the server sends and asks for actions; the
 * server decides (rule 16). A node with no network opens on the network choice for whoever placed
 * it, and its owner goes back to it with a click on the network (answers 139, 141 and 172). With a
 * network, the row of the 16 channels opens each one in the panel (answers 224 and 231).
 */
public final class NodeScreen extends AbstractContainerScreen<NodeMenu> {
  private static final int WIDTH = 340;
  private static final int HEIGHT = 186;
  private static final int PADDING = 10;
  private static final int FOOTER_HEIGHT = 20;
  private static final Box PANEL = new Box(10, 62, 320, 94);
  private static final Box CHOICE = new Box(10, 38, 320, 110);

  private static final int BACK_WIDTH = 48;
  private static final int BACK_HEIGHT = 16;
  private static final int BACK_Y = 2;
  private static final int NONE = -1;

  private final FaceCube cube = new FaceCube();
  private final NodeScreenFooter footer = new NodeScreenFooter();
  private final NetworkChoiceList choices = new NetworkChoiceList();
  private final NameField name = new NameField();
  private int selected = NONE;
  private boolean switching;

  public NodeScreen(NodeMenu menu, Inventory inventory, Component title) {
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
    tooltipAt(view(mouseX, mouseY))
        .ifPresent(
            text -> graphics.renderTooltip(font, font.split(text, PANEL.width()), mouseX, mouseY));
  }

  @Override
  protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
    NodeView view = view(mouseX, mouseY);
    NodeScreenFrame.draw(graphics, font, leftPos, topPos, WIDTH, HEIGHT, title, menu);
    name.drawBackground(graphics);
    if (!name.editing()) {
      NodeHeader.drawName(graphics, font, leftPos, topPos, menu, ownsNetwork());
    }
    if (showsChoice()) {
      drawChoice(graphics, view);
      return;
    }
    NodeHeader.drawLink(graphics, font, leftPos, topPos, WIDTH, menu, view);
    drawConfiguration(graphics, view);
  }

  /** The list of networks and, while switching, the way back (answers 199 and 205). */
  private void drawChoice(GuiGraphics graphics, NodeView view) {
    choices.draw(graphics, font, choiceArea(), menu.choices(), view.mouseX(), view.mouseY());
    if (switching) {
      ControlPainter.draw(graphics, font, backButton(), view);
    }
  }

  private void drawConfiguration(GuiGraphics graphics, NodeView view) {
    openFirstChannel();
    ChannelTabs.draw(
        graphics, font, leftPos, topPos, menu, selected, new Mouse(view.mouseX(), view.mouseY()));
    Box panel = panel();
    boolean inNetwork = menu.state().isPresent();
    NodeScreenPanel.draw(graphics, font, view, panel, selected, inNetwork);
    if (!inNetwork) {
      return;
    }
    controls(view).forEach(control -> ControlPainter.draw(graphics, font, control, view));
    ChannelSlot slot = SlotEditor.setUpOf(menu.slot(selected), selected, stuckFace());
    cube.draw(
        graphics,
        SlotEditor.cubeBox(panel),
        supportBlock(),
        Optional.of(Faces.toDirection(slot.face())),
        settableView(view, slot));
    NodeDelayRow.draw(graphics, font, view, delayRow());
    footer.draw(graphics, font, view, footerBox());
  }

  private Box footerBox() {
    return new Box(
        leftPos + PADDING, topPos + HEIGHT - FOOTER_HEIGHT, WIDTH - 2 * PADDING, FOOTER_HEIGHT);
  }

  /** The `Delay` row, the last of the panel, under the rows of the open channel (876). */
  private Box delayRow() {
    Box panel = panel();
    return new Box(panel.x(), panel.y() + SlotEditor.DELAY_Y, panel.width(), SlotEditor.ROW_HEIGHT);
  }

  /** The screen opens on the first enabled channel, or on the first one with none (telas.md). */
  private void openFirstChannel() {
    if (selected != NONE) {
      return;
    }
    selected = 0;
    for (int index = 0; index < menu.slotCount(); index++) {
      if (SlotEditor.isEnabled(menu.slot(index))) {
        selected = index;
        return;
      }
    }
  }

  /** Every text is drawn in renderBg, so the game does not draw its own labels on top. */
  @Override
  protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {}

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    cancelOutside(name, mouseX, mouseY);
    if (showsChoice()) {
      return clickChoice(mouseX, mouseY) || super.mouseClicked(mouseX, mouseY, button);
    }
    if (clickHeader(mouseX, mouseY) || clickChannel(mouseX, mouseY)) {
      return true;
    }
    NodeView view = view(mouseX, mouseY);
    boolean settable = openSlot().filter(open -> SlotEditor.isSettable(view, open)).isPresent();
    if (settable && view.hovers(SlotEditor.cubeBox(panel()))) {
      cube.press();
      return true;
    }
    return clickControl(view) || super.mouseClicked(mouseX, mouseY, button);
  }

  private static void cancelOutside(NameField field, double mouseX, double mouseY) {
    if (field.editing() && !field.box().contains(mouseX, mouseY)) {
      field.cancel();
    }
  }

  /** A click on a channel of the row opens it. */
  private boolean clickChannel(double mouseX, double mouseY) {
    Optional<ChannelColor> clicked = ChannelTabs.at(leftPos, topPos, mouseX, mouseY);
    clicked.ifPresent(channel -> selected = channel.ordinal());
    return clicked.isPresent();
  }

  @Override
  public boolean mouseDragged(
      double mouseX, double mouseY, int button, double dragX, double dragY) {
    cube.drag(dragX, dragY);
    return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
  }

  @Override
  public boolean mouseReleased(double mouseX, double mouseY, int button) {
    NodeView view = view(mouseX, mouseY);
    Optional<Direction> face = cube.release(SlotEditor.cubeBox(panel()), view);
    Optional<ChannelSlot> slot = openSlot().filter(open -> SlotEditor.isSettable(view, open));
    if (slot.isPresent() && face.isPresent()) {
      ChannelSlot open = slot.get();
      ChannelSlot changed =
          new ChannelSlot(open.direction(), Faces.of(face.get()), open.channel(), false);
      send(NodeAction.configure(selected, changed));
    }
    return super.mouseReleased(mouseX, mouseY, button);
  }

  /** The slot of the open channel, or none on the network choice, with no channel open (#179). */
  private Optional<ChannelSlot> openSlot() {
    return OpenChannel.slot(menu.slot(selected), selected, stuckFace());
  }

  /** Enter saves the name, Esc cancels it, and no key closes the screen while typing (204). */
  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    if (name.editing()) {
      name.keyPressed(
          keyCode,
          scanCode,
          modifiers,
          text -> PacketDistributor.sendToServer(new NodeRenamed(menu.spot(), text)));
      return true;
    }
    return super.keyPressed(keyCode, scanCode, modifiers);
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
    if (showsChoice() && choiceArea().contains(mouseX, mouseY)) {
      choices.scroll(scrollY, choiceArea(), menu.choices().size());
      return true;
    }
    return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
  }

  /** A click on a network picks it, and the screen goes on to the configuration (171). */
  private boolean clickChoice(double mouseX, double mouseY) {
    Optional<Integer> row = choices.rowAt(choiceArea(), menu.choices().size(), mouseX, mouseY);
    if (row.isPresent()) {
      send(NodeAction.choose(row.get()));
      switching = false;
      selected = NONE;
      return true;
    }
    if (switching && backButton().box().contains(mouseX, mouseY)) {
      switching = false;
      return true;
    }
    return false;
  }

  /** The owner renames the node or opens the network choice from the header (141 and 204). */
  private boolean clickHeader(double mouseX, double mouseY) {
    if (!ownsNetwork()) {
      return false;
    }
    if (NodeHeader.nameBox(font, leftPos, topPos, menu, true).contains(mouseX, mouseY)) {
      name.start(
          menu.nodeName(),
          NameField.limit(NeoForgeNetworkLimits::nodeNameLength),
          this::setFocused);
      return true;
    }
    Optional<Box> link = NodeHeader.linkBox(font, leftPos, topPos, WIDTH, menu);
    if (link.isPresent() && link.get().contains(mouseX, mouseY)) {
      switching = true;
      return true;
    }
    return false;
  }

  private boolean clickControl(NodeView view) {
    for (Control control : controls(view)) {
      if (control.clickable() && view.hovers(control.box())) {
        control.action().ifPresent(this::send);
        return true;
      }
    }
    return false;
  }

  private Optional<Component> tooltipAt(NodeView view) {
    if (showsChoice() || menu.state().isEmpty()) {
      return Optional.empty();
    }
    Optional<Component> header = headerTooltipAt(view);
    if (header.isPresent()) {
      return header;
    }
    for (Control control : controls(view)) {
      if (control.box().contains(view.mouseX(), view.mouseY()) && control.tooltip().isPresent()) {
        return control.tooltip();
      }
    }
    return NodeScreenPanel.tooltipAt(view, panel(), selected).or(() -> footer.tooltipAt(view));
  }

  private Optional<Component> headerTooltipAt(NodeView view) {
    if (!ownsNetwork() || name.editing()) {
      return Optional.empty();
    }
    if (view.hovers(NodeHeader.nameBox(font, leftPos, topPos, menu, true))) {
      return Optional.of(NodeHeader.renameTooltip());
    }
    return NodeHeader.linkBox(font, leftPos, topPos, WIDTH, menu)
        .filter(view::hovers)
        .map(link -> NodeHeader.switchTooltip());
  }

  /**
   * The network choice shows for whoever placed a node with no network, and for the owner who
   * clicked the network (answers 139, 141 and 172).
   */
  private boolean showsChoice() {
    return switching || (menu.state().isEmpty() && menu.viewerPlacedIt());
  }

  /** Only the owner of the network of the node renames it and switches it (answer 141). */
  private boolean ownsNetwork() {
    return menu.state().isPresent() && menu.viewerIsOwner();
  }

  private Control backButton() {
    Box box =
        new Box(
            leftPos + PADDING, topPos + HEIGHT - FOOTER_HEIGHT + BACK_Y, BACK_WIDTH, BACK_HEIGHT);
    return Control.button(
        box, Control.Look.BUTTON, Component.translatable(NodeScreenTexts.BACK), Optional.empty());
  }

  private Box choiceArea() {
    return new Box(leftPos + CHOICE.x(), topPos + CHOICE.y(), CHOICE.width(), CHOICE.height());
  }

  private List<Control> controls(NodeView view) {
    List<Control> all =
        new ArrayList<>(
            SlotEditor.controls(font, view, panel(), Math.max(0, selected), stuckFace()));
    all.addAll(NodeDelayRow.controls(view, delayRow()));
    return all;
  }

  private void send(NodeAction action) {
    if (minecraft != null && minecraft.gameMode != null) {
      minecraft.gameMode.handleInventoryButtonClick(
          menu.containerId, NodeScreenCodes.button(action));
    }
  }

  /** The cube reacts to the mouse only while the open channel may be set up (answer 237). */
  private static NodeView settableView(NodeView view, ChannelSlot slot) {
    boolean settable = SlotEditor.isSettable(view, slot);
    return new NodeView(view.menu(), view.mouseX(), view.mouseY(), settable);
  }

  /** The face of the block of support the node sticks on, from the spot of the menu. */
  private Face stuckFace() {
    return Faces.of(menu.spot().face().getOpposite());
  }

  /** The block the node sticks to, or polished deepslate for a block drawn some other way. */
  private BlockState supportBlock() {
    BlockState fallback = Blocks.POLISHED_DEEPSLATE.defaultBlockState();
    if (minecraft == null || minecraft.level == null) {
      return fallback;
    }
    BlockState block = minecraft.level.getBlockState(menu.spot().support());
    return block.getRenderShape() == RenderShape.MODEL ? block : fallback;
  }

  private NodeView view(double mouseX, double mouseY) {
    return new NodeView(menu, mouseX, mouseY, menu.viewerIsOwner());
  }

  private Box panel() {
    return new Box(leftPos + PANEL.x(), topPos + PANEL.y(), PANEL.width(), PANEL.height());
  }
}
