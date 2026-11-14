package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.application.ChangeRandomizer;
import io.github.marcelofabianov.redweave.block.SignalRandomizerBlockEntity;
import io.github.marcelofabianov.redweave.config.NeoForgeRandomizerLimits;
import io.github.marcelofabianov.redweave.domain.module.SignalModule;
import io.github.marcelofabianov.redweave.domain.module.SignalRandomizer;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

/**
 * What the buttons only the Signal Randomizer Screen has do on the server. Every action goes
 * through the owner of the plate: anyone else changes nothing (answers 378, 477, 480 and 487, rules
 * 16 and 17).
 */
final class RandomizerScreenActions {
  private RandomizerScreenActions() {}

  static void apply(Player player, BlockPos pos, RandomizerAction action) {
    if (!(player.level().getBlockEntity(pos) instanceof SignalRandomizerBlockEntity block)
        || block.module().isEmpty()
        || block.randomizer().isEmpty()) {
      return;
    }
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
      ChangeRandomizer change,
      PlayerId requester,
      SignalRandomizerBlockEntity block,
      RandomizerAction action) {
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
