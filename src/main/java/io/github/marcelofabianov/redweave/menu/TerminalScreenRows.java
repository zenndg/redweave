package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.application.TerminalRow;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

/**
 * The networks of the owner on the Network Terminal, in alphabetical order, sent when the screen
 * opens and again only when they change (answers 131 and 138, and rule 18). The IDs stay on the
 * server: the client names a network by its row.
 */
public record TerminalScreenRows(int menu, List<NetworkLine> networks)
    implements CustomPacketPayload {
  private static final String ID = "terminal_screen_rows";

  public static final Type<TerminalScreenRows> TYPE =
      new Type<>(ResourceLocation.fromNamespaceAndPath(RedweaveMod.MOD_ID, ID));

  /** A row: the name, and the color, the state and the nodes as numbers. */
  public record NetworkLine(String name, int color, int state, int nodes) {
    static final StreamCodec<RegistryFriendlyByteBuf, NetworkLine> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            NetworkLine::name,
            ByteBufCodecs.VAR_INT,
            NetworkLine::color,
            ByteBufCodecs.VAR_INT,
            NetworkLine::state,
            ByteBufCodecs.VAR_INT,
            NetworkLine::nodes,
            NetworkLine::new);

    static NetworkLine of(TerminalRow row) {
      return new NetworkLine(row.name(), row.color().ordinal(), row.state().ordinal(), row.nodes());
    }
  }

  public static final StreamCodec<RegistryFriendlyByteBuf, TerminalScreenRows> STREAM_CODEC =
      StreamCodec.composite(
          ByteBufCodecs.VAR_INT,
          TerminalScreenRows::menu,
          NetworkLine.STREAM_CODEC.apply(ByteBufCodecs.list()),
          TerminalScreenRows::networks,
          TerminalScreenRows::new);

  public TerminalScreenRows {
    networks = List.copyOf(networks);
  }

  static TerminalScreenRows of(int menu, List<TerminalRow> rows) {
    return new TerminalScreenRows(menu, rows.stream().map(NetworkLine::of).toList());
  }

  TerminalScreenRows forMenu(int other) {
    return new TerminalScreenRows(other, networks);
  }

  /** On the client: only the open Network Terminal of the same menu takes the rows. */
  public static void receive(TerminalScreenRows rows, Player player) {
    if (player.containerMenu instanceof TerminalMenu menu && menu.containerId == rows.menu()) {
      menu.acceptRows(rows);
    }
  }

  @Override
  public Type<TerminalScreenRows> type() {
    return TYPE;
  }
}
