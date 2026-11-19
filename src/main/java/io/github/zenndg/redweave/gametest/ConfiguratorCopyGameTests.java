package io.github.zenndg.redweave.gametest;

import io.github.zenndg.redweave.RedweaveMod;
import io.github.zenndg.redweave.block.ModuleMessages;
import io.github.zenndg.redweave.block.part.ButtonPart;
import io.github.zenndg.redweave.block.part.ItemSensorPart;
import io.github.zenndg.redweave.block.part.LampPart;
import io.github.zenndg.redweave.block.part.LeverPart;
import io.github.zenndg.redweave.block.part.LockPart;
import io.github.zenndg.redweave.block.part.PressurePlatePart;
import io.github.zenndg.redweave.block.part.TimerPart;
import io.github.zenndg.redweave.domain.module.Connection;
import io.github.zenndg.redweave.domain.module.LampMode;
import io.github.zenndg.redweave.domain.module.PlateMode;
import io.github.zenndg.redweave.domain.module.SignalTimer;
import io.github.zenndg.redweave.domain.network.NetworkId;
import io.github.zenndg.redweave.domain.network.PlayerId;
import io.github.zenndg.redweave.domain.signal.ChannelColor;
import io.github.zenndg.redweave.domain.signal.Signal;
import io.github.zenndg.redweave.item.ConfiguratorMessages;
import io.github.zenndg.redweave.item.ConfiguratorMode;
import io.github.zenndg.redweave.item.ModuleCopy;
import io.github.zenndg.redweave.registry.RedweaveDataComponents;
import io.github.zenndg.redweave.registry.RedweaveItems;
import io.github.zenndg.redweave.server.RedweaveServer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The Copy mode on every Signal Module: a click copies its settings, Shift + click pastes them on
 * one of the same type and owner, never what it is doing now, and never on a Signal Lock
 * (configurador.md, answers 722 to 731).
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class ConfiguratorCopyGameTests {
  private static final String EMPTY = "empty";
  private static final BlockPos STONE = new BlockPos(0, 1, 1);
  private static final BlockPos OTHER_STONE = new BlockPos(2, 1, 1);
  private static final BlockPos FIRST = STONE.above();
  private static final BlockPos SECOND = OTHER_STONE.above();
  private static final BlockPos CONTROLLER_FLOOR = new BlockPos(2, 0, 2);
  private static final BlockPos CONTROLLER = CONTROLLER_FLOOR.above();
  private static final int FIVE = 5;
  private static final int SEVEN = 7;
  private static final String NOT_PASTED = "The paste should give the setting of the copy";
  private static final String STATE_PASTED = "The paste should never give what it is doing now";
  private static final String WRONG_MESSAGE = "The action bar should say what happened";
  private static final String CHANGED = "The item should not change";
  private static final String NOT_DROPPED = "A channel of a network gone should be dropped";
  private static final String NOT_JOINED = "The paste should join the network of the copy";
  private static final String NOT_KEPT = "The copy should survive the save of the Configurator";

  private ConfiguratorCopyGameTests() {}

  /** The strength goes to the other lever, but not whether the first is on (725 and 726). */
  @GameTest(template = EMPTY)
  public static void aLeverPastesItsStrengthNotItsState(GameTestHelper helper) {
    List<Component> heard = new ArrayList<>();
    Player owner = CopyScene.copier(helper, heard);
    LeverPart from = CopyScene.place(helper, owner, CopyScene.lever(), STONE, LeverPart.class);
    LeverPart to = CopyScene.place(helper, owner, CopyScene.lever(), OTHER_STONE, LeverPart.class);
    from.setLever(from.lever().withStrength(FIVE).toggle());

    CopyScene.copyThenPaste(helper, owner, FIRST, SECOND);

    helper.assertValueEqual(to.lever().strength().strength(), FIVE, NOT_PASTED);
    helper.assertFalse(to.lever().on(), STATE_PASTED);
    helper.assertValueEqual(
        CopyScene.lastKey(heard), Optional.of(ConfiguratorMessages.PASTED_ITEM), WRONG_MESSAGE);
    helper.succeed();
  }

  /** The time of a button goes to the other button (answer 725). */
  @GameTest(template = EMPTY)
  public static void aButtonPastesItsTime(GameTestHelper helper) {
    Player owner = CopyScene.copier(helper, new ArrayList<>());
    ButtonPart from = ButtonScene.place(helper, owner, STONE);
    ButtonPart to = ButtonScene.place(helper, owner, OTHER_STONE);
    from.setButton(from.button().withStrength(SEVEN));

    CopyScene.copyThenPaste(helper, owner, FIRST, SECOND);

    helper.assertValueEqual(to.button().strength().strength(), SEVEN, NOT_PASTED);
    helper.assertValueEqual(to.button().time(), from.button().time(), NOT_PASTED);
    helper.succeed();
  }

  /** The mode of a pressure plate goes to the other plate (answer 725). */
  @GameTest(template = EMPTY)
  public static void aPressurePlatePastesItsMode(GameTestHelper helper) {
    Player owner = CopyScene.copier(helper, new ArrayList<>());
    PressurePlatePart from = PressurePlateScene.place(helper, owner, STONE);
    PressurePlatePart to = PressurePlateScene.place(helper, owner, OTHER_STONE);
    from.setPlate(from.plate().withMode(PlateMode.HOSTILE));

    CopyScene.copyThenPaste(helper, owner, FIRST, SECOND);

    helper.assertValueEqual(to.plate().mode(), PlateMode.HOSTILE, NOT_PASTED);
    helper.succeed();
  }

  /** The item and the count go to the other sensor (answer 725). */
  @GameTest(template = EMPTY)
  public static void anItemSensorPastesItsItemAndCount(GameTestHelper helper) {
    Player owner = CopyScene.copier(helper, new ArrayList<>());
    ItemSensorPart from = ItemSensorScene.place(helper, owner, STONE, Blocks.CHEST);
    ItemSensorPart to = ItemSensorScene.place(helper, owner, OTHER_STONE, Blocks.CHEST);
    ItemSensorScene.expect(from, Items.DIAMOND, SEVEN);

    CopyScene.copyThenPaste(helper, owner, FIRST, SECOND);

    helper.assertValueEqual(to.sensor().item(), from.sensor().item(), NOT_PASTED);
    helper.assertValueEqual(to.sensor().count(), SEVEN, NOT_PASTED);
    helper.succeed();
  }

  /** The mode and strength of a lamp go to the other lamp, not whether it is off (725). */
  @GameTest(template = EMPTY)
  public static void aLampPastesItsModeAndStrength(GameTestHelper helper) {
    Player owner = CopyScene.copier(helper, new ArrayList<>());
    LampPart from = LampScene.place(helper, owner, STONE);
    LampPart to = LampScene.place(helper, owner, OTHER_STONE);
    from.setLamp(from.lamp().withMode(LampMode.PULSE).withStrength(FIVE).toggle());

    CopyScene.copyThenPaste(helper, owner, FIRST, SECOND);

    helper.assertValueEqual(to.lamp().mode(), LampMode.PULSE, NOT_PASTED);
    helper.assertValueEqual(to.lamp().strength().strength(), FIVE, NOT_PASTED);
    helper.assertFalse(to.lamp().switchedOff(), STATE_PASTED);
    helper.succeed();
  }

  /** The cycle of a timer goes to the other timer, not whether it runs (answer 725). */
  @GameTest(template = EMPTY)
  public static void aTimerPastesItsCycleNotItsRun(GameTestHelper helper) {
    Player owner = CopyScene.copier(helper, new ArrayList<>());
    TimerPart from =
        CopyScene.place(helper, owner, RedweaveItems.SIGNAL_TIMER.get(), STONE, TimerPart.class);
    TimerPart to =
        CopyScene.place(
            helper, owner, RedweaveItems.SIGNAL_TIMER.get(), OTHER_STONE, TimerPart.class);
    SignalTimer timer = from.timer().orElseThrow();
    from.setTimer(timer.withCycle(timer.cycle().withPulse(SEVEN)).start());

    CopyScene.copyThenPaste(helper, owner, FIRST, SECOND);

    SignalTimer pasted = to.timer().orElseThrow();
    helper.assertValueEqual(pasted.cycle(), from.timer().orElseThrow().cycle(), NOT_PASTED);
    helper.assertValueEqual(pasted.cycle().pulseTicks(), SEVEN, NOT_PASTED);
    helper.assertFalse(pasted.isRunning(), STATE_PASTED);
    helper.succeed();
  }

  /** A copy of a lever never pastes on a button, and says where it came from (728). */
  @GameTest(template = EMPTY)
  public static void aCopyOnlyPastesOnTheSameType(GameTestHelper helper) {
    List<Component> heard = new ArrayList<>();
    Player owner = CopyScene.copier(helper, heard);
    LeverPart from = CopyScene.place(helper, owner, CopyScene.lever(), STONE, LeverPart.class);
    ButtonPart to = ButtonScene.place(helper, owner, OTHER_STONE);
    from.setLever(from.lever().withStrength(FIVE));

    CopyScene.copyThenPaste(helper, owner, FIRST, SECOND);

    helper.assertValueEqual(to.button().strength().strength(), Signal.MAX.strength(), CHANGED);
    helper.assertValueEqual(
        CopyScene.lastKey(heard), Optional.of(ConfiguratorMessages.WRONG_TYPE), WRONG_MESSAGE);
    helper.succeed();
  }

  /** Only the owner pastes; anyone else is told whose it is (answers 378 and 726). */
  @GameTest(template = EMPTY)
  public static void aStrangerPastesNothing(GameTestHelper helper) {
    List<Component> heard = new ArrayList<>();
    Player owner = CopyScene.copier(helper, new ArrayList<>());
    Player stranger = CopyScene.copier(helper, heard);
    LeverPart from = CopyScene.place(helper, owner, CopyScene.lever(), STONE, LeverPart.class);
    LeverPart to = CopyScene.place(helper, owner, CopyScene.lever(), OTHER_STONE, LeverPart.class);
    from.setLever(from.lever().withStrength(FIVE));
    ItemStack copied = ConfiguratorGameTests.holdConfigurator(owner, ConfiguratorMode.COPY);
    NodeScene.click(helper, owner, FIRST);

    stranger.setItemInHand(InteractionHand.MAIN_HAND, copied.copy());
    NodeScene.shiftClick(helper, stranger, SECOND);

    helper.assertValueEqual(to.lever().strength().strength(), Signal.MAX.strength(), CHANGED);
    helper.assertValueEqual(
        CopyScene.lastKey(heard), Optional.of(ModuleMessages.NOT_OWNER), WRONG_MESSAGE);
    helper.succeed();
  }

  /** A lever just placed joins the network of the copy and keeps its channel (answer 732). */
  @GameTest(template = EMPTY)
  public static void aPasteCarriesTheNetworkAndTheChannel(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    NetworkId network = NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    LeverPart from = CopyScene.place(helper, owner, CopyScene.lever(), STONE, LeverPart.class);
    LeverPart to = CopyScene.place(helper, owner, CopyScene.lever(), OTHER_STONE, LeverPart.class);
    RedweaveServer.running()
        .orElseThrow()
        .modules()
        .chooseNetwork()
        .choose(new PlayerId(owner.getUUID()), from.module().orElseThrow(), network);
    from.connectOutput(Connection.on(ChannelColor.RED));

    CopyScene.copyThenPaste(helper, owner, FIRST, SECOND);

    helper.assertValueEqual(to.module().orElseThrow().network(), Optional.of(network), NOT_JOINED);
    helper.assertValueEqual(to.output(), Connection.on(ChannelColor.RED), NOT_JOINED);
    helper.succeed();
  }

  /** With the network of the copy gone, the channels are dropped and the player told (732). */
  @GameTest(template = EMPTY)
  public static void aGoneNetworkDropsTheChannels(GameTestHelper helper) {
    List<Component> heard = new ArrayList<>();
    Player owner = CopyScene.copier(helper, heard);
    LeverPart from = CopyScene.place(helper, owner, CopyScene.lever(), STONE, LeverPart.class);
    LeverPart to = CopyScene.place(helper, owner, CopyScene.lever(), OTHER_STONE, LeverPart.class);
    from.connectOutput(Connection.on(ChannelColor.RED));
    ItemStack configurator = ConfiguratorGameTests.holdConfigurator(owner, ConfiguratorMode.COPY);
    NodeScene.click(helper, owner, FIRST);
    ModuleCopy copied = configurator.get(RedweaveDataComponents.MODULE_COPY.get());
    NetworkId gone = new NetworkId(UUID.randomUUID());
    configurator.set(
        RedweaveDataComponents.MODULE_COPY.get(),
        new ModuleCopy(copied.type(), Optional.of(gone), copied.settings()));

    NodeScene.shiftClick(helper, owner, SECOND);

    helper.assertTrue(to.output().isEdge(), NOT_DROPPED);
    helper.assertValueEqual(
        CopyScene.lastKey(heard),
        Optional.of(ConfiguratorMessages.CHANNELS_CLEARED),
        WRONG_MESSAGE);
    helper.succeed();
  }

  /** A Signal Lock is never copied, and the copy in the Configurator stays as it was (731). */
  @GameTest(template = EMPTY)
  public static void aLockIsNeverCopied(GameTestHelper helper) {
    List<Component> heard = new ArrayList<>();
    Player owner = CopyScene.copier(helper, heard);
    CopyScene.place(helper, owner, CopyScene.lever(), STONE, LeverPart.class);
    LockScene.place(helper, owner, OTHER_STONE);
    ItemStack configurator = ConfiguratorGameTests.holdConfigurator(owner, ConfiguratorMode.COPY);
    NodeScene.click(helper, owner, FIRST);
    ModuleCopy before = configurator.get(RedweaveDataComponents.MODULE_COPY.get());

    NodeScene.click(helper, owner, SECOND);

    helper.assertValueEqual(
        configurator.get(RedweaveDataComponents.MODULE_COPY.get()), before, CHANGED);
    helper.assertValueEqual(
        CopyScene.lastKey(heard), Optional.of(ConfiguratorMessages.NOT_COPIABLE), WRONG_MESSAGE);
    helper.succeed();
  }

  /** Nothing is pasted on a Signal Lock, not even from another lock (answer 731). */
  @GameTest(template = EMPTY)
  public static void nothingIsPastedOnALock(GameTestHelper helper) {
    List<Component> heard = new ArrayList<>();
    Player owner = CopyScene.copier(helper, heard);
    LeverPart from = CopyScene.place(helper, owner, CopyScene.lever(), STONE, LeverPart.class);
    LockPart lock = LockScene.place(helper, owner, OTHER_STONE);
    from.setLever(from.lever().withStrength(FIVE));

    CopyScene.copyThenPaste(helper, owner, FIRST, SECOND);

    helper.assertValueEqual(lock.lock().strength().strength(), Signal.MAX.strength(), CHANGED);
    helper.assertValueEqual(
        CopyScene.lastKey(heard), Optional.of(ConfiguratorMessages.NOT_COPIABLE), WRONG_MESSAGE);
    helper.succeed();
  }

  /** The copy is in the item, and survives its save (answer 729). */
  @GameTest(template = EMPTY)
  public static void theCopySurvivesTheSave(GameTestHelper helper) {
    HolderLookup.Provider registries = helper.getLevel().registryAccess();
    Player owner = CopyScene.copier(helper, new ArrayList<>());
    LeverPart from = CopyScene.place(helper, owner, CopyScene.lever(), STONE, LeverPart.class);
    from.setLever(from.lever().withStrength(FIVE));
    ItemStack configurator = ConfiguratorGameTests.holdConfigurator(owner, ConfiguratorMode.COPY);
    NodeScene.click(helper, owner, FIRST);

    ItemStack loaded = ItemStack.parse(registries, configurator.save(registries)).orElseThrow();

    helper.assertValueEqual(
        loaded.get(RedweaveDataComponents.MODULE_COPY.get()),
        configurator.get(RedweaveDataComponents.MODULE_COPY.get()),
        NOT_KEPT);
    helper.succeed();
  }
}
