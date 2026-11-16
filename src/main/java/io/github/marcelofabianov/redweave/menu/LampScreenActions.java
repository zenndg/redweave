package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.application.ChangeLamp;
import io.github.marcelofabianov.redweave.block.part.LampPart;
import io.github.marcelofabianov.redweave.block.part.ModulePart;
import io.github.marcelofabianov.redweave.block.part.PartSpot;
import io.github.marcelofabianov.redweave.domain.module.LampMode;
import io.github.marcelofabianov.redweave.domain.module.SignalLamp;
import io.github.marcelofabianov.redweave.domain.module.SignalModule;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.Optional;
import net.minecraft.world.entity.player.Player;

/**
 * What the buttons of the Signal Lamp Screen do on the server. Every action goes through the owner
 * of the lamp: anyone else changes nothing (answers 378, 651, 656 and 658, rules 16 and 17).
 */
final class LampScreenActions {
  private LampScreenActions() {}

  static void apply(Player player, PartSpot spot, LampAction action) {
    Optional<LampPart> found = ModulePart.at(player.level(), spot, LampPart.class);
    Optional<RedweaveServer> server = RedweaveServer.running();
    if (found.isEmpty() || found.get().module().isEmpty() || server.isEmpty()) {
      return;
    }
    LampPart part = found.get();
    PlayerId requester = new PlayerId(player.getUUID());
    switch (action.kind()) {
      case INTENSITY ->
          ModuleScreenActions.connect(requester, part, action.channel(), part::connectIntensity);
      case CONTROL ->
          ModuleScreenActions.connect(requester, part, action.channel(), part::connectControl);
      case CHOOSE -> {
        // A choice names a row of the list the menu keeps, so the menu applies it.
      }
      default ->
          change(server.get().modules().changeLamp(), requester, part, action)
              .ifPresent(part::setLamp);
    }
  }

  private static Optional<SignalLamp> change(
      ChangeLamp change, PlayerId requester, LampPart part, LampAction action) {
    SignalModule module = part.module().get();
    SignalLamp now = part.lamp();
    return switch (action.kind()) {
      case STRENGTH ->
          change.strength(requester, module, now, now.strength().strength() + action.step());
      case MODE -> change.mode(requester, module, now, LampMode.values()[action.value()]);
      default -> Optional.empty();
    };
  }
}
