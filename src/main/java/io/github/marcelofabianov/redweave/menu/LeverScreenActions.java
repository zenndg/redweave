package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.block.part.LeverPart;
import io.github.marcelofabianov.redweave.block.part.ModulePart;
import io.github.marcelofabianov.redweave.block.part.PartSpot;
import io.github.marcelofabianov.redweave.domain.module.SignalLever;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.Optional;
import net.minecraft.world.entity.player.Player;

/**
 * What the buttons only the Signal Lever Screen has do on the server. Every action goes through the
 * owner of the plate: anyone else changes nothing (answers 378, 577 and 582, rules 16 and 17).
 */
final class LeverScreenActions {
  private LeverScreenActions() {}

  static void apply(Player player, PartSpot spot, LeverAction action) {
    Optional<LeverPart> found = ModulePart.at(player.level(), spot, LeverPart.class);
    Optional<RedweaveServer> server = RedweaveServer.running();
    if (found.isEmpty() || found.get().module().isEmpty() || server.isEmpty()) {
      return;
    }
    LeverPart part = found.get();
    PlayerId requester = new PlayerId(player.getUUID());
    if (action.kind() == LeverAction.Kind.OUTPUT) {
      ModuleScreenActions.connect(requester, part, action.channel(), part::connectOutput);
      return;
    }
    SignalLever now = part.lever();
    server
        .get()
        .modules()
        .changeLever()
        .strength(requester, part.module().get(), now, now.strength().strength() + action.step())
        .ifPresent(part::setLever);
  }
}
