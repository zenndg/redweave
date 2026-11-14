package io.github.marcelofabianov.redweave.block;

import io.github.marcelofabianov.redweave.domain.module.Draws;
import net.minecraft.util.RandomSource;

/** The draws of the Signal Randomizer from the random source of the world, on the server (483). */
record RandomSourceDraws(RandomSource random) implements Draws {
  @Override
  public int next(int bound) {
    return random.nextInt(bound);
  }
}
