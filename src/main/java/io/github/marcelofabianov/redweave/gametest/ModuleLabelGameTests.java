package io.github.marcelofabianov.redweave.gametest;

import com.mojang.authlib.GameProfile;
import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.SignalTimerBlockEntity;
import io.github.marcelofabianov.redweave.domain.module.SignalModule;
import io.github.marcelofabianov.redweave.menu.TimerScreenTexts;
import io.github.marcelofabianov.redweave.network.NodeLabelsUpdate;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** The label over a Signal Module with the Configurator in hand (answer 417). */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class ModuleLabelGameTests {
  private static final String EMPTY = "empty";
  private static final String HOLDER_NAME = "holder";
  private static final BlockPos STONE = new BlockPos(1, 1, 1);
  private static final BlockPos TIMER = STONE.above();
  private static final int NO_CHANNEL = -1;
  private static final String NOT_SENT = "The holder should get the name and state of the timer";
  private static final String NOT_CHANGED = "A timer started should send its new state";

  private ModuleLabelGameTests() {}

  /** The name, the state and no channel, and the state again once the timer runs. */
  @GameTest(template = EMPTY)
  public static void theHolderGetsTheStateOfTheTimer(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    helper.setBlock(STONE, Blocks.STONE);
    GameTestActions.placeOn(
        helper, owner, new ItemStack(RedweaveItems.SIGNAL_TIMER.get()), STONE, Direction.UP);
    SignalTimerBlockEntity timer = helper.getBlockEntity(TIMER);
    ServerPlayer holder = holder(helper);

    UUID id = timer.module().orElseThrow().id().value();
    Optional<NodeLabelsUpdate.ModuleLine> first = lineOf(server().labelWatch().update(holder), id);
    timer.toggle();
    Optional<NodeLabelsUpdate.ModuleLine> after = lineOf(server().labelWatch().update(holder), id);

    String name = timer.module().map(SignalModule::displayName).orElseThrow();
    helper.assertValueEqual(
        first,
        Optional.of(
            new NodeLabelsUpdate.ModuleLine(
                id, helper.absolutePos(TIMER), name, TimerScreenTexts.STOPPED, NO_CHANNEL)),
        NOT_SENT);
    helper.assertValueEqual(
        after.map(NodeLabelsUpdate.ModuleLine::state),
        Optional.of(TimerScreenTexts.RUNNING),
        NOT_CHANGED);
    helper.succeed();
  }

  /** Other tests run in the same world, so the line is picked by the ID of this module. */
  private static Optional<NodeLabelsUpdate.ModuleLine> lineOf(
      Optional<NodeLabelsUpdate> update, UUID module) {
    return update.flatMap(
        found -> found.modules().stream().filter(line -> line.module().equals(module)).findFirst());
  }

  private static ServerPlayer holder(GameTestHelper helper) {
    ServerPlayer holder =
        FakePlayerFactory.get(helper.getLevel(), new GameProfile(UUID.randomUUID(), HOLDER_NAME));
    holder.setPos(Vec3.atCenterOf(helper.absolutePos(TIMER.above())));
    holder.setItemInHand(
        InteractionHand.MAIN_HAND, new ItemStack(RedweaveItems.CONFIGURATOR.get()));
    return holder;
  }

  private static RedweaveServer server() {
    return RedweaveServer.running().orElseThrow();
  }
}
