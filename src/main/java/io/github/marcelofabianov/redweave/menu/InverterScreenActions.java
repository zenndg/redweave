package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.application.ChangeInverter;
import io.github.marcelofabianov.redweave.block.part.InverterPart;
import io.github.marcelofabianov.redweave.block.part.ModulePart;
import io.github.marcelofabianov.redweave.block.part.PartSpot;
import io.github.marcelofabianov.redweave.config.NeoForgeOutputDelayLimits;
import io.github.marcelofabianov.redweave.domain.module.OutputDelayLimits;
import io.github.marcelofabianov.redweave.domain.module.SignalInverter;
import io.github.marcelofabianov.redweave.domain.module.SignalModule;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.Optional;
import net.minecraft.world.entity.player.Player;

/**
 * What the buttons of the Signal Inverter Screen do on the server: the connections, the strength
 * and the delay. Every action goes through the owner of the plate: anyone else changes nothing
 * (answers 378, 414, 451, 830 and 834, rules 16 and 17).
 */
final class InverterScreenActions {
  private static final OutputDelayLimits DELAY_LIMITS = new NeoForgeOutputDelayLimits();

  private InverterScreenActions() {}

  static void apply(Player player, PartSpot spot, InverterAction action) {
    Optional<InverterPart> found = ModulePart.at(player.level(), spot, InverterPart.class);
    if (found.isEmpty() || found.get().module().isEmpty()) {
      return;
    }
    InverterPart block = found.get();
    PlayerId requester = new PlayerId(player.getUUID());
    switch (action.kind()) {
      case INPUT ->
          ModuleScreenActions.connect(requester, block, action.channel(), block::connectInput);
      case OUTPUT ->
          ModuleScreenActions.connect(requester, block, action.channel(), block::connectOutput);
      case STRENGTH, DELAY -> change(requester, block, action);
      case CHOOSE -> {
        // A choice names a row of the list the menu keeps, so the menu applies it.
      }
    }
  }

  private static void change(PlayerId requester, InverterPart block, InverterAction action) {
    Optional<RedweaveServer> server = RedweaveServer.running();
    if (server.isEmpty()) {
      return;
    }
    ChangeInverter change = server.get().modules().changeInverter();
    SignalModule module = block.module().get();
    SignalInverter now = block.inverter();
    Optional<SignalInverter> next =
        action.kind() == InverterAction.Kind.STRENGTH
            ? change.strength(
                requester, module, now, now.settings().strength().strength() + action.step())
            : change.delay(requester, module, now, action.step(), DELAY_LIMITS);
    next.ifPresent(block::setSettings);
  }
}
