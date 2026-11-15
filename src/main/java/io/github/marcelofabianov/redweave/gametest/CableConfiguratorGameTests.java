package io.github.marcelofabianov.redweave.gametest;

import com.mojang.authlib.GameProfile;
import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.PartSpaceBlockEntity;
import io.github.marcelofabianov.redweave.block.part.CablePart;
import io.github.marcelofabianov.redweave.block.part.PartSpot;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.item.ConfiguratorMode;
import io.github.marcelofabianov.redweave.item.SignalCableItem;
import io.github.marcelofabianov.redweave.network.NodeLabelsUpdate;
import io.github.marcelofabianov.redweave.registry.RedweaveBlocks;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The Configurator on a Signal Cable: the Wrench picks it up with its color, only by its owner in
 * survival, Open, Copy and Toggle do nothing, and the label shows its color and strength (answers
 * 525 and 526).
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class CableConfiguratorGameTests {
  private static final String EMPTY = "empty";
  private static final String HOLDER_NAME = "cable_holder";
  private static final int FULL = 15;
  private static final double DROP_REACH = 3;
  private static final BlockPos SPACE = new BlockPos(1, 1, 1);
  private static final BlockPos FLOOR = SPACE.below();
  private static final List<ConfiguratorMode> IDLE =
      List.of(ConfiguratorMode.OPEN, ConfiguratorMode.COPY, ConfiguratorMode.TOGGLE);
  private static final String NOT_PICKED = "The Wrench should pick the cable up";
  private static final String NOT_IN_INVENTORY = "The cable should go to the inventory, red";
  private static final String NOT_DROPPED = "With a full inventory, the cable should drop";
  private static final String PICKED = "Another player should not pick the cable up";
  private static final String TOOK = "Open, Copy and Toggle should do nothing on a cable";
  private static final String NO_LABEL = "The holder should get the color and strength";

  private CableConfiguratorGameTests() {}

  @GameTest(template = EMPTY)
  public static void theWrenchPicksTheCableUpWithItsColor(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    redCable(helper, owner);
    ConfiguratorGameTests.holdConfigurator(owner, ConfiguratorMode.WRENCH);

    NodeScene.click(helper, owner, SPACE);

    helper.assertBlockNotPresent(RedweaveBlocks.PART_SPACE.get(), SPACE);
    helper.assertTrue(
        owner.getInventory().contains(SignalCableItem.stackOf(ChannelColor.RED, 1)),
        NOT_IN_INVENTORY);
    helper.succeed();
  }

  @GameTest(template = EMPTY)
  public static void aFullInventoryDropsTheCable(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    redCable(helper, owner);
    ConfiguratorGameTests.holdConfigurator(owner, ConfiguratorMode.WRENCH);
    fillInventory(owner);

    NodeScene.click(helper, owner, SPACE);

    helper.assertBlockNotPresent(RedweaveBlocks.PART_SPACE.get(), SPACE);
    helper.assertFalse(
        helper
            .getLevel()
            .getEntities(
                EntityType.ITEM,
                owner.getBoundingBox().inflate(DROP_REACH),
                item -> item.getItem().is(RedweaveItems.SIGNAL_CABLE.get()))
            .isEmpty(),
        NOT_DROPPED);
    helper.succeed();
  }

  /** In survival, only the owner picks the cable up (answer 526). */
  @GameTest(template = EMPTY)
  public static void anotherPlayerDoesNotPickTheCableUp(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    Player stranger = helper.makeMockPlayer(GameType.SURVIVAL);
    redCable(helper, owner);
    ConfiguratorGameTests.holdConfigurator(stranger, ConfiguratorMode.WRENCH);

    NodeScene.click(helper, stranger, SPACE);

    CableScene.at(helper, SPACE, Direction.DOWN);
    helper.assertFalse(
        stranger.getInventory().contains(SignalCableItem.stackOf(ChannelColor.RED, 1)), PICKED);
    helper.succeed();
  }

  /** In creative, anyone picks the cable up, as anyone breaks it (answer 526). */
  @GameTest(template = EMPTY)
  public static void aCreativePlayerPicksAnyCableUp(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    Player creative = helper.makeMockPlayer(GameType.CREATIVE);
    redCable(helper, owner);
    ConfiguratorGameTests.holdConfigurator(creative, ConfiguratorMode.WRENCH);

    NodeScene.click(helper, creative, SPACE);

    helper.assertBlockNotPresent(RedweaveBlocks.PART_SPACE.get(), SPACE);
    helper.succeed();
  }

  /** The cable has no screen and no state, so Open, Copy and Toggle do nothing (answer 525). */
  @GameTest(template = EMPTY)
  public static void openCopyAndToggleDoNothingOnACable(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    redCable(helper, owner);

    for (ConfiguratorMode mode : IDLE) {
      ConfiguratorGameTests.holdConfigurator(owner, mode);
      InteractionResult result = NodeScene.click(helper, owner, SPACE);
      helper.assertValueEqual(result, InteractionResult.FAIL, TOOK);
    }

    CableScene.at(helper, SPACE, Direction.DOWN);
    helper.succeed();
  }

  /** The label of the cable shows its color and its strength, as `15 Red · 12` (answer 525). */
  @GameTest(template = EMPTY)
  public static void theHolderGetsTheColorAndStrengthOfTheCable(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    helper.setBlock(FLOOR, Blocks.REDSTONE_BLOCK);
    CableScene.place(helper, owner, FLOOR, Direction.UP, ChannelColor.RED);
    PartSpot spot = new PartSpot(helper.absolutePos(SPACE), Direction.DOWN);
    NodeLabelsUpdate.CableLine expected =
        NodeLabelsUpdate.CableLine.of(
            new NodeLabelsUpdate.Place(spot, false), ChannelColor.RED, FULL);

    helper.succeedWhen(() -> {
      Optional<NodeLabelsUpdate> update =
          RedweaveServer.running().orElseThrow().labelWatch().update(holder(helper));
      Optional<NodeLabelsUpdate.CableLine> line =
          update.flatMap(found ->
              found.cables().stream()
                  .filter(cable -> cable.place().spot().equals(spot))
                  .findFirst());
      helper.assertValueEqual(line, Optional.of(expected), NO_LABEL);
    });
  }

  private static void redCable(GameTestHelper helper, Player owner) {
    helper.setBlock(FLOOR, Blocks.STONE);
    CableScene.place(helper, owner, FLOOR, Direction.UP, ChannelColor.RED);
    PartSpaceBlockEntity space = helper.getBlockEntity(SPACE);
    helper.assertTrue(space.parts().stream().anyMatch(CablePart.class::isInstance), NOT_PICKED);
  }

  /** A new holder each time, so it gets every label in reach. */
  private static ServerPlayer holder(GameTestHelper helper) {
    ServerPlayer holder =
        FakePlayerFactory.get(helper.getLevel(), new GameProfile(UUID.randomUUID(), HOLDER_NAME));
    holder.setPos(Vec3.atCenterOf(helper.absolutePos(SPACE.above())));
    holder.setItemInHand(
        InteractionHand.MAIN_HAND, new ItemStack(RedweaveItems.CONFIGURATOR.get()));
    return holder;
  }

  /** Every slot but the main hand, where the Configurator is, so nothing else fits. */
  private static void fillInventory(Player player) {
    var inventory = player.getInventory();
    for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
      if (slot != inventory.selected) {
        inventory.setItem(slot, new ItemStack(Items.STONE, Items.STONE.getDefaultMaxStackSize()));
      }
    }
  }
}
