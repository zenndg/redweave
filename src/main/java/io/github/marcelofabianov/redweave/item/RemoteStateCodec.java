package io.github.marcelofabianov.redweave.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.marcelofabianov.redweave.domain.network.BlockLocation;
import io.github.marcelofabianov.redweave.domain.network.Face;
import java.util.Arrays;
import java.util.Locale;
import net.minecraft.core.UUIDUtil;

/**
 * The Signal Remote in the saves of the item. The field names are part of the save (rule 15). The
 * link to a slot stays under `link`, as before, and the link to a Signal Emitter goes under
 * `emitter`, with its dimension, its block, the face of its space and the ID of its module (answer
 * 786). An unknown mode falls back to the lever, and a damaged link to an emitter is dropped, so a
 * damaged item never breaks the game (rule 14).
 */
public final class RemoteStateCodec {
  private static final String NETWORK = "network";
  private static final String NODE = "node";
  private static final String SLOT = "slot";
  private static final String ID = "id";
  private static final String LINK = "link";
  private static final String MODE = "mode";
  private static final String ON = "on";
  private static final String PULSE_END = "pulse_end";
  private static final String LOST = "lost";
  private static final String EMITTER = "emitter";
  private static final String DIMENSION = "dimension";
  private static final String X = "x";
  private static final String Y = "y";
  private static final String Z = "z";
  private static final String FACE = "face";
  private static final String MODULE = "module";
  private static final String UNKNOWN_FACE = "Unknown face of an emitter: ";
  private static final long NO_PULSE = 0;

  private static final Codec<RemoteLink> LINK_CODEC =
      RecordCodecBuilder.create(instance ->
          instance
              .group(
                  UUIDUtil.CODEC.fieldOf(NETWORK).forGetter(RemoteLink::network),
                  UUIDUtil.CODEC.fieldOf(NODE).forGetter(RemoteLink::node),
                  Codec.INT.fieldOf(SLOT).forGetter(RemoteLink::slot))
              .apply(instance, RemoteLink::new));

  private static final MapCodec<BlockLocation> PLACE_CODEC =
      RecordCodecBuilder.mapCodec(instance ->
          instance
              .group(
                  Codec.STRING.fieldOf(DIMENSION).forGetter(BlockLocation::dimension),
                  Codec.INT.fieldOf(X).forGetter(BlockLocation::x),
                  Codec.INT.fieldOf(Y).forGetter(BlockLocation::y),
                  Codec.INT.fieldOf(Z).forGetter(BlockLocation::z))
              .apply(instance, BlockLocation::new));

  private static final Codec<Face> FACE_CODEC =
      Codec.STRING.comapFlatMap(
          name ->
              Arrays.stream(Face.values())
                  .filter(face -> face.name().toLowerCase(Locale.ROOT).equals(name))
                  .findFirst()
                  .map(DataResult::success)
                  .orElseGet(() -> DataResult.error(() -> UNKNOWN_FACE + name)),
          face -> face.name().toLowerCase(Locale.ROOT));

  private static final Codec<EmitterLink> EMITTER_CODEC =
      RecordCodecBuilder.create(instance ->
          instance
              .group(
                  PLACE_CODEC.forGetter(EmitterLink::place),
                  FACE_CODEC.fieldOf(FACE).forGetter(EmitterLink::face),
                  UUIDUtil.CODEC.fieldOf(MODULE).forGetter(EmitterLink::module))
              .apply(instance, EmitterLink::new));

  private static final Codec<RemoteMode> MODE_CODEC =
      Codec.STRING.xmap(
          name -> RemoteMode.byName(name).orElse(RemoteMode.LEVER), RemoteMode::serializedName);

  public static final Codec<RemoteState> CODEC =
      RecordCodecBuilder.create(instance ->
          instance
              .group(
                  UUIDUtil.CODEC.optionalFieldOf(ID).forGetter(RemoteState::id),
                  LINK_CODEC.optionalFieldOf(LINK).forGetter(RemoteState::link),
                  EMITTER_CODEC.lenientOptionalFieldOf(EMITTER).forGetter(RemoteState::emitter),
                  MODE_CODEC.optionalFieldOf(MODE, RemoteMode.LEVER).forGetter(RemoteState::mode),
                  Codec.BOOL.optionalFieldOf(ON, false).forGetter(RemoteState::on),
                  Codec.LONG.optionalFieldOf(PULSE_END, NO_PULSE).forGetter(RemoteState::pulseEnd),
                  Codec.BOOL.optionalFieldOf(LOST, false).forGetter(RemoteState::isLost))
              .apply(instance, RemoteState::read));

  private RemoteStateCodec() {}
}
