package io.github.marcelofabianov.redweave.network;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.config.RedweaveConfig;
import io.github.marcelofabianov.redweave.menu.ControllerMenu;
import io.github.marcelofabianov.redweave.menu.RenameTarget;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

/**
 * The owner renamed the network, a channel or a node on the Controller Screen (answers 144, 156 and
 * 217). The server takes it only from the open screen of that controller, still valid, and the
 * domain checks the owner and the length (rules 16 and 17). A node is named by its row, never by
 * its ID, and a text longer than the ceiling of the config never decodes.
 */
public record ControllerRenamed(BlockPos pos, int target, int index, String name)
    implements CustomPacketPayload {
  private static final String ID = "controller_renamed";

  public static final Type<ControllerRenamed> TYPE =
      new Type<>(ResourceLocation.fromNamespaceAndPath(RedweaveMod.MOD_ID, ID));

  public static final StreamCodec<RegistryFriendlyByteBuf, ControllerRenamed> STREAM_CODEC =
      StreamCodec.composite(
          BlockPos.STREAM_CODEC,
          ControllerRenamed::pos,
          ByteBufCodecs.VAR_INT,
          ControllerRenamed::target,
          ByteBufCodecs.VAR_INT,
          ControllerRenamed::index,
          ByteBufCodecs.stringUtf8(RedweaveConfig.MAX_NAME),
          ControllerRenamed::name,
          ControllerRenamed::new);

  public static ControllerRenamed of(BlockPos pos, RenameTarget target, int index, String name) {
    return new ControllerRenamed(pos, target.ordinal(), index, name);
  }

  @Override
  public Type<ControllerRenamed> type() {
    return TYPE;
  }

  /** On the server thread (the default of the registrar). */
  public static void receive(ControllerRenamed renamed, Player player) {
    RenameTarget[] targets = RenameTarget.values();
    boolean screenOfTheController =
        player.containerMenu instanceof ControllerMenu menu
            && menu.pos().equals(renamed.pos())
            && menu.stillValid(player);
    if (!screenOfTheController || renamed.target() < 0 || renamed.target() >= targets.length) {
      return;
    }
    ((ControllerMenu) player.containerMenu)
        .rename(player, targets[renamed.target()], renamed.index(), renamed.name());
  }
}
