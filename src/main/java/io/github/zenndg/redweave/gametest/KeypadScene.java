package io.github.zenndg.redweave.gametest;

import com.mojang.authlib.GameProfile;
import io.github.zenndg.redweave.block.part.KeypadPart;
import io.github.zenndg.redweave.domain.network.NetworkId;
import io.github.zenndg.redweave.domain.network.PlayerId;
import io.github.zenndg.redweave.domain.network.TerminalActions;
import io.github.zenndg.redweave.domain.signal.ChannelColor;
import io.github.zenndg.redweave.menu.KeypadAction;
import io.github.zenndg.redweave.menu.KeypadMenu;
import io.github.zenndg.redweave.registry.RedweaveItems;
import io.github.zenndg.redweave.server.RedweaveServer;
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
 * The Signal Keypad of a GameTest: placed on a block of stone looking north, so its output is on
 * the north edge (answer 663), set as the owner does on its screen.
 */
final class KeypadScene {
  private static final float LOOK_NORTH = 180;
  private static final int MENU_ID = 1;
  private static final int FIRST_NETWORK = 0;

  private KeypadScene() {}

  /** A keypad on top of a block of stone, by the player, who owns it. */
  static KeypadPart place(GameTestHelper helper, Player owner, BlockPos stone) {
    helper.setBlock(stone, Blocks.STONE);
    owner.setYRot(LOOK_NORTH);
    owner.setYHeadRot(LOOK_NORTH);
    GameTestActions.placeOn(
        helper, owner, new ItemStack(RedweaveItems.SIGNAL_KEYPAD.get()), stone, Direction.UP);
    return ModuleParts.only(helper, stone.above(), KeypadPart.class);
  }

  /** The owner picks the first network, then a channel for a connection, on the screen. */
  static void connect(
      GameTestHelper helper,
      Player player,
      BlockPos keypad,
      KeypadAction.Kind kind,
      ChannelColor color) {
    ServerPlayer viewer = viewerAs(helper, player, keypad);
    KeypadMenu menu = new KeypadMenu(MENU_ID, viewer, ModuleParts.spotOf(helper, keypad));
    menu.broadcastChanges();
    menu.clickMenuButton(viewer, KeypadAction.choose(FIRST_NETWORK).button());
    menu.clickMenuButton(viewer, new KeypadAction(kind, color.ordinal() + 1).button());
  }

  /** A button of the Signal Flip-Flop Screen clicked by the player. */
  static void click(GameTestHelper helper, Player player, BlockPos keypad, KeypadAction action) {
    ServerPlayer viewer = viewerAs(helper, player, keypad);
    KeypadMenu menu = new KeypadMenu(MENU_ID, viewer, ModuleParts.spotOf(helper, keypad));
    menu.clickMenuButton(viewer, action.button());
  }

  /** The owner turns the network off or on again at a Network Terminal (answer 88). */
  static void turn(NetworkId network, Player owner, boolean on) {
    TerminalActions terminal =
        RedweaveServer.running().orElseThrow().network(network).orElseThrow().terminal();
    PlayerId requester = new PlayerId(owner.getUUID());
    if (on) {
      terminal.turnOn(requester);
    } else {
      terminal.turnOff(requester);
    }
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
