package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.block.part.FlipFlopPart;
import io.github.marcelofabianov.redweave.block.part.ModulePart;
import io.github.marcelofabianov.redweave.block.part.PartSpot;
import java.util.Optional;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ContainerData;

/**
 * The live values of the Signal Flip-Flop Screen, read from its plate each time the game compares
 * them, so the diagram follows every tick. The client never writes them (rule 16).
 */
final class FlipFlopScreenData implements ContainerData {
  private final ServerPlayer viewer;
  private final PartSpot spot;

  FlipFlopScreenData(ServerPlayer viewer, PartSpot spot) {
    this.viewer = viewer;
    this.spot = spot;
  }

  @Override
  public int get(int index) {
    Optional<FlipFlopPart> part = ModulePart.at(viewer.level(), spot, FlipFlopPart.class);
    if (part.isEmpty()) {
      return ModuleScreenValues.NONE;
    }
    FlipFlopPart found = part.get();
    return switch (index) {
      case FlipFlopScreenLayout.VIEWER_IS_OWNER -> found.isOwner(viewer) ? 1 : 0;
      case FlipFlopScreenLayout.STATE ->
          found.flipFlop().on() ? FlipFlopScreenLayout.ON_CODE : FlipFlopScreenLayout.OFF_CODE;
      case FlipFlopScreenLayout.NETWORK_STATE ->
          ModuleScreenValues.networkState(viewer, spot, found);
      case FlipFlopScreenLayout.INPUT -> ModuleScreenValues.code(found.input());
      case FlipFlopScreenLayout.OUTPUT -> ModuleScreenValues.code(found.output());
      default -> settingAt(index, found);
    };
  }

  /** The reading for the diagram, the strength and the delay, in whole seconds (830, 880). */
  private static int settingAt(int index, FlipFlopPart found) {
    return switch (index) {
      case FlipFlopScreenLayout.INPUT_ON -> found.inputOn() ? 1 : 0;
      case FlipFlopScreenLayout.STRENGTH -> found.flipFlop().strength().strength();
      case FlipFlopScreenLayout.DELAY_SECONDS -> found.delayed().delay().seconds();
      default -> ModuleScreenValues.NONE;
    };
  }

  @Override
  public void set(int index, int value) {
    // The server reads the values from the plate; the client cannot change them.
  }

  @Override
  public int getCount() {
    return FlipFlopScreenLayout.SIZE;
  }
}
