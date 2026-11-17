package io.github.marcelofabianov.redweave.gametest;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.part.InverterPart;
import io.github.marcelofabianov.redweave.domain.module.Connection;
import io.github.marcelofabianov.redweave.domain.module.InverterState;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.item.ConfiguratorMode;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedstoneLampBlock;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The Signal Inverter in the world: _docs/funcionalidades/signal-inverter.md, its placing, its
 * owner, its `Toggle` and its save (answers 442, 443, 450 and 453).
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class SignalInverterGameTests {
  private static final String EMPTY = "empty";
  private static final BlockPos STONE = new BlockPos(1, 1, 1);
  private static final BlockPos INVERTER = STONE.above();
  private static final BlockPos LAMP_IN_FRONT = INVERTER.north();
  private static final float LOOK_NORTH = 180;
  private static final int BURNOUT = 120;
  private static final int DAMAGED_BURNOUT = -5;
  private static final int SEVEN = 7;
  private static final int TWO_SECONDS = 40;
  private static final int WAITING = 12;
  private static final String NAME = "Lamp switch";
  private static final String RED = "red";
  private static final String YELLOW = "yellow";
  private static final UUID MODULE = UUID.fromString("6a7b8c9d-0e1f-4a2b-8c3d-4e5f6a7b8c9d");
  private static final UUID OWNER = UUID.fromString("7b8c9d0e-1f2a-4b3c-9d4e-5f6a7b8c9d0e");
  private static final UUID NETWORK = UUID.fromString("8c9d0e1f-2a3b-4c4d-8e5f-6a7b8c9d0e1f");
  private static final String NOT_OWNER = "Whoever places the inverter should own it";
  private static final String NOT_EDGES = "A new inverter should use its edges";
  private static final String NOT_ON = "A new inverter should emit at once";
  private static final String TOGGLED = "The Toggle should change nothing on an inverter";
  private static final String NO_WARNING = "The Toggle should warn there is nothing to toggle";
  private static final String BROKEN = "Only the owner should break the inverter";
  private static final String NOT_KEPT = "The save of the inverter should come back whole";
  private static final String NOT_SAFE = "A damaged burnout should fall back to none";

  private SignalInverterGameTests() {}

  /** Placed with no signal behind, it emits 15 at once, as a torch (answers 441 and 442). */
  @GameTest(template = EMPTY)
  public static void aPlacedInverterEmitsAtOnce(GameTestHelper helper) {
    Player player = helper.makeMockPlayer(GameType.SURVIVAL);
    helper.setBlock(LAMP_IN_FRONT, Blocks.REDSTONE_LAMP);
    place(helper, player);

    InverterPart placed = ModuleParts.only(helper, INVERTER, InverterPart.class);
    helper.assertTrue(
        placed.module().orElseThrow().isOwner(new PlayerId(player.getUUID())), NOT_OWNER);
    helper.assertValueEqual(placed.input(), Connection.EDGE, NOT_EDGES);
    helper.assertValueEqual(placed.output(), Connection.EDGE, NOT_EDGES);
    helper.succeedWhen(() -> {
      ModuleParts.assertPowered(helper, INVERTER, true);
      helper.assertBlockProperty(LAMP_IN_FRONT, RedstoneLampBlock.LIT, true);
      helper.assertValueEqual(placed.inverter().state(), InverterState.ON, NOT_ON);
    });
  }

  /** Another player cannot break the plate in survival; its owner can (answers 378 and 394). */
  @GameTest(template = EMPTY)
  public static void onlyTheOwnerBreaksTheInverter(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    Player stranger = helper.makeMockPlayer(GameType.SURVIVAL);
    place(helper, owner);

    helper.assertFalse(GameTestActions.breakBy(helper, stranger, INVERTER), BROKEN);
    ModuleParts.assertPresent(helper, INVERTER);
    helper.assertTrue(GameTestActions.breakBy(helper, owner, INVERTER), BROKEN);
    helper.succeed();
  }

  /** The Toggle has nothing to change and says so (answers 443 and 447). */
  @GameTest(template = EMPTY)
  public static void theToggleChangesNothing(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    place(helper, owner);
    ConfiguratorGameTests.holdConfigurator(owner, ConfiguratorMode.TOGGLE);
    InverterPart inverter = ModuleParts.only(helper, INVERTER, InverterPart.class);

    helper
        .startSequence()
        .thenWaitUntil(() -> ModuleParts.assertPowered(helper, INVERTER, true))
        .thenExecute(() -> NodeScene.click(helper, owner, INVERTER))
        .thenExecute(() -> helper.assertTrue(inverter.toggle().isPresent(), NO_WARNING))
        .thenExecute(
            () -> helper.assertValueEqual(inverter.inverter().state(), InverterState.ON, TOGGLED))
        .thenExecute(() -> ModuleParts.assertPowered(helper, INVERTER, true))
        .thenSucceed();
  }

  /** Its connections, burnout, strength, delay and line come back whole (450, 836, rule 15). */
  @GameTest(template = EMPTY)
  public static void anInverterKeepsItsSaveWhole(GameTestHelper helper) {
    CompoundTag saved = fullSave(BURNOUT);

    InverterPart loaded = load(saved);

    CompoundTag written = loaded.save();
    helper.assertValueEqual(written, saved, NOT_KEPT);
    helper.succeed();
  }

  /** A negative burnout is dropped with a warning, and the inverter works (rule 14). */
  @GameTest(template = EMPTY)
  public static void aDamagedBurnoutFallsBackToNone(GameTestHelper helper) {
    InverterPart loaded = load(fullSave(DAMAGED_BURNOUT));

    helper.assertValueEqual(loaded.inverter().burnoutTicks(), 0, NOT_SAFE);
    helper.succeed();
  }

  private static void place(GameTestHelper helper, Player player) {
    helper.setBlock(STONE, Blocks.STONE);
    player.setYHeadRot(LOOK_NORTH);
    GameTestActions.placeOn(
        helper, player, new ItemStack(RedweaveItems.SIGNAL_INVERTER.get()), STONE, Direction.UP);
  }

  /** The part on the floor, read from its tag as the space reads it (answer 543). */
  private static InverterPart load(CompoundTag tag) {
    return InverterPart.load(Direction.DOWN, tag);
  }

  /**
   * The save of a burnt inverter with a name, a network, both connections on channels, a strength,
   * a delay and a change waiting in its line (answers 450, 834, 836 and 844).
   */
  private static CompoundTag fullSave(int burnout) {
    CompoundTag tag = new CompoundTag();
    tag.putUUID(Keys.MODULE_ID, MODULE);
    tag.putUUID(Keys.OWNER, OWNER);
    tag.putString(Keys.NAME, NAME);
    tag.putUUID(Keys.NETWORK, NETWORK);
    tag.putString(Keys.INPUT, YELLOW);
    tag.putString(Keys.OUTPUT, RED);
    tag.putInt(Keys.BURNOUT, burnout);
    tag.putInt(Keys.STRENGTH, SEVEN);
    tag.putInt(Keys.DELAY, TWO_SECONDS);
    ListTag line = new ListTag();
    CompoundTag change = new CompoundTag();
    change.putBoolean(Keys.ON, false);
    change.putInt(Keys.TICKS, WAITING);
    line.add(change);
    tag.put(Keys.LINE, line);
    tag.putString(Keys.FRONT, Direction.NORTH.getSerializedName());
    tag.putBoolean(Keys.POWERED, true);
    return tag;
  }

  /** The keys of answer 450, written here again, so a renamed key fails the test (rule 15). */
  private static final class Keys {
    static final String FRONT = "front";
    static final String POWERED = "powered";
    static final String MODULE_ID = "module_id";
    static final String OWNER = "owner";
    static final String NAME = "name";
    static final String NETWORK = "network";
    static final String INPUT = "input";
    static final String OUTPUT = "output";
    static final String BURNOUT = "burnout_ticks";
    static final String STRENGTH = "strength";
    static final String DELAY = "output_delay_ticks";
    static final String LINE = "delay_line";
    static final String ON = "on";
    static final String TICKS = "ticks";

    private Keys() {}
  }
}
