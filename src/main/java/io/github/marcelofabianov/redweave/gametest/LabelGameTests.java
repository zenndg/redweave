package io.github.marcelofabianov.redweave.gametest;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.Face;
import io.github.marcelofabianov.redweave.domain.network.NodeCopy;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.registry.RedweaveDataComponents;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** The Label in the world: _docs/funcionalidades/etiqueta.md (answer 121). */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class LabelGameTests {
  private static final String EMPTY = "empty";
  private static final BlockPos CONTROLLER_FLOOR = new BlockPos(0, 0, 2);
  private static final BlockPos CONTROLLER = CONTROLLER_FLOOR.above();
  private static final BlockPos SOURCE_STONE = new BlockPos(0, 1, 0);
  private static final BlockPos SOURCE_NODE = SOURCE_STONE.east();
  private static final BlockPos TARGET_STONE = new BlockPos(2, 1, 2);
  private static final BlockPos TARGET_NODE = TARGET_STONE.north();
  private static final int LABELS = 3;
  private static final int LEFT = 2;
  private static final int GRID = 2;
  private static final NodeCopy OTHER_COPY = new NodeCopy(Face.WEST, List.of());
  private static final String NOT_ONE = "Writing should take one Label from the stack";
  private static final String NOT_WRITTEN = "The written Label should go to the inventory";
  private static final String WRITTEN_OVER = "A written Label should not be written over";
  private static final String NOT_APPLIED = "The Label should apply the copy to the node";
  private static final String SPENT = "Applying should not spend the Label";
  private static final String APPLIED = "Another player should not apply the Label";
  private static final String NOT_COPIED = "The grid should copy the written Label";
  private static final String NOT_CLEARED = "The grid should clear a written Label alone";

  private LabelGameTests() {}

  @GameTest(template = EMPTY)
  public static void writingTakesOneLabelFromTheStack(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    NodeId source = NodeScene.placeNode(helper, owner, SOURCE_STONE, Direction.EAST);
    hold(owner, InteractionHand.MAIN_HAND, new ItemStack(RedweaveItems.CONFIGURATOR.get()));
    ItemStack labels = hold(owner, InteractionHand.OFF_HAND, emptyLabels(LABELS));

    NodeScene.shiftClick(helper, owner, SOURCE_NODE);

    helper.assertTrue(labels.getCount() == LEFT && !written(labels), NOT_ONE);
    NodeCopy expected = NodeCopy.of(NodeScene.networkOf(source).node(source).orElseThrow());
    boolean inInventory =
        owner.getInventory().items.stream()
            .anyMatch(stack -> expected.equals(stack.get(RedweaveDataComponents.NODE_SLOTS.get())));
    helper.assertTrue(inInventory, NOT_WRITTEN);
    helper.succeed();
  }

  /** A written Label in the other hand is never written over (answer 118). */
  @GameTest(template = EMPTY)
  public static void aWrittenLabelIsNotWrittenOver(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    NodeScene.placeNode(helper, owner, SOURCE_STONE, Direction.EAST);
    hold(owner, InteractionHand.MAIN_HAND, new ItemStack(RedweaveItems.CONFIGURATOR.get()));
    ItemStack label = hold(owner, InteractionHand.OFF_HAND, writtenLabel(OTHER_COPY));

    NodeScene.shiftClick(helper, owner, SOURCE_NODE);

    helper.assertTrue(
        OTHER_COPY.equals(label.get(RedweaveDataComponents.NODE_SLOTS.get())), WRITTEN_OVER);
    helper.succeed();
  }

  @GameTest(template = EMPTY)
  public static void applyingDoesNotSpendTheLabel(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    NodeId source = NodeScene.placeNode(helper, owner, SOURCE_STONE, Direction.EAST);
    NodeId target = NodeScene.placeNode(helper, owner, TARGET_STONE, Direction.NORTH);
    NodeCopy copy = NodeCopy.of(NodeScene.networkOf(source).node(source).orElseThrow());
    ItemStack label = hold(owner, InteractionHand.MAIN_HAND, writtenLabel(copy));

    NodeScene.click(helper, owner, TARGET_NODE);

    List<Optional<ChannelSlot>> expected =
        copy.onto(NodeScene.networkOf(target).node(target).orElseThrow()).slots();
    helper.assertTrue(expected.equals(NodeScene.slotsOf(target)), NOT_APPLIED);
    helper.assertTrue(label.getCount() == 1 && written(label), SPENT);
    helper.succeed();
  }

  @GameTest(template = EMPTY)
  public static void anotherPlayerDoesNotApplyTheLabel(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    Player stranger = helper.makeMockPlayer(GameType.SURVIVAL);
    NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    NodeId target = NodeScene.placeNode(helper, owner, TARGET_STONE, Direction.NORTH);
    List<Optional<ChannelSlot>> before = NodeScene.slotsOf(target);
    hold(stranger, InteractionHand.MAIN_HAND, writtenLabel(OTHER_COPY));

    NodeScene.click(helper, stranger, TARGET_NODE);

    helper.assertTrue(before.equals(NodeScene.slotsOf(target)), APPLIED);
    helper.succeed();
  }

  /** The recipes come from the JSON of the datapack, as a pack would see them (answer 117). */
  @GameTest(template = EMPTY)
  public static void theGridCopiesAndClearsALabel(GameTestHelper helper) {
    ItemStack written = writtenLabel(OTHER_COPY);
    CraftingInput copying = CraftingInput.of(GRID, 1, List.of(written, emptyLabels(1)));
    CraftingInput clearing = CraftingInput.of(1, 1, List.of(written));

    ItemStack copied = craft(helper, copying);
    ItemStack cleared = craft(helper, clearing);

    helper.assertTrue(
        OTHER_COPY.equals(copied.get(RedweaveDataComponents.NODE_SLOTS.get())), NOT_COPIED);
    helper.assertTrue(cleared.is(RedweaveItems.LABEL.get()) && !written(cleared), NOT_CLEARED);
    helper.succeed();
  }

  private static ItemStack craft(GameTestHelper helper, CraftingInput input) {
    Optional<RecipeHolder<CraftingRecipe>> recipe =
        helper
            .getLevel()
            .getRecipeManager()
            .getRecipeFor(RecipeType.CRAFTING, input, helper.getLevel());
    return recipe
        .map(holder -> holder.value().assemble(input, helper.getLevel().registryAccess()))
        .orElse(ItemStack.EMPTY);
  }

  private static ItemStack hold(Player player, InteractionHand hand, ItemStack stack) {
    player.setItemInHand(hand, stack);
    return stack;
  }

  private static ItemStack emptyLabels(int count) {
    return new ItemStack(RedweaveItems.LABEL.get(), count);
  }

  private static ItemStack writtenLabel(NodeCopy copy) {
    ItemStack label = emptyLabels(1);
    label.set(RedweaveDataComponents.NODE_SLOTS.get(), copy);
    return label;
  }

  private static boolean written(ItemStack stack) {
    return stack.has(RedweaveDataComponents.NODE_SLOTS.get());
  }
}
