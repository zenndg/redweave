package io.github.marcelofabianov.redweave.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.marcelofabianov.redweave.config.RedweaveConfig;
import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.CopiedSlot;
import io.github.marcelofabianov.redweave.domain.network.NodeCopy;
import io.github.marcelofabianov.redweave.server.SaveCodecs;
import java.util.List;

/**
 * The copy of the Configurator as it is in the item: the face the node stuck to, and the index,
 * direction, face and channel of each slot, with the keys of the save of the networks (105).
 */
public final class NodeSlotsCodec {
  private static final String ATTACHED = "attached";
  private static final String SLOTS = "slots";
  private static final String INDEX = "index";
  private static final String DIRECTION = "direction";
  private static final String FACE = "face";
  private static final String CHANNEL = "channel";
  private static final String REPEATED = "A copy has each slot once";

  private static final Codec<CopiedSlot> SLOT =
      RecordCodecBuilder.create(instance ->
          instance
              .group(
                  Codec.intRange(0, RedweaveConfig.MAX_SLOTS - 1)
                      .fieldOf(INDEX)
                      .forGetter(CopiedSlot::index),
                  SaveCodecs.DIRECTION
                      .fieldOf(DIRECTION)
                      .forGetter(copied -> copied.slot().direction()),
                  SaveCodecs.FACE.fieldOf(FACE).forGetter(copied -> copied.slot().face()),
                  SaveCodecs.CHANNEL_COLOR
                      .fieldOf(CHANNEL)
                      .forGetter(copied -> copied.slot().channel()))
              .apply(
                  instance,
                  (index, direction, face, channel) ->
                      new CopiedSlot(index, new ChannelSlot(direction, face, channel))));

  /** A repeated slot would break the domain, so the codec refuses the copy first (rule 14). */
  private static final Codec<List<CopiedSlot>> SLOTS_CODEC =
      SLOT.listOf().validate(NodeSlotsCodec::eachIndexOnce);

  public static final Codec<NodeCopy> CODEC =
      RecordCodecBuilder.create(instance ->
          instance
              .group(
                  SaveCodecs.FACE.fieldOf(ATTACHED).forGetter(NodeCopy::attached),
                  SLOTS_CODEC.fieldOf(SLOTS).forGetter(NodeCopy::slots))
              .apply(instance, NodeCopy::new));

  private NodeSlotsCodec() {}

  private static DataResult<List<CopiedSlot>> eachIndexOnce(List<CopiedSlot> slots) {
    long distinct = slots.stream().map(CopiedSlot::index).distinct().count();
    return distinct == slots.size() ? DataResult.success(slots) : DataResult.error(() -> REPEATED);
  }
}
