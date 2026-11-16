package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.block.part.LampPart;
import io.github.marcelofabianov.redweave.block.part.ModulePart;
import io.github.marcelofabianov.redweave.block.part.PartSpot;
import java.util.Optional;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ContainerData;

/**
 * The live values of the Signal Lamp Screen, read from its part each time the game compares them,
 * for the player who has it open; the client never writes them (rule 16).
 */
final class LampScreenData implements ContainerData {
  private final ServerPlayer viewer;
  private final PartSpot spot;

  LampScreenData(ServerPlayer viewer, PartSpot spot) {
    this.viewer = viewer;
    this.spot = spot;
  }

  @Override
  public int get(int index) {
    Optional<LampPart> part = ModulePart.at(viewer.level(), spot, LampPart.class);
    if (part.isEmpty()) {
      return ModuleScreenValues.NONE;
    }
    LampPart found = part.get();
    return switch (index) {
      case LampScreenLayout.VIEWER_IS_OWNER -> found.isOwner(viewer) ? 1 : 0;
      case LampScreenLayout.LIT ->
          found.powered() ? LampScreenLayout.LIT_CODE : LampScreenLayout.DARK_CODE;
      case LampScreenLayout.NETWORK_STATE -> ModuleScreenValues.networkState(viewer, spot, found);
      default -> ofLamp(found, index);
    };
  }

  /** The values only the lamp has: its channels, its mode, its strength and its light. */
  private static int ofLamp(LampPart found, int index) {
    return switch (index) {
      case LampScreenLayout.INTENSITY -> ModuleScreenValues.code(found.intensity());
      case LampScreenLayout.CONTROL -> ModuleScreenValues.code(found.control());
      case LampScreenLayout.MODE -> found.lamp().mode().ordinal();
      case LampScreenLayout.STRENGTH -> found.lamp().strength().strength();
      case LampScreenLayout.LIGHT -> found.light();
      default -> ModuleScreenValues.NONE;
    };
  }

  @Override
  public void set(int index, int value) {
    // The server reads the values from the part; the client cannot change them.
  }

  @Override
  public int getCount() {
    return LampScreenLayout.SIZE;
  }
}
