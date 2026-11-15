package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.application.ChangeButton;
import io.github.marcelofabianov.redweave.block.part.ButtonPart;
import io.github.marcelofabianov.redweave.block.part.ModulePart;
import io.github.marcelofabianov.redweave.block.part.PartSpot;
import io.github.marcelofabianov.redweave.config.NeoForgeButtonLimits;
import io.github.marcelofabianov.redweave.domain.module.SignalButton;
import io.github.marcelofabianov.redweave.domain.module.SignalModule;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.Optional;
import net.minecraft.world.entity.player.Player;

/**
 * What the buttons only the Signal Button Screen has do on the server. Every action goes through
 * the owner of the plate: anyone else changes nothing, and the on time stays within the limits of
 * the config (answers 378, 588 and 592, rules 16 and 17).
 */
final class ButtonScreenActions {
  private ButtonScreenActions() {}

  static void apply(Player player, PartSpot spot, ButtonAction action) {
    Optional<ButtonPart> found = ModulePart.at(player.level(), spot, ButtonPart.class);
    Optional<RedweaveServer> server = RedweaveServer.running();
    if (found.isEmpty() || found.get().module().isEmpty() || server.isEmpty()) {
      return;
    }
    ButtonPart part = found.get();
    PlayerId requester = new PlayerId(player.getUUID());
    if (action.kind() == ButtonAction.Kind.OUTPUT) {
      ModuleScreenActions.connect(requester, part, action.channel(), part::connectOutput);
      return;
    }
    change(server.get().modules().changeButton(), requester, part, action)
        .ifPresent(part::setButton);
  }

  private static Optional<SignalButton> change(
      ChangeButton change, PlayerId requester, ButtonPart part, ButtonAction action) {
    SignalModule module = part.module().get();
    SignalButton now = part.button();
    return switch (action.kind()) {
      case STRENGTH ->
          change.strength(requester, module, now, now.strength().strength() + action.step());
      case TIME ->
          change.time(
              requester,
              module,
              now,
              now.time().ticks() + action.ticks(),
              new NeoForgeButtonLimits());
      default -> Optional.empty();
    };
  }
}
