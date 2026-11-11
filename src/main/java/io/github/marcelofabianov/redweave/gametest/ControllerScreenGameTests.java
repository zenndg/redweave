package io.github.marcelofabianov.redweave.gametest;

import com.mojang.authlib.GameProfile;
import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.item.ConfiguratorMode;
import io.github.marcelofabianov.redweave.menu.ControllerMenu;
import io.github.marcelofabianov.redweave.menu.ControllerScreenCodes;
import io.github.marcelofabianov.redweave.menu.RenameTarget;
import io.github.marcelofabianov.redweave.network.ControllerRenamed;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import io.netty.buffer.Unpooled;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The Controller Screen through its menu on the server: anyone opens it, only the owner sees the
 * positions and changes the network, and it never opens on a network turned off (telas.md, answers
 * 22, 149, 156, 158, 212 and 217).
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class ControllerScreenGameTests {
  private static final String EMPTY = "empty";
  private static final String OWNER_NAME = "owner";
  private static final String STRANGER_NAME = "stranger";
  private static final BlockPos CONTROLLER_FLOOR = new BlockPos(0, 0, 2);
  private static final BlockPos CONTROLLER = CONTROLLER_FLOOR.above();
  private static final BlockPos STONE = new BlockPos(1, 1, 1);
  private static final int MENU_ID = 1;
  private static final int FIRST_ROW = 0;
  private static final int MISSING_ROW = 7;
  private static final double FAR_AWAY = 64;
  private static final String NEW_NAME = "Factory";
  private static final String NODE_NAME = "Lever";
  private static final String DOOR_NAME = "Main door";
  private static final ChannelColor DOOR = ChannelColor.RED;
  private static final ChannelColor NEW_COLOR = ChannelColor.LIME;
  private static final String NOT_OPENED = "The client should read the rows the server wrote";
  private static final String OPENED_OFF = "A network turned off should not open its screen";
  private static final String NO_POSITION = "The owner should see the position of the node";
  private static final String SEES_POSITION = "Another player should not see the position";
  private static final String NOT_RECOLORED = "The owner should recolor the network";
  private static final String STRANGER_RECOLORED = "Only the owner should recolor the network";
  private static final String NOT_RENAMED = "The owner should rename the network, channel and node";
  private static final String STRANGER_RENAMED = "Only the owner should rename the network";
  private static final String VALID_FAR = "The screen should close beyond the reach";
  private static final String VALID_OFF = "The screen should close when the network turns off";
  private static final String INVALID_NEAR = "The screen should hold within reach";
  private static final String NOT_TAKEN = "The Configurator in Open should click the controller";
  private static final String TAKEN = "The Configurator in Copy should not click the controller";

  private ControllerScreenGameTests() {}

  /**
   * The mock player cannot receive a screen, so the test checks the opening: what the server
   * writes, the client menu reads back with the same rows (answer 113).
   */
  @GameTest(template = EMPTY)
  public static void theOpeningCarriesTheRows(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    NetworkId id = placeController(helper, owner);
    NodeScene.placeNode(helper, owner, STONE, Direction.UP);
    ServerPlayer viewer = playerNearTheController(helper, owner.getUUID(), OWNER_NAME);
    RegistryFriendlyByteBuf extra =
        new RegistryFriendlyByteBuf(Unpooled.buffer(), helper.getLevel().registryAccess());

    ControllerMenu.writeOpening(extra, viewer, pos(helper), id);
    ControllerMenu client = new ControllerMenu(MENU_ID, viewer.getInventory(), extra);

    ControllerMenu server = new ControllerMenu(MENU_ID, viewer, pos(helper), id);
    helper.assertTrue(client.pos().equals(pos(helper)), NOT_OPENED);
    helper.assertTrue(client.header().equals(server.header()), NOT_OPENED);
    helper.assertTrue(client.nodes().equals(server.nodes()), NOT_OPENED);
    helper.assertTrue(client.channels().equals(server.channels()), NOT_OPENED);
    helper.succeed();
  }

  /**
   * A network turned off keeps its screen closed, even for the owner (answer 158). Opening would
   * fail on the mock player, so the click passing proves nothing opened.
   */
  @GameTest(template = EMPTY)
  public static void aNetworkTurnedOffDoesNotOpen(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    NetworkId id = placeController(helper, owner);
    networkOf(id).turnOff(new PlayerId(owner.getUUID()));

    clickTheControllerByHand(helper, owner);

    helper.assertFalse(owner.containerMenu instanceof ControllerMenu, OPENED_OFF);
    helper.succeed();
  }

  /**
   * The Configurator in the Open mode clicks the controller as an empty hand, and the other modes
   * do not (answer 232). A network turned off keeps the mock player from receiving a screen.
   */
  @GameTest(template = EMPTY)
  public static void onlyTheOpenModeClicksTheController(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    NetworkId id = placeController(helper, owner);
    networkOf(id).turnOff(new PlayerId(owner.getUUID()));

    ConfiguratorGameTests.holdConfigurator(owner, ConfiguratorMode.OPEN);
    helper.assertTrue(clickTheController(helper, owner).consumesAction(), NOT_TAKEN);
    ConfiguratorGameTests.holdConfigurator(owner, ConfiguratorMode.COPY);
    helper.assertFalse(clickTheController(helper, owner).consumesAction(), TAKEN);
    helper.succeed();
  }

  /** The position stays on the server for anyone but the owner (answer 149). */
  @GameTest(template = EMPTY)
  public static void onlyTheOwnerSeesThePositions(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    NetworkId id = placeController(helper, owner);
    NodeScene.placeNode(helper, owner, STONE, Direction.UP);

    ControllerMenu ownerMenu = menuOf(helper, owner.getUUID(), OWNER_NAME, id);
    ControllerMenu strangerMenu = menuOf(helper, UUID.randomUUID(), STRANGER_NAME, id);

    helper.assertTrue(ownerMenu.nodes().get(FIRST_ROW).position().isPresent(), NO_POSITION);
    helper.assertTrue(strangerMenu.nodes().get(FIRST_ROW).position().isEmpty(), SEES_POSITION);
    helper.succeed();
  }

  /** A button is a color of the palette, taken only from the owner (answer 212). */
  @GameTest(template = EMPTY)
  public static void onlyTheOwnerRecolorsTheNetwork(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    NetworkId id = placeController(helper, owner);
    ServerPlayer stranger = playerNearTheController(helper, UUID.randomUUID(), STRANGER_NAME);
    ControllerMenu strangerMenu = new ControllerMenu(MENU_ID, stranger, pos(helper), id);
    ChannelColor before = networkOf(id).color();

    strangerMenu.clickMenuButton(stranger, ControllerScreenCodes.button(NEW_COLOR));
    helper.assertTrue(networkOf(id).color() == before, STRANGER_RECOLORED);
    ServerPlayer viewer = playerNearTheController(helper, owner.getUUID(), OWNER_NAME);
    new ControllerMenu(MENU_ID, viewer, pos(helper), id)
        .clickMenuButton(viewer, ControllerScreenCodes.button(NEW_COLOR));
    helper.assertTrue(networkOf(id).color() == NEW_COLOR, NOT_RECOLORED);
    helper.succeed();
  }

  /** The texts go as a packet from the open screen, and a node by its row (144, 156, 217). */
  @GameTest(template = EMPTY)
  public static void theOwnerRenamesTheNetworkAChannelAndANode(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    NetworkId id = placeController(helper, owner);
    NodeScene.placeNode(helper, owner, STONE, Direction.UP);
    ServerPlayer viewer = openFor(helper, owner.getUUID(), OWNER_NAME, id);

    rename(helper, viewer, RenameTarget.NETWORK, FIRST_ROW, NEW_NAME);
    rename(helper, viewer, RenameTarget.CHANNEL, DOOR.ordinal(), DOOR_NAME);
    rename(helper, viewer, RenameTarget.NODE, FIRST_ROW, NODE_NAME);
    rename(helper, viewer, RenameTarget.NODE, MISSING_ROW, NEW_NAME);

    Network network = networkOf(id);
    helper.assertTrue(network.name().equals(NEW_NAME), NOT_RENAMED);
    helper.assertTrue(network.channel(DOOR).name().orElseThrow().equals(DOOR_NAME), NOT_RENAMED);
    helper.assertTrue(network.nodes().get(FIRST_ROW).displayName().equals(NODE_NAME), NOT_RENAMED);
    helper.succeed();
  }

  @GameTest(template = EMPTY)
  public static void aStrangerCannotRenameTheNetwork(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    NetworkId id = placeController(helper, owner);
    String before = networkOf(id).name();
    ServerPlayer stranger = openFor(helper, UUID.randomUUID(), STRANGER_NAME, id);

    rename(helper, stranger, RenameTarget.NETWORK, FIRST_ROW, NEW_NAME);

    helper.assertTrue(networkOf(id).name().equals(before), STRANGER_RENAMED);
    helper.succeed();
  }

  /** The game only delivers a click while the menu holds (answers 81 and 158). */
  @GameTest(template = EMPTY)
  public static void theScreenHoldsWithinReachAndOnline(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    NetworkId id = placeController(helper, owner);
    ServerPlayer viewer = playerNearTheController(helper, owner.getUUID(), OWNER_NAME);
    ControllerMenu menu = new ControllerMenu(MENU_ID, viewer, pos(helper), id);

    helper.assertTrue(menu.stillValid(viewer), INVALID_NEAR);
    networkOf(id).turnOff(new PlayerId(owner.getUUID()));
    helper.assertFalse(menu.stillValid(viewer), VALID_OFF);
    networkOf(id).turnOn(new PlayerId(owner.getUUID()));
    viewer.setPos(viewer.position().add(FAR_AWAY, 0, FAR_AWAY));
    helper.assertFalse(menu.stillValid(viewer), VALID_FAR);
    helper.succeed();
  }

  private static NetworkId placeController(GameTestHelper helper, ServerPlayer owner) {
    return NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
  }

  /** A right-click on the controller with the hand empty. */
  private static void clickTheControllerByHand(GameTestHelper helper, ServerPlayer player) {
    player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
    clickTheController(helper, player);
  }

  /** A right-click on the controller with what the player holds, as the game handles it. */
  private static InteractionResult clickTheController(GameTestHelper helper, ServerPlayer player) {
    BlockPos pos = pos(helper);
    BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false);
    ItemStack held = player.getItemInHand(InteractionHand.MAIN_HAND);
    return player.gameMode.useItemOn(
        player, helper.getLevel(), held, InteractionHand.MAIN_HAND, hit);
  }

  private static BlockPos pos(GameTestHelper helper) {
    return helper.absolutePos(CONTROLLER);
  }

  private static ControllerMenu menuOf(
      GameTestHelper helper, UUID player, String name, NetworkId id) {
    return new ControllerMenu(
        MENU_ID, playerNearTheController(helper, player, name), pos(helper), id);
  }

  /** A player next to the controller with its screen open, as the game keeps it. */
  private static ServerPlayer openFor(
      GameTestHelper helper, UUID player, String name, NetworkId id) {
    ServerPlayer viewer = playerNearTheController(helper, player, name);
    viewer.containerMenu = new ControllerMenu(MENU_ID, viewer, pos(helper), id);
    return viewer;
  }

  private static void rename(
      GameTestHelper helper, ServerPlayer viewer, RenameTarget target, int index, String name) {
    ControllerRenamed.receive(ControllerRenamed.of(pos(helper), target, index, name), viewer);
  }

  private static ServerPlayer playerNearTheController(
      GameTestHelper helper, UUID player, String name) {
    ServerPlayer viewer = FakePlayerFactory.get(helper.getLevel(), new GameProfile(player, name));
    viewer.setPos(Vec3.atCenterOf(helper.absolutePos(CONTROLLER.above())));
    return viewer;
  }

  private static Network networkOf(NetworkId id) {
    return RedweaveServer.running().orElseThrow().network(id).orElseThrow();
  }
}
