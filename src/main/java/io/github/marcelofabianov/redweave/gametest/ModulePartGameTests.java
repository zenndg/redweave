package io.github.marcelofabianov.redweave.gametest;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.PartSpaceBlockEntity;
import io.github.marcelofabianov.redweave.block.part.InverterPart;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The Signal Modules as parts: a module shares its space with a node and feeds it as a neighbor,
 * and it keeps its `front` in its NBT, but not its `facing`, which its side gives (answers 514, 534
 * and 559).
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class ModulePartGameTests {
  private static final String EMPTY = "empty";
  private static final BlockPos CONTROLLER_FLOOR = new BlockPos(0, 0, 2);
  private static final BlockPos CONTROLLER = CONTROLLER_FLOOR.above();
  private static final BlockPos STONE = new BlockPos(1, 1, 1);
  private static final BlockPos SPACE = STONE.above();
  private static final BlockPos WALL = SPACE.north();
  private static final float LOOK_DOWN = 90;
  private static final ChannelColor CHANNEL = ChannelColor.WHITE;
  private static final String PARTS = "parts";
  private static final String DATA = "data";
  private static final String FRONT = "front";
  private static final String FACING = "facing";
  private static final String NOT_SHARED = "The node and the inverter should share the space";
  private static final String NOT_READ = "The node should read the inverter of its space";
  private static final String NO_FRONT = "The module should keep its front in its data";
  private static final String HAS_FACING = "The module should not keep its facing (559)";

  private ModulePartGameTests() {}

  /**
   * A node on the floor and an inverter on the wall of the same space, its front down: the inverter
   * feeds the stone under the node, and the node reads it into its channel (514).
   */
  @GameTest(template = EMPTY)
  public static void aModuleFeedsTheSupportOfANodeInItsSpace(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    NodeId node = NodeScene.placeNode(helper, owner, STONE, Direction.UP);
    NodeScene.enableSlot(owner, node, CHANNEL.ordinal());
    placeInverterLookingDown(helper, owner);

    helper.assertTrue(
        NodeParts.all(helper, SPACE).size() == 1
            && ModuleParts.find(helper, SPACE, InverterPart.class).isPresent(),
        NOT_SHARED);
    helper.succeedWhen(() -> {
      ModuleParts.assertFront(helper, SPACE, Direction.DOWN);
      helper.assertValueEqual(
          NodeScene.networkOf(node).channelValue(CHANNEL), Signal.MAX, NOT_READ);
    });
  }

  /** The save of the space keeps the `front` of a module, and no `facing` (answer 559). */
  @GameTest(template = EMPTY)
  public static void aModuleSavesItsFrontButNotItsFacing(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    placeInverterLookingDown(helper, owner);
    PartSpaceBlockEntity space = helper.getBlockEntity(SPACE);

    CompoundTag tag = space.saveWithoutMetadata(helper.getLevel().registryAccess());
    CompoundTag data = tag.getList(PARTS, Tag.TAG_COMPOUND).getCompound(0).getCompound(DATA);
    helper.assertValueEqual(data.getString(FRONT), Direction.DOWN.getSerializedName(), NO_FRONT);
    helper.assertFalse(data.contains(FACING), HAS_FACING);
    helper.succeed();
  }

  private static void placeInverterLookingDown(GameTestHelper helper, Player owner) {
    helper.setBlock(WALL, Blocks.STONE);
    owner.setXRot(LOOK_DOWN);
    GameTestActions.placeOn(
        helper, owner, new ItemStack(RedweaveItems.SIGNAL_INVERTER.get()), WALL, Direction.SOUTH);
  }
}
