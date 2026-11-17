package io.github.marcelofabianov.redweave.gametest;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.part.LeverPart;
import io.github.marcelofabianov.redweave.domain.network.ModuleId;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The register of the Signal Modules in the world: a loaded module on a network joins its register,
 * a broken one leaves it, and one taken out by `Clear components` lets go of the network on its
 * next tick (rede-e-canais.md, answers 803, 805, 806, 807 and 811).
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class ModuleRegisterGameTests {
  private static final String EMPTY = "empty";
  private static final BlockPos STONE = new BlockPos(1, 1, 1);
  private static final BlockPos LEVER = STONE.above();
  private static final BlockPos CONTROLLER_FLOOR = new BlockPos(0, 0, 2);
  private static final BlockPos CONTROLLER = CONTROLLER_FLOOR.above();
  private static final int ONE = 1;
  private static final String NOT_REGISTERED = "A module on a network should join its register";
  private static final String NOT_COUNTED = "The network should count the node-less module";
  private static final String STAYED = "A broken module should leave the register";
  private static final String KEPT_NETWORK = "A module taken out should let go of the network";

  private ModuleRegisterGameTests() {}

  @GameTest(template = EMPTY)
  public static void aModuleOnANetworkJoinsItsRegister(GameTestHelper helper) {
    Scene scene = Scene.of(helper);

    helper.succeedWhen(() -> {
      helper.assertTrue(
          scene.network().register().entry(scene.module()).isPresent(), NOT_REGISTERED);
      helper.assertValueEqual(scene.network().components(), ONE, NOT_COUNTED);
    });
  }

  @GameTest(template = EMPTY)
  public static void aBrokenModuleLeavesTheRegister(GameTestHelper helper) {
    Scene scene = Scene.of(helper);

    helper
        .startSequence()
        .thenWaitUntil(() ->
            helper.assertTrue(
                scene.network().register().entry(scene.module()).isPresent(), NOT_REGISTERED))
        .thenExecute(() -> helper.setBlock(LEVER, Blocks.AIR))
        .thenWaitUntil(() ->
            helper.assertTrue(scene.network().register().entry(scene.module()).isEmpty(), STAYED))
        .thenSucceed();
  }

  /** `Clear components` takes it out, and the module lets go on its next tick (807 and 811). */
  @GameTest(template = EMPTY)
  public static void aModuleTakenOutLetsGoOfTheNetwork(GameTestHelper helper) {
    Scene scene = Scene.of(helper);

    helper
        .startSequence()
        .thenWaitUntil(() ->
            helper.assertTrue(
                scene.network().register().entry(scene.module()).isPresent(), NOT_REGISTERED))
        .thenExecute(() -> scene.network().terminal().clearComponents(scene.owner()))
        .thenWaitUntil(() ->
            helper.assertTrue(
                scene.lever().module().orElseThrow().network().isEmpty(), KEPT_NETWORK))
        .thenSucceed();
  }

  /** A controller and a lever of the same owner, the lever on the network, as from its screen. */
  private record Scene(NetworkId id, LeverPart lever, PlayerId owner) {
    static Scene of(GameTestHelper helper) {
      ServerPlayer player = helper.makeMockServerPlayerInLevel();
      NetworkId id = NodeScene.placeController(helper, player, CONTROLLER_FLOOR, CONTROLLER);
      LeverPart lever = LeverScene.place(helper, player, STONE);
      PlayerId owner = new PlayerId(player.getUUID());
      lever.module().orElseThrow().choose(owner, id);
      return new Scene(id, lever, owner);
    }

    Network network() {
      return RedweaveServer.running().orElseThrow().network(id).orElseThrow();
    }

    ModuleId module() {
      return lever.module().orElseThrow().id();
    }
  }
}
