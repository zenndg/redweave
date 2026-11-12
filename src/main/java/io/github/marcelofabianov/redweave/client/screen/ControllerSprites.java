package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.RedweaveMod;
import net.minecraft.resources.ResourceLocation;

/**
 * The sprites of the Controller Screen, from _docs/imagens/telas/tela-do-controlador-sprites.png
 * (answer 214). The window, the panel, the buttons, the swatches and the lock come from the Node
 * Screen.
 */
final class ControllerSprites {
  private static final String FOLDER = "controller_screen/";

  static final ResourceLocation TAB = of("tab");
  static final ResourceLocation TAB_HOVER = of("tab_hover");
  static final ResourceLocation TABLE_HEADER = of("table_header");
  static final ResourceLocation NODE_ROW = of("node_row");
  static final ResourceLocation NODE_ROW_HOVER = of("node_row_hover");
  static final ResourceLocation NODE_ROW_EXPANDED = of("node_row_expanded");
  static final ResourceLocation SLOT_LINE = of("slot_line");
  static final ResourceLocation CHANNEL_ROW = of("channel_row");
  static final ResourceLocation LOG_ROW = of("log_row");
  static final ResourceLocation PALETTE_PANEL = of("palette_panel");
  static final ResourceLocation COLOR_SQUARE = of("color_square");
  static final ResourceLocation COLOR_SQUARE_HOVER = of("color_square_hover");
  static final ResourceLocation ICON_EXPAND = of("icon_expand");
  static final ResourceLocation ICON_COLLAPSE = of("icon_collapse");
  static final ResourceLocation ICON_INPUT = of("icon_input");
  static final ResourceLocation ICON_OUTPUT = of("icon_output");

  private ControllerSprites() {}

  private static ResourceLocation of(String name) {
    return ResourceLocation.fromNamespaceAndPath(RedweaveMod.MOD_ID, FOLDER + name);
  }
}
