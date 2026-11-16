package io.github.marcelofabianov.redweave.gametest;

import com.mojang.authlib.GameProfile;
import io.github.marcelofabianov.redweave.block.part.LampPart;
import io.github.marcelofabianov.redweave.block.part.LeverPart;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.menu.LampAction;
import io.github.marcelofabianov.redweave.menu.LampMenu;
import io.github.marcelofabianov.redweave.menu.LeverAction;
import io.github.marcelofabianov.redweave.menu.LeverMenu;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
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

/**
 * The Signal Lamp of a GameTest: placed on a block of stone, with a Signal Lever of the same
 * network writing the channels it listens to, set as the owner does on their screens.
 */
final class LampScene {
  private static final float LOOK_NORTH = 180;
  private static final int MENU_ID = 1;
  private static final int FIRST_NETWORK = 0;

  private LampScene() {}

  /** A lamp on top of a block of stone, by the player, who owns it, with its front north. */
  static LampPart place(GameTestHelper helper, Player owner, BlockPos stone) {
    helper.setBlock(stone, Blocks.STONE);
    owner.setYRot(LOOK_NORTH);
    owner.setYHeadRot(LOOK_NORTH);
    GameTestActions.placeOn(
        helper, owner, new ItemStack(RedweaveItems.SIGNAL_LAMP.get()), stone, Direction.UP);
    return ModuleParts.only(helper, stone.above(), LampPart.class);
  }

  /** The owner picks the first network of the lamp, then a channel for the intensity or control. */
  static void listen(
      GameTestHelper helper,
      Player player,
      BlockPos lamp,
      LampAction.Kind kind,
      ChannelColor color) {
    ServerPlayer viewer = viewerAs(helper, player, lamp);
    LampMenu menu = new LampMenu(MENU_ID, viewer, ModuleParts.spotOf(helper, lamp));
    menu.broadcastChanges();
    menu.clickMenuButton(viewer, LampAction.choose(FIRST_NETWORK).button());
    menu.clickMenuButton(viewer, LampAction.connect(kind, Optional.of(color.ordinal())).button());
  }

  /** A button of the Signal Lamp Screen clicked by the player. */
  static void click(GameTestHelper helper, Player player, BlockPos lamp, LampAction action) {
    ServerPlayer viewer = viewerAs(helper, player, lamp);
    LampMenu menu = new LampMenu(MENU_ID, viewer, ModuleParts.spotOf(helper, lamp));
    menu.clickMenuButton(viewer, action.button());
  }

  /** A lever of the network on top of the stone, writing the channel with the strength. */
  static LeverPart writer(
      GameTestHelper helper, Player owner, BlockPos stone, ChannelColor color, int strength) {
    LeverPart lever = LeverScene.place(helper, owner, stone);
    ServerPlayer viewer = viewerAs(helper, owner, stone.above());
    LeverMenu menu = new LeverMenu(MENU_ID, viewer, ModuleParts.spotOf(helper, stone.above()));
    menu.broadcastChanges();
    menu.clickMenuButton(viewer, LeverAction.choose(FIRST_NETWORK).button());
    menu.clickMenuButton(viewer, LeverAction.output(Optional.of(color.ordinal())).button());
    lever.setLever(lever.lever().withStrength(strength));
    return lever;
  }

  /** A server player with the profile of the player, standing by the part. */
  private static ServerPlayer viewerAs(GameTestHelper helper, Player player, BlockPos part) {
    ServerPlayer viewer =
        FakePlayerFactory.get(
            helper.getLevel(), new GameProfile(player.getUUID(), player.getScoreboardName()));
    viewer.setPos(Vec3.atCenterOf(helper.absolutePos(part.above())));
    return viewer;
  }
}
