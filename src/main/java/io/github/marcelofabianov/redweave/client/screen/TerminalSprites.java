package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.RedweaveMod;
import net.minecraft.resources.ResourceLocation;

/** The sprites of the screen of the Network Terminal, from tela-do-terminal-sprites.png. */
final class TerminalSprites {
  private static final String FOLDER = "terminal_screen/";

  static final ResourceLocation NETWORK_ROW_SELECTED = of("network_row_selected");
  static final ResourceLocation BUTTON_DANGER = of("button_danger");
  static final ResourceLocation BUTTON_DANGER_HOVER = of("button_danger_hover");
  static final ResourceLocation BUTTON_CONFIRM = of("button_confirm");

  private TerminalSprites() {}

  private static ResourceLocation of(String name) {
    return ResourceLocation.fromNamespaceAndPath(RedweaveMod.MOD_ID, FOLDER + name);
  }
}
