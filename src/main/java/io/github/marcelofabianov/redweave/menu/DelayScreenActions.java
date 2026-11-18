package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.block.part.DelayedPart;
import io.github.marcelofabianov.redweave.block.part.ModulePart;
import io.github.marcelofabianov.redweave.block.part.PartSpot;
import io.github.marcelofabianov.redweave.config.NeoForgeOutputDelayLimits;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.domain.signal.OutputDelayLimits;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.Optional;
import net.minecraft.world.entity.player.Player;

/**
 * What the `Delay` buttons of every module screen do on the server: one second shorter or longer,
 * through the owner of the plate; anyone else changes nothing (answers 830 and 850, rules 16 and
 * 17).
 */
final class DelayScreenActions {
  private static final OutputDelayLimits LIMITS = new NeoForgeOutputDelayLimits();

  private DelayScreenActions() {}

  static void step(Player player, PartSpot spot, int value) {
    Optional<ModulePart> found = ModulePart.at(player.level(), spot, ModulePart.class);
    Optional<RedweaveServer> server = RedweaveServer.running();
    if (found.isEmpty()
        || found.get().module().isEmpty()
        || server.isEmpty()
        || !(found.get() instanceof DelayedPart part)) {
      return;
    }
    server
        .get()
        .modules()
        .changeOutputDelay()
        .step(
            new PlayerId(player.getUUID()),
            found.get().module().get(),
            part.delayed().delay(),
            DelaySteps.seconds(value),
            LIMITS)
        .ifPresent(part::setDelay);
  }
}
