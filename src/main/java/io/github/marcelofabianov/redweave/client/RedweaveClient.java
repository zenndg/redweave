package io.github.marcelofabianov.redweave.client;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.NodeLook;
import io.github.marcelofabianov.redweave.block.SignalNodeBlockEntity;
import io.github.marcelofabianov.redweave.client.screen.ControllerScreen;
import io.github.marcelofabianov.redweave.client.screen.NodeScreen;
import io.github.marcelofabianov.redweave.client.screen.RemoteLinkScreen;
import io.github.marcelofabianov.redweave.client.screen.TerminalScreen;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.registry.RedweaveBlocks;
import io.github.marcelofabianov.redweave.registry.RedweaveMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/** What only the client registers: the screens and the colors of the blocks (rule 19). */
@EventBusSubscriber(
    modid = RedweaveMod.MOD_ID,
    bus = EventBusSubscriber.Bus.MOD,
    value = Dist.CLIENT)
public final class RedweaveClient {
  private RedweaveClient() {}

  @SubscribeEvent
  public static void registerScreens(RegisterMenuScreensEvent event) {
    event.register(RedweaveMenus.NODE.get(), NodeScreen::new);
    event.register(RedweaveMenus.CONTROLLER.get(), ControllerScreen::new);
    event.register(RedweaveMenus.TERMINAL.get(), TerminalScreen::new);
    event.register(RedweaveMenus.REMOTE_LINK.get(), RemoteLinkScreen::new);
  }

  @SubscribeEvent
  public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
    event.register(RedweaveClient::cornerColor, RedweaveBlocks.SIGNAL_NODE.get());
  }

  /** The tint index of a corner of the node is its slot, from 0 to 3 (answer 87). */
  private static int cornerColor(
      BlockState state, BlockAndTintGetter level, BlockPos pos, int tintIndex) {
    if (level == null
        || pos == null
        || !(level.getBlockEntity(pos) instanceof SignalNodeBlockEntity node)) {
      return ChannelColors.FREE;
    }
    int channel = node.corner(tintIndex);
    return channel == NodeLook.FREE
        ? ChannelColors.FREE
        : ChannelColors.argb(ChannelColor.values()[channel]);
  }
}
