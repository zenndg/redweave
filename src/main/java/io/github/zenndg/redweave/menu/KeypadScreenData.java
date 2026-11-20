package io.github.zenndg.redweave.menu;

import io.github.zenndg.redweave.block.part.KeypadPart;
import io.github.zenndg.redweave.block.part.ModulePart;
import io.github.zenndg.redweave.block.part.PartSpot;
import java.util.Optional;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ContainerData;

/**
 * The live values of the Signal Keypad Screen of the owner, read from the plate. It never carries
 * the code, the tries nor the cards (answers 660 and 910), and the client never writes it (rule
 * 16).
 */
final class KeypadScreenData implements ContainerData {
  private static final int TICKS_PER_SECOND = 20;

  private final ServerPlayer viewer;
  private final PartSpot spot;

  KeypadScreenData(ServerPlayer viewer, PartSpot spot) {
    this.viewer = viewer;
    this.spot = spot;
  }

  @Override
  public int get(int index) {
    Optional<KeypadPart> part = ModulePart.at(viewer.level(), spot, KeypadPart.class);
    if (part.isEmpty()) {
      return ModuleScreenValues.NONE;
    }
    KeypadPart found = part.get();
    return switch (index) {
      case KeypadScreenLayout.VIEWER_IS_OWNER -> found.isOwner(viewer) ? 1 : 0;
      case KeypadScreenLayout.UNLOCKED -> found.keypad().unlocked() ? 1 : 0;
      case KeypadScreenLayout.NETWORK_STATE -> ModuleScreenValues.networkState(viewer, spot, found);
      case KeypadScreenLayout.INPUT -> ModuleScreenValues.code(found.input());
      case KeypadScreenLayout.OUTPUT -> ModuleScreenValues.code(found.output());
      default -> settingAt(index, found);
    };
  }

  private static int settingAt(int index, KeypadPart found) {
    return switch (index) {
      case KeypadScreenLayout.STRENGTH -> found.keypad().strength().strength();
      case KeypadScreenLayout.OPEN_SECONDS -> found.keypad().openTicks() / TICKS_PER_SECOND;
      case KeypadScreenLayout.HAS_CODE -> found.keypad().secret().isPresent() ? 1 : 0;
      default -> ModuleScreenValues.NONE;
    };
  }

  @Override
  public void set(int index, int value) {
    // The server reads the values from the plate; the client cannot change them.
  }

  @Override
  public int getCount() {
    return KeypadScreenLayout.SIZE;
  }
}
