package io.github.marcelofabianov.redweave.client;

import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import java.util.EnumMap;
import java.util.Map;

/** The 16 wool colors of the channels, as in _docs/conceito/visual.md. */
public final class ChannelColors {
  private static final int OPAQUE = 0xFF000000;

  /** A corner of a free slot: the dark of the plate, so the dot looks off (answer 87). */
  public static final int FREE = 0xFF2E3238;

  private static final int WHITE = 0xF9FFFE;
  private static final int ORANGE = 0xF9801D;
  private static final int MAGENTA = 0xC74EBD;
  private static final int LIGHT_BLUE = 0x3AB3DA;
  private static final int YELLOW = 0xFED83D;
  private static final int LIME = 0x80C71F;
  private static final int PINK = 0xF38BAA;
  private static final int GRAY = 0x474F52;
  private static final int LIGHT_GRAY = 0x9D9D97;
  private static final int CYAN = 0x169C9C;
  private static final int PURPLE = 0x8932B8;
  private static final int BLUE = 0x3C44AA;
  private static final int BROWN = 0x835432;
  private static final int GREEN = 0x5E7C16;
  private static final int RED = 0xB02E26;
  private static final int BLACK = 0x1D1D21;

  private static final Map<ChannelColor, Integer> RGB = new EnumMap<>(ChannelColor.class);

  static {
    RGB.put(ChannelColor.WHITE, WHITE);
    RGB.put(ChannelColor.ORANGE, ORANGE);
    RGB.put(ChannelColor.MAGENTA, MAGENTA);
    RGB.put(ChannelColor.LIGHT_BLUE, LIGHT_BLUE);
    RGB.put(ChannelColor.YELLOW, YELLOW);
    RGB.put(ChannelColor.LIME, LIME);
    RGB.put(ChannelColor.PINK, PINK);
    RGB.put(ChannelColor.GRAY, GRAY);
    RGB.put(ChannelColor.LIGHT_GRAY, LIGHT_GRAY);
    RGB.put(ChannelColor.CYAN, CYAN);
    RGB.put(ChannelColor.PURPLE, PURPLE);
    RGB.put(ChannelColor.BLUE, BLUE);
    RGB.put(ChannelColor.BROWN, BROWN);
    RGB.put(ChannelColor.GREEN, GREEN);
    RGB.put(ChannelColor.RED, RED);
    RGB.put(ChannelColor.BLACK, BLACK);
  }

  private ChannelColors() {}

  /** The color with full opacity, as the screens and the block tint use it. */
  public static int argb(ChannelColor channel) {
    return OPAQUE | RGB.get(channel);
  }
}
