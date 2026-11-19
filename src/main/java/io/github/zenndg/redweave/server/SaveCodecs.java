package io.github.zenndg.redweave.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.zenndg.redweave.config.RedweaveConfig;
import io.github.zenndg.redweave.domain.network.BlockLocation;
import io.github.zenndg.redweave.domain.network.Channel;
import io.github.zenndg.redweave.domain.network.Direction;
import io.github.zenndg.redweave.domain.network.Face;
import io.github.zenndg.redweave.domain.network.NetworkId;
import io.github.zenndg.redweave.domain.network.NodeId;
import io.github.zenndg.redweave.domain.network.PlayerId;
import io.github.zenndg.redweave.domain.signal.ChannelColor;
import io.github.zenndg.redweave.domain.signal.OutputDelay;
import io.github.zenndg.redweave.domain.signal.OutputDelayLimits;
import io.github.zenndg.redweave.domain.signal.Signal;
import java.util.Optional;
import net.minecraft.core.UUIDUtil;

/**
 * The codecs of the small values of the saves (answer 89), also used by the copy of the
 * Configurator, so both keep the same keys (answer 105). The UUIDs use the codec of the game, the
 * same form as the NBT of the blocks of the mod.
 */
public final class SaveCodecs {
  static final String DIMENSION = "dimension";
  static final String X = "x";
  static final String Y = "y";
  static final String Z = "z";
  static final String COLOR = "color";
  static final String NAME = "name";

  /** The key of the output delay of a node, the same as in the NBT of a module (answer 869). */
  public static final String OUTPUT_DELAY_KEY = "output_delay_ticks";

  private static final OutputDelayLimits ANY_LENGTH = () -> Integer.MAX_VALUE;
  private static final String UNKNOWN_KEY = "Unknown key: ";
  private static final String INVALID_NAME = "Blank, padded or too long name: ";

  public static final Codec<Face> FACE = keyed(Face.class);
  public static final Codec<Direction> DIRECTION = keyed(Direction.class);
  public static final Codec<ChannelColor> CHANNEL_COLOR =
      Codec.STRING.comapFlatMap(
          key -> ChannelColor.fromKey(key).map(DataResult::success).orElseGet(() -> unknown(key)),
          ChannelColor::key);
  static final Codec<Signal> SIGNAL =
      Codec.intRange(Signal.OFF.strength(), Signal.MAX.strength())
          .xmap(Signal::new, Signal::strength);

  /**
   * An output delay in ticks, brought to whole seconds from 0 instead of refused (rule 14). Who
   * reads it fits it to the config, which a codec cannot read before the server starts (837).
   */
  public static final Codec<OutputDelay> OUTPUT_DELAY =
      Codec.INT.xmap(ticks -> OutputDelay.within(ticks, ANY_LENGTH), OutputDelay::ticks);

  public static final Codec<NetworkId> NETWORK_ID =
      UUIDUtil.CODEC.xmap(NetworkId::new, NetworkId::value);
  static final Codec<NodeId> NODE_ID = UUIDUtil.CODEC.xmap(NodeId::new, NodeId::value);
  static final Codec<PlayerId> PLAYER_ID = UUIDUtil.CODEC.xmap(PlayerId::new, PlayerId::value);

  /** A name kept as it was given, up to the ceiling of the config (answer 93). */
  public static final Codec<String> SAVED_NAME = Codec.STRING.validate(SaveCodecs::checkName);

  static final Codec<BlockLocation> BLOCK =
      RecordCodecBuilder.create(instance ->
          instance
              .group(
                  Codec.STRING.fieldOf(DIMENSION).forGetter(BlockLocation::dimension),
                  Codec.INT.fieldOf(X).forGetter(BlockLocation::x),
                  Codec.INT.fieldOf(Y).forGetter(BlockLocation::y),
                  Codec.INT.fieldOf(Z).forGetter(BlockLocation::z))
              .apply(instance, BlockLocation::new));

  /** Only a channel with a name goes to the saves. */
  static final Codec<Channel> CHANNEL =
      RecordCodecBuilder.create(instance ->
          instance
              .group(
                  CHANNEL_COLOR.fieldOf(COLOR).forGetter(Channel::color),
                  SAVED_NAME.fieldOf(NAME).forGetter(channel -> channel.name().orElseThrow()))
              .apply(instance, (color, name) -> new Channel(color, Optional.of(name))));

  private SaveCodecs() {}

  private static <E extends Enum<E>> Codec<E> keyed(Class<E> type) {
    return Codec.STRING.comapFlatMap(
        key -> SaveKeys.parse(type, key).map(DataResult::success).orElseGet(() -> unknown(key)),
        SaveKeys::of);
  }

  private static <A> DataResult<A> unknown(String key) {
    return DataResult.error(() -> UNKNOWN_KEY + key);
  }

  private static DataResult<String> checkName(String name) {
    boolean valid =
        !name.isEmpty() && name.strip().equals(name) && name.length() <= RedweaveConfig.MAX_NAME;
    return valid ? DataResult.success(name) : DataResult.error(() -> INVALID_NAME + name);
  }
}
