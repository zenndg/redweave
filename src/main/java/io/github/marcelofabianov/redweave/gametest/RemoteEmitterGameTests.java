package io.github.marcelofabianov.redweave.gametest;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.part.ButtonPart;
import io.github.marcelofabianov.redweave.block.part.LeverPart;
import io.github.marcelofabianov.redweave.block.part.PressurePlatePart;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.item.RemoteState;
import io.github.marcelofabianov.redweave.registry.RedweaveDataComponents;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The Signal Remote linked to a Signal Emitter: the owner links it with Shift + right click on the
 * emitter, without firing it, and whoever holds it fires the emitter as the click of its owner; far
 * away it is out of reach and keeps the link, a missing emitter loses it, and an offline network
 * refuses (signal-remote.md, answers 778 to 793).
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class RemoteEmitterGameTests {
  private static final String EMPTY = "empty";
  private static final BlockPos STONE = new BlockPos(1, 1, 1);
  private static final BlockPos EMITTER = STONE.above();
  private static final BlockPos CONTROLLER_FLOOR = new BlockPos(0, 0, 2);
  private static final BlockPos CONTROLLER = CONTROLLER_FLOOR.above();
  private static final double FAR_AWAY = 200;

  /** The `remote.pulse_ticks` of the default config, 20, and a few ticks more to see it end. */
  private static final int AFTER_THE_PULSE = 30;

  private static final String NOT_LINKED = "Shift + click on the emitter should link the tablet";
  private static final String FIRED_ON_LINK = "Linking should not turn the lever";
  private static final String STRANGER_LINKED = "Only the owner of the emitter should link it";
  private static final String NOT_ON = "The tablet should turn the lever on";
  private static final String NOT_OFF = "A second click should turn the lever off";
  private static final String NOT_PRESSED = "The tablet should press the button";
  private static final String PLATE_NOT_ON = "The tablet should turn the plate on";
  private static final String PLATE_STAYED = "The plate should turn off after the pulse";
  private static final String FIRED_FAR = "A lever out of reach should not turn";
  private static final String LINK_DROPPED = "Out of reach should keep the link";
  private static final String NOT_LOST = "A missing emitter should lose the link";
  private static final String FIRED_OFFLINE = "An offline network should refuse the tablet";

  private RemoteEmitterGameTests() {}

  @GameTest(template = EMPTY)
  public static void theOwnerLinksALeverWithoutTurningIt(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    LeverPart lever = LeverScene.place(helper, owner, STONE);

    ItemStack tablet = linkedTablet(helper, owner);

    helper.assertTrue(stateOf(tablet).emitter().isPresent(), NOT_LINKED);
    helper.assertFalse(lever.lever().on(), FIRED_ON_LINK);
    helper.succeed();
  }

  @GameTest(template = EMPTY)
  public static void aStrangerCannotLinkTheEmitter(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    LeverScene.place(helper, owner, STONE);
    ServerPlayer stranger = helper.makeMockServerPlayerInLevel();

    ItemStack tablet = linkedTablet(helper, stranger);

    helper.assertTrue(stateOf(tablet).emitter().isEmpty(), STRANGER_LINKED);
    helper.succeed();
  }

  /** Anyone who holds the tablet fires it, as with the nodes (answer 782). */
  @GameTest(template = EMPTY)
  public static void theTabletTurnsTheLeverOnAndOff(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    LeverPart lever = LeverScene.place(helper, owner, STONE);
    ItemStack tablet = linkedTablet(helper, owner);
    ServerPlayer friend = helper.makeMockServerPlayerInLevel();
    friend.setPos(Vec3.atCenterOf(helper.absolutePos(EMITTER.above())));

    fire(helper, friend, tablet);
    helper.assertTrue(lever.lever().on(), NOT_ON);
    fire(helper, friend, tablet);
    helper.assertFalse(lever.lever().on(), NOT_OFF);
    helper.succeed();
  }

  @GameTest(template = EMPTY)
  public static void theTabletPressesTheButton(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    ButtonPart button = ButtonScene.place(helper, owner, STONE);
    ItemStack tablet = linkedTablet(helper, owner);

    fire(helper, owner, tablet);

    helper.assertTrue(button.button().on(), NOT_PRESSED);
    helper.succeed();
  }

  /** The plate is on for the pulse of the tablet, with nothing on it (answer 783). */
  @GameTest(template = EMPTY)
  public static void theTabletPulsesThePlate(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    PressurePlatePart plate = PressurePlateScene.place(helper, owner, STONE);
    ItemStack tablet = linkedTablet(helper, owner);

    fire(helper, owner, tablet);

    helper.assertTrue(plate.plate().on(), PLATE_NOT_ON);
    helper.runAfterDelay(
        AFTER_THE_PULSE,
        () -> {
          helper.assertFalse(plate.plate().on(), PLATE_STAYED);
          helper.succeed();
        });
  }

  @GameTest(template = EMPTY)
  public static void farAwayTheLeverIsOutOfReachAndTheLinkStays(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    LeverPart lever = LeverScene.place(helper, owner, STONE);
    ItemStack tablet = linkedTablet(helper, owner);
    owner.setPos(owner.position().add(FAR_AWAY, 0, FAR_AWAY));

    fire(helper, owner, tablet);

    helper.assertFalse(lever.lever().on(), FIRED_FAR);
    helper.assertTrue(stateOf(tablet).emitter().isPresent(), LINK_DROPPED);
    helper.assertFalse(stateOf(tablet).isLost(), LINK_DROPPED);
    helper.succeed();
  }

  @GameTest(template = EMPTY)
  public static void aMissingEmitterLosesTheLink(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    LeverScene.place(helper, owner, STONE);
    ItemStack tablet = linkedTablet(helper, owner);
    helper.setBlock(EMITTER, Blocks.AIR);

    fire(helper, owner, tablet);

    helper.assertTrue(stateOf(tablet).isLost(), NOT_LOST);
    helper.succeed();
  }

  /** With the network of the emitter offline, the tablet refuses (answer 787). */
  @GameTest(template = EMPTY)
  public static void anOfflineNetworkRefusesTheTablet(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    NetworkId network = NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    LeverPart lever = LeverScene.place(helper, owner, STONE);
    PlayerId ownerId = new PlayerId(owner.getUUID());
    lever.module().orElseThrow().choose(ownerId, network);
    RedweaveServer.running()
        .orElseThrow()
        .network(network)
        .orElseThrow()
        .terminal()
        .turnOff(ownerId);
    ItemStack tablet = linkedTablet(helper, owner);

    fire(helper, owner, tablet);

    helper.assertFalse(lever.lever().on(), FIRED_OFFLINE);
    helper.succeed();
  }

  /** Shift + right click on the emitter with a new tablet, as the game handles the packet. */
  private static ItemStack linkedTablet(GameTestHelper helper, ServerPlayer player) {
    ItemStack tablet = new ItemStack(RedweaveItems.SIGNAL_REMOTE.get());
    player.setPos(Vec3.atCenterOf(helper.absolutePos(EMITTER.above())));
    player.setShiftKeyDown(true);
    LeverScene.click(helper, player, tablet, EMITTER);
    player.setShiftKeyDown(false);
    return tablet;
  }

  private static void fire(GameTestHelper helper, ServerPlayer player, ItemStack tablet) {
    player.setItemInHand(InteractionHand.MAIN_HAND, tablet);
    tablet.getItem().use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
  }

  private static RemoteState stateOf(ItemStack tablet) {
    return tablet.getOrDefault(RedweaveDataComponents.SIGNAL_REMOTE.get(), RemoteState.NEW);
  }
}
