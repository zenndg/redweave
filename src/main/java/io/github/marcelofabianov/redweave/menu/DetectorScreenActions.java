package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.block.WeatherDetectorBlockEntity;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

/**
 * What the mode and output buttons of the Weather Detector Screen do on the server. Every action
 * goes through the owner of the plate: anyone else changes nothing (answers 378, 414 and 470, rules
 * 16 and 17).
 */
final class DetectorScreenActions {
  private DetectorScreenActions() {}

  static void apply(Player player, BlockPos pos, DetectorAction action) {
    if (!(player.level().getBlockEntity(pos) instanceof WeatherDetectorBlockEntity block)
        || block.module().isEmpty()) {
      return;
    }
    PlayerId requester = new PlayerId(player.getUUID());
    switch (action.kind()) {
      case MODE ->
          RedweaveServer.running()
              .flatMap(server ->
                  server
                      .modules()
                      .changeDetector()
                      .mode(requester, block.module().get(), action.weatherMode()))
              .ifPresent(block::changeTo);
      case OUTPUT ->
          ModuleScreenActions.connect(requester, block, action.channel(), block::connectOutput);
      case CHOOSE -> {
        // A choice names a row of the list the menu keeps, so the menu applies it.
      }
    }
  }
}
