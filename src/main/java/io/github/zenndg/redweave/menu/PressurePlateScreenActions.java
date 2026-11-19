package io.github.zenndg.redweave.menu;

import io.github.zenndg.redweave.block.part.PartSpot;
import io.github.zenndg.redweave.block.part.PressurePlatePart;
import io.github.zenndg.redweave.domain.module.SignalModule;
import io.github.zenndg.redweave.domain.module.SignalPressurePlate;
import io.github.zenndg.redweave.domain.network.PlayerId;
import io.github.zenndg.redweave.server.RedweaveServer;
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
    EmitterScreenActions.target(player, spot, PressurePlatePart.class)
        .ifPresent(target -> apply(target, action));
  }

  private static void apply(
      EmitterScreenActions.Target<PressurePlatePart> target, PressurePlateAction action) {
    if (action.kind() == PressurePlateAction.Kind.OUTPUT) {
      target.connect(action.channel());
      return;
    }
    PressurePlatePart part = target.part();
    changed(target.server(), target.requester(), part, action).ifPresent(part::setPlate);
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
