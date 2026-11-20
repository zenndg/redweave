package io.github.zenndg.redweave.menu;

import io.github.zenndg.redweave.block.part.ItemSensorPart;
import io.github.zenndg.redweave.block.part.ModulePart;
import io.github.zenndg.redweave.block.part.PartSpot;
import io.github.zenndg.redweave.config.NeoForgeItemSensorLimits;
import io.github.zenndg.redweave.domain.module.ItemSensor;
import io.github.zenndg.redweave.domain.module.SignalModule;
import io.github.zenndg.redweave.domain.network.PlayerId;
import io.github.zenndg.redweave.server.RedweaveServer;
import java.util.Optional;
import net.minecraft.world.entity.player.Player;

/**
 * What the buttons only the Signal Item Sensor Screen has do on the server. Every action goes
 * through the owner of the plate: anyone else changes nothing (answers 378 and 696, rules 16 and
 * 17). The ghost slot takes the item the menu says is on the cursor, never one the client names.
 */
final class ItemSensorScreenActions {
  private ItemSensorScreenActions() {}

  /** The action, with the item on the cursor of the player, for the ghost slot. */
  static void apply(
      Player player, PartSpot spot, ItemSensorAction action, Optional<String> cursor) {
    Optional<ItemSensorPart> found = ModulePart.at(player.level(), spot, ItemSensorPart.class);
    Optional<RedweaveServer> server = RedweaveServer.running();
    if (found.isEmpty() || found.get().module().isEmpty() || server.isEmpty()) {
      return;
    }
    ItemSensorPart part = found.get();
    PlayerId requester = new PlayerId(player.getUUID());
    if (action.kind() == ItemSensorAction.Kind.OUTPUT) {
      ModuleScreenActions.connect(requester, part, action.channel(), part::connectOutput);
      return;
    }
    changed(server.get(), requester, part, action, cursor).ifPresent(part::setSensor);
  }

  private static Optional<ItemSensor> changed(
      RedweaveServer server,
      PlayerId requester,
      ItemSensorPart part,
      ItemSensorAction action,
      Optional<String> cursor) {
    SignalModule module = part.module().orElseThrow();
    ItemSensor now = part.sensor();
    return switch (action.kind()) {
      case ITEM -> server.modules().changeItemSensor().item(requester, module, now, cursor);
      case COUNT ->
          server
              .modules()
              .changeItemSensor()
              .count(
                  requester,
                  module,
                  now,
                  now.count() + action.step(),
                  NeoForgeItemSensorLimits.maxCount());
      default ->
          server
              .modules()
              .changeItemSensor()
              .strength(requester, module, now, now.strength().strength() + action.step());
    };
  }
}
