package io.github.zenndg.redweave.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.zenndg.redweave.domain.network.NetworkId;
import io.github.zenndg.redweave.domain.network.PlayerId;

/** The network one player last put a node in, as it is in the saves (answers 157 and 174). */
record LastUsedChoice(PlayerId player, NetworkId network) {
  private static final String PLAYER = "player";
  private static final String NETWORK = "network";

  static final Codec<LastUsedChoice> CODEC =
      RecordCodecBuilder.create(instance ->
          instance
              .group(
                  SaveCodecs.PLAYER_ID.fieldOf(PLAYER).forGetter(LastUsedChoice::player),
                  SaveCodecs.NETWORK_ID.fieldOf(NETWORK).forGetter(LastUsedChoice::network))
              .apply(instance, LastUsedChoice::new));
}
