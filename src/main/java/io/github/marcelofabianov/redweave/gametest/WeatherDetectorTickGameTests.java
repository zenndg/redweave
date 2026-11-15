package io.github.marcelofabianov.redweave.gametest;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.WeatherDetectorBlock;
import io.github.marcelofabianov.redweave.block.WeatherDetectorBlockEntity;
import io.github.marcelofabianov.redweave.block.part.NodePart;
import io.github.marcelofabianov.redweave.domain.module.WeatherMode;
import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.Direction;
import io.github.marcelofabianov.redweave.domain.network.Face;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedstoneLampBlock;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The tick of the Weather Detector in the world: each mode against the weather and its output on a
 * channel (answers 458 to 461 and 472). The weather is global to the world, so the test that
 * changes it runs alone in its own batch and gives the clear sky back at the end.
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class WeatherDetectorTickGameTests {
  private static final String EMPTY = "empty";
  private static final String WEATHER = "weather";
  private static final BlockPos RAIN_STONE = new BlockPos(0, 1, 2);
  private static final BlockPos THUNDER_STONE = new BlockPos(1, 1, 2);
  private static final BlockPos CLEAR_STONE = new BlockPos(2, 1, 2);
  private static final BlockPos STONE = new BlockPos(1, 1, 1);
  private static final BlockPos CONTROLLER_FLOOR = new BlockPos(2, 0, 2);
  private static final BlockPos CONTROLLER = CONTROLLER_FLOOR.above();
  private static final BlockPos NODE_LAMP = new BlockPos(0, 1, 0);
  private static final float LOOK_NORTH = 180;
  private static final int LONG_WEATHER = 1_000_000;
  private static final float FULL = 1;
  private static final float NONE = 0;
  private static final ChannelColor CHANNEL = ChannelColor.RED;
  private static final String NETWORK = "network";
  private static final String MODE = "mode";
  private static final String OUTPUT = "output";

  private WeatherDetectorTickGameTests() {}

  /** Clear, rain and a storm light only the detectors whose mode matches (459 and 460). */
  @GameTest(template = EMPTY, batch = WEATHER)
  public static void eachModeFollowsTheWeather(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    placeWatching(helper, owner, RAIN_STONE, WeatherMode.RAIN);
    placeWatching(helper, owner, THUNDER_STONE, WeatherMode.THUNDER);
    placeWatching(helper, owner, CLEAR_STONE, WeatherMode.CLEAR);
    ServerLevel level = helper.getLevel();

    helper
        .startSequence()
        .thenExecute(() -> weather(level, false, false))
        .thenWaitUntil(() -> powered(helper, false, false, true))
        .thenExecute(() -> weather(level, true, false))
        .thenWaitUntil(() -> powered(helper, true, false, false))
        .thenExecute(() -> weather(level, true, true))
        .thenWaitUntil(() -> powered(helper, true, true, false))
        .thenExecute(() -> weather(level, false, false))
        .thenSucceed();
  }

  /** With its output on a channel, a node of the network gives the signal to its lamp (461). */
  @GameTest(template = EMPTY)
  public static void aDetectorOnAChannelReachesANode(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NetworkId network = NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    NodeId node = placeNodeOnALamp(helper, owner);
    NodeScene.networkOf(node)
        .configureSlot(
            new PlayerId(owner.getUUID()),
            node,
            CHANNEL.ordinal(),
            new ChannelSlot(Direction.OUTPUT, Face.UP, CHANNEL));
    place(helper, owner, STONE);
    helper.setBlock(STONE.above().north(), Blocks.REDSTONE_LAMP);

    change(
        helper,
        STONE.above(),
        tag -> {
          tag.putUUID(NETWORK, network.value());
          tag.putString(MODE, WeatherMode.CLEAR.key());
          tag.putString(OUTPUT, CHANNEL.key());
        });

    helper.succeedWhen(() -> {
      helper.assertBlockProperty(NODE_LAMP, RedstoneLampBlock.LIT, true);
      helper.assertBlockProperty(STONE.above().north(), RedstoneLampBlock.LIT, false);
    });
  }

  /** A detector in the mode. */
  private static void placeWatching(
      GameTestHelper helper, Player owner, BlockPos stone, WeatherMode mode) {
    place(helper, owner, stone);
    change(helper, stone.above(), tag -> tag.putString(MODE, mode.key()));
  }

  private static void place(GameTestHelper helper, Player player, BlockPos stone) {
    helper.setBlock(stone, Blocks.STONE);
    player.setYHeadRot(LOOK_NORTH);
    GameTestActions.placeOn(
        helper,
        player,
        new ItemStack(RedweaveItems.WEATHER_DETECTOR.get()),
        stone,
        net.minecraft.core.Direction.UP);
  }

  /** Sets the weather at once: the flags of the world and the levels the game eases between. */
  private static void weather(ServerLevel level, boolean raining, boolean thundering) {
    level.setWeatherParameters(
        raining ? 0 : LONG_WEATHER, raining ? LONG_WEATHER : 0, raining, thundering);
    level.setRainLevel(raining ? FULL : NONE);
    level.setThunderLevel(thundering ? FULL : NONE);
  }

  /**
   * Whether each detector is on. Its blockstate, not a lamp: the plates sit side by side in the
   * small template, and a lit lamp, a solid block, would power the lamp next to it.
   */
  private static void powered(GameTestHelper helper, boolean rain, boolean thunder, boolean clear) {
    helper.assertBlockProperty(RAIN_STONE.above(), WeatherDetectorBlock.POWERED, rain);
    helper.assertBlockProperty(THUNDER_STONE.above(), WeatherDetectorBlock.POWERED, thunder);
    helper.assertBlockProperty(CLEAR_STONE.above(), WeatherDetectorBlock.POWERED, clear);
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

  /** Changes the NBT of the detector, as a `/data merge` would. */
  private static void change(GameTestHelper helper, BlockPos at, Consumer<CompoundTag> changes) {
    WeatherDetectorBlockEntity detector = helper.getBlockEntity(at);
    RegistryAccess registries = helper.getLevel().registryAccess();
    CompoundTag tag = detector.saveWithoutMetadata(registries);
    changes.accept(tag);
    detector.loadWithComponents(tag, registries);
  }
}
