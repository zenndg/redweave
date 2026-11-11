package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.RedweaveMod;
import net.minecraft.resources.ResourceLocation;

/** The sprites of the Node Screen, from _docs/imagens/telas/tela-do-no-sprites-*.png (78, 198). */
final class NodeSprites {
  private static final String FOLDER = "node_screen/";

  static final ResourceLocation WINDOW = of("window");
  static final ResourceLocation PANEL = of("panel");
  static final ResourceLocation SLOT = of("slot");
  static final ResourceLocation SLOT_HOVER = of("slot_hover");
  static final ResourceLocation SLOT_SELECTED = of("slot_selected");
  static final ResourceLocation SLOT_FREE = of("slot_free");
  static final ResourceLocation SLOT_FREE_HOVER = of("slot_free_hover");
  static final ResourceLocation TAB_SELECTED = of("tab_selected");
  static final ResourceLocation STATE_PILL = of("state_pill");
  static final ResourceLocation DOT = of("dot");
  static final ResourceLocation LOCK = of("lock");
  static final ResourceLocation BUTTON = of("button");
  static final ResourceLocation BUTTON_HOVER = of("button_hover");
  static final ResourceLocation BUTTON_DISABLED = of("button_disabled");
  static final ResourceLocation BUTTON_SELECTED = of("button_selected");
  static final ResourceLocation BUTTON_SELECTED_HOVER = of("button_selected_hover");
  static final ResourceLocation SWATCH = of("swatch");
  static final ResourceLocation SWATCH_HOVER = of("swatch_hover");
  static final ResourceLocation SWATCH_SELECTED = of("swatch_selected");
  static final ResourceLocation NETWORK_ROW = of("network_row");
  static final ResourceLocation NETWORK_ROW_HOVER = of("network_row_hover");
  static final ResourceLocation NETWORK_ROW_LAST_USED = of("network_row_last_used");
  static final ResourceLocation NETWORK_ROW_LAST_USED_HOVER = of("network_row_last_used_hover");
  static final ResourceLocation NAME_FIELD = of("name_field");
  static final ResourceLocation NAME_FIELD_FOCUSED = of("name_field_focused");
  static final ResourceLocation NETWORK_NAME_LINK = of("network_name_link");
  static final ResourceLocation NETWORK_NAME_LINK_HOVER = of("network_name_link_hover");
  static final ResourceLocation ICON_EDIT = of("icon_edit");
  static final ResourceLocation ICON_NO_NETWORK = of("icon_no_network");
  static final ResourceLocation BAR_OFF = of("bar_off");
  static final ResourceLocation BAR_ON = of("bar_on");
  static final ResourceLocation BAR_GLOW = of("bar_glow");

  private NodeSprites() {}

  private static ResourceLocation of(String name) {
    return ResourceLocation.fromNamespaceAndPath(RedweaveMod.MOD_ID, FOLDER + name);
  }
}
