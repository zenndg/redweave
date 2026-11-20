package io.github.zenndg.redweave.block.part;

import io.github.zenndg.redweave.RedweaveMod;
import io.github.zenndg.redweave.server.RedweaveServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/** The owner of a Signal Keypad hears, when joining, of the misses while away (answer 666). */
@EventBusSubscriber(modid = RedweaveMod.MOD_ID)
public final class KeypadJoinEvents {
  private KeypadJoinEvents() {}

  @SubscribeEvent
  public static void onJoin(PlayerEvent.PlayerLoggedInEvent event) {
    if (event.getEntity() instanceof ServerPlayer owner) {
      RedweaveServer.running()
          .ifPresent(server -> KeypadWarnings.joined(owner.server, server.modules(), owner));
    }
  }
}
