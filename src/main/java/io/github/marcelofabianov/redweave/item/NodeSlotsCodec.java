package io.github.marcelofabianov.redweave.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.CopiedSlot;
import io.github.marcelofabianov.redweave.domain.network.NodeCopy;
import io.github.marcelofabianov.redweave.server.SaveCodecs;
import java.util.List;

/**
 * The copy of the Configurator as it is in the item: the network of the node (answer 238), the face
 * it stuck to, and the channel, status, direction and face of each slot ever enabled, with the keys
 * of the save of the networks (answers 105, 230 and 277).
 */
public final class NodeSlotsCodec {
  private static final String NETWORK = "network";
  private static final String ATTACHED = "attached";
  private static final String SLOTS = "slots";
  private static final String ACTIVE = "active";
  private static final String DIRECTION = "direction";
  private static final String FACE = "face";
  private static final String CHANNEL = "channel";
  private static final String REPEATED = "A copy has each slot once";

  private static final Codec<CopiedSlot> SLOT =
      RecordCodecBuilder.create(instance ->
          instance
              .group(
                  SaveCodecs.CHANNEL_COLOR
                      .fieldOf(CHANNEL)
                      .forGetter(copied -> copied.slot().channel()),
                  Codec.BOOL.fieldOf(ACTIVE).forGetter(copied -> copied.slot().active()),
                  SaveCodecs.DIRECTION
                      .fieldOf(DIRECTION)
                      .forGetter(copied -> copied.slot().direction()),
                  SaveCodecs.FACE.fieldOf(FACE).forGetter(copied -> copied.slot().face()))
              .apply(
                  instance,
                  (channel, active, direction, face) ->
                      new CopiedSlot(
                          channel.ordinal(), new ChannelSlot(direction, face, channel, active))));

  /** A repeated slot would break the domain, so the codec refuses the copy first (rule 14). */
  private static final Codec<List<CopiedSlot>> SLOTS_CODEC =
      SLOT.listOf().validate(NodeSlotsCodec::eachIndexOnce);

  public static final Codec<ConfiguratorCopy> CODEC =
      RecordCodecBuilder.create(instance ->
          instance
              .group(
                  SaveCodecs.NETWORK_ID.fieldOf(NETWORK).forGetter(ConfiguratorCopy::network),
                  SaveCodecs.FACE.fieldOf(ATTACHED).forGetter(copy -> copy.slots().attached()),
                  SLOTS_CODEC.fieldOf(SLOTS).forGetter(copy -> copy.slots().slots()))
              .apply(
                  instance,
                  (network, attached, slots) ->
                      new ConfiguratorCopy(network, new NodeCopy(attached, slots))));

  private NodeSlotsCodec() {}

  private static DataResult<List<CopiedSlot>> eachIndexOnce(List<CopiedSlot> slots) {
    long distinct = slots.stream().map(CopiedSlot::index).distinct().count();
    return distinct == slots.size() ? DataResult.success(slots) : DataResult.error(() -> REPEATED);
  }
}
