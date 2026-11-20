package io.github.zenndg.redweave.menu;

import io.github.zenndg.redweave.block.part.AlarmPart;
import io.github.zenndg.redweave.block.part.ModulePart;
import io.github.zenndg.redweave.block.part.PartSpot;
import io.github.zenndg.redweave.domain.module.AlarmSettings;
import java.util.Optional;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ContainerData;

/**
 * The live values of the Signal Alarm Screen, read from its part each time the game compares them,
 * for the player who has it open; the client never writes them (rule 16).
 */
final class AlarmScreenData implements ContainerData {
  private final ServerPlayer viewer;
  private final PartSpot spot;

  AlarmScreenData(ServerPlayer viewer, PartSpot spot) {
    this.viewer = viewer;
    this.spot = spot;
  }

  @Override
  public int get(int index) {
    Optional<AlarmPart> part = ModulePart.at(viewer.level(), spot, AlarmPart.class);
    if (part.isEmpty()) {
      return ModuleScreenValues.NONE;
    }
    AlarmPart found = part.get();
    return switch (index) {
      case AlarmScreenLayout.VIEWER_IS_OWNER -> found.isOwner(viewer) ? 1 : 0;
      case AlarmScreenLayout.RINGING ->
          found.alarm().ringing() ? AlarmScreenLayout.RINGING_CODE : AlarmScreenLayout.QUIET_CODE;
      case AlarmScreenLayout.NETWORK_STATE -> ModuleScreenValues.networkState(viewer, spot, found);
      case AlarmScreenLayout.INPUT -> ModuleScreenValues.code(found.input());
      default -> ofSettings(found.alarm().settings(), index);
    };
  }

  /** The values the owner sets: who hears it, its rings, its interval and its duration. */
  private static int ofSettings(AlarmSettings settings, int index) {
    return switch (index) {
      case AlarmScreenLayout.LISTENERS -> settings.listeners().ordinal() + 1;
      case AlarmScreenLayout.REPEATS -> settings.repeats();
      case AlarmScreenLayout.INTERVAL -> settings.intervalTicks();
      case AlarmScreenLayout.DURATION -> settings.durationTicks();
      default -> ModuleScreenValues.NONE;
    };
  }

  @Override
  public void set(int index, int value) {
    // The server reads the values from the part; the client cannot change them.
  }

  @Override
  public int getCount() {
    return AlarmScreenLayout.SIZE;
  }
}
