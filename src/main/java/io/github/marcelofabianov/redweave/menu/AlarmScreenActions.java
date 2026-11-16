package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.application.ChangeAlarm;
import io.github.marcelofabianov.redweave.block.part.AlarmPart;
import io.github.marcelofabianov.redweave.block.part.ModulePart;
import io.github.marcelofabianov.redweave.block.part.PartSpot;
import io.github.marcelofabianov.redweave.config.NeoForgeAlarmLimits;
import io.github.marcelofabianov.redweave.domain.module.AlarmSettings;
import io.github.marcelofabianov.redweave.domain.module.SignalAlarm;
import io.github.marcelofabianov.redweave.domain.module.SignalModule;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.Optional;
import net.minecraft.world.entity.player.Player;

/**
 * What the buttons of the Signal Alarm Screen do on the server. Every action goes through the owner
 * of the plate: anyone else changes nothing, and the numbers stay within the limits of the config
 * (answers 378, 645, 646 and 648, rules 16 and 17).
 */
final class AlarmScreenActions {
  private AlarmScreenActions() {}

  static void apply(Player player, PartSpot spot, AlarmAction action) {
    Optional<AlarmPart> found = ModulePart.at(player.level(), spot, AlarmPart.class);
    Optional<RedweaveServer> server = RedweaveServer.running();
    if (found.isEmpty() || found.get().module().isEmpty() || server.isEmpty()) {
      return;
    }
    AlarmPart part = found.get();
    PlayerId requester = new PlayerId(player.getUUID());
    switch (action.kind()) {
      case INPUT ->
          ModuleScreenActions.connect(requester, part, action.channel(), part::connectInput);
      case CHOOSE -> {
        // A choice names a row of the list the menu keeps, so the menu applies it.
      }
      default ->
          change(server.get().modules().changeAlarm(), requester, part, action)
              .ifPresent(part::setAlarm);
    }
  }

  private static Optional<SignalAlarm> change(
      ChangeAlarm change, PlayerId requester, AlarmPart part, AlarmAction action) {
    SignalModule module = part.module().get();
    SignalAlarm now = part.alarm();
    AlarmSettings settings = now.settings();
    NeoForgeAlarmLimits limits = new NeoForgeAlarmLimits();
    return switch (action.kind()) {
      case LISTENERS -> change.listeners(requester, module, now);
      case REPEATS ->
          change.repeats(requester, module, now, settings.repeats() + action.step(), limits);
      case INTERVAL ->
          change.interval(
              requester, module, now, settings.intervalTicks() + action.ticks(), limits);
      case DURATION ->
          change.duration(
              requester, module, now, settings.durationTicks() + action.ticks(), limits);
      default -> Optional.empty();
    };
  }
}
