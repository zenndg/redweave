package io.github.zenndg.redweave.gametest;

import io.github.zenndg.redweave.block.PartSpaceBlockEntity;
import io.github.zenndg.redweave.block.part.NodePart;
import io.github.zenndg.redweave.block.part.PartSpot;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;

/** The node parts of a space in a GameTest, read as the old node block was (answer 542). */
final class NodeParts {
  private static final String NOT_ONE_NODE = "Expected exactly one node in the space";
  private static final String NODE_MISSING = "Expected a node in the space";
  private static final String NODE_STAYED = "Expected no node in the space";
  private static final String WRONG_POWERED = "The lens of the node is not as expected";
  private static final String WRONG_RING = "The ring of the node is not as expected";

  private NodeParts() {}

  /** The node parts of the space, none if it is not a part space. */
  static List<NodePart> all(GameTestHelper helper, BlockPos relative) {
    return PartSpaceBlockEntity.at(helper.getLevel(), helper.absolutePos(relative))
        .map(space ->
            space.parts().stream()
                .filter(NodePart.class::isInstance)
                .map(NodePart.class::cast)
                .toList())
        .orElse(List.of());
  }

  /** The only node of the space, which the test placed. */
  static NodePart only(GameTestHelper helper, BlockPos relative) {
    List<NodePart> nodes = all(helper, relative);
    if (nodes.size() != 1) {
      throw new GameTestAssertException(NOT_ONE_NODE);
    }
    return nodes.get(0);
  }

  /** The spot of the only node of the space, in the world. */
  static PartSpot spotOf(GameTestHelper helper, BlockPos relative) {
    return new PartSpot(helper.absolutePos(relative), only(helper, relative).face());
  }

  static void assertPresent(GameTestHelper helper, BlockPos relative) {
    helper.assertFalse(all(helper, relative).isEmpty(), NODE_MISSING);
  }

  static void assertAbsent(GameTestHelper helper, BlockPos relative) {
    helper.assertTrue(all(helper, relative).isEmpty(), NODE_STAYED);
  }

  static void assertPowered(GameTestHelper helper, BlockPos relative, boolean powered) {
    helper.assertTrue(only(helper, relative).powered() == powered, WRONG_POWERED);
  }

  static void assertOutOfRange(GameTestHelper helper, BlockPos relative, boolean out) {
    helper.assertTrue(only(helper, relative).outOfRange() == out, WRONG_RING);
  }
}
