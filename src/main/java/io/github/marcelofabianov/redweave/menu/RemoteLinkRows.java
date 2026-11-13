package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.RedweaveMod;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

/**
 * What the screen of the link shows, sent by the server: the step, the link of now and the mode on
 * top, and the lines of the step (answer 365). The client only draws it and asks by position.
 */
public record RemoteLinkRows(
    int menu, int step, Component current, int mode, List<RemoteLinkLine> lines)
    implements CustomPacketPayload {
  private static final String ID = "remote_link_rows";

  public static final Type<RemoteLinkRows> TYPE =
      new Type<>(ResourceLocation.fromNamespaceAndPath(RedweaveMod.MOD_ID, ID));

  public static final StreamCodec<RegistryFriendlyByteBuf, RemoteLinkRows> STREAM_CODEC =
      StreamCodec.composite(
          ByteBufCodecs.VAR_INT,
          RemoteLinkRows::menu,
          ByteBufCodecs.VAR_INT,
          RemoteLinkRows::step,
          ComponentSerialization.TRUSTED_STREAM_CODEC,
          RemoteLinkRows::current,
          ByteBufCodecs.VAR_INT,
          RemoteLinkRows::mode,
          RemoteLinkLine.STREAM_CODEC.apply(ByteBufCodecs.list()),
          RemoteLinkRows::lines,
          RemoteLinkRows::new);

  public RemoteLinkRows {
    lines = List.copyOf(lines);
  }

  RemoteLinkRows forMenu(int other) {
    return new RemoteLinkRows(other, step, current, mode, lines);
  }

  /** On the client: only the open link screen of the same menu takes the rows. */
  public static void receive(RemoteLinkRows rows, Player player) {
    if (player.containerMenu instanceof RemoteLinkMenu menu && menu.containerId == rows.menu()) {
      menu.acceptRows(rows);
    }
  }

  @Override
  public Type<RemoteLinkRows> type() {
    return TYPE;
  }
}
