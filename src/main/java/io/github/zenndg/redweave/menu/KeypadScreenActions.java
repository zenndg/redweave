package io.github.zenndg.redweave.menu;

import io.github.zenndg.redweave.application.ChangeKeypad;
import io.github.zenndg.redweave.block.part.KeypadPart;
import io.github.zenndg.redweave.block.part.ModulePart;
import io.github.zenndg.redweave.block.part.PartSpot;
import io.github.zenndg.redweave.config.NeoForgeKeypadLimits;
import io.github.zenndg.redweave.domain.module.CardId;
import io.github.zenndg.redweave.domain.module.SignalKeypad;
import io.github.zenndg.redweave.domain.module.SignalModule;
import io.github.zenndg.redweave.domain.network.PlayerId;
import io.github.zenndg.redweave.server.RedweaveServer;
import java.util.List;
import java.util.Optional;
import net.minecraft.world.entity.player.Player;

/**
 * What the buttons of the Signal Keypad Screen of the owner do on the server. Every action goes
 * through the owner of the plate: anyone else changes nothing (answers 378, 661, 663, 669, 905 and
 * 915, rules 16 and 17).
 */
final class KeypadScreenActions {
  private static final int TICKS_PER_SECOND = 20;

  private KeypadScreenActions() {}

  static void apply(Player player, PartSpot spot, KeypadAction action) {
    Optional<KeypadPart> found = ModulePart.at(player.level(), spot, KeypadPart.class);
    Optional<RedweaveServer> server = RedweaveServer.running();
    if (found.isEmpty() || found.get().module().isEmpty() || server.isEmpty()) {
      return;
    }
    KeypadPart part = found.get();
    PlayerId requester = new PlayerId(player.getUUID());
    switch (action.kind()) {
      case INPUT ->
          ModuleScreenActions.connect(requester, part, action.channel(), part::connectInput);
      case OUTPUT ->
          ModuleScreenActions.connect(requester, part, action.channel(), part::connectOutput);
      case CHOOSE -> {
        // A choice names a row of the list the menu keeps, so the menu applies it.
      }
      default ->
          change(server.get().modules().changeKeypad(), requester, part, action)
              .ifPresent(part::setKeypad);
    }
  }

  private static Optional<SignalKeypad> change(
      ChangeKeypad change, PlayerId requester, KeypadPart part, KeypadAction action) {
    SignalModule module = part.module().get();
    SignalKeypad now = part.keypad();
    return switch (action.kind()) {
      case STRENGTH ->
          change.strength(requester, module, now, now.strength().strength() + action.step());
      case OPEN ->
          change.open(
              requester,
              module,
              now,
              now.openTicks() + action.step() * TICKS_PER_SECOND,
              new NeoForgeKeypadLimits());
      case CODE ->
          KeypadCodes.decode(action.value())
              .flatMap(code -> change.code(requester, module, now, code));
      case REVOKE ->
          cardAt(now, action.value()).flatMap(card -> change.revoke(requester, module, now, card));
      default -> Optional.empty();
    };
  }

  private static Optional<CardId> cardAt(SignalKeypad keypad, int position) {
    List<CardId> cards = keypad.cards().ids();
    return position < cards.size() ? Optional.of(cards.get(position)) : Optional.empty();
  }
}
