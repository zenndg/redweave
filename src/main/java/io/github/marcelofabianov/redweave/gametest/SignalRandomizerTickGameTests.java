package io.github.marcelofabianov.redweave.gametest;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.SignalNodeBlockEntity;
import io.github.marcelofabianov.redweave.block.SignalRandomizerBlockEntity;
import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.Direction;
import io.github.marcelofabianov.redweave.domain.network.Face;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
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
 * The tick of the Signal Randomizer in the world: its output held on a channel and read by a node
 * (answers 478, 479 and 489). The value is set in its NBT, with an interval long enough for no new
 * draw during the test, as a `/data merge` would.
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class SignalRandomizerTickGameTests {
  private static final String EMPTY = "empty";
  private static final BlockPos STONE = new BlockPos(1, 1, 1);
  private static final BlockPos RANDOMIZER = STONE.above();
  private static final BlockPos LAMP_IN_FRONT = RANDOMIZER.north();
  private static final BlockPos CONTROLLER_FLOOR = new BlockPos(2, 0, 2);
  private static final BlockPos CONTROLLER = CONTROLLER_FLOOR.above();
  private static final BlockPos NODE_LAMP = new BlockPos(0, 1, 0);
  private static final float LOOK_NORTH = 180;
  private static final int LONG_INTERVAL = 6000;
  private static final ChannelColor CHANNEL = ChannelColor.RED;
  private static final String NETWORK = "network";
  private static final String RUNNING = "running";
  private static final String INTERVAL = "interval_ticks";
  private static final String VALUE = "value";
  private static final String OUTPUT = "output";

  private SignalRandomizerTickGameTests() {}

  /** With its output on a channel, a node of the network gives the held value to its lamp. */
  @GameTest(template = EMPTY)
  public static void aRandomizerOnAChannelReachesANode(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NetworkId network = NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    NodeId node = placeNodeOnALamp(helper, owner);
    NodeScene.networkOf(node)
        .configureSlot(
            new PlayerId(owner.getUUID()),
            node,
            CHANNEL.ordinal(),
            new ChannelSlot(Direction.OUTPUT, Face.UP, CHANNEL));
    place(helper, owner);
    helper.setBlock(LAMP_IN_FRONT, Blocks.REDSTONE_LAMP);

    SignalRandomizerBlockEntity randomizer = helper.getBlockEntity(RANDOMIZER);
    RegistryAccess registries = helper.getLevel().registryAccess();
    CompoundTag tag = randomizer.saveWithoutMetadata(registries);
    tag.putUUID(NETWORK, network.value());
    tag.putBoolean(RUNNING, true);
    tag.putInt(INTERVAL, LONG_INTERVAL);
    tag.putInt(VALUE, Signal.MAX.strength());
    tag.putString(OUTPUT, CHANNEL.key());
    randomizer.loadWithComponents(tag, registries);

    helper.succeedWhen(() -> {
      helper.assertBlockProperty(NODE_LAMP, RedstoneLampBlock.LIT, true);
      helper.assertBlockProperty(LAMP_IN_FRONT, RedstoneLampBlock.LIT, false);
    });
  }

  private static void place(GameTestHelper helper, Player player) {
    helper.setBlock(STONE, Blocks.STONE);
    player.setYHeadRot(LOOK_NORTH);
    GameTestActions.placeOn(
        helper,
        player,
        new ItemStack(RedweaveItems.SIGNAL_RANDOMIZER.get()),
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
    SignalNodeBlockEntity block = helper.getBlockEntity(NODE_LAMP.above());
    return block.node().orElseThrow();
  }
}
