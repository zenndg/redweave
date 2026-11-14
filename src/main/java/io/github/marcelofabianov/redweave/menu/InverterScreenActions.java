package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.block.SignalInverterBlockEntity;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

/**
 * What the connection buttons of the Signal Inverter Screen do on the server. Every action goes
 * through the owner of the plate: anyone else changes nothing (answers 378, 414 and 451, rules 16
 * and 17).
 */
final class InverterScreenActions {
  private InverterScreenActions() {}

  static void apply(Player player, BlockPos pos, InverterAction action) {
    if (!(player.level().getBlockEntity(pos) instanceof SignalInverterBlockEntity block)
        || block.module().isEmpty()) {
      return;
    }
    PlayerId requester = new PlayerId(player.getUUID());
    switch (action.kind()) {
      case INPUT ->
          ModuleScreenActions.connect(requester, block, action.channel(), block::connectInput);
      case OUTPUT ->
          ModuleScreenActions.connect(requester, block, action.channel(), block::connectOutput);
      case CHOOSE -> {
        // A choice names a row of the list the menu keeps, so the menu applies it.
      }
    }
  }
}
