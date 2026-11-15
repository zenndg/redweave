package io.github.marcelofabianov.redweave.network;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.part.ModulePart;
import io.github.marcelofabianov.redweave.block.part.PartSpot;
import io.github.marcelofabianov.redweave.config.NeoForgeModuleLimits;
import io.github.marcelofabianov.redweave.config.RedweaveConfig;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.menu.ModuleMenu;
import java.util.Optional;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

/**
 * The owner renamed the Signal Module on its screen (answers 389 and 404). The server takes it only
 * from the open screen of that module, still within reach, and the domain checks the owner and the
 * length of module.name_length (rules 16 and 17). A text longer than the ceiling never decodes.
 */
public record ModuleRenamed(PartSpot spot, String name) implements CustomPacketPayload {
  private static final String ID = "module_renamed";

  public static final Type<ModuleRenamed> TYPE =
      new Type<>(ResourceLocation.fromNamespaceAndPath(RedweaveMod.MOD_ID, ID));

  public static final StreamCodec<RegistryFriendlyByteBuf, ModuleRenamed> STREAM_CODEC =
      StreamCodec.composite(
          PartSpot.STREAM_CODEC,
          ModuleRenamed::spot,
          ByteBufCodecs.stringUtf8(RedweaveConfig.MAX_NAME),
          ModuleRenamed::name,
          ModuleRenamed::new);

  @Override
  public Type<ModuleRenamed> type() {
    return TYPE;
  }

  /** On the server thread (the default of the registrar). */
  public static void receive(ModuleRenamed renamed, Player player) {
    boolean screenOfTheModule =
        player.containerMenu instanceof ModuleMenu menu
            && menu.spot().equals(renamed.spot())
            && menu.stillValid(player);
    Optional<ModulePart> found = ModulePart.at(player.level(), renamed.spot(), ModulePart.class);
    if (!screenOfTheModule || found.isEmpty() || found.get().module().isEmpty()) {
      return;
    }
    ModulePart block = found.get();
    PlayerId requester = new PlayerId(player.getUUID());
    if (block.module().get().rename(requester, renamed.name(), new NeoForgeModuleLimits())
        == Outcome.ACCEPTED) {
      block.changed();
    }
  }
}
