package io.github.marcelofabianov.redweave.client.screen;

/** The text colors of the screens, from the palette of _docs/conceito/visual.md. */
final class ScreenColors {
  static final int TEXT = 0xFFE8EAED;
  static final int LABEL = 0xFF8A9099;
  static final int DIM = 0xFF5C626C;
  static final int ON_COPPER = 0xFF2B1A0E;
  static final int ONLINE = 0xFF6BD48F;
  static final int UNLOADED = 0xFFE3A33B;
  static final int OUT_OF_RANGE = 0xFFE2392F;
  static final int NO_NETWORK = 0xFF8A9099;

  /** A network turned off is grey, so the player knows it exists and is only off (177). */
  static final int OFFLINE = 0xFF8A9099;

  /**
   * The copper of the plates, and the red of a signal on, as the redstone (answers 437 and 452).
   */
  static final int COPPER = 0xFFC27B4C;

  static final int REDSTONE = 0xFFE2392F;

  /** A Signal Inverter burnt out, in amber, and its dark gray arrow (answers 448 and 452). */
  static final int BURNT_OUT = 0xFFE3A33B;

  static final int BURNT = 0xFF4A4F57;

  static final int SEPARATOR = 0xFF3A3E45;
  static final int SWATCH_EDGE = 0xFF15171B;
  static final int HIGHLIGHT = 0x8CE0975F;
  static final int SELECTED_FACE = 0xB4C27B4C;

  private ScreenColors() {}
}
