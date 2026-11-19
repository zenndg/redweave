package io.github.zenndg.redweave.client;

import io.github.zenndg.redweave.RedweaveMod;
import io.github.zenndg.redweave.item.RemoteState;
import io.github.zenndg.redweave.registry.RedweaveDataComponents;
import io.github.zenndg.redweave.registry.RedweaveItems;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * The look of the Signal Remote: no link, off, on or `Link lost`, as the ordinal of RemoteLook,
 * which the item model reads in its overrides (answer 362).
 */
@EventBusSubscriber(
    modid = RedweaveMod.MOD_ID,
    bus = EventBusSubscriber.Bus.MOD,
    value = Dist.CLIENT)
public final class RemoteLookProperty {
  private static final String LOOK_KEY = "look";
  private static final ResourceLocation LOOK =
      ResourceLocation.fromNamespaceAndPath(RedweaveMod.MOD_ID, LOOK_KEY);
  private static final long NO_TIME = 0;

  private RemoteLookProperty() {}

  @SubscribeEvent
  public static void registerProperties(FMLClientSetupEvent event) {
    event.enqueueWork(() ->
        ItemProperties.register(
            RedweaveItems.SIGNAL_REMOTE.get(), LOOK, RemoteLookProperty::lookOf));
  }

  private static float lookOf(ItemStack stack, ClientLevel level, LivingEntity entity, int seed) {
    RemoteState state =
        stack.getOrDefault(RedweaveDataComponents.SIGNAL_REMOTE.get(), RemoteState.NEW);
    return state.look(timeOf(level, entity)).ordinal();
  }

  /** The game tick, for the pulse; with no world, as in a menu, the pulse shows off. */
  private static long timeOf(ClientLevel level, LivingEntity entity) {
    if (entity != null) {
      return entity.level().getGameTime();
    }
    return level != null ? level.getGameTime() : NO_TIME;
  }
}
