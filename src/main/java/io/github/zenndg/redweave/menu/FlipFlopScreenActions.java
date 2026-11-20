package io.github.zenndg.redweave.menu;

import io.github.zenndg.redweave.block.part.FlipFlopPart;
import io.github.zenndg.redweave.block.part.ModulePart;
import io.github.zenndg.redweave.block.part.PartSpot;
import io.github.zenndg.redweave.domain.module.SignalFlipFlop;
import io.github.zenndg.redweave.domain.network.PlayerId;
import io.github.zenndg.redweave.server.RedweaveServer;
import java.util.Optional;
import net.minecraft.world.entity.player.Player;

/**
 * What the buttons of the Signal Flip-Flop Screen do on the server: the connections and the
 * strength; the delay goes through the shared `Delay` buttons. Every action goes through the owner
 * of the plate: anyone else changes nothing (answers 378, 880 and 884, rules 16 and 17).
 */
final class FlipFlopScreenActions {
  private FlipFlopScreenActions() {}

  static void apply(Player player, PartSpot spot, FlipFlopAction action) {
    Optional<FlipFlopPart> found = ModulePart.at(player.level(), spot, FlipFlopPart.class);
    Optional<RedweaveServer> server = RedweaveServer.running();
    if (found.isEmpty() || found.get().module().isEmpty() || server.isEmpty()) {
      return;
    }
    FlipFlopPart part = found.get();
    PlayerId requester = new PlayerId(player.getUUID());
    switch (action.kind()) {
      case INPUT ->
          ModuleScreenActions.connect(requester, part, action.channel(), part::connectInput);
      case OUTPUT ->
          ModuleScreenActions.connect(requester, part, action.channel(), part::connectOutput);
      case STRENGTH -> {
        SignalFlipFlop now = part.flipFlop();
        server
            .get()
            .modules()
            .changeFlipFlop()
            .strength(
                requester, part.module().get(), now, now.strength().strength() + action.step())
            .ifPresent(part::setFlipFlop);
      }
      case CHOOSE, DELAY -> {
        // The menu applies a choice, from the list it keeps, and a step of the shared delay.
      }
    }
  }
}
