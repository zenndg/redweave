package io.github.marcelofabianov.redweave.gametest;

import com.mojang.authlib.GameProfile;
import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.Direction;
import io.github.marcelofabianov.redweave.domain.network.Face;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import io.github.marcelofabianov.redweave.menu.ControllerMenu;
import io.github.marcelofabianov.redweave.menu.ControllerScreenLog;
import io.github.marcelofabianov.redweave.menu.ControllerScreenLog.LogLine;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import io.netty.buffer.Unpooled;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.RedstoneLampBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The log of the Controller Screen in the world: a lever and a lamp make one line each, newest
 * first, and an open screen receives only the new lines (telas.md, answers 147, 243 and 247).
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class ControllerLogGameTests {
  private static final String EMPTY = "empty";
  private static final String VIEWER_NAME = "viewer";
  private static final String CLIENT_NAME = "client";
  private static final BlockPos CONTROLLER_FLOOR = new BlockPos(0, 0, 2);
  private static final BlockPos CONTROLLER = CONTROLLER_FLOOR.above();
  private static final BlockPos LEVER_BLOCK = new BlockPos(0, 1, 0);
  private static final BlockPos LEVER = LEVER_BLOCK.above();
  private static final BlockPos LAMP = new BlockPos(2, 1, 2);
  private static final ChannelColor LEVER_CHANNEL = ChannelColor.WHITE;
  private static final int LEVER_SLOT = LEVER_CHANNEL.ordinal();
  private static final int MENU_ID = 1;
  private static final int NO_LINES = 0;
  private static final int TWO_LINES = 2;

  /**
   * The first evaluation of a network only learns its outputs (answer 244), so the lever waits for
   * it, as a player who places a lever after building the circuit.
   */
  private static final int SETTLE_TICKS = 2;

  private static final int NEWEST = 0;
  private static final int OLDER = 1;
  private static final String NOT_TWO = "The lever and the lamp should make one line each";
  private static final String NOT_OUTPUT = "The newest line should be the lamp, an output";
  private static final String NOT_INPUT = "The older line should be the lever, an input";
  private static final String NOT_EMPTY = "The log should start empty";
  private static final String SENT_AGAIN = "A line should reach the open screen only once";

  private ControllerLogGameTests() {}

  /** Opening the screen carries the whole log, the newest line first (answer 147). */
  @GameTest(template = EMPTY)
  public static void theOpeningCarriesTheLog(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(net.minecraft.world.level.GameType.SURVIVAL);
    NetworkId network = leverAndLamp(helper, owner);

    helper
        .startSequence()
        .thenIdle(SETTLE_TICKS)
        .thenExecute(() -> helper.pullLever(LEVER))
        .thenWaitUntil(() -> helper.assertBlockProperty(LAMP, RedstoneLampBlock.LIT, true))
        .thenExecute(() -> assertLeverAndLamp(helper, opened(helper, owner, network).log()))
        .thenSucceed();
  }

  /** An open screen starts with the log it had and then receives only the new lines (147). */
  @GameTest(template = EMPTY)
  public static void anOpenScreenReceivesOnlyTheNewLines(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(net.minecraft.world.level.GameType.SURVIVAL);
    NetworkId network = leverAndLamp(helper, owner);
    ServerPlayer viewer = viewer(helper, owner.getUUID(), VIEWER_NAME);
    ControllerMenu server = new ControllerMenu(MENU_ID, viewer, pos(helper), network);
    ControllerMenu client = opened(helper, owner, network);
    ServerPlayer clientPlayer = viewer(helper, UUID.randomUUID(), CLIENT_NAME);
    clientPlayer.containerMenu = client;
    helper.assertTrue(client.log().size() == NO_LINES, NOT_EMPTY);

    helper
        .startSequence()
        .thenIdle(SETTLE_TICKS)
        .thenExecute(() -> helper.pullLever(LEVER))
        .thenWaitUntil(() -> helper.assertBlockProperty(LAMP, RedstoneLampBlock.LIT, true))
        .thenExecute(
            () -> server.newLog().ifPresent(log -> ControllerScreenLog.receive(log, clientPlayer)))
        .thenExecute(() -> assertLeverAndLamp(helper, client.log()))
        .thenExecute(() -> helper.assertTrue(server.newLog().isEmpty(), SENT_AGAIN))
        .thenSucceed();
  }

  private static void assertLeverAndLamp(GameTestHelper helper, List<LogLine> log) {
    helper.assertTrue(log.size() == TWO_LINES, NOT_TWO);
    helper.assertTrue(isLine(log.get(NEWEST), Direction.OUTPUT), NOT_OUTPUT);
    helper.assertTrue(isLine(log.get(OLDER), Direction.INPUT), NOT_INPUT);
  }

  private static boolean isLine(LogLine line, Direction direction) {
    return line.direction() == direction.ordinal()
        && line.channel() == LEVER_CHANNEL.ordinal()
        && line.value() == Signal.MAX.strength();
  }

  /** What the client menu reads from the opening the server writes for the owner. */
  private static ControllerMenu opened(GameTestHelper helper, Player owner, NetworkId network) {
    ServerPlayer viewer = viewer(helper, owner.getUUID(), VIEWER_NAME);
    RegistryFriendlyByteBuf extra =
        new RegistryFriendlyByteBuf(Unpooled.buffer(), helper.getLevel().registryAccess());
    ControllerMenu.writeOpening(extra, viewer, pos(helper), network);
    return new ControllerMenu(MENU_ID, viewer.getInventory(), extra);
  }

  /**
   * A lever read by one node into the white channel, and a lamp lit by another node. The node goes
   * on stone, which then turns into the lamp, because placing a node sets its block to stone.
   */
  private static NetworkId leverAndLamp(GameTestHelper helper, Player owner) {
    NetworkId network = NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    helper.setBlock(LEVER_BLOCK, Blocks.STONE);
    BlockState lever = Blocks.LEVER.defaultBlockState().setValue(LeverBlock.FACE, AttachFace.FLOOR);
    helper.setBlock(LEVER, lever);
    NodeId input =
        NodeScene.placeNode(helper, owner, LEVER_BLOCK, net.minecraft.core.Direction.EAST);
    NodeScene.enableSlot(owner, input, LEVER_SLOT);
    NodeId output = NodeScene.placeNode(helper, owner, LAMP, net.minecraft.core.Direction.UP);
    helper.setBlock(LAMP, Blocks.REDSTONE_LAMP);
    RedweaveServer.running()
        .orElseThrow()
        .network(network)
        .orElseThrow()
        .configureSlot(
            new PlayerId(owner.getUUID()),
            output,
            LEVER_SLOT,
            new ChannelSlot(Direction.OUTPUT, Face.UP, LEVER_CHANNEL));
    return network;
  }

  private static ServerPlayer viewer(GameTestHelper helper, UUID player, String name) {
    ServerPlayer viewer = FakePlayerFactory.get(helper.getLevel(), new GameProfile(player, name));
    viewer.setPos(Vec3.atCenterOf(helper.absolutePos(CONTROLLER.above())));
    return viewer;
  }

  private static BlockPos pos(GameTestHelper helper) {
    return helper.absolutePos(CONTROLLER);
  }
}
