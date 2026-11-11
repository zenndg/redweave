package io.github.marcelofabianov.redweave.network;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.NodeSignals;
import io.github.marcelofabianov.redweave.config.RedweaveConfig;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.menu.NodeMenu;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

/**
 * The owner named a slot on the Node Screen (answer 227). The server takes it only from the open
 * screen of that node, still within reach, and the domain checks the owner, the slot and the length
 * of slot.name_length (rules 16 and 17). A text longer than the ceiling never decodes.
 */
public record SlotRenamed(BlockPos pos, int index, String name) implements CustomPacketPayload {
  private static final String ID = "slot_renamed";

  public static final Type<SlotRenamed> TYPE =
      new Type<>(ResourceLocation.fromNamespaceAndPath(RedweaveMod.MOD_ID, ID));

  public static final StreamCodec<RegistryFriendlyByteBuf, SlotRenamed> STREAM_CODEC =
      StreamCodec.composite(
          BlockPos.STREAM_CODEC,
          SlotRenamed::pos,
          ByteBufCodecs.VAR_INT,
          SlotRenamed::index,
          ByteBufCodecs.stringUtf8(RedweaveConfig.MAX_NAME),
          SlotRenamed::name,
          SlotRenamed::new);

  @Override
  public Type<SlotRenamed> type() {
    return TYPE;
  }

  /** On the server thread (the default of the registrar). */
  public static void receive(SlotRenamed renamed, Player player) {
    boolean screenOfTheNode =
        player.containerMenu instanceof NodeMenu menu
            && menu.pos().equals(renamed.pos())
            && menu.stillValid(player);
    Optional<RedweaveServer> server = RedweaveServer.running();
    Optional<NodeId> node = NodeSignals.nodeAt(player.level(), renamed.pos());
    if (!screenOfTheNode || server.isEmpty() || node.isEmpty()) {
      return;
    }
    server
        .get()
        .renameSlot()
        .rename(new PlayerId(player.getUUID()), node.get(), renamed.index(), renamed.name());
  }
}
