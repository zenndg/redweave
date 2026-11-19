package io.github.zenndg.redweave.gametest;

import com.mojang.authlib.GameProfile;
import io.github.zenndg.redweave.RedweaveMod;
import io.github.zenndg.redweave.domain.network.NetworkId;
import io.github.zenndg.redweave.domain.network.NodeId;
import io.github.zenndg.redweave.network.NodeLabelsUpdate;
import io.github.zenndg.redweave.registry.RedweaveItems;
import io.github.zenndg.redweave.server.RedweaveServer;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The labels over the nodes and the Network Controllers go only to the player who holds the
 * Configurator, only for what is within reach, and only what changed (configurador.md, answers 268,
 * 269, 272 and 773).
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class NodeLabelGameTests {
  private static final String EMPTY = "empty";
  private static final String HOLDER_NAME = "holder";
  private static final BlockPos CONTROLLER_FLOOR = new BlockPos(0, 0, 2);
  private static final BlockPos CONTROLLER = CONTROLLER_FLOOR.above();
  private static final BlockPos STONE = new BlockPos(1, 1, 1);
  private static final double FAR_AWAY = 200;
  private static final String NOT_SENT = "The holder of the Configurator should get the label";
  private static final String SENT_AGAIN = "A label that did not change should not go again";
  private static final String SENT_WITHOUT = "Without the Configurator no label should go";
  private static final String NOT_CLEARED = "Letting the Configurator go should clear the labels";
  private static final String SENT_FAR = "A node beyond the reach should have no label";
  private static final String NO_CONTROLLER = "The holder should get the label of the controller";
  private static final String CONTROLLER_AGAIN = "An unchanged controller should not go again";
  private static final String CONTROLLER_FAR = "A controller beyond the reach should have no label";
  private static final String NO_SLOT = "The label should have the color of the enabled slot";
  private static final int SLOT = 3;

  private NodeLabelGameTests() {}

  @GameTest(template = EMPTY)
  public static void theHolderGetsTheLabelOnce(GameTestHelper helper) {
    NodeId node = nodeOnANetwork(helper);
    ServerPlayer holder = holder(helper);
    hold(holder, configurator());

    Optional<NodeLabelsUpdate> first = server().labelWatch().update(holder);

    helper.assertTrue(first.map(update -> has(update, node)).orElse(false), NOT_SENT);
    helper.assertTrue(server().labelWatch().update(holder).isEmpty(), SENT_AGAIN);
    helper.succeed();
  }

  @GameTest(template = EMPTY)
  public static void withoutTheConfiguratorNoLabelGoes(GameTestHelper helper) {
    nodeOnANetwork(helper);
    ServerPlayer holder = holder(helper);
    hold(holder, ItemStack.EMPTY);

    helper.assertTrue(server().labelWatch().update(holder).isEmpty(), SENT_WITHOUT);
    helper.succeed();
  }

  /** Letting the Configurator go clears what the client shows. */
  @GameTest(template = EMPTY)
  public static void lettingGoClearsTheLabels(GameTestHelper helper) {
    nodeOnANetwork(helper);
    ServerPlayer holder = holder(helper);
    hold(holder, configurator());
    server().labelWatch().update(holder);

    hold(holder, ItemStack.EMPTY);
    Optional<NodeLabelsUpdate> after = server().labelWatch().update(holder);

    helper.assertTrue(after.map(NodeLabelsUpdate::clearAll).orElse(false), NOT_CLEARED);
    helper.succeed();
  }

  @GameTest(template = EMPTY)
  public static void aNodeBeyondTheReachHasNoLabel(GameTestHelper helper) {
    NodeId node = nodeOnANetwork(helper);
    ServerPlayer holder = holder(helper);
    holder.setPos(holder.position().add(FAR_AWAY, 0, FAR_AWAY));
    hold(holder, configurator());

    Optional<NodeLabelsUpdate> update = server().labelWatch().update(holder);

    helper.assertFalse(update.map(found -> has(found, node)).orElse(false), SENT_FAR);
    helper.succeed();
  }

  @GameTest(template = EMPTY)
  public static void theHolderGetsTheLabelOfTheControllerOnce(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NetworkId network = NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    ServerPlayer holder = holder(helper);
    hold(holder, configurator());

    Optional<NodeLabelsUpdate> first = server().labelWatch().update(holder);

    helper.assertTrue(first.map(update -> has(update, network)).orElse(false), NO_CONTROLLER);
    helper.assertTrue(server().labelWatch().update(holder).isEmpty(), CONTROLLER_AGAIN);
    helper.succeed();
  }

  @GameTest(template = EMPTY)
  public static void aControllerBeyondTheReachHasNoLabel(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NetworkId network = NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    ServerPlayer holder = holder(helper);
    holder.setPos(holder.position().add(FAR_AWAY, 0, FAR_AWAY));
    hold(holder, configurator());

    Optional<NodeLabelsUpdate> update = server().labelWatch().update(holder);

    helper.assertFalse(update.map(found -> has(found, network)).orElse(false), CONTROLLER_FAR);
    helper.succeed();
  }

  /** The label of a node goes with the color of its first enabled slot (answer 772). */
  @GameTest(template = EMPTY)
  public static void theLabelHasTheColorOfTheEnabledSlot(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    NodeId node = NodeScene.placeNode(helper, owner, STONE, Direction.UP);
    NodeScene.enableSlot(owner, node, SLOT);
    ServerPlayer holder = holder(helper);
    hold(holder, configurator());

    Optional<NodeLabelsUpdate> update = server().labelWatch().update(holder);

    boolean colored =
        update.stream()
            .flatMap(found -> found.labels().stream())
            .anyMatch(label -> label.node().equals(node.value()) && label.slot() == SLOT);
    helper.assertTrue(colored, NO_SLOT);
    helper.succeed();
  }

  private static NodeId nodeOnANetwork(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    return NodeScene.placeNode(helper, owner, STONE, Direction.UP);
  }

  private static boolean has(NodeLabelsUpdate update, NodeId node) {
    return update.labels().stream().anyMatch(label -> label.node().equals(node.value()));
  }

  private static boolean has(NodeLabelsUpdate update, NetworkId network) {
    return update.controllers().stream()
        .anyMatch(controller -> controller.network().equals(network.value()));
  }

  private static ServerPlayer holder(GameTestHelper helper) {
    ServerPlayer holder =
        FakePlayerFactory.get(helper.getLevel(), new GameProfile(UUID.randomUUID(), HOLDER_NAME));
    holder.setPos(Vec3.atCenterOf(helper.absolutePos(STONE.above())));
    return holder;
  }

  private static void hold(ServerPlayer player, ItemStack stack) {
    player.setItemInHand(InteractionHand.MAIN_HAND, stack);
  }

  private static ItemStack configurator() {
    return new ItemStack(RedweaveItems.CONFIGURATOR.get());
  }

  private static RedweaveServer server() {
    return RedweaveServer.running().orElseThrow();
  }
}
