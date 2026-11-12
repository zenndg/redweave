package io.github.marcelofabianov.redweave.block;

import io.github.marcelofabianov.redweave.application.TerminalPlacement;
import io.github.marcelofabianov.redweave.domain.network.BlockLocation;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;

/** The world side of placing a Network Terminal, for its item (answers 137 and 253). */
public final class NetworkTerminalPlacement {
  private NetworkTerminalPlacement() {}

  /** Asks for the one terminal of the player, reading the world without loading a chunk. */
  public static TerminalPlacement ask(
      RedweaveServer server, MinecraftServer game, PlayerId owner, BlockLocation at) {
    return server.terminal().placeTerminal().place(owner, at, WorldTerminals.of(game));
  }

  /** The placed block keeps who placed it (answer 175). */
  public static void own(Level level, BlockPos pos, UUID owner) {
    if (level.getBlockEntity(pos) instanceof NetworkTerminalBlockEntity terminal) {
      terminal.own(owner);
    }
  }
}
