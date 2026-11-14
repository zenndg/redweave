package io.github.marcelofabianov.redweave.block;

import com.mojang.logging.LogUtils;
import io.github.marcelofabianov.redweave.config.NeoForgeTimerLimits;
import io.github.marcelofabianov.redweave.domain.module.Connection;
import io.github.marcelofabianov.redweave.domain.module.SignalModule;
import io.github.marcelofabianov.redweave.domain.module.SignalTimer;
import io.github.marcelofabianov.redweave.domain.module.TimerCycle;
import io.github.marcelofabianov.redweave.domain.module.TimerLimits;
import io.github.marcelofabianov.redweave.registry.RedweaveBlockEntities;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

/**
 * What a Signal Timer block keeps: its module, its timer and its two connections, all in its NBT
 * and none in the save of the network (answers 387 and 411). The rules stay in the domain.
 */
public final class SignalTimerBlockEntity extends BlockEntity {
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String FITTED = "Fitted the Signal Timer at {} to the config: {}";
  private static final String RESTARTED = "A Signal Timer at {} had no valid cycle and starts anew";

  private Optional<SignalModule> module = Optional.empty();
  private Optional<SignalTimer> timer = Optional.empty();
  private Connection output = Connection.EDGE;
  private Connection pause = Connection.EDGE;
  private int emitted;

  public SignalTimerBlockEntity(BlockPos pos, BlockState state) {
    super(RedweaveBlockEntities.SIGNAL_TIMER.get(), pos, state);
  }

  /** A timer just placed: stopped, on the edges, one second with a half-second pulse (390). */
  void place(SignalModule placed, TimerLimits limits) {
    module = Optional.of(placed);
    timer = Optional.of(SignalTimer.stopped(TimerCycle.initial(limits)));
    output = Connection.EDGE;
    pause = Connection.EDGE;
    setChanged();
  }

  public Optional<SignalModule> module() {
    return module;
  }

  public Optional<SignalTimer> timer() {
    return timer;
  }

  public Connection output() {
    return output;
  }

  public Connection pause() {
    return pause;
  }

  /** Keeps the timer after a tick, and marks the block to save only when it changed. */
  void setTimer(SignalTimer next) {
    if (!timer.equals(Optional.of(next))) {
      timer = Optional.of(next);
      setChanged();
    }
  }

  /** The network of the module was deleted: it has none until its owner picks another (385). */
  void loseNetwork() {
    module.ifPresent(SignalModule::loseNetwork);
    setChanged();
  }

  /** What the front edge gives now, not saved, as the next tick works it out again. */
  int emitted() {
    return emitted;
  }

  void setEmitted(int strength) {
    emitted = strength;
  }

  /**
   * On the server, a loaded timer fits the limits of the config of now: an interval outside them
   * goes to the nearest limit and the cycle restarts, with a warning (answer 425 and rule 14).
   */
  @Override
  public void onLoad() {
    super.onLoad();
    if (level == null || level.isClientSide || module.isEmpty()) {
      return;
    }
    TimerLimits limits = new NeoForgeTimerLimits();
    if (timer.isEmpty()) {
      LOGGER.warn(RESTARTED, worldPosition);
      timer = Optional.of(SignalTimer.stopped(TimerCycle.initial(limits)));
      setChanged();
    } else if (!timer.get().cycle().fits(limits)) {
      LOGGER.warn(FITTED, worldPosition, timer.get().cycle().intervalTicks());
      timer = timer.map(found -> found.withCycle(found.cycle().fittedTo(limits)));
      setChanged();
    }
  }

  @Override
  protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.saveAdditional(tag, registries);
    module.ifPresent(found -> TimerSave.writeModule(tag, found));
    timer.ifPresent(found -> TimerSave.writeTimer(tag, found));
    TimerSave.writeConnection(tag, TimerSave.OUTPUT, output);
    TimerSave.writeConnection(tag, TimerSave.PAUSE, pause);
  }

  @Override
  protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.loadAdditional(tag, registries);
    module = TimerSave.readModule(tag);
    timer = TimerSave.readTimer(tag);
    output = TimerSave.readConnection(tag, TimerSave.OUTPUT);
    pause = TimerSave.readConnection(tag, TimerSave.PAUSE);
  }
}
