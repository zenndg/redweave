package io.github.marcelofabianov.redweave.item;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.slf4j.Logger;

/**
 * The color of a Signal Cable in its item, saved as the key of the dye, such as `red` (answer 561).
 * An unknown key, as from a damaged stack, gives a white cable and a line in the log instead of
 * losing the stack (rule 14); the item then drops the white component (SignalCableItem).
 */
public final class CableColorCodec {
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String UNKNOWN = "Unknown Signal Cable color '{}', made white";

  public static final Codec<ChannelColor> CODEC =
      Codec.STRING.xmap(CableColorCodec::fromKey, ChannelColor::key);

  public static final StreamCodec<ByteBuf, ChannelColor> STREAM_CODEC =
      ByteBufCodecs.VAR_INT.map(CableColorCodec::fromIndex, ChannelColor::ordinal);

  private CableColorCodec() {}

  private static ChannelColor fromKey(String key) {
    return ChannelColor.fromKey(key)
        .orElseGet(() -> {
          LOGGER.warn(UNKNOWN, key);
          return ChannelColor.WHITE;
        });
  }

  private static ChannelColor fromIndex(int index) {
    ChannelColor[] colors = ChannelColor.values();
    return index >= 0 && index < colors.length ? colors[index] : ChannelColor.WHITE;
  }
}
