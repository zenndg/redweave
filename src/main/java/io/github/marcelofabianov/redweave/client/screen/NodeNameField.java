package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.config.NeoForgeNetworkLimits;
import io.github.marcelofabianov.redweave.config.RedweaveConfig;
import io.github.marcelofabianov.redweave.network.NodeRenamed;
import java.util.function.Consumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

/**
 * The name of the node while its owner renames it: Enter sends it to the server, which checks the
 * owner and node.name_length, and Esc cancels (answer 204). Blank goes back to the default (144).
 */
final class NodeNameField {
  private static final int TEXT_X = 3;
  private static final int TEXT_Y = 2;

  private EditBox field;
  private Box box = new Box(0, 0, 0, 0);

  /** Creates the field again whenever the screen is laid out, hidden until a click. */
  void attach(Font font, Box where, Consumer<EditBox> add) {
    box = where;
    field =
        new EditBox(
            font,
            where.x() + TEXT_X,
            where.y() + TEXT_Y,
            where.width() - 2 * TEXT_X,
            where.height() - TEXT_Y,
            Component.empty());
    field.setBordered(false);
    field.setMaxLength(maxLength());
    field.setTextColor(ScreenColors.TEXT);
    field.setVisible(false);
    add.accept(field);
  }

  Box box() {
    return box;
  }

  boolean editing() {
    return field != null && field.isVisible();
  }

  void start(String current, Consumer<EditBox> focus) {
    field.setValue(current);
    field.setVisible(true);
    focus.accept(field);
    field.setFocused(true);
  }

  void cancel() {
    field.setVisible(false);
    field.setFocused(false);
  }

  void drawBackground(GuiGraphics graphics) {
    if (editing()) {
      graphics.blitSprite(
          NodeSprites.NAME_FIELD_FOCUSED, box.x(), box.y(), box.width(), box.height());
    }
  }

  void keyPressed(int keyCode, int scanCode, int modifiers, BlockPos pos) {
    if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
      PacketDistributor.sendToServer(new NodeRenamed(pos, field.getValue()));
      cancel();
      return;
    }
    if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
      cancel();
      return;
    }
    field.keyPressed(keyCode, scanCode, modifiers);
  }

  /** The limit of the server, which syncs its config, or the ceiling before it arrives. */
  private static int maxLength() {
    return RedweaveConfig.SPEC.isLoaded()
        ? new NeoForgeNetworkLimits().nodeNameLength()
        : RedweaveConfig.MAX_NAME;
  }
}
