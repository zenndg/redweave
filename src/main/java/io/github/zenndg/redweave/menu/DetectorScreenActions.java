package io.github.zenndg.redweave.menu;

import io.github.zenndg.redweave.block.part.DetectorPart;
import io.github.zenndg.redweave.block.part.ModulePart;
import io.github.zenndg.redweave.block.part.PartSpot;
import io.github.zenndg.redweave.domain.network.PlayerId;
import io.github.zenndg.redweave.server.RedweaveServer;
import java.util.Optional;
import net.minecraft.world.entity.player.Player;

/**
 * What the mode and output buttons of the Weather Detector Screen do on the server. Every action
 * goes through the owner of the plate: anyone else changes nothing (answers 378, 414 and 470, rules
 * 16 and 17).
 */
final class DetectorScreenActions {
  private DetectorScreenActions() {}

  static void apply(Player player, PartSpot spot, DetectorAction action) {
    Optional<DetectorPart> found = ModulePart.at(player.level(), spot, DetectorPart.class);
    if (found.isEmpty() || found.get().module().isEmpty()) {
      return;
    }
    DetectorPart block = found.get();
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
