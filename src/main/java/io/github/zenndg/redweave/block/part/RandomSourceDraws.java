package io.github.zenndg.redweave.block.part;

import io.github.zenndg.redweave.domain.module.Draws;
import net.minecraft.util.RandomSource;

/** The draws of the Signal Randomizer from the random source of the world, on the server (483). */
record RandomSourceDraws(RandomSource random) implements Draws {
  @Override
  public int next(int bound) {
    return random.nextInt(bound);
  }
}
