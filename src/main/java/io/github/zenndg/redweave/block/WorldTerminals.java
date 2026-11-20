package io.github.zenndg.redweave.block;

import io.github.zenndg.redweave.application.TerminalCheck;
import io.github.zenndg.redweave.application.TerminalLookup;
import io.github.zenndg.redweave.domain.network.BlockLocation;
import io.github.zenndg.redweave.registry.RedweaveBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

/**
 * Tells what is at a registered Network Terminal without loading its chunk (rule 6 and answer 253).
 * A dimension that no longer exists has no terminal, so the registered one is gone.
 */
final class WorldTerminals {
  private WorldTerminals() {}

  static TerminalLookup of(MinecraftServer server) {
    return terminal -> check(server, terminal);
  }

  private static TerminalCheck check(MinecraftServer server, BlockLocation terminal) {
    ResourceLocation dimension = ResourceLocation.tryParse(terminal.dimension());
    ServerLevel level =
        dimension == null
            ? null
            : server.getLevel(ResourceKey.create(Registries.DIMENSION, dimension));
    if (level == null) {
      return TerminalCheck.GONE;
    }
    BlockPos pos = new BlockPos(terminal.x(), terminal.y(), terminal.z());
    if (!level.isLoaded(pos)) {
      return TerminalCheck.UNLOADED;
    }
    return level.getBlockState(pos).is(RedweaveBlocks.NETWORK_TERMINAL.get())
        ? TerminalCheck.STANDING
        : TerminalCheck.GONE;
  }
}
