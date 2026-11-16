package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.block.part.LeverPart;
import io.github.marcelofabianov.redweave.block.part.PartSpot;
import io.github.marcelofabianov.redweave.domain.module.SignalLever;
import net.minecraft.world.entity.player.Player;

/**
 * What the buttons only the Signal Lever Screen has do on the server. Every action goes through the
 * owner of the plate: anyone else changes nothing (answers 378, 577 and 582, rules 16 and 17).
 */
final class LeverScreenActions {
  private LeverScreenActions() {}

  static void apply(Player player, PartSpot spot, LeverAction action) {
    EmitterScreenActions.target(player, spot, LeverPart.class)
        .ifPresent(target -> apply(target, action));
  }

  private static void apply(EmitterScreenActions.Target<LeverPart> target, LeverAction action) {
    if (action.kind() == LeverAction.Kind.OUTPUT) {
      target.connect(action.channel());
      return;
    }
    LeverPart part = target.part();
    SignalLever now = part.lever();
    target
        .server()
        .modules()
        .changeLever()
        .strength(
            target.requester(), target.module(), now, now.strength().strength() + action.step())
        .ifPresent(part::setLever);
  }
}
