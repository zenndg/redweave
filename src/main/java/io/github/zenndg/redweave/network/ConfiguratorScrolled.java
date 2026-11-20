package io.github.zenndg.redweave.network;

import io.github.zenndg.redweave.RedweaveMod;
import io.github.zenndg.redweave.item.ConfiguratorItem;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

/**
 * Shift + scroll with the Configurator: the client only says which way the wheel turned, and the
 * server checks the item in the hand and switches the mode (rules 16 and 17, answer 143).
 */
public record ConfiguratorScrolled(boolean down) implements CustomPacketPayload {
  private static final String ID = "configurator_scrolled";
  private static final double DOWN = -1;
  private static final double UP = 1;

  public static final Type<ConfiguratorScrolled> TYPE =
      new Type<>(ResourceLocation.fromNamespaceAndPath(RedweaveMod.MOD_ID, ID));

  public static final StreamCodec<RegistryFriendlyByteBuf, ConfiguratorScrolled> STREAM_CODEC =
      StreamCodec.composite(
          ByteBufCodecs.BOOL, ConfiguratorScrolled::down, ConfiguratorScrolled::new);

  @Override
  public Type<ConfiguratorScrolled> type() {
    return TYPE;
  }

  /** On the server thread (the default of the registrar): only a Configurator in the hand. */
  static void receive(ConfiguratorScrolled scrolled, Player player) {
    ConfiguratorItem.switchMode(player, scrolled.down() ? DOWN : UP);
  }
}
