package io.github.marcelofabianov.redweave.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.Optional;

/**
 * A slot as it is in the saves: its channel, which is its place on the node, whether it is enabled,
 * its direction and face, and the last reading, left out when it is off (answers 37, 89 and 224). A
 * slot never enabled is not saved. A slot has no name (answer 277), and the codec ignores the
 * {@code name} key an older save may still carry, like any other unknown key.
 */
record SavedSlot(ChannelSlot slot, Signal reading) {
  private static final String DIRECTION = "direction";
  private static final String FACE = "face";
  private static final String CHANNEL = "channel";
  private static final String ACTIVE = "active";
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
                  Lenient.optionalField(READING, SaveCodecs.SIGNAL, READING)
                      .forGetter(saved -> Optional.of(saved.reading()).filter(Signal::isOn)))
              .apply(
                  instance,
                  (channel, active, direction, face, reading) ->
                      new SavedSlot(
                          new ChannelSlot(direction, face, channel, active),
                          reading.orElse(Signal.OFF))));

  /** The place of the slot on the node is its channel (answer 224). */
  int index() {
    return slot.channel().ordinal();
  }
}
