package io.github.marcelofabianov.redweave.block;

import io.github.marcelofabianov.redweave.application.LoadedNodes;
import io.github.marcelofabianov.redweave.block.part.PartSpot;
import io.github.marcelofabianov.redweave.domain.network.BlockLocation;
import io.github.marcelofabianov.redweave.domain.network.NodePosition;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

/**
 * Finds the block of a node from its position in the domain, which is the block it sticks to and
 * the face: the node is the next block on that face. Never loads a chunk to find it (rule 6).
 */
public final class NodeBlocks {
  private NodeBlocks() {}

  /** The block of the node itself, next to the block it sticks to. */
  public static BlockPos posOf(NodePosition node) {
    BlockLocation support = node.block();
    return new BlockPos(support.x(), support.y(), support.z())
        .relative(Faces.toDirection(node.face()));
  }

  /** The block of a node, when its dimension exists and its chunk is loaded (rule 6). */
  static Optional<Located> locate(MinecraftServer minecraft, NodePosition node) {
    BlockPos pos = posOf(node);
    return levelOf(minecraft, node.block().dimension())
        .filter(level -> level.isLoaded(pos))
        .map(level -> new Located(level, spotOf(node)));
  }

  /** The spot of the node part: its space and the side it rests on, toward its support. */
  public static PartSpot spotOf(NodePosition node) {
    return new PartSpot(posOf(node), Faces.toDirection(node.face()).getOpposite());
  }

  /** The world answering whether a node is loaded, for the Controller Screen (answer 29). */
  public static LoadedNodes loadedIn(MinecraftServer minecraft) {
    return node -> locate(minecraft, node).isPresent();
  }

  private static Optional<ServerLevel> levelOf(MinecraftServer minecraft, String dimension) {
    ResourceLocation id = ResourceLocation.tryParse(dimension);
    if (id == null) {
      return Optional.empty();
    }
    return Optional.ofNullable(minecraft.getLevel(ResourceKey.create(Registries.DIMENSION, id)));
  }

  record Located(ServerLevel level, PartSpot spot) {}
}
