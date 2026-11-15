package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.block.part.ModulePart;
import io.github.marcelofabianov.redweave.block.part.PartSpot;
import io.github.marcelofabianov.redweave.block.part.PressurePlatePart;
import io.github.marcelofabianov.redweave.domain.module.SignalModule;
import io.github.marcelofabianov.redweave.domain.module.SignalPressurePlate;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.Optional;
import net.minecraft.world.entity.player.Player;

/**
 * What the buttons only the Signal Pressure Plate Screen has do on the server. Every action goes
 * through the owner of the plate: anyone else changes nothing (answers 378 and 621, rules 16 and
 * 17).
 */
final class PressurePlateScreenActions {
  private PressurePlateScreenActions() {}

  static void apply(Player player, PartSpot spot, PressurePlateAction action) {
    Optional<PressurePlatePart> found =
        ModulePart.at(player.level(), spot, PressurePlatePart.class);
    Optional<RedweaveServer> server = RedweaveServer.running();
    if (found.isEmpty() || found.get().module().isEmpty() || server.isEmpty()) {
      return;
    }
    PressurePlatePart part = found.get();
    PlayerId requester = new PlayerId(player.getUUID());
    if (action.kind() == PressurePlateAction.Kind.OUTPUT) {
      ModuleScreenActions.connect(requester, part, action.channel(), part::connectOutput);
      return;
    }
    changed(server.get(), requester, part, action).ifPresent(part::setPlate);
  }

  private static Optional<SignalPressurePlate> changed(
      RedweaveServer server,
      PlayerId requester,
      PressurePlatePart part,
      PressurePlateAction action) {
    SignalModule module = part.module().orElseThrow();
    SignalPressurePlate now = part.plate();
    if (action.kind() == PressurePlateAction.Kind.MODE) {
      return server
          .modules()
          .changePressurePlate()
          .mode(requester, module, now, action.plateMode());
    }
    return server
        .modules()
        .changePressurePlate()
        .strength(requester, module, now, now.strength().strength() + action.step());
  }
}
