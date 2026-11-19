package io.github.zenndg.redweave.gametest;

import com.mojang.authlib.GameProfile;
import io.github.zenndg.redweave.block.part.AlarmPart;
import io.github.zenndg.redweave.domain.signal.ChannelColor;
import io.github.zenndg.redweave.menu.AlarmAction;
import io.github.zenndg.redweave.menu.AlarmMenu;
import io.github.zenndg.redweave.registry.RedweaveItems;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayerFactory;

/** The Signal Alarm of a GameTest: placed on a block of stone, with its front to the north. */
final class AlarmScene {
  private static final float LOOK_NORTH = 180;
  private static final int MENU_ID = 1;
  private static final int FIRST_NETWORK = 0;

  private AlarmScene() {}

  /** An alarm on top of a block of stone, by the player, who owns it, with its front north. */
  static AlarmPart place(GameTestHelper helper, Player owner, BlockPos stone) {
    helper.setBlock(stone, Blocks.STONE);
    owner.setYRot(LOOK_NORTH);
    owner.setYHeadRot(LOOK_NORTH);
    GameTestActions.placeOn(
        helper, owner, new ItemStack(RedweaveItems.SIGNAL_ALARM.get()), stone, Direction.UP);
    return ModuleParts.only(helper, stone.above(), AlarmPart.class);
  }

  /** The owner picks the first network of the alarm, then a channel for its input (answer 643). */
  static void listen(GameTestHelper helper, Player player, BlockPos alarm, ChannelColor color) {
    ServerPlayer viewer = viewerAs(helper, player, alarm);
    AlarmMenu menu = new AlarmMenu(MENU_ID, viewer, ModuleParts.spotOf(helper, alarm));
    menu.broadcastChanges();
    menu.clickMenuButton(viewer, AlarmAction.choose(FIRST_NETWORK).button());
    menu.clickMenuButton(viewer, AlarmAction.connect(Optional.of(color.ordinal())).button());
  }

  /** A button of the Signal Alarm Screen clicked by the player. */
  static void click(GameTestHelper helper, Player player, BlockPos alarm, AlarmAction action) {
    ServerPlayer viewer = viewerAs(helper, player, alarm);
    AlarmMenu menu = new AlarmMenu(MENU_ID, viewer, ModuleParts.spotOf(helper, alarm));
    menu.clickMenuButton(viewer, action.button());
  }

  /** A server player with the profile of the player, standing by the alarm. */
  private static ServerPlayer viewerAs(GameTestHelper helper, Player player, BlockPos part) {
    ServerPlayer viewer =
        FakePlayerFactory.get(
            helper.getLevel(), new GameProfile(player.getUUID(), player.getScoreboardName()));
    viewer.setPos(Vec3.atCenterOf(helper.absolutePos(part.above())));
    return viewer;
  }
}
