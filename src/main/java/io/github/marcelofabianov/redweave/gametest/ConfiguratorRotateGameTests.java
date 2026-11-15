package io.github.marcelofabianov.redweave.gametest;

import com.mojang.authlib.GameProfile;
import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.ModuleMessages;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.item.ConfiguratorItem;
import io.github.marcelofabianov.redweave.item.ConfiguratorMessages;
import io.github.marcelofabianov.redweave.item.ConfiguratorMode;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The Rotate mode of the Configurator: it turns a Signal Module with the message of the new front,
 * tells that only modules turn on a node or a cable, and keeps its saved name (answers 610 and 613
 * to 619).
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class ConfiguratorRotateGameTests {
  private static final String EMPTY = "empty";
  private static final String LISTENER_NAME = "rotate_listener";
  private static final BlockPos STONE = new BlockPos(1, 1, 1);
  private static final BlockPos PLATE = STONE.above();
  private static final String NO_OUTPUT = "A turn should tell the new front of the plate";
  private static final String NOT_ONLY_MODULES = "The Rotate should tell that only modules turn";
  private static final String GONE = "The Rotate should leave the part where it was";
  private static final String NOT_KEPT = "A Configurator in the Rotate should keep its mode";

  private ConfiguratorRotateGameTests() {}

  /** A turn tells the new front in the action bar, as `Output: East` (answers 432 and 619). */
  @GameTest(template = EMPTY)
  public static void aTurnTellsTheNewFront(GameTestHelper helper) {
    List<Component> heard = new ArrayList<>();
    Player owner = listener(helper, heard);
    helper.setBlock(STONE, Blocks.STONE);
    GameTestActions.placeOn(
        helper, owner, new ItemStack(RedweaveItems.SIGNAL_TIMER.get()), STONE, Direction.UP);
    ConfiguratorGameTests.holdConfigurator(owner, ConfiguratorMode.ROTATE);

    NodeScene.click(helper, owner, PLATE);

    helper.assertValueEqual(lastKey(heard), Optional.of(ModuleMessages.OUTPUT), NO_OUTPUT);
    helper.succeed();
  }

  /** A node does not turn, and the action bar says only modules do (answer 617). */
  @GameTest(template = EMPTY)
  public static void theRotateTellsANodeDoesNotTurn(GameTestHelper helper) {
    List<Component> heard = new ArrayList<>();
    Player owner = listener(helper, heard);
    NodeScene.placeLooseNode(helper, owner, STONE, Direction.UP);
    ConfiguratorGameTests.holdConfigurator(owner, ConfiguratorMode.ROTATE);

    NodeScene.click(helper, owner, PLATE);

    helper.assertValueEqual(
        lastKey(heard), Optional.of(ConfiguratorMessages.ONLY_MODULES), NOT_ONLY_MODULES);
    helper.assertTrue(NodeParts.only(helper, PLATE).node().isPresent(), GONE);
    helper.succeed();
  }

  /** A cable does not turn either, and stays in its space (answer 617). */
  @GameTest(template = EMPTY)
  public static void theRotateTellsACableDoesNotTurn(GameTestHelper helper) {
    List<Component> heard = new ArrayList<>();
    Player owner = listener(helper, heard);
    helper.setBlock(STONE, Blocks.STONE);
    CableScene.place(helper, owner, STONE, Direction.UP, ChannelColor.RED);
    ConfiguratorGameTests.holdConfigurator(owner, ConfiguratorMode.ROTATE);

    NodeScene.click(helper, owner, PLATE);

    helper.assertValueEqual(
        lastKey(heard), Optional.of(ConfiguratorMessages.ONLY_MODULES), NOT_ONLY_MODULES);
    CableScene.at(helper, PLATE, Direction.DOWN);
    helper.succeed();
  }

  /** The saved name `rotate` comes back as the Rotate mode (answer 613 and rule 15). */
  @GameTest(template = EMPTY)
  public static void theRotateModeSurvivesTheSave(GameTestHelper helper) {
    HolderLookup.Provider registries = helper.getLevel().registryAccess();
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    ItemStack configurator = ConfiguratorGameTests.holdConfigurator(owner, ConfiguratorMode.ROTATE);

    ItemStack loaded = ItemStack.parse(registries, configurator.save(registries)).orElseThrow();

    helper.assertValueEqual(ConfiguratorItem.modeOf(loaded), ConfiguratorMode.ROTATE, NOT_KEPT);
    helper.succeed();
  }

  /** A mock player in survival that keeps what the action bar shows it. */
  private static Player listener(GameTestHelper helper, List<Component> heard) {
    return new Player(
        helper.getLevel(), BlockPos.ZERO, 0, new GameProfile(UUID.randomUUID(), LISTENER_NAME)) {
      @Override
      public boolean isSpectator() {
        return false;
      }

      @Override
      public boolean isCreative() {
        return false;
      }

      @Override
      public boolean isLocalPlayer() {
        return true;
      }

      @Override
      public void displayClientMessage(Component message, boolean actionBar) {
        heard.add(message);
      }
    };
  }

  private static Optional<String> lastKey(List<Component> heard) {
    if (heard.isEmpty()) {
      return Optional.empty();
    }
    return heard.getLast().getContents() instanceof TranslatableContents translatable
        ? Optional.of(translatable.getKey())
        : Optional.empty();
  }
}
