package io.github.marcelofabianov.redweave.block;

import io.github.marcelofabianov.redweave.domain.module.SignalRandomizer;
import io.github.marcelofabianov.redweave.server.ModuleServices;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Each tick of a loaded Signal Randomizer: it ticks its rule in the domain with the draws of the
 * world, writes its output and shows its look (signal-randomizer.md). An unloaded randomizer gets
 * no tick, so it freezes and goes on from where it was (answers 481 and 485).
 */
final class RandomizerTicks {
  private RandomizerTicks() {}

  static void tick(Level level, BlockPos pos, BlockState state, SignalRandomizerBlockEntity block) {
    Optional<ModuleServices> modules = ModuleTicks.start(level, pos, block);
    if (modules.isEmpty() || block.randomizer().isEmpty()) {
      return;
    }
    SignalRandomizer next = block.randomizer().get().tick(block.draws());
    block.setRandomizer(next);
    ModuleTicks.write(modules.get(), level, pos, state, block, next.output());
    look(level, pos, state, next);
  }

  /** The arrow lit with the output above 0, and the LED while running (answer 488). */
  private static void look(Level level, BlockPos pos, BlockState state, SignalRandomizer next) {
    BlockState shown =
        state
            .setValue(SignalRandomizerBlock.POWERED, next.output().isOn())
            .setValue(SignalRandomizerBlock.RUNNING, next.running());
    if (shown != state) {
      level.setBlock(pos, shown, Block.UPDATE_CLIENTS);
    }
  }
}
