package io.github.zenndg.redweave.gametest;

import io.github.zenndg.redweave.RedweaveMod;
import io.github.zenndg.redweave.block.NetworkControllerBlock;
import io.github.zenndg.redweave.block.NetworkControllerBlockEntity;
import io.github.zenndg.redweave.domain.network.Network;
import io.github.zenndg.redweave.domain.network.NetworkId;
import io.github.zenndg.redweave.domain.network.PlayerId;
import io.github.zenndg.redweave.registry.RedweaveBlocks;
import io.github.zenndg.redweave.registry.RedweaveDataComponents;
import io.github.zenndg.redweave.registry.RedweaveItems;
import io.github.zenndg.redweave.server.RedweaveServer;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** The Network Controller in the world: _docs/funcionalidades/controlador-de-rede.md (62). */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class NetworkControllerGameTests {
  private static final String EMPTY = "empty";
  private static final BlockPos FLOOR = new BlockPos(1, 0, 1);
  private static final BlockPos CONTROLLER = FLOOR.above();
  private static final BlockPos OTHER_FLOOR = new BlockPos(0, 0, 0);
  private static final float EXPLOSION_POWER = 4.0F;
  private static final double CENTER = 0.5;
  private static final String NOT_LINKED = "A placed controller should keep its network";
  private static final String NOT_ONLINE = "The network should be online";
  private static final String STILL_ONLINE = "The network should be offline";
  private static final String STRANGER_BREAKS = "Only the owner should break the controller";
  private static final String NO_DROP = "The owner should get the item with the network";
  private static final String NOT_BACK = "The item should bring the same network back";

  private NetworkControllerGameTests() {}

  @GameTest(template = EMPTY)
  public static void placingTheControllerCreatesAnOnlineNetwork(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NetworkId network = place(helper, owner, controllerItem(), FLOOR);

    helper.assertBlockPresent(RedweaveBlocks.NETWORK_CONTROLLER.get(), CONTROLLER);
    helper.assertTrue(isLinked(helper, CONTROLLER), NOT_LINKED);
    helper.assertTrue(networkOf(network).isOwner(new PlayerId(owner.getUUID())), NOT_LINKED);
    helper.assertTrue(networkOf(network).isOnline(), NOT_ONLINE);
    helper.succeed();
  }

  /** Only a real player creates a network, because only they change it later (answer 58). */
  @GameTest(template = EMPTY)
  public static void aFakePlayerDoesNotPlaceTheController(GameTestHelper helper) {
    Player fake = FakePlayerFactory.getMinecraft(helper.getLevel());
    GameTestActions.placeOn(helper, fake, controllerItem(), FLOOR, Direction.UP);

    helper.assertBlockNotPresent(RedweaveBlocks.NETWORK_CONTROLLER.get(), CONTROLLER);
    helper.succeed();
  }

  /** The owner breaks it and gets the item with the network, which goes offline (24 and 30). */
  @GameTest(template = EMPTY)
  public static void theOwnerBreaksTheControllerAndKeepsTheNetworkInTheItem(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NetworkId network = place(helper, owner, controllerItem(), FLOOR);

    helper.assertTrue(GameTestActions.breakBy(helper, owner, CONTROLLER), STRANGER_BREAKS);

    helper.assertBlockNotPresent(RedweaveBlocks.NETWORK_CONTROLLER.get(), CONTROLLER);
    helper.assertFalse(networkOf(network).isOnline(), STILL_ONLINE);
    helper.assertTrue(droppedNetwork(helper).equals(Optional.of(network)), NO_DROP);
    helper.succeed();
  }

  @GameTest(template = EMPTY)
  public static void theItemWithTheNetworkBringsItBackOnline(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NetworkId network = place(helper, owner, controllerItem(), FLOOR);
    GameTestActions.breakBy(helper, owner, CONTROLLER);
    ItemStack stored = controllerItem();
    stored.set(RedweaveDataComponents.NETWORK.get(), network);

    NetworkId placedAgain = place(helper, owner, stored, OTHER_FLOOR);

    helper.assertTrue(placedAgain.equals(network), NOT_BACK);
    helper.assertTrue(networkOf(network).isOnline(), NOT_ONLINE);
    helper.succeed();
  }

  /** In survival, another player cannot break the controller of the owner (answer 30). */
  @GameTest(template = EMPTY)
  public static void anotherPlayerCannotBreakTheController(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    Player stranger = helper.makeMockPlayer(GameType.SURVIVAL);
    NetworkId network = place(helper, owner, controllerItem(), FLOOR);
    BlockPos absolute = helper.absolutePos(CONTROLLER);
    BlockState state = helper.getLevel().getBlockState(absolute);

    helper.assertTrue(
        state.getDestroyProgress(stranger, helper.getLevel(), absolute) == 0, STRANGER_BREAKS);
    helper.assertFalse(GameTestActions.breakBy(helper, stranger, CONTROLLER), STRANGER_BREAKS);
    helper.assertBlockPresent(RedweaveBlocks.NETWORK_CONTROLLER.get(), CONTROLLER);
    helper.assertTrue(networkOf(network).isOnline(), NOT_ONLINE);
    helper.succeed();
  }

  @GameTest(template = EMPTY)
  public static void anExplosionDoesNotDestroyTheController(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    place(helper, owner, controllerItem(), FLOOR);
    Vec3 center = Vec3.atCenterOf(helper.absolutePos(CONTROLLER));

    helper
        .getLevel()
        .explode(
            null,
            center.x,
            center.y + CENTER,
            center.z,
            EXPLOSION_POWER,
            Level.ExplosionInteraction.TNT);

    helper.assertBlockPresent(RedweaveBlocks.NETWORK_CONTROLLER.get(), CONTROLLER);
    helper.succeed();
  }

  /** Gone without its owner, as by a command, the controller leaves the network offline (49). */
  @GameTest(template = EMPTY)
  public static void aControllerRemovedWithoutTheOwnerTakesTheNetworkOffline(
      GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NetworkId network = place(helper, owner, controllerItem(), FLOOR);

    helper.destroyBlock(CONTROLLER);

    helper.assertFalse(networkOf(network).isOnline(), STILL_ONLINE);
    helper.succeed();
  }

  private static ItemStack controllerItem() {
    return new ItemStack(RedweaveItems.NETWORK_CONTROLLER.get());
  }

  /** Places the item on top of the floor block and returns the network the block keeps. */
  private static NetworkId place(
      GameTestHelper helper, Player player, ItemStack stack, BlockPos floor) {
    GameTestActions.placeOn(helper, player, stack, floor, Direction.UP);
    NetworkControllerBlockEntity controller = helper.getBlockEntity(floor.above());
    return controller.network().orElseThrow();
  }

  private static Optional<NetworkId> droppedNetwork(GameTestHelper helper) {
    AABB around = new AABB(helper.absolutePos(CONTROLLER)).inflate(1);
    return helper.getLevel().getEntitiesOfClass(ItemEntity.class, around).stream()
        .map(item -> item.getItem().get(RedweaveDataComponents.NETWORK.get()))
        .filter(network -> network != null)
        .findFirst();
  }

  private static boolean isLinked(GameTestHelper helper, BlockPos relative) {
    return helper
        .getLevel()
        .getBlockState(helper.absolutePos(relative))
        .getValue(NetworkControllerBlock.LINKED);
  }

  private static Network networkOf(NetworkId id) {
    return server().network(id).orElseThrow();
  }

  private static RedweaveServer server() {
    return RedweaveServer.running().orElseThrow();
  }
}
