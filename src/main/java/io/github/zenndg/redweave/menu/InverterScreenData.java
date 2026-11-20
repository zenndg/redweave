package io.github.zenndg.redweave.menu;

import io.github.zenndg.redweave.block.part.InverterPart;
import io.github.zenndg.redweave.block.part.ModulePart;
import io.github.zenndg.redweave.block.part.PartSpot;
import io.github.zenndg.redweave.domain.module.InverterSettings;
import java.util.Optional;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ContainerData;

/**
 * The live values of the Signal Inverter Screen, read from its block each time the game compares
 * them, so the diagram follows every tick. The client never writes them (rule 16).
 */
final class InverterScreenData implements ContainerData {
  private final ServerPlayer viewer;
  private final PartSpot spot;

  InverterScreenData(ServerPlayer viewer, PartSpot spot) {
    this.viewer = viewer;
    this.spot = spot;
  }

  @Override
  public int get(int index) {
    Optional<InverterPart> block = block();
    if (block.isEmpty()) {
      return ModuleScreenValues.NONE;
    }
    InverterPart found = block.get();
    return switch (index) {
      case InverterScreenLayout.VIEWER_IS_OWNER -> found.isOwner(viewer) ? 1 : 0;
      case InverterScreenLayout.STATE -> found.inverter().state().ordinal() + 1;
      case InverterScreenLayout.NETWORK_STATE ->
          ModuleScreenValues.networkState(viewer, spot, found);
      case InverterScreenLayout.INPUT -> ModuleScreenValues.code(found.input());
      case InverterScreenLayout.OUTPUT -> ModuleScreenValues.code(found.output());
      case InverterScreenLayout.INPUT_ON -> found.inputOn() ? 1 : 0;
      default -> settingAt(index, found.inverter().settings());
    };
  }

  /** The strength and the delay, in whole seconds (answers 830 and 834). */
  private static int settingAt(int index, InverterSettings settings) {
    return switch (index) {
      case InverterScreenLayout.STRENGTH -> settings.strength().strength();
      case InverterScreenLayout.DELAY_SECONDS -> settings.delay().seconds();
      default -> ModuleScreenValues.NONE;
    };
  }

  private Optional<InverterPart> block() {
    return ModulePart.at(viewer.level(), spot, InverterPart.class);
  }

  @Override
  public void set(int index, int value) {
    // The server reads the values from the block; the client cannot change them.
  }

  @Override
  public int getCount() {
    return InverterScreenLayout.SIZE;
  }
}
