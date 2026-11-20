package io.github.zenndg.redweave.client.screen;

import io.github.zenndg.redweave.config.NeoForgeModuleLimits;
import io.github.zenndg.redweave.config.NeoForgeNetworkLimits;
import io.github.zenndg.redweave.config.RedweaveConfig;
import java.util.function.Consumer;
import java.util.function.ToIntFunction;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

/**
 * A name while its owner renames it: Enter hands the text on to be sent to the server, which checks
 * the owner and the length of the config, and Esc cancels (answers 156, 204 and 217).
 */
final class NameField {
  private static final int TEXT_X = 3;
  private static final int TEXT_Y = 2;

  private EditBox field;
  private Box box = new Box(0, 0, 0, 0);

  /** Creates the field again whenever the screen is laid out, hidden until a click. */
  void attach(Font font, Box where, Consumer<EditBox> add) {
    field = new EditBox(font, 0, 0, 0, 0, Component.empty());
    field.setBordered(false);
    field.setTextColor(ScreenColors.TEXT);
    field.setVisible(false);
    moveTo(where);
    add.accept(field);
  }

  /** Places the field over another name, as a row of a list. */
  void moveTo(Box where) {
    box = where;
    field.setX(where.x() + TEXT_X);
    field.setY(where.y() + TEXT_Y);
    field.setWidth(where.width() - 2 * TEXT_X);
    field.setHeight(where.height() - TEXT_Y);
  }

  Box box() {
    return box;
  }

  boolean editing() {
    return field != null && field.isVisible();
  }

  void start(String current, int maxLength, Consumer<EditBox> focus) {
    field.setMaxLength(maxLength);
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

  void keyPressed(int keyCode, int scanCode, int modifiers, Consumer<String> submit) {
    if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
      submit.accept(field.getValue());
      cancel();
      return;
    }
    if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
      cancel();
      return;
    }
    field.keyPressed(keyCode, scanCode, modifiers);
  }

  /** A limit of the server, which syncs its config, or the ceiling before it arrives. */
  static int limit(ToIntFunction<NeoForgeNetworkLimits> length) {
    return RedweaveConfig.SPEC.isLoaded()
        ? length.applyAsInt(new NeoForgeNetworkLimits())
        : RedweaveConfig.MAX_NAME;
  }

  /** The limit of the name of a module, from the config of the server (answer 391). */
  static int moduleLimit() {
    return RedweaveConfig.SPEC.isLoaded()
        ? new NeoForgeModuleLimits().moduleNameLength()
        : RedweaveConfig.MAX_NAME;
  }
}
