package io.github.marcelofabianov.redweave.block.part;

import io.github.marcelofabianov.redweave.application.ModuleSighting;
import io.github.marcelofabianov.redweave.application.ModuleSync;
import io.github.marcelofabianov.redweave.block.BlockLocations;
import io.github.marcelofabianov.redweave.block.Faces;
import io.github.marcelofabianov.redweave.block.PartSpaceBlockEntity;
import io.github.marcelofabianov.redweave.domain.module.Connection;
import io.github.marcelofabianov.redweave.domain.module.SignalModule;
import io.github.marcelofabianov.redweave.domain.network.BlockLocation;
import io.github.marcelofabianov.redweave.domain.network.ModuleEntry;
import io.github.marcelofabianov.redweave.domain.network.ModuleInput;
import io.github.marcelofabianov.redweave.domain.network.NodePosition;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import io.github.marcelofabianov.redweave.server.ModuleServices;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * What the tick of every loaded Signal Module shares: it lets a deleted network go, reads an input
 * from the back edge or a channel, and writes its output on the front edge or is sighted on a
 * channel (answers 375, 385, 387, 407 and 412). Each module adds its rule between the two.
 */
final class ModuleTicks {
  private ModuleTicks() {}

  /**
   * The services of the modules, after a deleted network is let go and the entry of the module in
   * its network is up to date (answer 803), or none to skip the tick.
   */
  static Optional<ModuleServices> start(PartSpaceBlockEntity space, ModulePart part) {
    Optional<RedweaveServer> server = RedweaveServer.running();
    if (server.isEmpty() || part.module().isEmpty()) {
      return Optional.empty();
    }
    ModuleServices modules = server.get().modules();
    if (modules.show().show(part.module().get(), locationOf(space)).gone()) {
      part.loseNetwork();
    }
    sync(modules, space, part);
    return Optional.of(modules);
  }

  /**
   * Brings the entry of the module in its network up to date and keeps the network it is registered
   * in; taken out by `Clear components`, it lets go of the network (answers 803, 805, 810, 811 and
   * 817).
   */
  private static void sync(ModuleServices modules, PartSpaceBlockEntity space, ModulePart part) {
    SignalModule module = part.module().orElseThrow();
    ModuleSync result = modules.sync().sync(module, part.registeredIn(), entryOf(space, part));
    if (result.cleared()) {
      part.loseNetwork();
    }
    if (!result.registeredIn().equals(part.registeredIn())) {
      part.registeredIn(result.registeredIn());
    }
  }

  /** What the network keeps of the module: its kind, name, place and channels (answer 810). */
  private static ModuleEntry entryOf(PartSpaceBlockEntity space, ModulePart part) {
    SignalModule module = part.module().orElseThrow();
    return new ModuleEntry(
        module.id(),
        part.id(),
        module.name(),
        new NodePosition(locationOf(space), Faces.of(part.face())),
        ModuleChannels.of(part));
  }

  /** An input from the back edge, or from a channel of the last tick (answers 387 and 412). */
  static Signal read(
      ModuleServices modules, PartSpaceBlockEntity space, ModulePart part, Connection input) {
    if (input.isEdge()) {
      Direction back = part.front().getOpposite();
      Signal behind = fromBehind(space.getLevel(), space.getBlockPos(), back);
      return Signal.clamped(Math.max(behind.strength(), cableOn(space, back)));
    }
    return modules
        .read()
        .read(part.module().get(), locationOf(space), input.channel().orElseThrow());
  }

  /**
   * The output on the front edge, or sighted on its channel with the edge off, and the lit arrow of
   * the output (answers 376, 387 and 401).
   */
  static void write(
      ModuleServices modules, PartSpaceBlockEntity space, ModulePart part, Signal output) {
    sight(modules, part, locationOf(space), output);
    emit(space, part, part.output().isEdge() ? output : Signal.OFF);
    part.setPowered(output.isOn());
  }

  /**
   * A signal given only by the part itself, never sighted on a channel, as the Signal Lamp gives to
   * its block of support (answer 653): the space and its neighbors wake only when it changes.
   */
  static void give(PartSpaceBlockEntity space, ModulePart part, Signal output) {
    if (part.emitted() != output.strength()) {
      emit(space, part, output);
      space.getLevel().updateNeighborsAt(space.getBlockPos(), space.getBlockState().getBlock());
    }
    part.setPowered(output.isOn());
  }

  private static BlockLocation locationOf(PartSpaceBlockEntity space) {
    return BlockLocations.of(space.getLevel(), space.getBlockPos());
  }

  /**
   * What reaches the back edge, as the input of a repeater: the signal of the block behind, or the
   * power of a wire there whatever its shape (answer 412). An unloaded block gives none (rule 6).
   */
  private static Signal fromBehind(Level level, BlockPos pos, Direction back) {
    BlockPos behind = pos.relative(back);
    if (!level.isLoaded(behind)) {
      return Signal.OFF;
    }
    int strength = level.getSignal(behind, back);
    BlockState wire = level.getBlockState(behind);
    if (wire.is(Blocks.REDSTONE_WIRE)) {
      strength = Math.max(strength, wire.getValue(RedStoneWireBlock.POWER));
    }
    return Signal.clamped(strength);
  }

  /** A Signal Cable of the space on the side of the back edge feeds it (answer 566). */
  private static int cableOn(PartSpaceBlockEntity space, Direction back) {
    return space
        .at(back)
        .filter(CablePart.class::isInstance)
        .map(part -> ((CablePart) part).strength())
        .orElse(0);
  }

  /** With its output on a channel of its network, the module writes into it this tick (387). */
  private static void sight(
      ModuleServices modules, ModulePart part, BlockLocation plate, Signal output) {
    SignalModule module = part.module().get();
    Optional<ChannelColor> channel = part.output().channel();
    if (channel.isEmpty() || module.network().isEmpty()) {
      return;
    }
    ModuleInput input = new ModuleInput(module.id(), plate, channel.get(), output);
    modules
        .sightings()
        .add(new ModuleSighting(module.network().get(), input, module.displayName()));
  }

  /**
   * Feeds the block in front with strength, as a repeater, and wakes it only when the value
   * changes, so there are no cascading block updates (answer 407 and rule 18).
   */
  private static void emit(PartSpaceBlockEntity space, ModulePart part, Signal signal) {
    if (part.emitted() == signal.strength()) {
      return;
    }
    part.setEmitted(signal.strength());
    part.wakeOutputs(space.getLevel(), space.getBlockPos());
    space.wakeOthers(part);
  }
}
