package io.github.zenndg.redweave.block.part;

import com.mojang.logging.LogUtils;
import io.github.zenndg.redweave.block.ModuleMessages;
import io.github.zenndg.redweave.block.PartSpaceBlockEntity;
import io.github.zenndg.redweave.config.NeoForgeAlarmLimits;
import io.github.zenndg.redweave.domain.module.AlarmLimits;
import io.github.zenndg.redweave.domain.module.Connection;
import io.github.zenndg.redweave.domain.module.SignalAlarm;
import io.github.zenndg.redweave.domain.module.SignalModule;
import io.github.zenndg.redweave.domain.network.NetworkId;
import io.github.zenndg.redweave.domain.signal.Signal;
import io.github.zenndg.redweave.menu.AlarmMenu;
import io.github.zenndg.redweave.menu.AlarmScreenTexts;
import io.github.zenndg.redweave.registry.RedweaveItems;
import io.github.zenndg.redweave.server.ModuleServices;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.slf4j.Logger;

/**
 * The Signal Alarm as a part: _docs/funcionalidades/alarme.md. A Signal Module of output, with its
 * input on its back edge or a channel and no output: when the input turns on, it rings for its
 * duration, with the `pling` of the note block and `Alarm <name>: Ringing...` for its listeners,
 * and its warning light pulses (answers 643 to 649). The `Toggle` of its owner silences it until
 * the input turns off. What the client draws, whether it rings, comes from its save, which the
 * client receives.
 */
public final class AlarmPart extends ModulePart {
  public static final String ID = "signal_alarm";

  /** The looks of the top: quiet, with the warning light dark, and ringing, with it pulsing. */
  public static final List<String> LOOKS = List.of(ModuleLooks.NONE, ModuleLooks.ON);

  private static final double SIREN_EDGE = 5;
  private static final double SIREN_FAR_EDGE = 11;
  private static final double SIREN_HEIGHT = 4;
  private static final RaisedShapes SIREN =
      new RaisedShapes(SIREN_EDGE, SIREN_FAR_EDGE, SIREN_HEIGHT);
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String FITTED = "Fitted the Signal Alarm at {} to the config: {}";

  private SignalAlarm alarm = SignalAlarm.placed(new NeoForgeAlarmLimits());
  private Connection input = Connection.EDGE;

  /** The strength its input had on the last tick; not saved, as the next tick reads it again. */
  private Signal read = Signal.OFF;

  private AlarmPart(Direction face) {
    super(face);
  }

  /** An alarm on the side of its space, from its NBT, or a new one from an empty tag. */
  public static AlarmPart load(Direction face, CompoundTag tag) {
    AlarmPart part = new AlarmPart(face);
    part.loadPlate(tag);
    if (!tag.isEmpty()) {
      part.readRule(tag);
    }
    return part;
  }

  /** The settings the Configurator copies; never the alarm under way (answers 725 and 727). */
  @Override
  protected List<String> copiedKeys() {
    return List.of(
        AlarmSave.LISTENERS,
        AlarmSave.REPEATS,
        AlarmSave.INTERVAL,
        AlarmSave.DURATION,
        AlarmSave.INPUT);
  }

  @Override
  protected void dropChannels(ConnectionMapper drop) {
    input = drop.of(input);
  }

  /** Reads its rule from its save, as it loads and as the Configurator pastes (answer 725). */
  @Override
  protected void readRule(CompoundTag tag) {
    alarm = AlarmSave.read(tag);
    input = ModuleSave.readConnection(tag, AlarmSave.INPUT);
  }

  @Override
  protected String id() {
    return ID;
  }

  @Override
  public Item item() {
    return RedweaveItems.SIGNAL_ALARM.get();
  }

  /**
   * The owner silences it with the `Toggle` even with its network off, as the Signal Lock (707).
   */
  @Override
  public boolean togglesOffline() {
    return true;
  }

  /** The plate and the siren over it, so the siren can be aimed at too. */
  @Override
  public VoxelShape shape() {
    return Shapes.or(super.shape(), SIREN.of(face()));
  }

  /** Ringing while its alarm is under way, which the client reads from the save (answer 649). */
  @Override
  protected String lookSuffix() {
    return alarm.ringing() ? ModuleLooks.ON : ModuleLooks.NONE;
  }

  /** A new alarm: its owner hears it, 3 rings 1 s apart, for 5 s, on its back edge (648). */
  @Override
  protected void placeRule() {
    alarm = SignalAlarm.placed(new NeoForgeAlarmLimits());
    input = Connection.EDGE;
  }

  /** Its input, one tick of its alarm, and a ring for its listeners when one comes (646). */
  @Override
  protected void tickRule(ModuleServices modules, PartSpaceBlockEntity space) {
    read = ModuleTicks.read(modules, space, this, input);
    SignalAlarm.Step step = alarm.tick(read.isOn());
    if (step.ring() && space.getLevel() instanceof ServerLevel level) {
      AlarmCall.ring(level, space.getBlockPos(), this);
    }
    if (!step.alarm().equals(alarm)) {
      alarm = step.alarm();
      markDirty();
    }
  }

  @Override
  protected void saveRule(CompoundTag tag) {
    AlarmSave.write(tag, alarm);
    ModuleSave.writeConnection(tag, AlarmSave.INPUT, input);
  }

  public SignalAlarm alarm() {
    return alarm;
  }

  /** The strength its input had on the last tick. */
  public Signal read() {
    return read;
  }

  public Connection input() {
    return input;
  }

  /** It has no output; the label shows the channel of its input (answer 643). */
  @Override
  public Connection output() {
    return input;
  }

  /** The name of the alarm, as the action bar shows it (answer 647). */
  public String displayName() {
    return module().map(SignalModule::displayName).orElse(ID);
  }

  /** The network of the alarm, for the listeners who use it (answer 645). */
  public Optional<NetworkId> network() {
    return module().flatMap(SignalModule::network);
  }

  /** The `Toggle` silences it until its input turns off, and says so (answer 649). */
  @Override
  public Optional<Component> toggle() {
    setAlarm(alarm.silenced());
    return module()
        .map(found -> Component.translatable(ModuleMessages.ALARM_SILENCED, found.displayName()));
  }

  /** `Ringing` or `Quiet`. */
  @Override
  public Optional<String> stateKey() {
    return Optional.of(AlarmScreenTexts.of(alarm.ringing()));
  }

  @Override
  public void openScreen(ServerPlayer viewer) {
    PartSpot spot = spot();
    viewer.openMenu(
        new SimpleMenuProvider(
            (id, inventory, opener) -> new AlarmMenu(id, viewer, spot), item().getDescription()),
        extra -> AlarmMenu.writeOpening(extra, viewer, spot));
  }

  /** Keeps the alarm after a change and shows it at once. */
  public void setAlarm(SignalAlarm next) {
    alarm = next;
    changed();
  }

  /** The owner moved the input to its edge or to a channel (answer 643). */
  public void connectInput(Connection connection) {
    input = connection;
    changed();
  }

  /**
   * On the server, a loaded alarm fits the limits of the config of now: a number outside them goes
   * to the nearest limit, with a warning (answers 425 and 646, rule 14).
   */
  @Override
  public void loaded(PartSpaceBlockEntity space) {
    AlarmLimits limits = new NeoForgeAlarmLimits();
    if (module().isPresent() && !alarm.settings().fits(limits)) {
      LOGGER.warn(FITTED, space.getBlockPos(), alarm.settings());
      alarm = alarm.withSettings(alarm.settings().fittedTo(limits));
      markDirty();
    }
  }
}
