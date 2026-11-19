package io.github.zenndg.redweave.menu;

import io.github.zenndg.redweave.block.part.EmitterPart;
import io.github.zenndg.redweave.block.part.ModulePart;
import io.github.zenndg.redweave.block.part.PartSpot;
import io.github.zenndg.redweave.domain.module.SignalModule;
import io.github.zenndg.redweave.domain.network.PlayerId;
import io.github.zenndg.redweave.server.RedweaveServer;
import java.util.Optional;
import net.minecraft.world.entity.player.Player;

/**
 * What every Signal Emitter screen does first on the server: find the emitter with its module and
 * the running server, and move its output, through the owner of the plate (answers 378, 577 and
 * 629, rules 16 and 17).
 */
final class EmitterScreenActions {
  private EmitterScreenActions() {}

  /** The emitter at the spot, with its module and the server, ready for an action. */
  static <P extends EmitterPart> Optional<Target<P>> target(
      Player player, PartSpot spot, Class<P> type) {
    Optional<P> found = ModulePart.at(player.level(), spot, type);
    Optional<RedweaveServer> server = RedweaveServer.running();
    if (found.isEmpty() || found.get().module().isEmpty() || server.isEmpty()) {
      return Optional.empty();
    }
    P part = found.get();
    return Optional.of(
        new Target<>(part, part.module().get(), new PlayerId(player.getUUID()), server.get()));
  }

  /** An emitter about to change, with who asks for it. */
  record Target<P extends EmitterPart>(
      P part, SignalModule module, PlayerId requester, RedweaveServer server) {

    /** Moves the output to the edges or to a channel, if the owner may (answers 548 and 549). */
    void connect(Optional<Integer> channel) {
      ModuleScreenActions.connect(requester, part, channel, part::connectOutput);
    }
  }
}
