package io.github.zenndg.redweave.menu;

import io.github.zenndg.redweave.application.ChangeRandomizer;
import io.github.zenndg.redweave.block.part.ModulePart;
import io.github.zenndg.redweave.block.part.PartSpot;
import io.github.zenndg.redweave.block.part.RandomizerPart;
import io.github.zenndg.redweave.config.NeoForgeRandomizerLimits;
import io.github.zenndg.redweave.domain.module.SignalModule;
import io.github.zenndg.redweave.domain.module.SignalRandomizer;
import io.github.zenndg.redweave.domain.network.PlayerId;
import io.github.zenndg.redweave.server.RedweaveServer;
import java.util.Optional;
import net.minecraft.world.entity.player.Player;

/**
 * What the buttons only the Signal Randomizer Screen has do on the server. Every action goes
 * through the owner of the plate: anyone else changes nothing (answers 378, 477, 480 and 487, rules
 * 16 and 17).
 */
final class RandomizerScreenActions {
  private RandomizerScreenActions() {}

  static void apply(Player player, PartSpot spot, RandomizerAction action) {
    Optional<RandomizerPart> found = ModulePart.at(player.level(), spot, RandomizerPart.class);
    if (found.isEmpty() || found.get().module().isEmpty() || found.get().randomizer().isEmpty()) {
      return;
    }
    RandomizerPart block = found.get();
    Optional<RedweaveServer> server = RedweaveServer.running();
    if (server.isEmpty()) {
      return;
    }
    PlayerId requester = new PlayerId(player.getUUID());
    if (action.kind() == RandomizerAction.Kind.OUTPUT) {
      ModuleScreenActions.connect(requester, block, action.channel(), block::connectOutput);
      return;
    }
    change(server.get().modules().changeRandomizer(), requester, block, action)
        .ifPresent(block::setRandomizer);
  }

  private static Optional<SignalRandomizer> change(
      ChangeRandomizer change, PlayerId requester, RandomizerPart block, RandomizerAction action) {
    SignalModule module = block.module().get();
    SignalRandomizer now = block.randomizer().get();
    return switch (action.kind()) {
      case TOGGLE -> change.toggle(requester, module, now, block.draws());
      case MODE -> change.mode(requester, module, now, action.randomizerMode(), block.draws());
      case INTERVAL ->
          change.interval(
              requester,
              module,
              now,
              now.interval().ticks() + action.ticks(),
              new NeoForgeRandomizerLimits());
      default -> Optional.empty();
    };
  }
}
