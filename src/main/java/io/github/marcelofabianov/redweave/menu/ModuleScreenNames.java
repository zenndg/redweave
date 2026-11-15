package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.part.ModulePart;
import io.github.marcelofabianov.redweave.block.part.PartSpot;
import io.github.marcelofabianov.redweave.domain.module.SignalModule;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.Optional;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * The names a Signal Module Screen shows, built on the server: the name of the module, or the first
 * 8 characters of its ID, and the name, color and channels of its network (answers 385, 404 and
 * 427). Sent when the screen opens and again when they change.
 */
public record ModuleScreenNames(
    int menu, String module, String network, int color, NodeScreenNames.Labels labels)
    implements CustomPacketPayload {
  private static final String ID = "module_screen_names";
  private static final String NONE = "";

  public static final Type<ModuleScreenNames> TYPE =
      new Type<>(ResourceLocation.fromNamespaceAndPath(RedweaveMod.MOD_ID, ID));

  public static final StreamCodec<RegistryFriendlyByteBuf, ModuleScreenNames> STREAM_CODEC =
      StreamCodec.composite(
          ByteBufCodecs.VAR_INT,
          ModuleScreenNames::menu,
          ByteBufCodecs.STRING_UTF8,
          ModuleScreenNames::module,
          ByteBufCodecs.STRING_UTF8,
          ModuleScreenNames::network,
          ByteBufCodecs.VAR_INT,
          ModuleScreenNames::color,
          NodeScreenNames.Labels.STREAM_CODEC,
          ModuleScreenNames::labels,
          ModuleScreenNames::new);

  @Override
  public Type<ModuleScreenNames> type() {
    return TYPE;
  }

  static ModuleScreenNames read(ServerPlayer player, PartSpot spot, int menu) {
    Optional<SignalModule> module =
        ModulePart.at(player.level(), spot, ModulePart.class).flatMap(ModulePart::module);
    String name = module.map(SignalModule::displayName).orElse(NONE);
    Optional<Network> network =
        module
            .flatMap(SignalModule::network)
            .flatMap(id -> RedweaveServer.running().flatMap(server -> server.network(id)));
    return network
        .map(found ->
            new ModuleScreenNames(
                menu, name, found.name(), found.color().ordinal(), NodeScreenNames.labelsOf(found)))
        .orElse(
            new ModuleScreenNames(
                menu, name, NONE, NodeScreenNames.NO_COLOR, NodeScreenNames.Labels.NONE));
  }

  static void send(ServerPlayer player, ModuleScreenNames names) {
    PacketDistributor.sendToPlayer(player, names);
  }

  /** On the client: only the open Signal Module Screen of the same menu takes the names. */
  public static void receive(ModuleScreenNames names, Player player) {
    if (player.containerMenu instanceof ModuleMenu menu
        && player.containerMenu.containerId == names.menu()) {
      menu.sync().acceptNames(names);
    }
  }
}
