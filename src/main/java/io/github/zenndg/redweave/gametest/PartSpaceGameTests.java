package io.github.zenndg.redweave.gametest;

import io.github.zenndg.redweave.RedweaveMod;
import io.github.zenndg.redweave.block.PartSpaceBlockEntity;
import io.github.zenndg.redweave.block.part.NodePart;
import io.github.zenndg.redweave.block.part.Part;
import io.github.zenndg.redweave.registry.RedweaveItems;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The part space in the world: parts share one block space, one per side, only the aimed part
 * breaks, a taken side refuses, and the space keeps its parts in its NBT (answers 513, 515, 533,
 * 539, 543 and 545).
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class PartSpaceGameTests {
  private static final String EMPTY = "empty";
  private static final BlockPos SPACE = new BlockPos(1, 1, 1);
  private static final BlockPos FLOOR = SPACE.below();
  private static final BlockPos WALL = SPACE.north();
  private static final float DOWN = 90;
  private static final double ABOVE_THE_FLOOR = 0.2;
  private static final double CENTER = 0.5;
  private static final double DROP_RADIUS = 2;
  private static final String PARTS = "parts";
  private static final String TYPE = "type";
  private static final String FACE = "face";
  private static final String DATA = "data";
  private static final String NODE = "node";
  private static final String NO_ID = "";
  private static final String UNKNOWN_TYPE = "redweave:no_such_part";
  private static final String NOT_TWO = "The space should hold both nodes";
  private static final String TAKEN = "A taken side should keep the item in the hand";
  private static final String WRONG_BREAK = "Only the aimed node should break";
  private static final String NOT_SAVED = "Each part should keep its type, face and data";
  private static final String NOT_LOADED = "The space should load the same parts";

  private PartSpaceGameTests() {}

  @GameTest(template = EMPTY)
  public static void twoNodesShareOneSpace(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);

    floorAndWall(helper, owner);

    helper.assertTrue(facesOf(helper).equals(Set.of(Direction.DOWN, Direction.NORTH)), NOT_TWO);
    helper.succeed();
  }

  @GameTest(template = EMPTY)
  public static void aTakenSideRefusesAnotherPart(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    helper.setBlock(FLOOR, Blocks.STONE);
    placeNode(helper, owner, FLOOR, Direction.UP);
    ItemStack second = new ItemStack(RedweaveItems.SIGNAL_NODE.get());

    GameTestActions.placeOn(helper, owner, second, FLOOR, Direction.UP);

    helper.assertTrue(NodeParts.all(helper, SPACE).size() == 1 && second.getCount() == 1, TAKEN);
    helper.succeed();
  }

  @GameTest(template = EMPTY)
  public static void onlyTheAimedPartBreaks(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    floorAndWall(helper, owner);
    Vec3 above = Vec3.atLowerCornerOf(helper.absolutePos(SPACE));
    owner.setPos(above.add(CENTER, ABOVE_THE_FLOOR, CENTER));
    owner.setXRot(DOWN);

    boolean spaceGone = GameTestActions.breakBy(helper, owner, SPACE);

    helper.assertTrue(!spaceGone && facesOf(helper).equals(Set.of(Direction.NORTH)), WRONG_BREAK);
    helper.assertItemEntityPresent(RedweaveItems.SIGNAL_NODE.get(), SPACE, DROP_RADIUS);
    helper.succeed();
  }

  @GameTest(template = EMPTY)
  public static void theSpaceSavesItsParts(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    floorAndWall(helper, owner);
    PartSpaceBlockEntity space = helper.getBlockEntity(SPACE);
    HolderLookup.Provider registries = helper.getLevel().registryAccess();

    CompoundTag tag = space.saveWithoutMetadata(registries);
    ListTag parts = tag.getList(PARTS, Tag.TAG_COMPOUND);
    helper.assertTrue(parts.size() == 2 && savedAsNodes(parts), NOT_SAVED);

    parts.add(unknownPart());
    PartSpaceBlockEntity loaded =
        new PartSpaceBlockEntity(space.getBlockPos(), space.getBlockState());
    loaded.loadWithComponents(tag, registries);
    helper.assertTrue(nodesOf(loaded).equals(nodesOf(space)), NOT_LOADED);
    helper.succeed();
  }

  private static void floorAndWall(GameTestHelper helper, Player owner) {
    helper.setBlock(FLOOR, Blocks.STONE);
    helper.setBlock(WALL, Blocks.STONE);
    placeNode(helper, owner, FLOOR, Direction.UP);
    placeNode(helper, owner, WALL, Direction.SOUTH);
  }

  private static void placeNode(
      GameTestHelper helper, Player owner, BlockPos block, Direction face) {
    ItemStack item = new ItemStack(RedweaveItems.SIGNAL_NODE.get());
    GameTestActions.placeOn(helper, owner, item, block, face);
  }

  private static Set<Direction> facesOf(GameTestHelper helper) {
    return NodeParts.all(helper, SPACE).stream().map(Part::face).collect(Collectors.toSet());
  }

  private static boolean savedAsNodes(ListTag parts) {
    String type = RedweaveItems.SIGNAL_NODE.getId().toString();
    for (int index = 0; index < parts.size(); index++) {
      CompoundTag entry = parts.getCompound(index);
      boolean valid =
          type.equals(entry.getString(TYPE))
              && Direction.byName(entry.getString(FACE)) != null
              && entry.getCompound(DATA).hasUUID(NODE);
      if (!valid) {
        return false;
      }
    }
    return true;
  }

  private static CompoundTag unknownPart() {
    CompoundTag entry = new CompoundTag();
    entry.putString(TYPE, UNKNOWN_TYPE);
    entry.putString(FACE, Direction.UP.getSerializedName());
    entry.put(DATA, new CompoundTag());
    return entry;
  }

  private static List<String> nodesOf(PartSpaceBlockEntity space) {
    return space.parts().stream()
        .filter(NodePart.class::isInstance)
        .map(part -> part.face() + ((NodePart) part).node().map(Object::toString).orElse(NO_ID))
        .sorted()
        .toList();
  }
}
