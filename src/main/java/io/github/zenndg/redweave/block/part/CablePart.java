package io.github.zenndg.redweave.block.part;

import com.mojang.logging.LogUtils;
import io.github.zenndg.redweave.RedweaveMod;
import io.github.zenndg.redweave.block.PartSpaceBlockEntity;
import io.github.zenndg.redweave.domain.cable.Insulation;
import io.github.zenndg.redweave.domain.network.PlayerId;
import io.github.zenndg.redweave.domain.part.PartKind;
import io.github.zenndg.redweave.domain.signal.ChannelColor;
import io.github.zenndg.redweave.item.SignalCableItem;
import io.github.zenndg.redweave.registry.RedweaveItems;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.slf4j.Logger;

/**
 * The Signal Cable as a part (_docs/funcionalidades/signal-cable.md): it rests on any side of a
 * space, keeps only its color and owner in the save, and gives the strength its run works out to
 * the block it rests on and where it points, as the game wire (answers 495, 508, 567 and 568).
 */
public final class CablePart implements Part, LivePart, OwnedPart, ClientPart {
  static final String COLOR_TAG = "color";
  static final String OWNER_TAG = "owner";
  private static final String STRENGTH_TAG = "strength";
  private static final String ARMS_TAG = "arms";
  private static final PlayerId NO_OWNER = new PlayerId(new UUID(0, 0));
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String DROPPED = "Dropped an invalid color of a Signal Cable: {}";
  private static final String CENTER_PATH = "block/signal_cable_center";

  /** The model of the center of the cable, on the floor (answer 521). */
  public static final ResourceLocation CENTER =
      ResourceLocation.fromNamespaceAndPath(RedweaveMod.MOD_ID, CENTER_PATH);

  /** Every model of the cable, which the client loads for the part space (answer 536). */
  public static final List<ResourceLocation> MODELS =
      Stream.concat(Stream.of(CENTER), Arrays.stream(CableReach.values()).map(CableReach::model))
          .toList();

  private final Direction face;
  private final ChannelColor color;
  private Optional<UUID> owner = Optional.empty();
  private int strength;
  private int input;
  private Map<Direction, CableReach> arms = new EnumMap<>(Direction.class);

  private CablePart(Direction face, ChannelColor color) {
    this.face = face;
    this.color = color;
  }

  /** A new cable of the color on the side, whose owner is who places it (answer 526). */
  public static CablePart placed(Direction face, ChannelColor color) {
    return new CablePart(face, color);
  }

  /** The cable of its `data`; an unknown color, as from a damaged save, is white (rule 14). */
  public static Part load(Direction face, CompoundTag data) {
    String key = data.getString(COLOR_TAG);
    Optional<ChannelColor> color = ChannelColor.fromKey(key);
    if (color.isEmpty() && !key.isEmpty()) {
      LOGGER.warn(DROPPED, key);
    }
    CablePart cable = new CablePart(face, color.orElse(ChannelColor.WHITE));
    cable.owner = data.hasUUID(OWNER_TAG) ? Optional.of(data.getUUID(OWNER_TAG)) : cable.owner;
    return cable;
  }

  /** The cable on the side of the spot, when its space is loaded and has one there (rule 6). */
  public static Optional<CablePart> at(Level level, PartSpot spot) {
    return PartSpaceBlockEntity.at(level, spot.pos())
        .flatMap(space -> space.at(spot.face()))
        .filter(CablePart.class::isInstance)
        .map(CablePart.class::cast);
  }

  @Override
  public Direction face() {
    return face;
  }

  @Override
  public PartKind kind() {
    return PartKind.CABLE;
  }

  @Override
  public Item item() {
    return RedweaveItems.SIGNAL_CABLE.get();
  }

  /** The cable drops with its color, and a white one with none (answers 539 and 561). */
  @Override
  public ItemStack drop() {
    return SignalCableItem.stackOf(color, 1);
  }

  /** The shape follows what is drawn: the center and the arms (answer 571). */
  @Override
  public VoxelShape shape() {
    return CableShapes.of(face, arms);
  }

  /**
   * The block it rests on and each block it points to get its strength, as from the game wire;
   * while a run reads its inputs, no cable gives anything, so a cable never reads itself back.
   */
  @Override
  public int signal(Direction side) {
    if (strength == 0 || CableInputs.isReading()) {
      return 0;
    }
    return side == face.getOpposite() || arms.containsKey(side.getOpposite()) ? strength : 0;
  }

  /** The game wire beside it gets one less, as from another wire (answer 565). */
  @Override
  public int signal(BlockGetter level, BlockPos pos, Direction side) {
    int given = signal(side);
    boolean wire = level.getBlockState(pos.relative(side.getOpposite())).is(Blocks.REDSTONE_WIRE);
    return wire && given > 0 ? given - 1 : given;
  }

  /** The cable draws with its own pieces, which turn to any direction (answer 572). */
  @Override
  public List<PartLook> looks() {
    return List.of();
  }

  /** The center and each arm, which the client joins into the space (answers 521 and 572). */
  public List<CableLook> pieces() {
    List<CableLook> pieces = new ArrayList<>();
    pieces.add(new CableLook(CENTER, face, CableLinks.across(face).getFirst()));
    arms.forEach((toward, reach) -> pieces.add(new CableLook(reach.model(), face, toward)));
    return List.copyOf(pieces);
  }

  @Override
  public CompoundTag save() {
    CompoundTag tag = new CompoundTag();
    tag.putString(COLOR_TAG, color.key());
    owner.ifPresent(id -> tag.putUUID(OWNER_TAG, id));
    return tag;
  }

  public ChannelColor color() {
    return color;
  }

  /** The strength the run gave it, from 0 to 15, never saved (answers 529 and 567). */
  public int strength() {
    return strength;
  }

  /** The directions across its side where it points, and how far each arm reaches (571). */
  public Map<Direction, CableReach> arms() {
    return Map.copyOf(arms);
  }

  /** The color and the owner that decide which cables join (answers 497 and 528). */
  Insulation insulation() {
    return new Insulation(color, owner.map(PlayerId::new).orElse(NO_OWNER));
  }

  int input() {
    return input;
  }

  void setInput(int read) {
    input = read;
  }

  /** Keeps the strength and tells whether it changed, so only a change wakes the neighbors. */
  boolean setStrength(int next) {
    boolean moved = strength != next;
    strength = next;
    return moved;
  }

  /** Keeps where it points and tells whether that changed. */
  boolean setArms(Map<Direction, CableReach> next) {
    boolean moved = !arms.equals(next);
    arms = next.isEmpty() ? new EnumMap<>(Direction.class) : new EnumMap<>(next);
    return moved;
  }

  @Override
  public void placed(PartSpaceBlockEntity space, Player placer) {
    owner = Optional.of(placer.getUUID());
    space.changed();
    CableRuns.update(space.getLevel(), Set.of(space.spotOf(this)));
  }

  @Override
  public void neighborChanged(PartSpaceBlockEntity space) {
    CableRuns.heard(space.getLevel(), space.spotOf(this), this);
  }

  /** Scheduled when the space loads: the run works the strength out again (answer 529). */
  @Override
  public void tick(PartSpaceBlockEntity space) {
    CableRuns.update(space.getLevel(), Set.of(space.spotOf(this)));
  }

  /** Gone, the cables it joined work their strength out without it. */
  @Override
  public void removed(PartSpaceBlockEntity space) {
    strength = 0;
    CableRuns.removed(space.getLevel(), space.spotOf(this));
  }

  /** In survival, only the owner; a cable without one, as from a damaged save, anyone (526). */
  @Override
  public boolean mayBreak(Player player) {
    return owner.map(player.getUUID()::equals).orElse(true);
  }

  @Override
  public boolean breakBy(PartSpaceBlockEntity space, Player player) {
    return mayBreak(player);
  }

  /** The client gets the strength and the arms, to draw them (answers 522 and 573). */
  @Override
  public void writeClient(CompoundTag tag) {
    tag.putInt(STRENGTH_TAG, strength);
    tag.putInt(ARMS_TAG, CableReach.pack(arms));
  }

  @Override
  public void readClient(CompoundTag tag) {
    strength = tag.getInt(STRENGTH_TAG);
    arms = new EnumMap<>(Direction.class);
    arms.putAll(CableReach.unpack(tag.getInt(ARMS_TAG)));
  }
}
