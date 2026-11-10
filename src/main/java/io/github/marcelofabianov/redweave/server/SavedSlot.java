package io.github.marcelofabianov.redweave.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.marcelofabianov.redweave.config.RedweaveConfig;
import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.Optional;

/**
 * A slot in use, as it is in the saves: where it is on the node, its configuration and the last
 * reading, which is left out when it is off (answers 37 and 89).
 */
record SavedSlot(int index, ChannelSlot slot, Signal reading) {
  private static final String INDEX = "index";
  private static final String DIRECTION = "direction";
  private static final String FACE = "face";
  private static final String CHANNEL = "channel";
  private static final String READING = "reading";

  static final Codec<SavedSlot> CODEC =
      RecordCodecBuilder.create(instance ->
          instance
              .group(
                  Codec.intRange(0, RedweaveConfig.MAX_SLOTS - 1)
                      .fieldOf(INDEX)
                      .forGetter(SavedSlot::index),
                  SaveCodecs.DIRECTION
                      .fieldOf(DIRECTION)
                      .forGetter(saved -> saved.slot().direction()),
                  SaveCodecs.FACE.fieldOf(FACE).forGetter(saved -> saved.slot().face()),
                  SaveCodecs.CHANNEL_COLOR
                      .fieldOf(CHANNEL)
                      .forGetter(saved -> saved.slot().channel()),
                  Lenient.optionalField(READING, SaveCodecs.SIGNAL, READING)
                      .forGetter(saved -> Optional.of(saved.reading()).filter(Signal::isOn)))
              .apply(
                  instance,
                  (index, direction, face, channel, reading) ->
                      new SavedSlot(
                          index,
                          new ChannelSlot(direction, face, channel),
                          reading.orElse(Signal.OFF))));
}
