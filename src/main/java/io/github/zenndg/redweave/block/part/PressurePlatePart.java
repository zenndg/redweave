package io.github.zenndg.redweave.block.part;

import io.github.zenndg.redweave.block.ModuleMessages;
import io.github.zenndg.redweave.block.PartSpaceBlockEntity;
import io.github.zenndg.redweave.config.NeoForgePressurePlateCheck;
import io.github.zenndg.redweave.domain.module.Emitter;
import io.github.zenndg.redweave.domain.module.PlateMode;
import io.github.zenndg.redweave.domain.module.SignalPressurePlate;
import io.github.zenndg.redweave.menu.PressurePlateMenu;
import io.github.zenndg.redweave.menu.PressurePlateScreenTexts;
import io.github.zenndg.redweave.registry.RedweaveItems;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * The Signal Pressure Plate as a part: _docs/funcionalidades/placa-de-pressao.md. A source Signal
 * Module on the floor, on while something of its mode stands on it (answers 549, 551 and 621). Off,
 * it costs nothing until an entity walks into its space; on, it looks again every
 * `pressure_plate.check_ticks` and turns off when nothing of its mode is left (answer 607). On, it
 * feeds its block of support and the block in front with strength, or writes on its channel
 * (answers 553 and 606).
 */
public final class PressurePlatePart extends EmitterPart implements SteppedPart {
  public static final String ID = "signal_pressure_plate";

  /** The looks of the top: off, with the red light, and on, with the green one (609). */
  public static final List<String> LOOKS = EmitterPart.LOOKS;

  private static final float VOLUME = 0.3F;
  private static final float PITCH_ON = 0.6F;
  private static final float PITCH_OFF = 0.5F;

  private SignalPressurePlate plate = SignalPressurePlate.placed();

  /** The ticks until an on plate looks again; not saved, so a loaded plate looks at once. */
  private int countdown;

  private PressurePlatePart(Direction face) {
    super(face);
  }

  /** A plate on the side of its space, from its NBT, or a new one from an empty tag. */
  public static PressurePlatePart load(Direction face, CompoundTag tag) {
    PressurePlatePart part = new PressurePlatePart(face);
    part.loadPlate(tag);
    if (!tag.isEmpty()) {
      part.readRule(tag);
    }
    return part;
  }

  /** What the Configurator copies: its settings, never what it is doing now (answer 725). */
  @Override
  protected List<String> copiedKeys() {
    return List.of(
        StrengthSave.STRENGTH, PressurePlateSave.MODE, EmitterPart.OUTPUT, OutputDelaySlot.COPIED);
  }

  /** Reads its rule from its save, as it loads and as the Configurator pastes (answer 725). */
  @Override
  protected void readRule(CompoundTag tag) {
    plate = PressurePlateSave.readPlate(tag);
    loadOutput(tag);
  }

  @Override
  protected String id() {
    return ID;
  }

  @Override
  public Item item() {
    return RedweaveItems.SIGNAL_PRESSURE_PLATE.get();
  }

  /** The plate and the tread over it, so the tread can be aimed at too. */
  @Override
  public VoxelShape shape() {
    return Shapes.or(super.shape(), PressurePlateShapes.of(face()));
  }

  /** A plate just placed: off, at 15, sensing players, on its edge (552 and 604). */
  @Override
  protected void placeRule() {
    plate = SignalPressurePlate.placed();
    placeOutput();
  }

  @Override
  protected Emitter emitter() {
    return plate;
  }

  /**
   * After its output, the pulse of a Signal Remote counts down (answer 783), and a plate with
   * something on it counts down to its next look and lets go when nothing of its mode is left on it
   * (answer 607).
   */
  @Override
  protected void afterOutput(PartSpaceBlockEntity space) {
    countPulse();
    if (!plate.stood() || --countdown > 0) {
      return;
    }
    SignalPressurePlate next =
        plate.sensed(PlateSensing.presentAt(space.getLevel(), space.getBlockPos()));
    if (next.stood()) {
      countdown = NeoForgePressurePlateCheck.ticks();
      return;
    }
    setPlate(next);
  }

  /** One tick of the pulse, shown only when it turns the plate off, as the Signal Button. */
  private void countPulse() {
    if (plate.pulse() == 0) {
      return;
    }
    SignalPressurePlate next = plate.tick();
    if (next.on() != plate.on()) {
      setPlate(next);
      return;
    }
    plate = next;
    markDirty();
  }

  /** The click of a Signal Remote: on for its pulse, again if on (answers 781 and 783). */
  @Override
  public Optional<Component> fireRemotely(int pulseTicks) {
    setPlate(plate.pulsed(pulseTicks));
    return module()
        .map(found ->
            Component.translatable(ModuleMessages.PRESSURE_PLATE_PULSED, found.displayName()));
  }

  /**
   * An entity walked into the space: a plate with nothing on it senses it at once if it touches the
   * plate and is of its mode, without searching the area (answers 607 and 621, rule 18).
   */
  @Override
  public void steppedOn(PartSpaceBlockEntity space, Entity entity) {
    if (plate.stood() || module().isEmpty() || !PlateSensing.touches(space.getBlockPos(), entity)) {
      return;
    }
    Optional<PlateMode> kind = PlateSensing.kindOf(entity);
    if (kind.isPresent() && plate.senses(List.of(kind.get()))) {
      countdown = NeoForgePressurePlateCheck.ticks();
      setPlate(plate.sensed(List.of(kind.get())));
    }
  }

  @Override
  protected void writeEmitter(CompoundTag tag) {
    PressurePlateSave.writePlate(tag, plate);
  }

  public SignalPressurePlate plate() {
    return plate;
  }

  /**
   * The next mode, said with the name of the module, as `<name>: Players` (answers 603 and 621).
   */
  @Override
  public Optional<Component> toggle() {
    setPlate(plate.toggled());
    return module()
        .map(found ->
            Component.translatable(
                ModuleMessages.PRESSURE_PLATE_MODE,
                found.displayName(),
                Component.translatable(PressurePlateScreenTexts.of(plate.mode()))));
  }

  /** The hand does nothing, with no message, as the plate of the game (answer 606). */
  @Override
  public void emptyHand(Player player) {}

  /** `On` or `Off` and the mode, as `On · Players` (answer 609). */
  @Override
  public Optional<String> stateKey() {
    return Optional.of(PressurePlateScreenTexts.stateOf(plate.on(), plate.mode()));
  }

  @Override
  public void openScreen(ServerPlayer viewer) {
    PartSpot spot = spot();
    viewer.openMenu(
        new SimpleMenuProvider(
            (id, inventory, opener) -> new PressurePlateMenu(id, viewer, spot),
            item().getDescription()),
        extra -> PressurePlateMenu.writeOpening(extra, viewer, spot));
  }

  /**
   * Keeps the plate after a change and shows it at once; turned on or off, it clicks as the stone
   * pressure plate of the game (answer 609).
   */
  public void setPlate(SignalPressurePlate next) {
    boolean turned = next.on() != plate.on();
    plate = next;
    changed();
    if (turned) {
      click(
          next.on()
              ? SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON
              : SoundEvents.STONE_PRESSURE_PLATE_CLICK_OFF,
          VOLUME,
          next.on() ? PITCH_ON : PITCH_OFF);
    }
  }
}
