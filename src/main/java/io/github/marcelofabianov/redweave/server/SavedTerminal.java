package io.github.marcelofabianov.redweave.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.marcelofabianov.redweave.domain.network.BlockLocation;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;

/**
 * The Network Terminal of a player in the saves, so the limit of one holds with its chunk unloaded
 * (answer 175). The keys are fixed in issue #26.
 */
record SavedTerminal(PlayerId player, BlockLocation block) {
  private static final String PLAYER = "player";
  private static final String BLOCK = "block";

  static final Codec<SavedTerminal> CODEC =
      RecordCodecBuilder.create(instance ->
          instance
              .group(
                  SaveCodecs.PLAYER_ID.fieldOf(PLAYER).forGetter(SavedTerminal::player),
                  SaveCodecs.BLOCK.fieldOf(BLOCK).forGetter(SavedTerminal::block))
              .apply(instance, SavedTerminal::new));
}
