package io.github.marcelofabianov.redweave.gametest;

import com.mojang.authlib.GameProfile;
import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import io.github.marcelofabianov.redweave.item.RemoteLink;
import io.github.marcelofabianov.redweave.item.RemoteLinking;
import io.github.marcelofabianov.redweave.item.RemoteMode;
import io.github.marcelofabianov.redweave.item.RemoteState;
import io.github.marcelofabianov.redweave.menu.RemoteLinkCodes;
import io.github.marcelofabianov.redweave.menu.RemoteLinkMenu;
import io.github.marcelofabianov.redweave.menu.RemoteLinkStep;
import io.github.marcelofabianov.redweave.registry.RedweaveDataComponents;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The Signal Remote in the world: whoever holds a linked tablet fires it, the lever and the button
 * reach the channel only while the tablet is in the inventory, and a slot that stops being an input
 * loses the link (answers 287, 355 to 357, 367 and 369).
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class SignalRemoteGameTests {
  private static final String EMPTY = "empty";
  private static final BlockPos FLOOR = new BlockPos(0, 0, 2);
  private static final BlockPos CONTROLLER = FLOOR.above();
  private static final BlockPos STONE = new BlockPos(1, 1, 1);
  private static final int SLOT = 0;
  private static final ChannelColor CHANNEL = ChannelColor.values()[SLOT];
  private static final int PULSE_TICKS = 20;
  private static final String NOT_ON = "A tablet that is on should turn the channel on";
  private static final String NOT_OFF = "A tablet out of the inventory should not count";
  private static final String PULSE_STAYED = "The button pulse should end";
  private static final String NOT_LOST = "A slot that is no longer an input should lose the link";
  private static final String NOT_LINKED = "Two clicks should link the tablet to the only input";
  private static final String NOT_LISTED = "The owner should see their online network";
  private static final String OFFLINE_LISTED = "An offline network should not be listed";
  private static final String STRANGER_SEES = "A stranger should not see the network of another";
  private static final String NO_SLOT_STEP = "A node with two inputs should ask for the slot";
  private static final String STRANGER_NAME = "stranger";
  private static final int MENU_ID = 1;
  private static final int FIRST_LINE = 0;
  private static final int SECOND_SLOT = 1;
  private static final int CHANNEL_PART = 2;
  private static final int SQUARE_PART = 0;
  private static final String MARKED_CHANNEL = "■ 01";
  private static final String NOT_MARKED = "The channel should show as a square and its number";
  private static final String NOT_COLORED = "The square should have the color of the channel";

  private SignalRemoteGameTests() {}

  @GameTest(template = EMPTY)
  public static void aLeverTabletTurnsTheChannelOnWhileHeld(GameTestHelper helper) {
    ServerPlayer owner = playerNearTheController(helper);
    ItemStack tablet = linkedTablet(helper, owner);
    fire(helper, owner, tablet);

    helper.succeedWhen(() -> helper.assertTrue(channelOf(tablet).isOn(), NOT_ON));
  }

  @GameTest(template = EMPTY)
  public static void aTabletOutOfTheInventoryStopsCounting(GameTestHelper helper) {
    ServerPlayer owner = playerNearTheController(helper);
    ItemStack tablet = linkedTablet(helper, owner);
    fire(helper, owner, tablet);

    helper
        .startSequence()
        .thenWaitUntil(() -> helper.assertTrue(channelOf(tablet).isOn(), NOT_ON))
        .thenExecute(() -> owner.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY))
        .thenWaitUntil(() -> helper.assertFalse(channelOf(tablet).isOn(), NOT_OFF))
        .thenSucceed();
  }

  @GameTest(template = EMPTY)
  public static void theButtonPulseEnds(GameTestHelper helper) {
    ServerPlayer owner = playerNearTheController(helper);
    ItemStack tablet = linkedTablet(helper, owner);
    tablet.set(
        RedweaveDataComponents.SIGNAL_REMOTE.get(),
        tablet.get(RedweaveDataComponents.SIGNAL_REMOTE.get()).withMode(RemoteMode.BUTTON));
    fire(helper, owner, tablet);

    helper
        .startSequence()
        .thenWaitUntil(() -> helper.assertTrue(channelOf(tablet).isOn(), NOT_ON))
        .thenIdle(PULSE_TICKS)
        .thenWaitUntil(() -> helper.assertFalse(channelOf(tablet).isOn(), PULSE_STAYED))
        .thenSucceed();
  }

  @GameTest(template = EMPTY)
  public static void aSlotThatIsNoLongerAnInputLosesTheLink(GameTestHelper helper) {
    ServerPlayer owner = playerNearTheController(helper);
    ItemStack tablet = linkedTablet(helper, owner);
    owner.setItemInHand(InteractionHand.MAIN_HAND, tablet);
    NodeId node = nodeOf(tablet);
    NodeScene.networkOf(node).disableSlot(new PlayerId(owner.getUUID()), node, SLOT);

    helper.succeedWhen(() ->
        helper.assertTrue(
            tablet.get(RedweaveDataComponents.SIGNAL_REMOTE.get()).isLost(), NOT_LOST));
  }

  @GameTest(template = EMPTY)
  public static void theOwnerLinksTheOnlyInputInTwoClicks(GameTestHelper helper) {
    ServerPlayer owner = playerNearTheController(helper);
    ItemStack tablet = linkedTablet(helper, owner);
    tablet.remove(RedweaveDataComponents.SIGNAL_REMOTE.get());
    owner.setItemInHand(InteractionHand.MAIN_HAND, tablet);
    RemoteLinkMenu menu = new RemoteLinkMenu(MENU_ID, owner, InteractionHand.MAIN_HAND);

    helper.assertTrue(menu.rows().lines().size() == 1, NOT_LISTED);
    menu.clickMenuButton(owner, RemoteLinkCodes.line(FIRST_LINE));
    menu.clickMenuButton(owner, RemoteLinkCodes.line(FIRST_LINE));

    RemoteState state = tablet.get(RedweaveDataComponents.SIGNAL_REMOTE.get());
    helper.assertTrue(state != null && state.link().isPresent(), NOT_LINKED);
    helper.succeed();
  }

  @GameTest(template = EMPTY)
  public static void aNodeWithTwoInputsAsksForTheSlot(GameTestHelper helper) {
    ServerPlayer owner = playerNearTheController(helper);
    ItemStack tablet = linkedTablet(helper, owner);
    NodeScene.enableSlot(owner, nodeOf(tablet), SECOND_SLOT);
    owner.setItemInHand(InteractionHand.MAIN_HAND, tablet);
    RemoteLinkMenu menu = new RemoteLinkMenu(MENU_ID, owner, InteractionHand.MAIN_HAND);

    menu.clickMenuButton(owner, RemoteLinkCodes.line(FIRST_LINE));
    menu.clickMenuButton(owner, RemoteLinkCodes.line(FIRST_LINE));

    helper.assertTrue(menu.step() == RemoteLinkStep.SLOTS, NO_SLOT_STEP);
    helper.succeed();
  }

  @GameTest(template = EMPTY)
  public static void onlyOnlineNetworksOfThePlayerAreListed(GameTestHelper helper) {
    ServerPlayer owner = playerNearTheController(helper);
    ItemStack tablet = linkedTablet(helper, owner);
    ServerPlayer stranger =
        FakePlayerFactory.get(helper.getLevel(), new GameProfile(UUID.randomUUID(), STRANGER_NAME));
    stranger.setItemInHand(InteractionHand.MAIN_HAND, tablet.copy());

    RemoteLinkMenu strangers = new RemoteLinkMenu(MENU_ID, stranger, InteractionHand.MAIN_HAND);
    helper.assertTrue(strangers.rows().lines().isEmpty(), STRANGER_SEES);

    NodeScene.networkOf(nodeOf(tablet)).terminal().turnOff(new PlayerId(owner.getUUID()));
    owner.setItemInHand(InteractionHand.MAIN_HAND, tablet);
    RemoteLinkMenu owners = new RemoteLinkMenu(MENU_ID, owner, InteractionHand.MAIN_HAND);
    helper.assertTrue(owners.rows().lines().isEmpty(), OFFLINE_LISTED);
    helper.succeed();
  }

  /**
   * The link shows the channel as a `■` in its color and its number, with no name of the color, as
   * in the screens (answers 770 and 775).
   */
  @GameTest(template = EMPTY)
  public static void theLinkShowsTheChannelAsASquareAndItsNumber(GameTestHelper helper) {
    ServerPlayer owner = playerNearTheController(helper);
    ItemStack tablet = linkedTablet(helper, owner);
    tablet.remove(RedweaveDataComponents.SIGNAL_REMOTE.get());
    owner.setItemInHand(InteractionHand.MAIN_HAND, tablet);
    RemoteLinkMenu menu = new RemoteLinkMenu(MENU_ID, owner, InteractionHand.MAIN_HAND);
    menu.clickMenuButton(owner, RemoteLinkCodes.line(FIRST_LINE));
    menu.clickMenuButton(owner, RemoteLinkCodes.line(FIRST_LINE));

    Component link = RemoteLinking.describe(tablet);
    Component channel =
        (Component) ((TranslatableContents) link.getContents()).getArgs()[CHANNEL_PART];

    helper.assertTrue(MARKED_CHANNEL.equals(channel.getString()), NOT_MARKED);
    TextColor square = channel.getSiblings().get(SQUARE_PART).getStyle().getColor();
    TextColor wanted = TextColor.fromRgb(DyeColor.byId(CHANNEL.ordinal()).getTextColor());
    helper.assertTrue(wanted.equals(square), NOT_COLORED);
    helper.succeed();
  }

  /** The tests run far from the origin, so the player stands by the controller, in its reach. */
  private static ServerPlayer playerNearTheController(GameTestHelper helper) {
    ServerPlayer player = helper.makeMockServerPlayerInLevel();
    player.setPos(Vec3.atCenterOf(helper.absolutePos(CONTROLLER.above())));
    return player;
  }

  private static void fire(GameTestHelper helper, ServerPlayer owner, ItemStack tablet) {
    owner.setItemInHand(InteractionHand.MAIN_HAND, tablet);
    tablet.getItem().use(helper.getLevel(), owner, InteractionHand.MAIN_HAND);
  }

  /** A controller, a node with an input on the first slot, and a tablet linked to it. */
  private static ItemStack linkedTablet(GameTestHelper helper, ServerPlayer owner) {
    NetworkId network = NodeScene.placeController(helper, owner, FLOOR, CONTROLLER);
    NodeId node = NodeScene.placeNode(helper, owner, STONE, Direction.UP);
    NodeScene.enableSlot(owner, node, SLOT);
    ItemStack tablet = new ItemStack(RedweaveItems.SIGNAL_REMOTE.get());
    RemoteLink link = new RemoteLink(network.value(), node.value(), SLOT);
    tablet.set(
        RedweaveDataComponents.SIGNAL_REMOTE.get(),
        RemoteState.NEW.linkedTo(link, UUID.randomUUID()));
    return tablet;
  }

  private static NodeId nodeOf(ItemStack tablet) {
    return new NodeId(
        tablet.get(RedweaveDataComponents.SIGNAL_REMOTE.get()).link().orElseThrow().node());
  }

  private static Signal channelOf(ItemStack tablet) {
    Network network = NodeScene.networkOf(nodeOf(tablet));
    return network.channelValue(CHANNEL);
  }
}
