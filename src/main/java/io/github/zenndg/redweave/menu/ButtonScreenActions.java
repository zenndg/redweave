package io.github.zenndg.redweave.menu;

import io.github.zenndg.redweave.application.ChangeButton;
import io.github.zenndg.redweave.block.part.ButtonPart;
import io.github.zenndg.redweave.block.part.PartSpot;
import io.github.zenndg.redweave.config.NeoForgeButtonLimits;
import io.github.zenndg.redweave.domain.module.SignalButton;
import io.github.zenndg.redweave.domain.module.SignalModule;
import io.github.zenndg.redweave.domain.network.PlayerId;
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
    EmitterScreenActions.target(player, spot, ButtonPart.class)
        .ifPresent(target -> apply(target, action));
  }

  private static void apply(EmitterScreenActions.Target<ButtonPart> target, ButtonAction action) {
    if (action.kind() == ButtonAction.Kind.OUTPUT) {
      target.connect(action.channel());
      return;
    }
    ButtonPart part = target.part();
    change(target.server().modules().changeButton(), target.requester(), part, action)
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
