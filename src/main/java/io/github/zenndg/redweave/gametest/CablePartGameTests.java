package io.github.zenndg.redweave.gametest;

import io.github.zenndg.redweave.RedweaveMod;
import io.github.zenndg.redweave.block.PartSpaceBlockEntity;
import io.github.zenndg.redweave.block.part.CablePart;
import io.github.zenndg.redweave.domain.signal.ChannelColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedstoneLampBlock;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The Signal Cable as a part of its space: a lone cable powers like a cross, a loose end powers
 * where it points, only its owner breaks it, a space holds 2 cables, and the save keeps only the
 * color and the owner (answers 498, 508, 526, 533, 567 and 568).
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class CablePartGameTests {
  private static final String EMPTY = "empty";
  private static final int FULL = 15;
  private static final BlockPos SPACE = new BlockPos(1, 1, 1);
  private static final BlockPos FLOOR = SPACE.below();
  private static final BlockPos WEST_LAMP = SPACE.west();
  private static final BlockPos EAST = SPACE.east();
  private static final BlockPos SOUTH_LAMP = SPACE.south();
  private static final String PARTS = "parts";
  private static final String DATA = "data";
  private static final String COLOR = "color";
  private static final String OWNER = "owner";
  private static final String STRENGTH = "strength";
  private static final String RED = "red";
  private static final String NOT_SAVED = "The cable should keep its color and owner, not more";
  private static final String NOT_TWO = "The space should keep only its 2 cables";
  private static final String BROKEN = "Only the owner should break the cable";

  private CablePartGameTests() {}

  /** A cable with no link powers the block it rests on and the 4 around it (answer 568). */
  @GameTest(template = EMPTY)
  public static void aLoneCablePowersLikeACross(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    helper.setBlock(WEST_LAMP, Blocks.REDSTONE_LAMP);
    helper.setBlock(EAST, Blocks.REDSTONE_LAMP);
    poweredFloorCable(helper, owner);

    helper.succeedWhen(() -> {
      CableScene.assertFloor(helper, SPACE, FULL);
      helper.assertBlockProperty(WEST_LAMP, RedstoneLampBlock.LIT, true);
      helper.assertBlockProperty(EAST, RedstoneLampBlock.LIT, true);
    });
  }

  /** A cable with one link runs on through and powers the block at its loose end (508). */
  @GameTest(template = EMPTY)
  public static void aLooseEndPowersWhereItPoints(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    helper.setBlock(WEST_LAMP, Blocks.REDSTONE_LAMP);
    helper.setBlock(SOUTH_LAMP, Blocks.REDSTONE_LAMP);
    CableScene.floorRow(helper, owner, EAST.below(), 1, ChannelColor.RED);
    poweredFloorCable(helper, owner);

    helper.succeedWhen(() -> {
      CableScene.assertFloor(helper, EAST, FULL - 1);
      helper.assertBlockProperty(WEST_LAMP, RedstoneLampBlock.LIT, true);
      helper.assertBlockProperty(SOUTH_LAMP, RedstoneLampBlock.LIT, false);
    });
  }

  /** The data of a cable keeps its color and its owner, and its strength is never saved (567). */
  @GameTest(template = EMPTY)
  public static void aCableSavesOnlyItsColorAndOwner(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    poweredFloorCable(helper, owner);
    PartSpaceBlockEntity space = helper.getBlockEntity(SPACE);

    helper.succeedWhen(() -> {
      CableScene.assertFloor(helper, SPACE, FULL);
      CompoundTag tag = space.saveWithoutMetadata(helper.getLevel().registryAccess());
      CompoundTag data = tag.getList(PARTS, Tag.TAG_COMPOUND).getCompound(0).getCompound(DATA);
      helper.assertTrue(
          RED.equals(data.getString(COLOR))
              && data.hasUUID(OWNER)
              && owner.getUUID().equals(data.getUUID(OWNER))
              && !data.contains(STRENGTH),
          NOT_SAVED);
    });
  }

  /** A space holds 2 cables; a third one is refused (answers 498 and 533). */
  @GameTest(template = EMPTY)
  public static void aSpaceHoldsTwoCables(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    helper.setBlock(FLOOR, Blocks.STONE);
    helper.setBlock(SPACE.north(), Blocks.STONE);
    helper.setBlock(SPACE.south(), Blocks.STONE);
    CableScene.place(helper, owner, FLOOR, Direction.UP, ChannelColor.RED);
    CableScene.place(helper, owner, SPACE.north(), Direction.SOUTH, ChannelColor.RED);
    CableScene.place(helper, owner, SPACE.south(), Direction.NORTH, ChannelColor.RED);

    PartSpaceBlockEntity space = helper.getBlockEntity(SPACE);
    long cables = space.parts().stream().filter(CablePart.class::isInstance).count();
    helper.assertTrue(cables == 2, NOT_TWO);
    helper.succeed();
  }

  /** In survival, only the owner breaks a cable (answer 526). */
  @GameTest(template = EMPTY)
  public static void onlyTheOwnerBreaksACable(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    Player stranger = helper.makeMockPlayer(GameType.SURVIVAL);
    helper.setBlock(FLOOR, Blocks.STONE);
    CableScene.place(helper, owner, FLOOR, Direction.UP, ChannelColor.RED);

    boolean broke = GameTestActions.breakBy(helper, stranger, SPACE);

    helper.assertFalse(broke, BROKEN);
    CableScene.at(helper, SPACE, Direction.DOWN);
    helper.succeed();
  }

  /** A cable on the floor of the space, on a block of redstone that gives it 15. */
  private static void poweredFloorCable(GameTestHelper helper, Player owner) {
    helper.setBlock(FLOOR, Blocks.REDSTONE_BLOCK);
    CableScene.place(helper, owner, FLOOR, Direction.UP, ChannelColor.RED);
  }
}
