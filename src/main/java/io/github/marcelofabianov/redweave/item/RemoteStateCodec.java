package io.github.marcelofabianov.redweave.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;

/**
 * The Signal Remote in the saves of the item. The field names are part of the save (rule 15). An
 * unknown mode falls back to the lever, so a damaged item never breaks the game (rule 14).
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
  private static final long NO_PULSE = 0;

  private static final Codec<RemoteLink> LINK_CODEC =
      RecordCodecBuilder.create(instance ->
          instance
              .group(
                  UUIDUtil.CODEC.fieldOf(NETWORK).forGetter(RemoteLink::network),
                  UUIDUtil.CODEC.fieldOf(NODE).forGetter(RemoteLink::node),
                  Codec.INT.fieldOf(SLOT).forGetter(RemoteLink::slot))
              .apply(instance, RemoteLink::new));

  private static final Codec<RemoteMode> MODE_CODEC =
      Codec.STRING.xmap(
          name -> RemoteMode.byName(name).orElse(RemoteMode.LEVER), RemoteMode::serializedName);

  public static final Codec<RemoteState> CODEC =
      RecordCodecBuilder.create(instance ->
          instance
              .group(
                  UUIDUtil.CODEC.optionalFieldOf(ID).forGetter(RemoteState::id),
                  LINK_CODEC.optionalFieldOf(LINK).forGetter(RemoteState::link),
                  MODE_CODEC.optionalFieldOf(MODE, RemoteMode.LEVER).forGetter(RemoteState::mode),
                  Codec.BOOL.optionalFieldOf(ON, false).forGetter(RemoteState::on),
                  Codec.LONG.optionalFieldOf(PULSE_END, NO_PULSE).forGetter(RemoteState::pulseEnd),
                  Codec.BOOL.optionalFieldOf(LOST, false).forGetter(RemoteState::isLost))
              .apply(instance, RemoteState::new));

  private RemoteStateCodec() {}
}
