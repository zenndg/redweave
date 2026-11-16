package io.github.marcelofabianov.redweave.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.item.CableColorCodec;
import io.github.marcelofabianov.redweave.item.ConfiguratorCopy;
import io.github.marcelofabianov.redweave.item.ConfiguratorMode;
import io.github.marcelofabianov.redweave.item.ModuleCopy;
import io.github.marcelofabianov.redweave.item.ModuleCopyCodec;
import io.github.marcelofabianov.redweave.item.NodeSlotsCodec;
import io.github.marcelofabianov.redweave.item.RemoteLabel;
import io.github.marcelofabianov.redweave.item.RemoteState;
import io.github.marcelofabianov.redweave.item.RemoteStateCodec;
import io.netty.buffer.ByteBuf;
import java.util.function.Supplier;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/** The data components of the mod. Their IDs are part of the save (AGENTS.md, rule 15). */
public final class RedweaveDataComponents {
  private static final String NETWORK_ID = "network";
  private static final String NODE_SLOTS_ID = "node_slots";
  private static final String MODULE_COPY_ID = "module_copy";
  private static final String CONFIGURATOR_MODE_ID = "configurator_mode";
  private static final String SIGNAL_REMOTE_ID = "signal_remote";
  private static final String SIGNAL_REMOTE_LABEL_ID = "signal_remote_label";
  private static final String CABLE_COLOR_ID = "cable_color";
  private static final String UNKNOWN_MODE = "Unknown Configurator mode: ";

  /** A saved mode that is not known is refused, and the item falls back to the first mode. */
  private static final Codec<ConfiguratorMode> MODE_CODEC =
      Codec.STRING.comapFlatMap(
          name ->
              ConfiguratorMode.byName(name)
                  .map(DataResult::success)
                  .orElseGet(() -> DataResult.error(() -> UNKNOWN_MODE + name)),
          ConfiguratorMode::serializedName);

  private static final DeferredRegister.DataComponents COMPONENTS =
      DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, RedweaveMod.MOD_ID);

  /** The network an item keeps, such as a broken controller (answers 24 and 65). */
  public static final Supplier<DataComponentType<NetworkId>> NETWORK =
      COMPONENTS.registerComponentType(
          NETWORK_ID,
          builder ->
              builder
                  .persistent(UUIDUtil.CODEC.xmap(NetworkId::new, NetworkId::value))
                  .networkSynchronized(
                      UUIDUtil.STREAM_CODEC.map(NetworkId::new, NetworkId::value)));

  /** The slots and network copied by the Configurator, to paste on other nodes (105, 238). */
  public static final Supplier<DataComponentType<ConfiguratorCopy>> NODE_SLOTS =
      COMPONENTS.registerComponentType(
          NODE_SLOTS_ID,
          builder ->
              builder
                  .persistent(NodeSlotsCodec.CODEC)
                  .networkSynchronized(ByteBufCodecs.fromCodec(NodeSlotsCodec.CODEC)));

  /** The settings of a Signal Module copied by the Configurator (answers 722 to 729). */
  public static final Supplier<DataComponentType<ModuleCopy>> MODULE_COPY =
      COMPONENTS.registerComponentType(
          MODULE_COPY_ID,
          builder ->
              builder
                  .persistent(ModuleCopyCodec.CODEC)
                  .networkSynchronized(ByteBufCodecs.fromCodec(ModuleCopyCodec.CODEC)));

  /** The mode of the Configurator, switched with Shift + scroll (answers 143 and 173). */
  public static final Supplier<DataComponentType<ConfiguratorMode>> CONFIGURATOR_MODE =
      COMPONENTS.registerComponentType(
          CONFIGURATOR_MODE_ID,
          builder ->
              builder
                  .persistent(MODE_CODEC)
                  .networkSynchronized(ByteBufCodecs.fromCodec(MODE_CODEC)));

  /** The link, mode and state of a Signal Remote, all in the item (answers 287, 356 and 369). */
  public static final Supplier<DataComponentType<RemoteState>> SIGNAL_REMOTE =
      COMPONENTS.registerComponentType(
          SIGNAL_REMOTE_ID,
          builder ->
              builder
                  .persistent(RemoteStateCodec.CODEC)
                  .networkSynchronized(ByteBufCodecs.fromCodec(RemoteStateCodec.CODEC)));

  private static final StreamCodec<ByteBuf, ChannelColor> COLOR_STREAM =
      ByteBufCodecs.VAR_INT.map(index -> ChannelColor.values()[index], ChannelColor::ordinal);

  /** The names of the link of a Signal Remote, only synchronized, never saved (answer 366). */
  public static final Supplier<DataComponentType<RemoteLabel>> SIGNAL_REMOTE_LABEL =
      COMPONENTS.registerComponentType(
          SIGNAL_REMOTE_LABEL_ID,
          builder ->
              builder.networkSynchronized(
                  StreamCodec.composite(
                      ByteBufCodecs.STRING_UTF8,
                      RemoteLabel::network,
                      COLOR_STREAM,
                      RemoteLabel::color,
                      ByteBufCodecs.STRING_UTF8,
                      RemoteLabel::node,
                      COLOR_STREAM,
                      RemoteLabel::channel,
                      ByteBufCodecs.STRING_UTF8,
                      RemoteLabel::channelName,
                      RemoteLabel::new)));

  /** The color of a Signal Cable; a white cable has none (answer 561). */
  public static final Supplier<DataComponentType<ChannelColor>> CABLE_COLOR =
      COMPONENTS.registerComponentType(
          CABLE_COLOR_ID,
          builder ->
              builder
                  .persistent(CableColorCodec.CODEC)
                  .networkSynchronized(CableColorCodec.STREAM_CODEC));

  private RedweaveDataComponents() {}

  public static void register(IEventBus modBus) {
    COMPONENTS.register(modBus);
  }
}
