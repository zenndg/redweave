package io.github.zenndg.redweave.block.part;

import io.netty.buffer.ByteBuf;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.StreamCodec;

/**
 * Where a part is: its block space and the side of that space it rests on, as `down` for a node on
 * the floor (answers 513 and 542). Two parts of one space differ by the side.
 */
public record PartSpot(BlockPos pos, Direction face) {
  private static final String MISSING = "A part spot needs a position and a face";

  public static final StreamCodec<ByteBuf, PartSpot> STREAM_CODEC =
      StreamCodec.composite(
          BlockPos.STREAM_CODEC,
          PartSpot::pos,
          Direction.STREAM_CODEC,
          PartSpot::face,
          PartSpot::new);

  public PartSpot {
    Objects.requireNonNull(pos, MISSING);
    Objects.requireNonNull(face, MISSING);
  }

  /** The block the part rests on, which holds it (answers 74 and 394). */
  public BlockPos support() {
    return pos.relative(face);
  }
}
