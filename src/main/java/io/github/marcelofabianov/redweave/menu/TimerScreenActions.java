package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.application.ChangeTimer;
import io.github.marcelofabianov.redweave.block.part.ModulePart;
import io.github.marcelofabianov.redweave.block.part.PartSpot;
import io.github.marcelofabianov.redweave.block.part.TimerPart;
import io.github.marcelofabianov.redweave.config.NeoForgeTimerLimits;
import io.github.marcelofabianov.redweave.domain.module.SignalModule;
import io.github.marcelofabianov.redweave.domain.module.SignalTimer;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.Optional;
import net.minecraft.world.entity.player.Player;

/**
 * What the buttons only the Signal Timer Screen has do on the server. Every action goes through the
 * owner of the plate: anyone else changes nothing (answers 378, 398 and 404, rules 16 and 17).
 */
final class TimerScreenActions {
  private TimerScreenActions() {}

  static void apply(Player player, PartSpot spot, TimerAction action) {
    Optional<TimerPart> block = blockAt(player, spot);
    Optional<RedweaveServer> server = RedweaveServer.running();
    if (block.isEmpty() || server.isEmpty() || block.get().module().isEmpty()) {
      return;
    }
    PlayerId requester = new PlayerId(player.getUUID());
    switch (action.kind()) {
      case TOGGLE, INTERVAL, PULSE ->
          change(server.get().modules().changeTimer(), requester, block.get(), action);
      case OUTPUT ->
          ModuleScreenActions.connect(
              requester, block.get(), action.channel(), block.get()::connectOutput);
      case PAUSE ->
          ModuleScreenActions.connect(
              requester, block.get(), action.channel(), block.get()::connectPause);
      case CHOOSE -> {
        // A choice names a row of the list the menu keeps, so the menu applies it.
      }
    }
  }

  private static void change(
      ChangeTimer change, PlayerId requester, TimerPart block, TimerAction action) {
    SignalModule module = block.module().get();
    Optional<SignalTimer> timer = block.timer();
    if (timer.isEmpty()) {
      return;
    }
    SignalTimer now = timer.get();
    Optional<SignalTimer> next =
        switch (action.kind()) {
          case INTERVAL ->
              change.interval(
                  requester,
                  module,
                  now,
                  now.cycle().intervalTicks() + action.ticks(),
                  new NeoForgeTimerLimits());
          case PULSE ->
              change.pulse(requester, module, now, now.cycle().pulseTicks() + action.ticks());
          default -> change.toggle(requester, module, now);
        };
    next.ifPresent(block::setTimer);
  }

  private static Optional<TimerPart> blockAt(Player player, PartSpot spot) {
    return ModulePart.at(player.level(), spot, TimerPart.class);
  }
}
