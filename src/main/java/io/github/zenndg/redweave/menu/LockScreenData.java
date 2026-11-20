package io.github.zenndg.redweave.menu;

import io.github.zenndg.redweave.block.part.LockPart;
import io.github.zenndg.redweave.block.part.ModulePart;
import io.github.zenndg.redweave.block.part.PartSpot;
import java.util.Optional;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ContainerData;

/**
 * The live values of the Signal Lock Screen, read from its part each time the game compares them,
 * for the player who has it open. The key goes only to its owner (answer 637); the client never
 * writes them (rule 16).
 */
final class LockScreenData implements ContainerData {
  private final ServerPlayer viewer;
  private final PartSpot spot;

  LockScreenData(ServerPlayer viewer, PartSpot spot) {
    this.viewer = viewer;
    this.spot = spot;
  }

  @Override
  public int get(int index) {
    Optional<LockPart> part = ModulePart.at(viewer.level(), spot, LockPart.class);
    if (part.isEmpty()) {
      return ModuleScreenValues.NONE;
    }
    LockPart found = part.get();
    return switch (index) {
      case LockScreenLayout.VIEWER_IS_OWNER -> found.isOwner(viewer) ? 1 : 0;
      case LockScreenLayout.OPEN ->
          found.powered() ? LockScreenLayout.UNLOCKED_CODE : LockScreenLayout.LOCKED_CODE;
      case LockScreenLayout.NETWORK_STATE -> ModuleScreenValues.networkState(viewer, spot, found);
      default -> ofLock(found, index);
    };
  }

  /** The values only the lock has: its ends, its key, its strength and its pulse. */
  private int ofLock(LockPart found, int index) {
    return switch (index) {
      case LockScreenLayout.INPUT -> ModuleScreenValues.code(found.input());
      case LockScreenLayout.OUTPUT -> ModuleScreenValues.code(found.output());
      case LockScreenLayout.KEY -> keyFor(found);
      case LockScreenLayout.STRENGTH -> found.lock().strength().strength();
      case LockScreenLayout.PULSE -> found.lock().pulse().ticks();
      default -> ModuleScreenValues.NONE;
    };
  }

  /** The key only for its owner; anyone else gets none (answer 637). */
  private int keyFor(LockPart part) {
    return part.isOwner(viewer) ? part.lock().key().strength() : LockScreenLayout.HIDDEN_KEY;
  }

  @Override
  public void set(int index, int value) {
    // The server reads the values from the part; the client cannot change them.
  }

  @Override
  public int getCount() {
    return LockScreenLayout.SIZE;
  }
}
