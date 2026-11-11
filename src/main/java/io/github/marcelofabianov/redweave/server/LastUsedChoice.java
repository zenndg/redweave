package io.github.marcelofabianov.redweave.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;

/** The current network of one player, as it is in the saves (answers 25 and 91). */
record CurrentChoice(PlayerId player, NetworkId network) {
  private static final String PLAYER = "player";
  private static final String NETWORK = "network";

  static final Codec<CurrentChoice> CODEC =
      RecordCodecBuilder.create(instance ->
          instance
              .group(
                  SaveCodecs.PLAYER_ID.fieldOf(PLAYER).forGetter(CurrentChoice::player),
                  SaveCodecs.NETWORK_ID.fieldOf(NETWORK).forGetter(CurrentChoice::network))
              .apply(instance, CurrentChoice::new));
}
