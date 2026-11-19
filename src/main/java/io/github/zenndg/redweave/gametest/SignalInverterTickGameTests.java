package io.github.zenndg.redweave.gametest;

import io.github.zenndg.redweave.RedweaveMod;
import io.github.zenndg.redweave.block.part.InverterPart;
import io.github.zenndg.redweave.block.part.ModulePart;
import io.github.zenndg.redweave.block.part.NodePart;
import io.github.zenndg.redweave.domain.module.InverterState;
import io.github.zenndg.redweave.domain.network.ChannelSlot;
import io.github.zenndg.redweave.domain.network.Direction;
import io.github.zenndg.redweave.domain.network.Face;
import io.github.zenndg.redweave.domain.network.NetworkId;
import io.github.zenndg.redweave.domain.network.NodeId;
import io.github.zenndg.redweave.domain.network.PlayerId;
import io.github.zenndg.redweave.domain.signal.ChannelColor;
import io.github.zenndg.redweave.registry.RedweaveItems;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedstoneLampBlock;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The tick of the Signal Inverter in the world: its back edge with the delay of a torch, its output
 * on a channel and the burnout of a loop (answers 444, 445 and 453). The tests join a network in
 * the NBT, as a `/data merge` would.
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class SignalInverterTickGameTests {
  private static final String EMPTY = "empty";
  private static final BlockPos STONE = new BlockPos(1, 1, 1);
  private static final BlockPos INVERTER = STONE.above();
  private static final BlockPos LAMP_IN_FRONT = INVERTER.north();
  private static final BlockPos BEHIND = INVERTER.south();
  private static final BlockPos CONTROLLER_FLOOR = new BlockPos(2, 0, 2);
  private static final BlockPos CONTROLLER = CONTROLLER_FLOOR.above();
  private static final BlockPos NODE_LAMP = new BlockPos(0, 1, 0);
  private static final float LOOK_NORTH = 180;
  private static final ChannelColor CHANNEL = ChannelColor.RED;
  private static final String NETWORK = "network";
  private static final String INPUT = "input";
  private static final String OUTPUT = "output";
  private static final String TOO_SOON = "The inverter should wait for its delay";
  private static final String NOT_BURNT = "A loop should burn the inverter out";

  private SignalInverterTickGameTests() {}

  /** A signal behind turns B off, not on the next tick but after the delay (answer 444). */
  @GameTest(template = EMPTY)
  public static void aSignalBehindTurnsTheOutputOffAfterTheDelay(GameTestHelper helper) {
    placeInverter(helper, helper.makeMockPlayer(GameType.SURVIVAL));

    helper
        .startSequence()
        .thenWaitUntil(() -> ModuleParts.assertPowered(helper, INVERTER, true))
        .thenExecute(() -> helper.setBlock(BEHIND, Blocks.REDSTONE_BLOCK))
        .thenIdle(1)
        .thenExecute(() -> powered(helper, true))
        .thenWaitUntil(() -> powered(helper, false))
        .thenExecute(() -> helper.setBlock(BEHIND, Blocks.AIR))
        .thenWaitUntil(() -> powered(helper, true))
        .thenSucceed();
  }

  /** With its output on a channel, a node of the network gives the signal to its lamp (375). */
  @GameTest(template = EMPTY)
  public static void anInverterOnAChannelReachesANode(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NetworkId network = NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    NodeId node = placeNodeOnALamp(helper, owner);
    NodeScene.networkOf(node)
        .configureSlot(
            new PlayerId(owner.getUUID()),
            node,
            CHANNEL.ordinal(),
            new ChannelSlot(Direction.OUTPUT, Face.UP, CHANNEL));
    placeInverter(helper, owner);
    helper.setBlock(LAMP_IN_FRONT, Blocks.REDSTONE_LAMP);

    change(
        helper,
        tag -> {
          tag.putUUID(NETWORK, network.value());
          tag.putString(OUTPUT, CHANNEL.key());
        });

    helper.succeedWhen(() -> {
      helper.assertBlockProperty(NODE_LAMP, RedstoneLampBlock.LIT, true);
      helper.assertBlockProperty(LAMP_IN_FRONT, RedstoneLampBlock.LIT, false);
    });
  }

  /** Reading its own channel, the inverter flips until it burns out, dark (445 and 455). */
  @GameTest(template = EMPTY)
  public static void anInverterInALoopBurnsOut(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NetworkId network = NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    placeInverter(helper, owner);

    change(
        helper,
        tag -> {
          tag.putUUID(NETWORK, network.value());
          tag.putString(INPUT, CHANNEL.key());
          tag.putString(OUTPUT, CHANNEL.key());
        });

    InverterPart inverter = ModuleParts.only(helper, INVERTER, InverterPart.class);
    helper.succeedWhen(() -> {
      ModuleParts.assertBurnt(helper, INVERTER, true);
      powered(helper, false);
      helper.assertValueEqual(inverter.inverter().state(), InverterState.BURNT_OUT, NOT_BURNT);
    });
  }

  private static void placeInverter(GameTestHelper helper, Player player) {
    helper.setBlock(STONE, Blocks.STONE);
    player.setYHeadRot(LOOK_NORTH);
    GameTestActions.placeOn(
        helper,
        player,
        new ItemStack(RedweaveItems.SIGNAL_INVERTER.get()),
        STONE,
        net.minecraft.core.Direction.UP);
  }

  /** A node on top of a lamp, in the network, which feeds the lamp under it (answer 71). */
  private static NodeId placeNodeOnALamp(GameTestHelper helper, Player owner) {
    helper.setBlock(NODE_LAMP, Blocks.REDSTONE_LAMP);
    GameTestActions.placeOn(
        helper,
        owner,
        new ItemStack(RedweaveItems.SIGNAL_NODE.get()),
        NODE_LAMP,
        net.minecraft.core.Direction.UP);
    NodeScene.choose(helper, owner, NODE_LAMP.above(), NodeScene.FIRST_CHOICE);
    NodePart block = NodeParts.only(helper, NODE_LAMP.above());
    return block.node().orElseThrow();
  }

  /** Changes the NBT of the inverter, as a `/data merge` would. */
  private static void change(GameTestHelper helper, Consumer<CompoundTag> changes) {
    CompoundTag tag = ModuleParts.only(helper, INVERTER, ModulePart.class).save();
    changes.accept(tag);
    ModuleParts.reload(helper, INVERTER, tag);
  }

  private static void powered(GameTestHelper helper, boolean on) {
    ModuleParts.assertPowered(helper, INVERTER, on);
  }
}
