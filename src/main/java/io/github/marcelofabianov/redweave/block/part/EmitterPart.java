package io.github.marcelofabianov.redweave.block.part;

import io.github.marcelofabianov.redweave.block.ModuleMessages;
import io.github.marcelofabianov.redweave.block.PartSpaceBlockEntity;
import io.github.marcelofabianov.redweave.domain.module.Connection;
import io.github.marcelofabianov.redweave.domain.module.Emitter;
import io.github.marcelofabianov.redweave.server.ModuleServices;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

/**
 * A Signal Emitter as a part: _docs/funcionalidades/emissores.md. The contract every emitter
 * shares, written once (answers 627 to 630): one output, on its edges or a channel, strong on its
 * block of support and the block in front (553); written each tick, so it follows its trigger on
 * the next tick (587); a `Toggle` that works with the network `Offline` (578); and a light that
 * shows whether it is on, never what reaches its channel (554 and 581). Each emitter only brings
 * its trigger, its `Toggle`, its sound, its 3D piece and its own save.
 */
public abstract class EmitterPart extends ModulePart {
  /** The looks of the top: off, with the red light, and on, with the green one (554). */
  public static final List<String> LOOKS = List.of(ModuleLooks.NONE, ModuleLooks.ON);

  static final String OUTPUT = "output";

  private Connection output = Connection.EDGE;

  protected EmitterPart(Direction face) {
    super(face);
  }

  /** The rule of the emitter, as it is now. */
  protected abstract Emitter emitter();

  /** Writes the keys of its own rule, as `on` and `strength` (answers 579 and 584). */
  protected abstract void writeEmitter(CompoundTag tag);

  /**
   * The click of a Signal Remote linked to it, as the click of its owner, with the message of the
   * action bar (answers 781, 783 and 789).
   */
  public abstract Optional<Component> fireRemotely(int pulseTicks);

  /** Whether the click of a Signal Remote presses it, as the button, or turns it on and off. */
  public boolean remotePresses() {
    return true;
  }

  /** What it does after giving its output each tick, as counting down; nothing by default. */
  protected void afterOutput(PartSpaceBlockEntity space) {}

  /** The output saved with the plate, on its edges or a channel. */
  /** Its one output off its channel, for a paste out of its network (answer 732). */
  @Override
  protected final void dropChannels(ChannelDrop drop) {
    output = drop.of(output);
  }

  protected final void loadOutput(CompoundTag tag) {
    output = ModuleSave.readConnection(tag, OUTPUT);
  }

  /** A new emitter gives its output on its edges (answer 553). */
  protected final void placeOutput() {
    output = Connection.EDGE;
  }

  /** The light and the piece follow the emitter, never what reaches its channel (581). */
  @Override
  protected final String lookSuffix() {
    return emitter().on() ? ModuleLooks.ON : ModuleLooks.NONE;
  }

  /** It gives what it holds, each tick, on its edges or its channel (553 and 587). */
  @Override
  protected final void tickRule(ModuleServices modules, PartSpaceBlockEntity space) {
    ModuleTicks.write(modules, space, this, emitter().output());
    afterOutput(space);
  }

  @Override
  protected final boolean feedsSupport() {
    return true;
  }

  @Override
  public final boolean togglesOffline() {
    return true;
  }

  @Override
  protected final void saveRule(CompoundTag tag) {
    writeEmitter(tag);
    ModuleSave.writeConnection(tag, OUTPUT, output);
  }

  @Override
  public final Connection output() {
    return output;
  }

  /** The owner moved the output to its edges or to a channel (answers 548 and 549). */
  public final void connectOutput(Connection connection) {
    output = connection;
    changed();
  }

  /** Runs what the hand of the owner does; anyone else is told whose it is (576 and 577). */
  protected final void byOwner(Player player, Runnable action) {
    if (!isOwner(player)) {
      player.displayClientMessage(Component.translatable(ModuleMessages.NOT_OWNER), true);
      return;
    }
    action.run();
  }

  /** Plays a sound of the game at the emitter, as its trigger clicks (580, 589 and 609). */
  protected final void click(SoundEvent sound, float volume, float pitch) {
    level()
        .ifPresent(
            found -> found.playSound(null, spot().pos(), sound, SoundSource.BLOCKS, volume, pitch));
  }
}
