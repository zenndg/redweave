package io.github.marcelofabianov.redweave.menu;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * One line of a step of the link, built by the server: the name, the color square as the ordinal of
 * a ChannelColor or none, a detail on the right and the state of a node as the ordinal of NodeState
 * or none, for its color (answers 283 to 286 and 365).
 */
public record RemoteLinkLine(Component name, int swatch, Component detail, int state) {
  /** No color square, or no state. */
  public static final int NONE = -1;

  public static final StreamCodec<RegistryFriendlyByteBuf, RemoteLinkLine> STREAM_CODEC =
      StreamCodec.composite(
          ComponentSerialization.TRUSTED_STREAM_CODEC,
          RemoteLinkLine::name,
          ByteBufCodecs.INT,
          RemoteLinkLine::swatch,
          ComponentSerialization.TRUSTED_STREAM_CODEC,
          RemoteLinkLine::detail,
          ByteBufCodecs.INT,
          RemoteLinkLine::state,
          RemoteLinkLine::new);
}
