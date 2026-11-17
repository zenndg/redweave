package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.domain.network.Face;
import java.util.Locale;

/**
 * The translation keys of the Node Screen. They live outside the client package, because the data
 * generation, which is common code, writes the texts (AGENTS.md, rules 1 and 19).
 */
public final class NodeScreenTexts {
  private static final String PREFIX = "screen." + RedweaveMod.MOD_ID + ".node.";

  public static final String INPUT = PREFIX + "input";
  public static final String OUTPUT = PREFIX + "output";
  public static final String DIRECTION = PREFIX + "direction";
  public static final String FACE_TOOLTIP = PREFIX + "face_tooltip";
  public static final String SIGNAL = PREFIX + "signal";
  public static final String CHANNELS_ACTIVE = PREFIX + "channels_active";
  public static final String STATUS = PREFIX + "status";
  public static final String ENABLED = PREFIX + "enabled";
  public static final String DISABLED = PREFIX + "disabled";
  public static final String READ_ONLY = PREFIX + "read_only";
  public static final String NOT_IN_A_NETWORK = PREFIX + "not_in_a_network";
  public static final String STATE_ONLINE = PREFIX + "state.online";
  public static final String STATE_UNLOADED = PREFIX + "state.unloaded";
  public static final String STATE_OUT_OF_RANGE = PREFIX + "state.out_of_range";
  public static final String STATE_NO_NETWORK = PREFIX + "state.no_network";
  public static final String STATE_OFFLINE = PREFIX + "state.offline";
  public static final String CHOOSE_NETWORK = PREFIX + "choose_network";
  public static final String NO_NETWORK_ONLINE = PREFIX + "no_network_online";
  public static final String COMPONENT_COUNT_ONE = PREFIX + "component_count.one";
  public static final String COMPONENT_COUNT_MANY = PREFIX + "component_count.many";
  public static final String BACK = PREFIX + "back";
  public static final String RENAME_TOOLTIP = PREFIX + "rename_tooltip";
  public static final String SWITCH_NETWORK_TOOLTIP = PREFIX + "switch_network_tooltip";

  private static final String FACE_PREFIX = PREFIX + "face.";

  /** The color names come from the game, which already translates the 16 dyes. */
  private static final String COLOR_PREFIX = "color.minecraft.";

  private NodeScreenTexts() {}

  public static String face(Face face) {
    return FACE_PREFIX + face.name().toLowerCase(Locale.ROOT);
  }

  public static String color(String colorKey) {
    return COLOR_PREFIX + colorKey;
  }
}
