package io.github.marcelofabianov.redweave.block;

import io.github.marcelofabianov.redweave.config.NeoForgeInverterLimits;
import io.github.marcelofabianov.redweave.domain.module.InverterLimits;
import io.github.marcelofabianov.redweave.domain.module.InverterState;
import io.github.marcelofabianov.redweave.domain.module.SignalInverter;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import io.github.marcelofabianov.redweave.server.ModuleServices;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Each tick of a loaded Signal Inverter: it reads its input, ticks its rule in the domain with the
 * delay of the connection, writes its output and shows its look (signal-inverter.md). An unloaded
 * inverter gets no tick and is not sighted, as every module (answer 386).
 */
final class InverterTicks {
  private static final InverterLimits LIMITS = new NeoForgeInverterLimits();

  private InverterTicks() {}

  static void tick(Level level, BlockPos pos, BlockState state, SignalInverterBlockEntity block) {
    Optional<ModuleServices> modules = ModuleTicks.start(level, pos, block);
    if (modules.isEmpty()) {
      return;
    }
    Signal input = ModuleTicks.read(modules.get(), level, pos, state, block, block.input());
    int delay = block.input().isEdge() ? SignalInverter.EDGE_DELAY : SignalInverter.CHANNEL_DELAY;
    SignalInverter next = block.inverter().tick(input, delay, LIMITS);
    block.setInverter(next, input);
    ModuleTicks.write(modules.get(), level, pos, state, block, next.output());
    look(level, pos, state, next);
  }

  /** The arrow lit, dark or burnt, only sent to the clients (answers 452 and 455). */
  private static void look(Level level, BlockPos pos, BlockState state, SignalInverter inverter) {
    BlockState shown =
        state
            .setValue(SignalInverterBlock.POWERED, inverter.output().isOn())
            .setValue(SignalInverterBlock.BURNT, inverter.state() == InverterState.BURNT_OUT);
    if (shown != state) {
      level.setBlock(pos, shown, Block.UPDATE_CLIENTS);
    }
  }
}
