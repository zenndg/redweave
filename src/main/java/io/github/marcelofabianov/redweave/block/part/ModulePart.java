package io.github.marcelofabianov.redweave.block.part;

import com.mojang.logging.LogUtils;
import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.ModuleMessages;
import io.github.marcelofabianov.redweave.block.NodeShapes;
import io.github.marcelofabianov.redweave.block.PartSpaceBlockEntity;
import io.github.marcelofabianov.redweave.block.Plates;
import io.github.marcelofabianov.redweave.domain.module.Connection;
import io.github.marcelofabianov.redweave.domain.module.SignalModule;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.domain.part.PartKind;
import io.github.marcelofabianov.redweave.registry.RedweaveBlocks;
import io.github.marcelofabianov.redweave.server.ModuleServices;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.slf4j.Logger;

/**
 * The plate every Signal Module shares, now a part of a space (answers 511 and 516): it rests on a
 * side, points its front where the player looks, feeds only the block in front with strength,
 * turns, belongs to its placer and teaches the empty hand (answers 394, 399, 403, 407, 415, 416 and
 * 428). Its module, with the owner, the name and the network, is in the NBT of answer 411, with the
 * `front` and the lit arrow, `powered`, of its old block; the `facing` comes from the side (answers
 * 534 and 559). Each module adds its rule, its connections and its screen.
 */
public abstract class ModulePart
    implements Part, LivePart, TickingPart, OwnedPart, HandPart, HostedPart {
  static final String FRONT_TAG = "front";
  static final String POWERED_TAG = "powered";
  private static final String TURNED = "_turned";
  private static final String BLOCK_FOLDER = "block/";
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String DROPPED = "Dropped an invalid front of a Signal Module: {}";

  private final Direction face;
  private Direction front;
  private boolean powered;
  private Optional<SignalModule> module = Optional.empty();
  private int emitted;
  private Optional<PartSpaceBlockEntity> host = Optional.empty();

  protected ModulePart(Direction face) {
    this.face = face;
    this.front = PlateTurn.acrossOf(face.getOpposite());
  }

  /** The models of every look of a module, with the top turned or not (answer 429). */
  public static List<ResourceLocation> models(String id, List<String> looks) {
    List<ResourceLocation> models = new ArrayList<>();
    for (String look : looks) {
      models.add(model(id + look));
      models.add(model(id + look + TURNED));
    }
    return List.copyOf(models);
  }

  /** The module part at the spot, of the type, if the space is loaded and has one there. */
  public static <T extends ModulePart> Optional<T> at(Level level, PartSpot spot, Class<T> type) {
    return PartSpaceBlockEntity.at(level, spot.pos())
        .flatMap(space -> space.at(spot.face()))
        .filter(type::isInstance)
        .map(type::cast);
  }

  private static ResourceLocation model(String name) {
    return ResourceLocation.fromNamespaceAndPath(RedweaveMod.MOD_ID, BLOCK_FOLDER + name);
  }

  /** Reads what every module keeps; an empty tag is a module just placed, with nothing yet. */
  protected final void loadPlate(CompoundTag tag) {
    if (tag.isEmpty()) {
      return;
    }
    module = ModuleSave.readModule(tag);
    powered = tag.getBoolean(POWERED_TAG);
    Optional<Direction> saved =
        Optional.ofNullable(Direction.byName(tag.getString(FRONT_TAG)))
            .filter(found -> found.getAxis() != face.getAxis());
    if (saved.isEmpty()) {
      LOGGER.warn(DROPPED, tag.getString(FRONT_TAG));
    }
    front = saved.orElse(front);
  }

  /** The ID of the module, the name of its item and of its models. */
  protected abstract String id();

  /** The suffix of the model of the look of now, as the states of the old block (answer 534). */
  protected abstract String lookSuffix();

  /** The module just placed, with its rule and connections as new (answers 390 and 416). */
  protected abstract void placeRule();

  /** One tick of the rule, between the reading of the input and the output (answer 415). */
  protected abstract void tickRule(ModuleServices modules, PartSpaceBlockEntity space);

  protected abstract void saveRule(CompoundTag tag);

  /** The `Toggle` of the Configurator, with the message of the action bar, if any (395). */
  public abstract Optional<Component> toggle();

  /** The output, whose channel the label shows and the network counts (answers 417 and 424). */
  public abstract Connection output();

  /** The key of the text of the state, which the client writes in its language (417). */
  public abstract Optional<String> stateKey();

  /** Opens the screen of the module for the player (answer 393). */
  public abstract void openScreen(ServerPlayer viewer);

  @Override
  public Direction face() {
    return face;
  }

  /** The face of the block of support the plate sticks on, pointing away from it (428). */
  public Direction facing() {
    return face.getOpposite();
  }

  /** Where the front edge points in the world, always across `facing` (answer 428). */
  public Direction front() {
    return front;
  }

  @Override
  public PartKind kind() {
    return PartKind.PLATE;
  }

  @Override
  public VoxelShape shape() {
    return NodeShapes.of(facing());
  }

  /** Only the block in front gets the signal, with strength, as from a repeater (407). */
  @Override
  public int signal(Direction side) {
    return side == front.getOpposite() ? emitted : 0;
  }

  @Override
  public PartLook look() {
    PlateTurn turn = PlateTurn.of(facing(), front);
    return new PartLook(
        model(id() + lookSuffix() + (turn.turned() ? TURNED : ModuleLooks.NONE)), face, turn);
  }

  @Override
  public CompoundTag save() {
    CompoundTag tag = new CompoundTag();
    module.ifPresent(found -> ModuleSave.writeModule(tag, found));
    tag.putString(FRONT_TAG, front.getSerializedName());
    tag.putBoolean(POWERED_TAG, powered);
    saveRule(tag);
    return tag;
  }

  public Optional<SignalModule> module() {
    return module;
  }

  /** Whether the arrow is lit: the front edge gives a signal (answers 401 and 452). */
  public boolean powered() {
    return powered;
  }

  /** A plate without a valid module, as from a damaged save, has no owner to protect it. */
  public boolean isOwner(Player player) {
    return module.map(found -> found.isOwner(new PlayerId(player.getUUID()))).orElse(true);
  }

  /** Saves the space and sends it to the clients, after the module changed from the screen. */
  public void changed() {
    host.ifPresent(PartSpaceBlockEntity::changed);
  }

  /** Saves the space without sending it, as after a tick that changes nothing in sight. */
  protected void markDirty() {
    host.ifPresent(PartSpaceBlockEntity::setChanged);
  }

  /** The spot of the plate, once it is in a space. */
  protected PartSpot spot() {
    return host.map(space -> space.spotOf(this)).orElseThrow();
  }

  protected Optional<Level> level() {
    return host.map(PartSpaceBlockEntity::getLevel);
  }

  /** The network of the module was deleted: it has none until its owner picks another (385). */
  void loseNetwork() {
    module.ifPresent(SignalModule::loseNetwork);
    markDirty();
  }

  /** What the front edge gives now, not saved, as the next tick works it out again. */
  int emitted() {
    return emitted;
  }

  void setEmitted(int strength) {
    emitted = strength;
  }

  void setPowered(boolean on) {
    powered = on;
  }

  @Override
  public void attach(PartSpaceBlockEntity space) {
    host = Optional.of(space);
  }

  /**
   * The front points where the player looks, laid on the face, as a repeater (answer 399), and
   * whoever places the module owns it (answer 416).
   */
  @Override
  public void placed(PartSpaceBlockEntity space, Player placer) {
    attach(space);
    front =
        Arrays.stream(Direction.orderedByNearest(placer))
            .filter(direction -> direction.getAxis() != face.getAxis())
            .findFirst()
            .orElse(front);
    RedweaveServer.running()
        .ifPresent(server -> {
          placeRule();
          module = Optional.of(server.modules().place().place(new PlayerId(placer.getUUID())));
        });
    space.changed();
  }

  @Override
  public void neighborChanged(PartSpaceBlockEntity space) {}

  @Override
  public void tick(PartSpaceBlockEntity space) {}

  /** Each tick: the rule, and the clients see the plate again only when its look changed. */
  @Override
  public void serverTick(PartSpaceBlockEntity space) {
    Optional<ModuleServices> modules = ModuleTicks.start(space, this);
    if (modules.isEmpty()) {
      return;
    }
    PartLook before = look();
    tickRule(modules.get(), space);
    if (!look().equals(before)) {
      space.changed();
    }
  }

  /** Gone by any means, the front edge goes to 0 at once (answer 394). */
  @Override
  public void removed(PartSpaceBlockEntity space) {
    emitted = 0;
    wakeFront(space.getLevel(), space.getBlockPos(), front);
    space.wakeOthers(this);
  }

  @Override
  public boolean mayBreak(Player player) {
    return isOwner(player);
  }

  /** In survival, only the owner breaks the plate; the server has the last word (394). */
  @Override
  public boolean breakBy(PartSpaceBlockEntity space, Player player) {
    return isOwner(player);
  }

  /** An empty hand does not open the module, and the action bar teaches how (answer 395). */
  @Override
  public void emptyHand(Player player) {
    player.displayClientMessage(Component.translatable(ModuleMessages.USE_TO_OPEN), true);
  }

  /**
   * Turns the plate 90° clockwise, seen from its front, with the click of a comparator (answers
   * 399, 403 and 432). The old front loses the signal at once, and the next tick feeds the new one.
   */
  public Direction turn() {
    Level level = level().orElseThrow();
    BlockPos pos = spot().pos();
    emitted = 0;
    wakeFront(level, pos, front);
    front = Plates.clockwise(facing(), front);
    host.ifPresent(space -> space.wakeOthers(this));
    changed();
    level.playSound(null, pos, SoundEvents.COMPARATOR_CLICK, SoundSource.BLOCKS);
    return front;
  }

  /** The block in front and its neighbors, as a repeater wakes them, if it is loaded (rule 6). */
  static void wakeFront(Level level, BlockPos pos, Direction front) {
    BlockPos ahead = pos.relative(front);
    if (level.isLoaded(ahead)) {
      Block space = RedweaveBlocks.PART_SPACE.get();
      level.neighborChanged(ahead, space, pos);
      level.updateNeighborsAtExceptFromFacing(ahead, space, front.getOpposite());
    }
  }
}
