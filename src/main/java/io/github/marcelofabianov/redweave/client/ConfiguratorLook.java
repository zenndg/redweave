package io.github.marcelofabianov.redweave.client;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.item.ConfiguratorItem;
import io.github.marcelofabianov.redweave.registry.RedweaveDataComponents;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

/**
 * The display of the Configurator: its 2×2 pixels take the color of the mode, and the pixel above
 * on the right lights up when there is a copy (answer 271). The name of the mode stays in the
 * action bar and the tooltip, so the display does not stand alone on the color.
 */
@EventBusSubscriber(
    modid = RedweaveMod.MOD_ID,
    bus = EventBusSubscriber.Bus.MOD,
    value = Dist.CLIENT)
public final class ConfiguratorLook {
  private static final String COPY_KEY = "copy";

  /** The item property that picks the model with the copy pixel; its key is in the item model. */
  private static final ResourceLocation COPY =
      ResourceLocation.fromNamespaceAndPath(RedweaveMod.MOD_ID, COPY_KEY);

  private static final int DISPLAY_LAYER = 1;
  private static final int COPY_LAYER = 2;
  private static final int OPAQUE = 0xFF000000;
  private static final int UNTINTED = -1;
  private static final int COPY_LIGHT = 0xFFE8EAED;
  private static final float WITH_COPY = 1;
  private static final float WITHOUT_COPY = 0;

  private ConfiguratorLook() {}

  @SubscribeEvent
  public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
    event.register(ConfiguratorLook::colorOf, RedweaveItems.CONFIGURATOR.get());
  }

  @SubscribeEvent
  public static void registerProperties(FMLClientSetupEvent event) {
    event.enqueueWork(() ->
        ItemProperties.register(
            RedweaveItems.CONFIGURATOR.get(),
            COPY,
            (stack, level, entity, seed) -> hasCopy(stack) ? WITH_COPY : WITHOUT_COPY));
  }

  private static int colorOf(ItemStack stack, int layer) {
    return switch (layer) {
      case DISPLAY_LAYER -> OPAQUE | ConfiguratorItem.modeOf(stack).color();
      case COPY_LAYER -> COPY_LIGHT;
      default -> UNTINTED;
    };
  }

  private static boolean hasCopy(ItemStack stack) {
    return stack.has(RedweaveDataComponents.NODE_SLOTS.get());
  }
}
