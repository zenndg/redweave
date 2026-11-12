package io.github.marcelofabianov.redweave.network;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.config.RedweaveConfig;
import io.github.marcelofabianov.redweave.menu.TerminalMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

/**
 * The owner renames a network from the screen of a Network Terminal (answers 156 and 252). The
 * server checks that the menu of that terminal is open and valid, and the domain the name (17).
 */
public record TerminalRenamed(BlockPos pos, int row, String name) implements CustomPacketPayload {
  private static final String ID = "terminal_renamed";

  public static final Type<TerminalRenamed> TYPE =
      new Type<>(ResourceLocation.fromNamespaceAndPath(RedweaveMod.MOD_ID, ID));

  public static final StreamCodec<RegistryFriendlyByteBuf, TerminalRenamed> STREAM_CODEC =
      StreamCodec.composite(
          BlockPos.STREAM_CODEC,
          TerminalRenamed::pos,
          ByteBufCodecs.VAR_INT,
          TerminalRenamed::row,
          ByteBufCodecs.stringUtf8(RedweaveConfig.MAX_NAME),
          TerminalRenamed::name,
          TerminalRenamed::new);

  @Override
  public Type<TerminalRenamed> type() {
    return TYPE;
  }

  /** On the server thread (the default of the registrar). */
  public static void receive(TerminalRenamed renamed, Player player) {
    if (player.containerMenu instanceof TerminalMenu menu
        && menu.pos().equals(renamed.pos())
        && menu.stillValid(player)) {
      menu.rename(player, renamed.row(), renamed.name());
    }
  }
}
