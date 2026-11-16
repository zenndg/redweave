package io.github.marcelofabianov.redweave.gametest;

import com.mojang.authlib.GameProfile;
import io.github.marcelofabianov.redweave.block.part.ModulePart;
import io.github.marcelofabianov.redweave.item.ConfiguratorMode;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

/** Players and clicks for the copy of every item by the Configurator (answers 722 to 731). */
final class CopyScene {
  private static final String LISTENER_NAME = "Copier";
  private static final float LOOK_NORTH = 180;

  private CopyScene() {}

  /** A survival player who keeps what the action bar shows them, holding a Copy Configurator. */
  static Player copier(GameTestHelper helper, List<Component> heard) {
    Player player =
        new Player(
            helper.getLevel(),
            BlockPos.ZERO,
            0,
            new GameProfile(UUID.randomUUID(), LISTENER_NAME)) {
          @Override
          public boolean isSpectator() {
            return false;
          }

          @Override
          public boolean isCreative() {
            return false;
          }

          @Override
          public void displayClientMessage(Component message, boolean actionBar) {
            heard.add(message);
          }
        };
    player.setYRot(LOOK_NORTH);
    player.setYHeadRot(LOOK_NORTH);
    return player;
  }

  /** A module of the item on top of a block of stone, by the player, who owns it. */
  static <T extends ModulePart> T place(
      GameTestHelper helper, Player owner, Item item, BlockPos stone, Class<T> type) {
    helper.setBlock(stone, Blocks.STONE);
    GameTestActions.placeOn(helper, owner, new ItemStack(item), stone, Direction.UP);
    return ModuleParts.only(helper, stone.above(), type);
  }

  /** Copies the module at the spot, then pastes on the other, with the same Configurator. */
  static ItemStack copyThenPaste(GameTestHelper helper, Player player, BlockPos from, BlockPos to) {
    ItemStack configurator = ConfiguratorGameTests.holdConfigurator(player, ConfiguratorMode.COPY);
    NodeScene.click(helper, player, from);
    NodeScene.shiftClick(helper, player, to);
    return configurator;
  }

  static Item lever() {
    return RedweaveItems.SIGNAL_LEVER.get();
  }

  /** The key of the last message of the action bar, if any. */
  static Optional<String> lastKey(List<Component> heard) {
    if (heard.isEmpty()) {
      return Optional.empty();
    }
    return heard.getLast().getContents() instanceof TranslatableContents translatable
        ? Optional.of(translatable.getKey())
        : Optional.empty();
  }
}
