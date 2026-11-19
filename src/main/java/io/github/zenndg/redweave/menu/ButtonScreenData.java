package io.github.zenndg.redweave.menu;

import io.github.zenndg.redweave.block.part.ButtonPart;
import io.github.zenndg.redweave.block.part.ModulePart;
import io.github.zenndg.redweave.block.part.PartSpot;
import java.util.Optional;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ContainerData;

/**
 * The live values of the Signal Button Screen, read from its part each time the game compares them,
 * so the state follows a press and the release while the screen is open. The client never writes
 * them (rule 16).
 */
final class ButtonScreenData implements ContainerData {
  private final ServerPlayer viewer;
  private final PartSpot spot;

  ButtonScreenData(ServerPlayer viewer, PartSpot spot) {
    this.viewer = viewer;
    this.spot = spot;
  }

  @Override
  public int get(int index) {
    Optional<ButtonPart> part = ModulePart.at(viewer.level(), spot, ButtonPart.class);
    if (part.isEmpty()) {
      return ModuleScreenValues.NONE;
    }
    ButtonPart found = part.get();
    if (index == ButtonScreenLayout.DELAY_SECONDS) {
      return found.delayed().delay().seconds();
    }
    return switch (index) {
      case ButtonScreenLayout.VIEWER_IS_OWNER -> found.isOwner(viewer) ? 1 : 0;
      case ButtonScreenLayout.ON ->
          found.button().on() ? ButtonScreenLayout.ON_CODE : ButtonScreenLayout.OFF_CODE;
      case ButtonScreenLayout.NETWORK_STATE -> ModuleScreenValues.networkState(viewer, spot, found);
      case ButtonScreenLayout.OUTPUT -> ModuleScreenValues.code(found.output());
      case ButtonScreenLayout.STRENGTH -> found.button().strength().strength();
      default ->
          TimerScreenLayout.half(found.button().time().ticks(), index, ButtonScreenLayout.TIME);
    };
  }

  @Override
  public void set(int index, int value) {
    // The server reads the values from the part; the client cannot change them.
  }

  @Override
  public int getCount() {
    return ButtonScreenLayout.SIZE;
  }
}
