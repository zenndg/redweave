package io.github.zenndg.redweave.menu;

import io.github.zenndg.redweave.application.ChangeLock;
import io.github.zenndg.redweave.block.part.LockPart;
import io.github.zenndg.redweave.block.part.ModulePart;
import io.github.zenndg.redweave.block.part.PartSpot;
import io.github.zenndg.redweave.config.NeoForgeLockLimits;
import io.github.zenndg.redweave.domain.module.SignalLock;
import io.github.zenndg.redweave.domain.module.SignalModule;
import io.github.zenndg.redweave.domain.network.PlayerId;
import io.github.zenndg.redweave.server.RedweaveServer;
import java.util.Optional;
import net.minecraft.world.entity.player.Player;

/**
 * What the buttons of the Signal Lock Screen do on the server. Every action goes through the owner
 * of the plate: anyone else changes nothing, and the pulse stays within the limits of the config
 * (answers 378, 636, 637 and 640, rules 16 and 17).
 */
final class LockScreenActions {
  private LockScreenActions() {}

  static void apply(Player player, PartSpot spot, LockAction action) {
    Optional<LockPart> found = ModulePart.at(player.level(), spot, LockPart.class);
    Optional<RedweaveServer> server = RedweaveServer.running();
    if (found.isEmpty() || found.get().module().isEmpty() || server.isEmpty()) {
      return;
    }
    LockPart part = found.get();
    PlayerId requester = new PlayerId(player.getUUID());
    switch (action.kind()) {
      case INPUT ->
          ModuleScreenActions.connect(requester, part, action.channel(), part::connectInput);
      case OUTPUT ->
          ModuleScreenActions.connect(requester, part, action.channel(), part::connectOutput);
      case CHOOSE -> {
        // A choice names a row of the list the menu keeps, so the menu applies it.
      }
      default ->
          change(server.get().modules().changeLock(), requester, part, action)
              .ifPresent(part::setLock);
    }
  }

  private static Optional<SignalLock> change(
      ChangeLock change, PlayerId requester, LockPart part, LockAction action) {
    SignalModule module = part.module().get();
    SignalLock now = part.lock();
    return switch (action.kind()) {
      case KEY -> change.key(requester, module, now, now.key().strength() + action.step());
      case STRENGTH ->
          change.strength(requester, module, now, now.strength().strength() + action.step());
      case PULSE ->
          change.pulse(
              requester,
              module,
              now,
              now.pulse().ticks() + action.ticks(),
              new NeoForgeLockLimits());
      default -> Optional.empty();
    };
  }
}
