package io.github.marcelofabianov.redweave.gametest;

import com.mojang.authlib.GameProfile;
import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.NetworkState;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.menu.ControllerMenu;
import io.github.marcelofabianov.redweave.menu.TerminalAction;
import io.github.marcelofabianov.redweave.menu.TerminalMenu;
import io.github.marcelofabianov.redweave.menu.TerminalScreenCodes;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import io.netty.buffer.Unpooled;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The Network Terminal through its menu on the server: the list of the networks of the owner and
 * the actions on each, checked on the server (terminal-de-rede.md, answers 131 to 166 and 259).
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class TerminalMenuGameTests {
  private static final String EMPTY = "empty";
  private static final String OWNER_NAME = "owner";
  private static final String STRANGER_NAME = "stranger";
  private static final BlockPos TERMINAL_FLOOR = new BlockPos(0, 0, 0);
  private static final BlockPos TERMINAL = TERMINAL_FLOOR.above();
  private static final BlockPos CONTROLLER_FLOOR = new BlockPos(2, 0, 2);
  private static final BlockPos CONTROLLER = CONTROLLER_FLOOR.above();
  private static final BlockPos STONE = new BlockPos(1, 1, 2);
  private static final int MENU_ID = 1;
  private static final int FIRST_ROW = 0;
  private static final int ONE_NETWORK = 1;
  private static final double FAR_AWAY = 64;
  private static final String NOT_OPENED = "The client should read the networks the server wrote";
  private static final String NOT_TURNED = "The owner should turn the network off and on";
  private static final String NOT_CLEARED = "Clearing should leave the node with no network";
  private static final String NOT_DELETED = "An unplaced network should be deleted";
  private static final String STRANGER_ACTS = "Only the owner should use the terminal";
  private static final String VALID_FAR = "The terminal should close beyond the reach";
  private static final String NOT_ANCHORED = "The Controller Screen should hold at the terminal";

  private TerminalMenuGameTests() {}

  /** The mock player cannot receive a screen, so the test checks what the opening carries. */
  @GameTest(template = EMPTY)
  public static void theOpeningCarriesTheNetworks(GameTestHelper helper) {
    Player owner = terminalAndNetwork(helper);
    ServerPlayer viewer = viewer(helper, owner.getUUID(), OWNER_NAME);
    RegistryFriendlyByteBuf extra =
        new RegistryFriendlyByteBuf(Unpooled.buffer(), helper.getLevel().registryAccess());

    TerminalMenu.writeOpening(extra, viewer, pos(helper, TERMINAL));
    TerminalMenu client = new TerminalMenu(MENU_ID, viewer.getInventory(), extra);

    helper.assertTrue(client.networks().size() == ONE_NETWORK, NOT_OPENED);
    helper.assertTrue(client.pos().equals(pos(helper, TERMINAL)), NOT_OPENED);
    helper.succeed();
  }

  @GameTest(template = EMPTY)
  public static void theOwnerTurnsANetworkOffAndOn(GameTestHelper helper) {
    Player owner = terminalAndNetwork(helper);
    TerminalMenu menu = menuOf(helper, owner.getUUID(), OWNER_NAME);
    NetworkId network = onlyNetworkOf(owner);

    click(menu, owner, TerminalAction.TURN_OFF);
    helper.assertTrue(networkOf(network).state() == NetworkState.OFFLINE, NOT_TURNED);
    click(menu, owner, TerminalAction.TURN_ON);

    helper.assertTrue(networkOf(network).state() == NetworkState.ONLINE, NOT_TURNED);
    helper.succeed();
  }

  /** The node stays in the world with no network (answer 133). */
  @GameTest(template = EMPTY)
  public static void clearingLeavesTheNodesWithNoNetwork(GameTestHelper helper) {
    Player owner = terminalAndNetwork(helper);
    NodeId node = NodeScene.placeNode(helper, owner, STONE, Direction.UP);
    TerminalMenu menu = menuOf(helper, owner.getUUID(), OWNER_NAME);

    click(menu, owner, TerminalAction.CLEAR_NODES);

    helper.assertTrue(server().networkOfNode(node).isEmpty(), NOT_CLEARED);
    helper.succeed();
  }

  /** Only an unplaced network is deleted (answer 166). */
  @GameTest(template = EMPTY)
  public static void anUnplacedNetworkIsDeleted(GameTestHelper helper) {
    Player owner = terminalAndNetwork(helper);
    NetworkId network = onlyNetworkOf(owner);
    TerminalMenu menu = menuOf(helper, owner.getUUID(), OWNER_NAME);
    click(menu, owner, TerminalAction.DELETE);
    helper.assertTrue(server().network(network).isPresent(), NOT_DELETED);
    GameTestActions.breakBy(helper, owner, CONTROLLER);
    menu.broadcastChanges();

    click(menu, owner, TerminalAction.DELETE);

    helper.assertTrue(server().network(network).isEmpty(), NOT_DELETED);
    helper.succeed();
  }

  /** Another player neither lists nor changes the networks of the owner (answers 134 and 153). */
  @GameTest(template = EMPTY)
  public static void anotherPlayerCannotUseTheTerminal(GameTestHelper helper) {
    Player owner = terminalAndNetwork(helper);
    NetworkId network = onlyNetworkOf(owner);
    ServerPlayer stranger = viewer(helper, UUID.randomUUID(), STRANGER_NAME);
    TerminalMenu menu = new TerminalMenu(MENU_ID, stranger, pos(helper, TERMINAL));

    menu.clickMenuButton(stranger, button(TerminalAction.TURN_OFF));

    helper.assertFalse(menu.stillValid(stranger), STRANGER_ACTS);
    helper.assertTrue(networkOf(network).state() == NetworkState.ONLINE, STRANGER_ACTS);
    helper.succeed();
  }

  @GameTest(template = EMPTY)
  public static void theTerminalClosesBeyondTheReach(GameTestHelper helper) {
    Player owner = terminalAndNetwork(helper);
    ServerPlayer viewer = viewer(helper, owner.getUUID(), OWNER_NAME);
    TerminalMenu menu = new TerminalMenu(MENU_ID, viewer, pos(helper, TERMINAL));

    viewer.setPos(viewer.position().add(FAR_AWAY, 0, FAR_AWAY));

    helper.assertFalse(menu.stillValid(viewer), VALID_FAR);
    helper.succeed();
  }

  /**
   * Opened by the terminal, the Controller Screen holds at the terminal, wherever the controller is
   * (answer 259): the player next to the terminal keeps it open.
   */
  @GameTest(template = EMPTY)
  public static void theControllerScreenHoldsAtTheTerminal(GameTestHelper helper) {
    Player owner = terminalAndNetwork(helper);
    ServerPlayer viewer = viewer(helper, owner.getUUID(), OWNER_NAME);

    ControllerMenu menu =
        ControllerMenu.atTerminal(MENU_ID, viewer, pos(helper, TERMINAL), onlyNetworkOf(owner));

    helper.assertTrue(menu.stillValid(viewer), NOT_ANCHORED);
    viewer.setPos(viewer.position().add(FAR_AWAY, 0, FAR_AWAY));
    helper.assertFalse(menu.stillValid(viewer), VALID_FAR);
    helper.succeed();
  }

  /** The owner places a terminal and a controller, which makes one network. */
  private static Player terminalAndNetwork(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    ItemStack terminal = new ItemStack(RedweaveItems.NETWORK_TERMINAL.get());
    GameTestActions.placeOn(helper, owner, terminal, TERMINAL_FLOOR, Direction.UP);
    NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    return owner;
  }

  private static NetworkId onlyNetworkOf(Player owner) {
    return server()
        .terminal()
        .terminalNetworks()
        .of(new PlayerId(owner.getUUID()))
        .get(FIRST_ROW)
        .id();
  }

  private static void click(TerminalMenu menu, Player owner, TerminalAction action) {
    menu.clickMenuButton(owner, button(action));
  }

  private static int button(TerminalAction action) {
    return TerminalScreenCodes.button(new TerminalScreenCodes.Click(FIRST_ROW, action));
  }

  private static TerminalMenu menuOf(GameTestHelper helper, UUID player, String name) {
    return new TerminalMenu(MENU_ID, viewer(helper, player, name), pos(helper, TERMINAL));
  }

  private static ServerPlayer viewer(GameTestHelper helper, UUID player, String name) {
    ServerPlayer viewer = FakePlayerFactory.get(helper.getLevel(), new GameProfile(player, name));
    viewer.setPos(Vec3.atCenterOf(helper.absolutePos(TERMINAL.above())));
    return viewer;
  }

  private static BlockPos pos(GameTestHelper helper, BlockPos relative) {
    return helper.absolutePos(relative);
  }

  private static Network networkOf(NetworkId id) {
    return server().network(id).orElseThrow();
  }

  private static RedweaveServer server() {
    return RedweaveServer.running().orElseThrow();
  }
}
