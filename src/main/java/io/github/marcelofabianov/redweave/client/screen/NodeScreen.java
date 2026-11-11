package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.block.Faces;
import io.github.marcelofabianov.redweave.block.SignalNodeBlock;
import io.github.marcelofabianov.redweave.config.NeoForgeNetworkLimits;
import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.menu.NodeAction;
import io.github.marcelofabianov.redweave.menu.NodeMenu;
import io.github.marcelofabianov.redweave.menu.NodeScreenCodes;
import io.github.marcelofabianov.redweave.menu.NodeScreenTexts;
import io.github.marcelofabianov.redweave.network.NodeRenamed;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
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
 * it, and its owner goes back to it with a click on the network (answers 139, 141 and 172).
 */
public final class NodeScreen extends AbstractContainerScreen<NodeMenu> {
  private static final int WIDTH = 312;
  private static final int HEIGHT = 234;
  private static final int PADDING = 10;
  private static final int FOOTER_HEIGHT = 32;
  private static final int LIST_LABEL_Y = 40;
  private static final int LIST_Y = 52;
  private static final int LIST_WIDTH = 118;
  private static final Box PANEL = new Box(136, 38, 166, 160);
  private static final int BACK_WIDTH = 48;
  private static final int BACK_HEIGHT = 16;
  private static final int BACK_Y = 8;

  private final SlotList list = new SlotList();
  private final FaceCube cube = new FaceCube();
  private final NodeScreenFooter footer = new NodeScreenFooter();
  private final NetworkChoiceList choices = new NetworkChoiceList();
  private final NameField name = new NameField();
  private int selected;
  private boolean clearArmed;
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
    selected = Math.max(0, Math.min(selected, menu.slotCount() - 1));
    NodeScreenFrame.label(graphics, font, leftPos + PADDING, topPos + LIST_LABEL_Y);
    list.draw(graphics, font, view, listArea(), selected);
    Box panel = panel();
    NodeScreenPanel.draw(graphics, font, view, panel, selected, menu.slotCount() > 0);
    controls(view).forEach(control -> ControlPainter.draw(graphics, font, control, view));
    menu.slot(selected)
        .ifPresent(slot ->
            cube.draw(
                graphics,
                SlotEditor.cubeBox(panel),
                supportBlock(),
                Optional.of(Faces.toDirection(slot.face())),
                view));
    footer.draw(
        graphics,
        font,
        view,
        new Box(
            leftPos + PADDING, topPos + HEIGHT - FOOTER_HEIGHT, WIDTH - 2 * PADDING, FOOTER_HEIGHT),
        selected);
  }

  /** Every text is drawn in renderBg, so the game does not draw its own labels on top. */
  @Override
  protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {}

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    if (name.editing() && !name.box().contains(mouseX, mouseY)) {
      name.cancel();
    }
    if (showsChoice()) {
      return clickChoice(mouseX, mouseY) || super.mouseClicked(mouseX, mouseY, button);
    }
    if (clickHeader(mouseX, mouseY)) {
      return true;
    }
    NodeView view = view(mouseX, mouseY);
    Optional<Integer> row = list.slotAt(listArea(), menu.slotCount(), mouseX, mouseY);
    if (row.isPresent()) {
      choose(row.get(), view);
      return true;
    }
    if (menu.slot(selected).isPresent() && view.hovers(SlotEditor.cubeBox(panel()))) {
      cube.press();
      return true;
    }
    return clickControl(view) || super.mouseClicked(mouseX, mouseY, button);
  }

  @Override
  public boolean mouseDragged(
      double mouseX, double mouseY, int button, double dragX, double dragY) {
    cube.drag(dragX, dragY);
    return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
  }

  @Override
  public boolean mouseReleased(double mouseX, double mouseY, int button) {
    Optional<ChannelSlot> slot = menu.slot(selected);
    Optional<Direction> face = cube.release(SlotEditor.cubeBox(panel()), view(mouseX, mouseY));
    if (slot.isPresent() && face.isPresent()) {
      ChannelSlot changed =
          new ChannelSlot(slot.get().direction(), Faces.of(face.get()), slot.get().channel());
      send(NodeAction.configure(selected, changed));
    }
    return super.mouseReleased(mouseX, mouseY, button);
  }

  /** Enter saves the name, Esc cancels it, and no key closes the screen while typing (204). */
  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    if (!name.editing()) {
      return super.keyPressed(keyCode, scanCode, modifiers);
    }
    name.keyPressed(
        keyCode,
        scanCode,
        modifiers,
        text -> PacketDistributor.sendToServer(new NodeRenamed(menu.pos(), text)));
    return true;
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
    if (showsChoice() && choiceArea().contains(mouseX, mouseY)) {
      choices.scroll(scrollY, choiceArea(), menu.choices().size());
      return true;
    }
    if (listArea().contains(mouseX, mouseY)) {
      list.scroll(scrollY);
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
      selected = 0;
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

  /** A click on a free slot uses it at once (answer 83); on a used one, selects it. */
  private void choose(int index, NodeView view) {
    selected = index;
    clearArmed = false;
    list.reveal(index);
    if (menu.slot(index).isEmpty() && view.editable()) {
      send(NodeAction.use(index));
    }
  }

  private boolean clickControl(NodeView view) {
    for (Control control : controls(view)) {
      if (control.clickable() && view.hovers(control.box())) {
        clearArmed = control.look() == Control.Look.ARMS_CLEAR;
        control.action().ifPresent(this::send);
        return true;
      }
    }
    clearArmed = false;
    return false;
  }

  private Optional<Component> tooltipAt(NodeView view) {
    if (showsChoice()) {
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
    return NodeScreenPanel.tooltipAt(font, view, panel(), selected)
        .or(() -> footer.tooltipAt(view));
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
    return new Box(leftPos + PADDING, topPos + PANEL.y(), WIDTH - 2 * PADDING, PANEL.height());
  }

  private List<Control> controls(NodeView view) {
    return SlotEditor.controls(font, view, panel(), selected, clearArmed);
  }

  private void send(NodeAction action) {
    if (minecraft != null && minecraft.gameMode != null) {
      minecraft.gameMode.handleInventoryButtonClick(
          menu.containerId, NodeScreenCodes.button(action));
    }
  }

  /** The block the node sticks to, or polished deepslate for a block drawn some other way. */
  private BlockState supportBlock() {
    BlockState fallback = Blocks.POLISHED_DEEPSLATE.defaultBlockState();
    if (minecraft == null || minecraft.level == null) {
      return fallback;
    }
    BlockState node = minecraft.level.getBlockState(menu.pos());
    if (!node.hasProperty(SignalNodeBlock.FACING)) {
      return fallback;
    }
    BlockPos support = menu.pos().relative(node.getValue(SignalNodeBlock.FACING).getOpposite());
    BlockState block = minecraft.level.getBlockState(support);
    return block.getRenderShape() == RenderShape.MODEL ? block : fallback;
  }

  private NodeView view(double mouseX, double mouseY) {
    return new NodeView(menu, mouseX, mouseY, menu.viewerIsOwner());
  }

  private Box listArea() {
    return new Box(leftPos + PADDING, topPos + LIST_Y, LIST_WIDTH, SlotList.height());
  }

  private Box panel() {
    return new Box(leftPos + PANEL.x(), topPos + PANEL.y(), PANEL.width(), PANEL.height());
  }
}
