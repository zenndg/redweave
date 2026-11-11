package io.github.marcelofabianov.redweave.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.Optional;

/**
 * A slot as it is in the saves: its channel, which is its place on the node, whether it is enabled,
 * its direction, face and name, and the last reading, left out when it is off (answers 37, 89, 224
 * and 227). A slot never enabled is not saved.
 */
record SavedSlot(ChannelSlot slot, Signal reading) {
  private static final String DIRECTION = "direction";
  private static final String FACE = "face";
  private static final String CHANNEL = "channel";
  private static final String ACTIVE = "active";
  private static final String NAME_PART = "slot name";
  private static final String READING = "reading";

  static final Codec<SavedSlot> CODEC =
      RecordCodecBuilder.create(instance ->
          instance
              .group(
                  SaveCodecs.CHANNEL_COLOR
                      .fieldOf(CHANNEL)
                      .forGetter(saved -> saved.slot().channel()),
                  Codec.BOOL.fieldOf(ACTIVE).forGetter(saved -> saved.slot().active()),
                  SaveCodecs.DIRECTION
                      .fieldOf(DIRECTION)
                      .forGetter(saved -> saved.slot().direction()),
                  SaveCodecs.FACE.fieldOf(FACE).forGetter(saved -> saved.slot().face()),
                  Lenient.optionalField(SaveCodecs.NAME, SaveCodecs.SAVED_NAME, NAME_PART)
                      .forGetter(saved -> saved.slot().name()),
                  Lenient.optionalField(READING, SaveCodecs.SIGNAL, READING)
                      .forGetter(saved -> Optional.of(saved.reading()).filter(Signal::isOn)))
              .apply(
                  instance,
                  (channel, active, direction, face, name, reading) ->
                      new SavedSlot(
                          new ChannelSlot(direction, face, channel, active, name),
                          reading.orElse(Signal.OFF))));

  /** The place of the slot on the node is its channel (answer 224). */
  int index() {
    return slot.channel().ordinal();
  }
}
